/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class NodeHandler  extends IloCplex.NodeCallback {
    
    public List<NodeAttachment> leafNodeList = new ArrayList<NodeAttachment>();
    
    protected void main() throws IloException {
        //
        final long LEAFCOUNT =getNremainingNodes64();
        if (LEAFCOUNT>ZERO) {
            for (long leafNum = ZERO; leafNum < LEAFCOUNT; leafNum ++){
                
                try {
                    IloCplex.NodeId   nodeID =getNodeId(leafNum) ;
                    NodeAttachment attachment= (NodeAttachment)getNodeData(  leafNum );
                    double lpRelaxObjective = getObjValue (leafNum) ;
                    
                    if (null!= attachment) {
                        
                        //this leaf and its ancestors have always been in memory
                        leafNodeList.add (attachment);
                        
                        //System.out.println(nodeID );
                    }
                    
                }catch (Exception ex){
                    //ignore this leaf                    
                }
                                     
            }
        }
        
        /*System.out.println("printing leafs");
        for (TreeStructureNode  leaf :leafNodeAttahments){
            System.out.println(leaf.nodeID);
        }*/
        
        
        System.out.println(" out of " + LEAFCOUNT + " farmed " + leafNodeList.size());
        
        abort();
    }
    
    
}

