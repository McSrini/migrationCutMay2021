/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/**
 *
 * @author tamvadss
 */
public class BranchHandler extends IloCplex.BranchCallback{

    protected void main() throws IloException {
        // 
        if ( getNbranches()> ZERO ){  
            
            String thisNodeID=getNodeId().toString();
            if (thisNodeID.equals( MIPROOT_NODE_ID)){
                //root node
                NodeAttachment attachment = new   NodeAttachment ( );
                setNodeData (attachment );
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
                   
                    //create the kid
                    kid = makeBranch(var,bound, dir ,getObjValue(), attach); 
                     
                    
                }

                //TEST
                //System.out.println("Node " + getNodeId() + " created " + kid + " isdown " +  isDownBranch + " var " + var.getName() + " bound "+ bound) ;

            }//for both kids 

        }//end if num branches > 0 
    }//end main
    
  
 
}//end class
