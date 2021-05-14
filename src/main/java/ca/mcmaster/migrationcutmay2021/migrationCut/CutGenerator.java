/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.migrationCut;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import static ca.mcmaster.migrationcutmay2021.Parameters.*;
import static ca.mcmaster.migrationcutmay2021.utils.CplexUtils.*;
import static ca.mcmaster.migrationcutmay2021.utils.FileUtils.savePriorityListToDisk;
import ilog.concert.IloException;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import static ilog.concert.IloNumVarType.Float;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author tamvadss
 */
public class CutGenerator {
        
    private IloCplex  cplex;
    private Map<String, IloNumVar> mapOfVaribles_in_the_Model ;
    private static Map <String, Integer> variables_LowerBounds = new  TreeMap <String, Integer>();
    private static Map <String, Integer> variables_UpperBounds = new  TreeMap <String, Integer>();
    
    //binary variable already created, for using in the migration cut ?
    private TreeMap<String, TreeMap<Integer, IloNumVar >> binaryVariableMap_downBranch = 
            new  TreeMap<String, TreeMap<Integer, IloNumVar >> ();
    private TreeMap<String, TreeMap<Integer, IloNumVar >> binaryVariableMap_upBranch = 
            new  TreeMap<String, TreeMap<Integer, IloNumVar >> ();
    
    //stores frequency of occurence of  vars used in migration cuts
    private TreeMap<String, Integer > variable_FrequencyMap = new   TreeMap<String, Integer > ();    
    private TreeMap< Integer , List<String>> inverted_variable_FrequencyMap = new   TreeMap< Integer , List<String>>();
     
    
    
    
    public CutGenerator (IloCplex cplex) throws IloException {
         
        this.cplex = cplex;
        mapOfVaribles_in_the_Model = getVariables(this.cplex);
        for (Map.Entry <String, IloNumVar> entry : mapOfVaribles_in_the_Model.entrySet()){
            variables_LowerBounds.put (entry.getKey(), (int)Math.round(entry.getValue().getLB() ));
            double ub = entry.getValue().getUB();
            //missing upper bound is returned as 1e20, which is larger than java max int
            if (ub >= BILLION) ub = BILLION;
            variables_UpperBounds.put (entry.getKey(), (int)Math.round ( ub) );
                         
        }
        
    }
    
    public    void prohibitAll (List<List<VarBoundDirection>> fathomedBranches) throws IloException {
        for (List<VarBoundDirection> fb :fathomedBranches ){
            prohibit(fb);
        }
        
        
        
    }
        
    public void createPriorityList () throws Exception {
        for (Map.Entry<String, Integer > entry :variable_FrequencyMap.entrySet()){
            List<String> current = inverted_variable_FrequencyMap.get( -entry.getValue());
            if (null==current) current = new ArrayList<String>();
            current.add (entry.getKey() );
            inverted_variable_FrequencyMap.put(- entry.getValue(), current);
        }
        
        List<String> varPriorityList = new ArrayList<String>   ();
        for (List<String> sl: inverted_variable_FrequencyMap.values()){
            varPriorityList.addAll(sl);
        }
        //Collections.reverse(varPriorityList);
        savePriorityListToDisk(varPriorityList, PRIORITY_LIST_FILENAME) ;
        List<String> origVars = new ArrayList<String>();
        for (Map.Entry<String , IloNumVar> entry : mapOfVaribles_in_the_Model.entrySet()){
            if (! entry.getValue().getType().equals(Float)) origVars.add (  entry.getKey());
        }
        
        savePriorityListToDisk( origVars, PRIORITY_LIST_ORIGINAL_VARS ) ;
        
    }
    
    
    private   void prohibit (List<VarBoundDirection> fathomedBranch) throws IloException {
        
        /*System.out.println("TEST Prohibit branch ");
        for (VarBoundDirection vbd: fathomedBranch){
            vbd.printMe();
        }
        System.out.println("END TEST");*/
        
        IloNumExpr migrationCut = cplex.numExpr();
        
        for (VarBoundDirection vbd :  fathomedBranch){
            
            IloNumVar thisVar =mapOfVaribles_in_the_Model.get (vbd.varName) ;
            if (thisVar.getType().equals(IloNumVarType.Bool )){
                
                if (vbd.isUpperBound) {
                    migrationCut= cplex.sum(migrationCut, thisVar);
                }else {
                    IloNumExpr expr = cplex.numExpr();
                    expr = cplex.sum(expr, -ONE );
                    expr = cplex.sum(expr, thisVar );
                    expr = cplex.prod (expr , -ONE );
                    migrationCut= cplex.sum(migrationCut,  expr);
                }
                
                Integer current = variable_FrequencyMap.get (thisVar.getName()) ;
                variable_FrequencyMap.put (thisVar.getName(), ONE+ (current==null? ZERO: current)) ; 
                
                continue;
            }            
            
            //check if integer variable already split
            //if yes, get binary var used for migration cut
            //else create binary var needed for migration cut
            IloNumVar binaryVar = null;
            IloNumVar complementaryBinaryVar = null;
            if (vbd.isUpperBound){
                //down branch
                binaryVar = null;
                if ( binaryVariableMap_downBranch.containsKey(vbd.varName)) {
                    binaryVar = binaryVariableMap_downBranch.get(vbd.varName).get(vbd.bound);
                }          
                
                if (null == binaryVar){
                    //check if binary var has been created for complementary condition
                    //in which case its compliment can be used in the migration cut
                    if ( binaryVariableMap_upBranch.containsKey(vbd.varName)) {
                        complementaryBinaryVar = binaryVariableMap_upBranch.get (vbd.varName) .get(vbd.bound +ONE);
                    }                    
                }
                
                if (null == binaryVar && null== complementaryBinaryVar){
                    //create binary var
                    IloNumVar var = mapOfVaribles_in_the_Model.get(vbd. varName);
                    binaryVar=  prohibit_DownBranch (var, vbd. bound);
                    //update map
                    TreeMap<Integer, IloNumVar > current = binaryVariableMap_downBranch.get(vbd.varName);
                    if (current == null)current = new  TreeMap<Integer, IloNumVar >();
                    current.put (vbd.bound,  binaryVar );
                    binaryVariableMap_downBranch.put (vbd.varName, current );
                } 
                    
                if (null != binaryVar){
                    Integer current = variable_FrequencyMap.get (binaryVar.getName()) ;
                    variable_FrequencyMap.put ( binaryVar.getName() , ONE+ (current==null? ZERO: current)) ;      
                }else {
                    //complementaryBinaryVar is not null
                    Integer current = variable_FrequencyMap.get (complementaryBinaryVar.getName()) ;
                    variable_FrequencyMap.put ( complementaryBinaryVar.getName() , ONE+ (current==null? ZERO: current)) ;   
                }
                          
                                
            }else {
                //up branch
                binaryVar = null;                
                if (binaryVariableMap_upBranch.containsKey(vbd.varName)){
                    binaryVar = binaryVariableMap_upBranch.get(vbd.varName).get(vbd.bound);
                }
                
                if (null == binaryVar){
                    //check if binary var has been created for complementary condition
                    //in which case its compliment can be used in the migration cut
                    if ( binaryVariableMap_downBranch.containsKey(vbd.varName)) {
                        complementaryBinaryVar = binaryVariableMap_downBranch.get (vbd.varName) .get(vbd.bound -ONE);
                    }                    
                }
                
                if (null == binaryVar && null== complementaryBinaryVar){
                    IloNumVar var = mapOfVaribles_in_the_Model.get(vbd. varName);
                    binaryVar=  prohibit_UpBranch ( var, vbd. bound);
                    //update map
                    TreeMap<Integer, IloNumVar > current = binaryVariableMap_upBranch.get(vbd.varName);
                    if (current == null)current = new  TreeMap<Integer, IloNumVar >();
                    current.put (vbd.bound,  binaryVar );
                    binaryVariableMap_upBranch.put (vbd.varName, current );
                }
                 
                if (null != binaryVar){
                    Integer current = variable_FrequencyMap.get (binaryVar.getName()) ;
                    variable_FrequencyMap.put ( binaryVar.getName() , ONE+ (current==null? ZERO: current)) ;      
                }else {
                    //complementaryBinaryVar is not null
                    Integer current = variable_FrequencyMap.get (complementaryBinaryVar.getName()) ;
                    variable_FrequencyMap.put ( complementaryBinaryVar.getName() , ONE+ (current==null? ZERO: current)) ;   
                }
                
            }
            
            if (null != binaryVar){
                migrationCut= cplex.sum(migrationCut, binaryVar);
            }else {
                //use complimentary var
                IloNumExpr expr = cplex.numExpr();
                expr = cplex.sum(expr, -ONE );
                expr = cplex.sum(expr,  complementaryBinaryVar );
                expr = cplex.prod (expr , -ONE );
                migrationCut= cplex.sum(migrationCut,  expr);
            }
            
        }//end for
        
        cplex.addGe( migrationCut, ONE);
    }
        
        
    //upperbound
    //return binary variable that should be added onto the migration cut
    private IloNumVar prohibit_DownBranch (IloNumVar var, int bound) throws IloException {
        
        //example: prohibit  Y <= 5
        
        
        //System.out.println("TEST : prohibit_DownBranch " + var.getName() + " <= " + bound) ;
        
        IloNumVar binaryVar = null;
        
        IloNumVar[] createdVars= new IloNumVar[FIVE] ;
        
        String[] xName_Binary = new String[ONE];
        xName_Binary[ZERO]=(  CUT_BINARY_VARIABLE_PREFIX + (++COUNTER_FOR_CUT_VARIABLES));
        createdVars[ZERO] =  cplex.boolVarArray (ONE,  xName_Binary )[ZERO];
        binaryVar = createdVars[ZERO];
        
        String[] xName0 = new String[ONE];
        xName0[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + "" + ZERO;
        createdVars[ONE] =  cplex.numVarArray (ONE, 
                                               variables_LowerBounds.get( var.getName()), 
                                               ONE+ bound, 
                                               IloNumVarType.Int,  xName0)[ZERO] ;
        
        String[] xName1 = new String[ONE];
        xName1[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + ""+ ONE;
        createdVars [TWO]=  cplex.numVarArray (ONE, ZERO, 
                                               variables_UpperBounds.get( var.getName()) - bound -ONE, 
                                               IloNumVarType.Int, xName1 ) [ZERO];
        
      
        String[] xName2 = new String[ONE];
        xName2[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + ""+ TWO;
        createdVars [THREE]=  cplex.numVarArray (ONE, variables_LowerBounds.get( var.getName()), 
                                                bound, 
                                               IloNumVarType.Int,  xName2)[ZERO] ;
        
        String[] xName3 = new String[ONE];
        xName3[ZERO]=CUT_INTEGER_VARIABLE_PREFIX +  var.getName() + COUNTER_FOR_CUT_VARIABLES + ""+ THREE;
        createdVars [FOUR]=  cplex.numVarArray (ONE, ZERO, 
                                               variables_UpperBounds.get( var.getName()) - bound , 
                                               IloNumVarType.Int, xName3 ) [ZERO];
        
               
        cplex.add(createdVars);
        
        //add constraints to model
        //example:    Y = Y1 + Y2
        IloNumExpr sumExpression = cplex.numExpr();
        sumExpression =cplex.sum(sumExpression, createdVars[ONE]);
        sumExpression =cplex.sum(sumExpression, createdVars[TWO]);
        cplex.addEq(sumExpression,  var);
        
        //example:    Y1 >= X * (1+5) 
        IloNumExpr boundExpression = cplex.numExpr();
        boundExpression= cplex.sum(boundExpression, cplex.prod( binaryVar,  ONE + bound)) ;
        cplex.addGe(  createdVars[ONE], boundExpression);
        
        //need complementary constraint
                
        //example:    Y = Y3 + Y4
        IloNumExpr another_sumExpression = cplex.numExpr();
        another_sumExpression =cplex.sum(another_sumExpression, createdVars[THREE]);
        another_sumExpression =cplex.sum(another_sumExpression, createdVars[FOUR]);
        cplex.addEq(another_sumExpression,  var);
        
        //example:    Y4 <= X * (UB-5) 
        IloNumExpr another_boundExpression = cplex.numExpr();
        another_boundExpression= cplex.sum(another_boundExpression, cplex.prod( binaryVar,   
                variables_UpperBounds.get( var.getName()) - bound )) ;
        cplex.addLe(  createdVars[FOUR], another_boundExpression);
        
        //System.out.println("TEST : Binary var name is "+  binaryVar.getName());
        
        return binaryVar;
        
    }
    
    private IloNumVar prohibit_UpBranch (IloNumVar var, int bound) throws IloException {
        
        //example : prohibit  Y >= 27
        
        //System.out.println("TEST: prohibit_UpBranch " + var.getName() + " >= " + bound) ;
        
        IloNumVar binaryVar = null;
        
        IloNumVar[] createdVars= new IloNumVar[FIVE] ;
        
        String[] xName_Binary = new String[ONE];
        xName_Binary[ZERO]=(  CUT_BINARY_VARIABLE_PREFIX +  + (++COUNTER_FOR_CUT_VARIABLES));
        createdVars[ZERO] =  cplex.boolVarArray (ONE,  xName_Binary )[ZERO];
        binaryVar = createdVars[ZERO];
        
        String[] xName0 = new String[ONE];
        xName0[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + "" + ZERO;
        createdVars[ONE] =  cplex.numVarArray (ONE, 
                                              variables_LowerBounds.get( var.getName()) , 
                                               bound -ONE  , 
                                               IloNumVarType.Int,  xName0)[ZERO] ;
        
        int upperBnd =  variables_UpperBounds.get( var.getName());
        String[] xName1 = new String[ONE];
        xName1[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + ""+ ONE;
        createdVars [TWO]=  cplex.numVarArray (ONE,  ZERO, 
                                                 upperBnd - bound +ONE, 
                                               IloNumVarType.Int, xName1 ) [ZERO];
                
        //if X =0, prohibit Y <=26, ie ensure Y >=27
        String[] xName2 = new String[ONE];
        xName2[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + ""+ TWO;
        createdVars [THREE]=  cplex.numVarArray (ONE, variables_LowerBounds.get( var.getName()), 
                                                bound, 
                                               IloNumVarType.Int,  xName2)[ZERO] ;
        
        String[] xName3 = new String[ONE];
        xName3[ZERO]= CUT_INTEGER_VARIABLE_PREFIX + var.getName() + COUNTER_FOR_CUT_VARIABLES + ""+ THREE;
        createdVars [FOUR]=  cplex.numVarArray (ONE, ZERO, 
                                               variables_UpperBounds.get( var.getName()) - bound , 
                                               IloNumVarType.Int, xName3 ) [ZERO];
        
               
        cplex.add(createdVars);
        
        //add constraints to model
        //example :    Y =  Y1 + Y2
        IloNumExpr sumExpression = cplex.numExpr();
        sumExpression =cplex.sum(sumExpression, createdVars[ONE]);
        sumExpression =cplex.sum(sumExpression, createdVars[TWO]);
        cplex.addEq(sumExpression,  var);
        
        //example :    Y2 <=   (X-1)*(27-100-1)= 74* Xbar
        IloNumExpr boundExpression = cplex.numExpr();
        boundExpression= cplex.sum  (boundExpression, -ONE) ;
        boundExpression= cplex.sum  (boundExpression,  binaryVar) ;
        boundExpression=  cplex.prod(boundExpression, bound -upperBnd   -ONE)  ;
        
        cplex.addLe( createdVars [TWO], boundExpression );
        
        //complimentary constraints also needed
         //example:    Y = Y3 + Y4
        IloNumExpr another_sumExpression = cplex.numExpr();
        another_sumExpression =cplex.sum(another_sumExpression, createdVars[THREE]);
        another_sumExpression =cplex.sum(another_sumExpression, createdVars[FOUR]);
        cplex.addEq(another_sumExpression,  var);
        
        //example:    Y3 >= (1-X) * 27
        // prohibit Y <=26, ie ensure Y >=27 when X=0   
        IloNumExpr another_boundExpression = cplex.numExpr();
        another_boundExpression= cplex.sum(another_boundExpression, cplex.prod( binaryVar, - bound )) ;
        another_boundExpression= cplex.sum(another_boundExpression,  bound ) ;
        cplex.addGe(  createdVars[THREE], another_boundExpression);
                
        //System.out.println("TEST: Binary var name is "+  binaryVar.getName());
        
        return binaryVar;
        
    }
    
    
    
}//end class
