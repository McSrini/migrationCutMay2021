/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.drivers;

import ca.mcmaster.migrationcutmay2021.migrationCut.*;
import ca.mcmaster.migrationcutmay2021.tree.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class TreeTestDriver {
    
    public static void main(String[] args) throws Exception{
        
        //prepare Tree
        TreeNode rootNode = new TreeNode ();
        List<VarBoundDirection> offsetFrom_MIPRoot = new ArrayList<VarBoundDirection>();
        VarBoundDirection vb1 = new VarBoundDirection ("origVar1", 1, true) ;
        offsetFrom_MIPRoot.add (vb1 );
        VarBoundDirection vb2 = new VarBoundDirection ("origVar2", 0, false) ;
        offsetFrom_MIPRoot.add (vb2 );
       
        
        TreeNode a1 = new TreeNode ();
        rootNode.downChild = a1;
        rootNode.downBranch = new VarBoundDirection ("x1", 0, true);
        
        TreeNode a2 = new TreeNode ();
        a1.downChild= a2;
        a1.downBranch = new VarBoundDirection ("y2", 40, true);
        
        TreeNode a3 = new TreeNode ();
        a2.downBranch =  new VarBoundDirection ("y3", 23, true);
        a2.downChild= a3;
        
        TreeNode b1 = new TreeNode ();
        rootNode.upChild = b1;
        rootNode.upBranch = new VarBoundDirection ("x1", 1, false);
        
        
       /* TreeNode optionalNode = new TreeNode ();
        b1.upChild= optionalNode;
        b1.upBranch = new VarBoundDirection ("y2", 41, false);*/
        
        TreeNode b2 = new TreeNode ();
        b1.downChild=b2;
        b1.downBranch = new VarBoundDirection ("y2", 40, true);
        
        TreeNode b3 = new TreeNode ();
        b2.upChild= b3;
        b2.upBranch = new VarBoundDirection ("y3", 93, false);
        
        Tree testTree = new Tree (rootNode, offsetFrom_MIPRoot) ;
        
        for ( List<VarBoundDirection> fb : testTree.getFathomedBranches()){
            System.out.println("Fathomed branch:");
            for (VarBoundDirection vbd : fb){
                vbd.printMe();
            }
            System.out.println();
        }
         
    }
    
}
