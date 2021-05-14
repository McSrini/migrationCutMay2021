/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.drivers;

import static ca.mcmaster.migrationcutmay2021.Parameters.*;
import ca.mcmaster.migrationcutmay2021.migrationCut.CutGenerator;
import ca.mcmaster.migrationcutmay2021.migrationCut.VarBoundDirection;
import ca.mcmaster.migrationcutmay2021.mip.*;
import ca.mcmaster.migrationcutmay2021.tree.Tree;
import ca.mcmaster.migrationcutmay2021.tree.TreeNode;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class SearchTestDriver {
    
    public static void main(String[] args) throws Exception{
        SearchTree originalTree = new SearchTree (null, false);
        double bestSolutionFound = originalTree.solve(NUM_CYCLES, null);
        ObjectForExport exportObj = originalTree.getObjectForExport();
        //exportObj.tree.printMe();
        
        //use with MIP lrsa120
        exportObj.tree = createTreeFor_lrsa120();
        CutGenerator cg = new CutGenerator (exportObj.cplex);
        cg.prohibitAll( exportObj.tree .getFathomedBranches());
        cg.createPriorityList();
        
        exportObj.cplex.exportModel( MIP_FOLDER +OUTPUT_MIP_NAME + ".lp");
        exportObj.cplex.exportModel( MIP_FOLDER + OUTPUT_MIP_NAME);
          
        IloCplex newCplex = new IloCplex();
        newCplex.importModel(MIP_FOLDER+ OUTPUT_MIP_NAME);
                
        SearchTree st= new SearchTree ( newCplex , true );
        
       
        
    }
    
    private static Tree createTreeFor_lrsa120 () throws Exception {
        
        TreeNode rootNode = new TreeNode ();
        
        /* 0 <= C03603 <= 2       
        0 <= C03605 <= 3
        0 <= C03726 <= 1 */        
         
        TreeNode a1 = new TreeNode ();
        rootNode.downChild = a1;
        rootNode.downBranch = new VarBoundDirection ("C03605", 1, true);
        
        TreeNode a2 = new TreeNode ();
        a1.downChild= a2;
        a1.downBranch = new VarBoundDirection ("C03603", 0, true);
        
        TreeNode a3 = new TreeNode ();
        a2.downBranch =  new VarBoundDirection ("C03726", 0, true);
        a2.downChild= a3;
        
        TreeNode b1 = new TreeNode ();
        rootNode.upChild = b1;
        rootNode.upBranch = new VarBoundDirection ("C03605", 2, false);
        
        
       /* TreeNode optionalNode = new TreeNode ();
        b1.upChild= optionalNode;
        b1.upBranch = new VarBoundDirection ("y2", 41, false);*/
        
        TreeNode b2 = new TreeNode ();
        b1.downChild=b2;
        b1.downBranch = new VarBoundDirection ("C03726", 0, true);
        
        TreeNode b3 = new TreeNode ();
        b2.upChild= b3;
        b2.upBranch = new VarBoundDirection ("C03603", 1, false);
        
        List<VarBoundDirection> offsetFrom_MIPRoot = new ArrayList<VarBoundDirection>();
        Tree testTree = new Tree (rootNode, offsetFrom_MIPRoot) ;
        
        return testTree;
        
    }
    
}
