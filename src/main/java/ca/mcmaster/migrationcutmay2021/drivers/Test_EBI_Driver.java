/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.drivers;

import static ca.mcmaster.migrationcutmay2021.Constants.BILLION;
import static ca.mcmaster.migrationcutmay2021.Parameters.NUM_CYCLES;
import ca.mcmaster.migrationcutmay2021.mip.NodeAttachment;
import ca.mcmaster.migrationcutmay2021.mip.SearchTree;
import ca.mcmaster.migrationcutmay2021.mip.embeddedBI.BI_TREE_Generator;
import ca.mcmaster.migrationcutmay2021.mip.embeddedBI.BI_Tree;
import static ca.mcmaster.migrationcutmay2021.utils.CplexUtils.getVariables;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class Test_EBI_Driver {
    public static void main(String[] args) throws Exception{
        List<NodeAttachment> leafNodes = new ArrayList<NodeAttachment> ();
        
        NodeAttachment leaf1 = new NodeAttachment ();
        NodeAttachment leaf2 = new NodeAttachment ();
        NodeAttachment leaf3 = new NodeAttachment ();
        NodeAttachment root = new NodeAttachment ();
                
        NodeAttachment n1 = new NodeAttachment ();
        NodeAttachment n2 = new NodeAttachment ();
        NodeAttachment n3 = new NodeAttachment ();
        NodeAttachment n4 = new NodeAttachment ();
        NodeAttachment n5 = new NodeAttachment (); 
        
        
        root.branchingVarName ="x0";
        root.upperBound= 0.0;
        
        n1.parentNode = root;
        n1.amITheDownBranchChild = true;
        n1.branchingVarName ="x396";
        n1.upperBound= 0.0;
        
        n2.parentNode = n1;
        n2.amITheDownBranchChild = true;
        n2.branchingVarName ="x404";
        n2.upperBound= 0.0;
        
        n3.parentNode = n1;
        n3.amITheDownBranchChild = false;
        n3.branchingVarName ="x251";
        n3.upperBound= 0.0;
        
        n4.parentNode = n2;
        n4.amITheDownBranchChild = false;
        n4.branchingVarName ="x4";
        n4.upperBound= 0.0;
        
        n5.parentNode = n4;
        n5.amITheDownBranchChild = true;
        n5.branchingVarName ="x251";
        n5.upperBound= 0.0;
        
        leaf3.parentNode = n4;
        leaf3.amITheDownBranchChild = false;
         
        
        leaf1.parentNode = n5;
        leaf1.amITheDownBranchChild = true;
         
        
        leaf2.parentNode = n3;
        leaf2.amITheDownBranchChild = false;
        
        
        leafNodes.add (leaf2);
        leafNodes.add (leaf1);
        leafNodes.add (leaf3);
         
        
        BI_Tree biTree =  BI_TREE_Generator.generate_BI_Tree (leafNodes);
        
        biTree.printMe();
        
        //use bnatt 500, emph 0
        SearchTree newTree = new SearchTree ( null ,  false );
        
        //start solving new tree
        newTree.solve(NUM_CYCLES  +NUM_CYCLES , (double)BILLION, biTree,    getVariables (newTree.cplex) );
        
    }
    
}
