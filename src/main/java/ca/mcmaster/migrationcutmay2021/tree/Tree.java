/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.tree;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import ca.mcmaster.migrationcutmay2021.migrationCut.VarBoundDirection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class Tree {
    
    public LCANode rootNode = new  LCANode();
    public List<VarBoundDirection> offsetFrom_MIPRoot;
    
    public Tree (TreeNode rootNode, List<VarBoundDirection> offsetFrom_MIPRoot) throws Exception {
        
        //assert        
        if (!isLCAOrLeaf (rootNode)) {
            throw new Exception ("Root NOT an LCA node!") ;
        }
        
        this.rootNode.downBranch=rootNode.downBranch;
        this.rootNode.upBranch=rootNode.upBranch;
        this.rootNode.downChild=rootNode.downChild;
        this.rootNode.upChild=rootNode.upChild;
        
        this.offsetFrom_MIPRoot= offsetFrom_MIPRoot;
    }
    
    public void printMe () {
        System.out.println("Size of offsetFrom_MIPRoot is = ");
        System.out.println(offsetFrom_MIPRoot==null ? ZERO: offsetFrom_MIPRoot.size()) ;
        rootNode.printMe();
    }
        
  
    
    public static  boolean isLCAOrLeaf (TreeNode node) {
        int numKids = 0 ;
        if (node.upBranch!=null) numKids++;
        if (node.downBranch!=null) numKids++;
        return (ONE != numKids) ;
    }
    
    public List<List<VarBoundDirection>> getFathomedBranches(   ) throws Exception{
        return getFathomedBranches(rootNode) ;
    }
       
    private  List<List<VarBoundDirection>> getFathomedBranches(LCANode lcaNode) throws Exception{
        
        if (! isLCAOrLeaf (lcaNode) ) {
            throw new Exception ("Not an LCA node");
        }
        
        List<List<VarBoundDirection>> result = new ArrayList<List<VarBoundDirection>> () ;
        
        if (lcaNode.downChild==null && lcaNode.upChild== null){
            
        }else {
            List<List<VarBoundDirection>> fathomedBrances_until_downLCA = new ArrayList<List<VarBoundDirection>> () ;
            LCANode lcaNode_down = getFathomedBranches(lcaNode, true, fathomedBrances_until_downLCA) ;
            
            List<List<VarBoundDirection>> fathomedBrances_until_upLCA = new ArrayList<List<VarBoundDirection>> () ;
            LCANode lcaNode_up = getFathomedBranches(lcaNode, false, fathomedBrances_until_upLCA) ;
            
            result.addAll (fathomedBrances_until_downLCA) ;
            result.addAll (fathomedBrances_until_upLCA) ;
            result.addAll (getFathomedBranches(lcaNode_down) );
            result.addAll (getFathomedBranches(lcaNode_up)) ;
            
        }
        
        return result;         
    }
     
    //all the fathomed branches until next lca node, and the next lca node
    //note that both have  absolute paths from tree root 
    private LCANode  getFathomedBranches (LCANode startNode , boolean isDown,
            List<List<VarBoundDirection>> accumulated_fathomedBranches ) {
        
        List<VarBoundDirection> distanceFromStartNode= new ArrayList<VarBoundDirection> ();
                        
        TreeNode childNode = null;
        if (isDown){
            childNode = startNode.downChild;
            distanceFromStartNode.add ( startNode.downBranch);
        }else {
            childNode = startNode.upChild;
            distanceFromStartNode.add (startNode.upBranch);
        }
        
        while (!isLCAOrLeaf(childNode)){
            
            //accumulate fathomed branch
            List<VarBoundDirection> fb = new ArrayList<VarBoundDirection> ();
            fb.addAll(startNode.offsetFromTreeRoot) ;
            fb.addAll( distanceFromStartNode);
            
            VarBoundDirection missingBranch = null; 
            if (childNode.downChild!=null){
                missingBranch = new VarBoundDirection( childNode.downBranch.varName, childNode.downBranch.bound + ONE, false);
                 
            } else {
                missingBranch = new VarBoundDirection(childNode.upBranch.varName, childNode.upBranch.bound - ONE , true);
                                 
            }
            
            fb.add (missingBranch) ;           
            accumulated_fathomedBranches.add (fb );
            
            //move to child
            if (childNode.downChild==null){
                distanceFromStartNode.add (childNode .upBranch);
                childNode =childNode.upChild;                
            }else {
                distanceFromStartNode.add ( childNode .downBranch);
                childNode = childNode.downChild;                
            }             
            
        }
        
        
        LCANode result = new LCANode ();
        result.upChild= childNode.upChild;
        result.downChild= childNode.downChild;
        result.upBranch= childNode.upBranch;
        result.downBranch= childNode.downBranch;
        result.offsetFromTreeRoot.addAll(startNode.offsetFromTreeRoot );
        result.offsetFromTreeRoot.addAll(distanceFromStartNode);
        return result;
        
    }
    
    
}
