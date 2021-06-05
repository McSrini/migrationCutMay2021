/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.tree;

import static ca.mcmaster.migrationcutmay2021.Constants.*; 
import ca.mcmaster.migrationcutmay2021.migrationCut.VarBoundDirection;

/**
 *
 * @author tamvadss
 */
public class TreeNode {
    
    /*public static int id = ZERO;
    public int myId ;
    public String myName ;
    public TreeNode (){
        myId = id ++;
    }
    public TreeNode (String name){
        myId = id ++;
        myName= name;
    }*/
         
    public TreeNode downChild =null;
    public TreeNode upChild =null;
    public VarBoundDirection downBranch = null;
    public VarBoundDirection upBranch = null;
        
    public void printMe (){
        System.out.println("Printing tree node "/*+ myId + " "+ myName*/) ;
        if (downBranch!=null)  downBranch.printMe();
        if (upChild!=null) upBranch .printMe();
        if (downBranch!=null) downChild.printMe();
        if (upChild!=null) upChild.printMe();;
    }
}
