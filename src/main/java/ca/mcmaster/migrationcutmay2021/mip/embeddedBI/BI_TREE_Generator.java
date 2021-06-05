/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip.embeddedBI;

import static ca.mcmaster.migrationcutmay2021.Constants.ONE;
import ca.mcmaster.migrationcutmay2021.mip.NodeAttachment;
import ca.mcmaster.migrationcutmay2021.tree.LCANode;
import ca.mcmaster.migrationcutmay2021.tree.Tree;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class BI_TREE_Generator {
    
    public static BI_Tree generate_BI_Tree ( List<NodeAttachment> leafNodes){
        
        BI_Tree result = null;
        // create tree rooted at rootNode
        NodeAttachment rootNode = null;
        
        //algorithm 1 : find perfect LCA nodes
        for (NodeAttachment leaf : leafNodes){
            
            NodeAttachment currentNode = leaf;
            boolean isCurrentNodeALeaf = true;
            NodeAttachment parentNode = currentNode.parentNode;
            
            while (null!=parentNode){
                
                if (currentNode.amITheDownBranchChild){
                    parentNode.numLeafsinDownBranch ++;
                    if (null==  parentNode.downChild) parentNode.downChild = currentNode;
                }else {
                    parentNode.numLeafsinUpBranch++;
                    if (null==  parentNode.upChild) parentNode.upChild = currentNode;
                }
                
                if (! isCurrentNodeALeaf){
                    
                    int currentNode_leafCount = 
                            currentNode.numLeafsinDownBranch + currentNode.numLeafsinUpBranch;
                    int currentNode_nonLeafCount = ONE + currentNode.num_nonLeafsUp + currentNode.num_nonLeafsDown;
                    if (currentNode_nonLeafCount + ONE == currentNode_leafCount){
                        //currentNode is perfect
                    }
                    
                    if (currentNode.amITheDownBranchChild){
                        parentNode.num_nonLeafsDown =  currentNode_nonLeafCount;
                    }else {
                        parentNode.num_nonLeafsUp = currentNode_nonLeafCount;
                    }
                    
                }
                
                currentNode = parentNode;
                parentNode = parentNode.parentNode;
                isCurrentNodeALeaf = false;
                
            }//end while     
            
            rootNode = currentNode;
                    
        }//end for
        
        while (rootNode.downChild==null || rootNode.upChild==null) {
            rootNode =  (null==  rootNode.upChild) ?  rootNode.downChild: rootNode.upChild;             
        }
        
        if (! rootNode.isPerfect()){
            result = getBI_Tree (rootNode);
        }
        
        return result;
        
    }  
    
    private static BI_Tree getBI_Tree (NodeAttachment node){
        
        BI_Tree result = null ;
             
        if (!node.isPerfect()){
            result = new BI_Tree ();
            
            //find lca node down and up
            CompoundBI downInstrcution = new CompoundBI ();
            downInstrcution.add(node.branchingVarName, (int) Math.round( node.upperBound), true);
            NodeAttachment lcaDown = getLCANode (node.downChild, downInstrcution) ;
            
            CompoundBI upInstrcution = new CompoundBI ();
            upInstrcution.add(node.branchingVarName, (int) Math.round( ONE+node.upperBound), false);
            NodeAttachment lcaUp   = getLCANode (node.upChild, upInstrcution) ;
             
            result.downBranch = downInstrcution;
            result.upBranch = upInstrcution;
            result.downTree = getBI_Tree(lcaDown);
            result.upTree = getBI_Tree( lcaUp);            
            
        }
        
        return result;
    }
    
    private static NodeAttachment getLCANode (NodeAttachment startNode , CompoundBI cbi ){
        NodeAttachment current = startNode ;
                
        while ( current.isParentOfSingleCHild()  ){
            //accumulate instrcution
            if (current.downChild==null){
                cbi.add(current.branchingVarName, (int) Math.round( ONE+current.upperBound), false);
                current= current.upChild;                
            }else {
                cbi.add (current.branchingVarName, (int) Math.round( current.upperBound), true );
                current = current.downChild;                
            }
        }
        return current;
        
    }
   
}
