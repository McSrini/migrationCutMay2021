/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip.embeddedBI;

/**
 *
 * @author tamvadss
 */
public class BI_Tree {
    public BI_Tree downTree=null;
    public BI_Tree upTree = null;
    public CompoundBI downBranch = null;
    public CompoundBI upBranch = null;
    
    public void printMe(){
        System.out.println("BI Tree down branch") ;
        downBranch.printMe();
        System.out.println("BI Tree up branch") ;
        upBranch.printMe();
        if (downTree!=null){
            System.out.println("BI Tree down tree") ;
            downTree.printMe();
        }
        if (upTree!=null){
            System.out.println("BI Tree up tree") ;
            upTree.printMe();
        }
    }
    
}
