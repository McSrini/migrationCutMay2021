/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import static ca.mcmaster.migrationcutmay2021.Parameters.*;
import ca.mcmaster.migrationcutmay2021.migrationCut.VarBoundDirection;
import ca.mcmaster.migrationcutmay2021.tree.*;
import static ca.mcmaster.migrationcutmay2021.utils.CplexUtils.*;
import static ca.mcmaster.migrationcutmay2021.utils.FileUtils.*;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import static java.lang.System.exit;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author tamvadss
 */
public class SearchTree {
    
    private static Logger logger;
    
    private IloCplex cplex;
   
    static {
        logger=Logger.getLogger(SearchTree.class);
        logger.setLevel(LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  
                RollingFileAppender(layout,LOG_FOLDER+SearchTree.class.getSimpleName()+ LOG_FILE_EXTENSION);
            rfa.setMaxBackupIndex(SIXTY);
            logger.addAppender(rfa);
            logger.setAdditivity(false);     
                         
        } catch (Exception ex) {
            ///
            System.err.println("Exit: unable to initialize logging"+ex);       
            exit(ONE);
        }
    }
    
    public SearchTree (IloCplex  cplex, boolean applyPriorities) throws Exception{
        
        if (null == cplex) {
            this.cplex = new IloCplex ();
            this. cplex.importModel( MIP_FOLDER + INPUT_MIP_NAME);
        }else {
            this.cplex = cplex;
        }
        
        if (applyPriorities){ 
            applyVarPriority(this.cplex) ;
        }
                
        setConfiguration(this.cplex);                
    }
    
    public void end (){
        cplex.end();
    }
    
    public double solve (int NUM_HOURS, Double upperCutoff) throws Exception{
        
        double bestSolutionFound = BILLION;
        
        logger.info ("Solve starting  on host "+ InetAddress.getLocalHost().getHostName() + 
                       " for MIP " + INPUT_MIP_NAME
                       + " emphasis " + MIP_EMPHASIS_TO_USE);
        cplex.setParam( IloCplex.Param.Threads, MAX_THREADS);
        
        cplex.setParam( IloCplex.Param.TimeLimit,  SIXTY* SOLUTION_CYCLE_TIME_MINUTES  );
        
        if (null!=upperCutoff) cplex.setParam (IloCplex.Param.MIP.Tolerances.UpperCutoff, upperCutoff) ;
                 
        cplex.clearCallbacks();
        BranchHandler bh = new BranchHandler ();
        cplex.use (bh );
        
        for (int hour = ONE; hour <=  NUM_HOURS ; hour ++){    
            
            cplex.solve();                
            
            double relativeMipGap = BILLION;
            if (cplex.getStatus().equals( IloCplex.Status.Feasible ) || cplex.getStatus().equals( IloCplex.Status.Optimal )){
                bestSolutionFound =cplex.getObjValue();
                relativeMipGap=  cplex.getMIPRelativeGap();
            } 
           
            
            logger.info("" + hour + ","+  bestSolutionFound + ","+  
                cplex.getBestObjValue() + "," + cplex.getNnodesLeft64() +
                "," + cplex.getNnodes64() + "," + relativeMipGap ) ;     
             
           
            
            if (cplex.getStatus().equals( IloCplex.Status.Infeasible ) || cplex.getStatus().equals( IloCplex.Status.Optimal )){
                break;
            }
            
        }//end for
        
        return bestSolutionFound;
        
    }
    
    public ObjectForExport getObjectForExport () throws Exception {
        
        List<NodeAttachment> leafNodes = getLeaves();
        
        List<VarBoundDirection> lcaNode_BranchingConditions = new ArrayList<VarBoundDirection>();
        Tree tree = getTree(  leafNodes, lcaNode_BranchingConditions);
                
        IloCplex cplexForExport = new IloCplex ();
        cplexForExport.importModel( MIP_FOLDER + INPUT_MIP_NAME);
        Map <String, IloNumVar> variablesInTheExportModel =  getVariables(cplexForExport) ;
        for (VarBoundDirection vbd: lcaNode_BranchingConditions){
            updateVariableBounds (variablesInTheExportModel.get(vbd.varName),   vbd.bound,   vbd.isUpperBound  ) ;
            System.out.println(variablesInTheExportModel.get(vbd.varName) + " " +   vbd.bound + " " +    vbd.isUpperBound );
        }
        
        ObjectForExport exportObj = new ObjectForExport ();
        exportObj.cplex =cplexForExport;
        exportObj .tree = tree;
        return exportObj;
    }
    
    private  List<NodeAttachment> getLeaves() throws IloException{
        cplex.setParam( IloCplex.Param.Threads, ONE);
        NodeHandler nh = new NodeHandler ();
        cplex.use (nh );
        cplex.solve();
        return nh.leafNodeList;
    }
    
    //return tree rooted at LCA node of the supplied leafs
    //accumulate LCA node branching conditions from MIP root
    private static Tree getTree(List<NodeAttachment> leafNodes,  List<VarBoundDirection> lcaNode_BranchingConditions) 
            throws Exception{
        
        TreeNode rootNode = new TreeNode(   );
        
         
        
        for (NodeAttachment attachment : leafNodes){
            List<VarBoundDirection> branchingConditions = new ArrayList <VarBoundDirection>();
            NodeAttachment parent = attachment.parentNode;
            NodeAttachment thisNode = attachment;
            while (parent!=null){
                if (thisNode.amITheDownBranchChild){
                    VarBoundDirection condition = 
                            new VarBoundDirection (parent.branchingVarName,  
                                    (int) Math.round( parent.upperBound), 
                                    true);
                    branchingConditions.add (condition);
                }else {
                    VarBoundDirection condition = new VarBoundDirection (
                            parent.branchingVarName,ONE+ ( (int) Math.round( parent.upperBound) ) ,
                            false );                   
                    branchingConditions.add (condition);
                }
                
                thisNode = parent;
                parent = parent.parentNode;                
            }
            
            Collections.reverse(branchingConditions);
            TreeNode thisTreeNode = rootNode;
            for (VarBoundDirection vbd: branchingConditions){
                TreeNode childNode=null;
                if (vbd.isUpperBound && thisTreeNode.downChild==null){                    
                    childNode = new TreeNode ();
                    thisTreeNode.downChild=childNode;
                    thisTreeNode.downBranch = vbd;
                } else                 if (! vbd.isUpperBound && thisTreeNode.upChild==null){                    
                     childNode = new TreeNode ();
                     thisTreeNode.upChild= childNode;
                     thisTreeNode.upBranch = vbd;
                }else {
                     childNode =  vbd.isUpperBound? thisTreeNode.downChild: thisTreeNode.upChild;
                }
                
                thisTreeNode = childNode;
            }
            
        }
        
        while (! Tree.isLCAOrLeaf(rootNode) ){
            VarBoundDirection cond = rootNode.downChild== null ? rootNode.upBranch: rootNode.downBranch;
            lcaNode_BranchingConditions.add (cond);
            rootNode = rootNode.downChild== null ? rootNode.upChild: rootNode.downChild;            
        }
        
        return new Tree (rootNode, lcaNode_BranchingConditions);        
    }//end method gettree 
    
}//end class
