/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip.embeddedBI;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import ca.mcmaster.migrationcutmay2021.mip.NodeAttachment;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class EmbeddedBI_BranchHandler extends IloCplex.BranchCallback{
    private BI_Tree biTree;
    private Map<String, IloNumVar> variablesInThisModel;
    public EmbeddedBI_BranchHandler (BI_Tree biTree, Map<String, IloNumVar> variablesInThisModel ) {
        this.biTree = biTree;
        this. variablesInThisModel = variablesInThisModel;
    }


    protected void main() throws IloException {
        // 
        if ( getNbranches()> ZERO ){  
            
            String thisNodeID=getNodeId().toString();
            if (thisNodeID.equals( MIPROOT_NODE_ID)){
                //root node
                NodeAttachment attachment = new   NodeAttachment ( );
                setNodeData (attachment );
                
                attachment.biTree= this.biTree;
            } 
            
            //get the branches about to be created
            IloNumVar[][] vars = new IloNumVar[TWO][] ;
            double[ ][] bounds = new double[TWO ][];
            IloCplex.BranchDirection[ ][]  dirs = new  IloCplex.BranchDirection[ TWO][];
            getBranches(  vars, bounds, dirs);
            
            NodeAttachment thisNodesAttachment = null;
            try {
                thisNodesAttachment  = (NodeAttachment) getNodeData () ;
            }        catch (Exception ex){
                //stays null
            }
            
            //now allow  both kids to spawn
            for (int childNum = ZERO ;childNum<getNbranches();  childNum++) {   

                IloNumVar var = vars[childNum][ZERO];
                double bound = bounds[childNum][ZERO];
                IloCplex.BranchDirection dir =  dirs[childNum][ZERO];     

                boolean isDownBranch = dir.equals(   IloCplex.BranchDirection.Down);

                IloCplex.NodeId  kid = null;
                if (null==thisNodesAttachment){
                    //default
                    kid = makeBranch(var,bound, dir ,getObjValue());
                }else {
                    NodeAttachment attach = new NodeAttachment ( );
                    attach.parentNode = thisNodesAttachment;
                    
                    if (isDownBranch) {
                        attach.amITheDownBranchChild = true;
                        thisNodesAttachment.branchingVarName= var.getName();
                        thisNodesAttachment.upperBound= bound;                         
                    } else {
                        if (thisNodesAttachment.branchingVarName==null){
                            thisNodesAttachment.branchingVarName= var.getName();
                            thisNodesAttachment.upperBound= bound- ONE; 
                        }
                    }
                    
                    if (thisNodesAttachment.biTree!=null){
                        //overrule CPLEX branching, and pass on branching instruction tree to child node
                        CompoundBI cbi = null;
                        if (isDownBranch){
                            attach.biTree= thisNodesAttachment.biTree.downTree;
                            cbi = thisNodesAttachment.biTree.downBranch;                            
                        }else{
                            attach.biTree= thisNodesAttachment.biTree.upTree;
                            cbi = thisNodesAttachment.biTree.upBranch;
                        }
                        
                        kid = makeBranch (cbi, attach) ;
                        
                        //TEST
                        //System.out.println("Node " + getNodeId() + " created " + kid + " isdown " +  isDownBranch);
                        //cbi.printMe();
                        
                    }else {
                        //create the kid using default CPLEX branching
                        kid = makeBranch(var,bound, dir ,getObjValue(), attach); 
                        //System.out.println("Node " + getNodeId() + " created " + kid + " isdown " +  isDownBranch + " var " + var.getName() + " bound "+ bound) ;
                    }
                    
                }

            }//for both kids 

        }//end if num branches > 0 
    }//end main
    
    private IloCplex.NodeId makeBranch (CompoundBI cbi, NodeAttachment attach) throws IloException {
                
        // branches about to be created
        int size = cbi.size();
        IloNumVar[] vars = new IloNumVar[size] ;
        double[] bounds = new double[size];
        IloCplex.BranchDirection[ ]  dirs = new  IloCplex.BranchDirection[size];
        
        //System.out.println("compound branch condition") ;
               
        for ( int index = ZERO; index < size ; index ++){
             
            vars[index] = this.variablesInThisModel.get( cbi.vars.get(index));
            bounds[index] =  cbi.bounds.get(index);
            dirs[index] = cbi.isDownBranch.get(index)? IloCplex.BranchDirection.Down : IloCplex.BranchDirection.Up;
            
            //System.out.println("Var " + SubTree.varMap.get( entry.getKey().varName) + " dir " +entry.getValue() ) ;
             
        }
                
        return makeBranch (vars, bounds, dirs ,   getObjValue(), attach);
    }
    
}//end class
