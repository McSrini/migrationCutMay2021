/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.drivers;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import static ca.mcmaster.migrationcutmay2021.Parameters.*;
import ca.mcmaster.migrationcutmay2021.migrationCut.*;
import ca.mcmaster.migrationcutmay2021.mip.*;
import static ca.mcmaster.migrationcutmay2021.mip.embeddedBI.BI_TREE_Generator.generate_BI_Tree;
import ca.mcmaster.migrationcutmay2021.mip.embeddedBI.BI_Tree;
import static ca.mcmaster.migrationcutmay2021.utils.CplexUtils.getVariables;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class EBI_Driver {
    
    public static void main(String[] args) throws Exception{
        
        SearchTree originalTree = new SearchTree (null, false);
        double bestSolutionFound = originalTree.solve(NUM_CYCLES, null);
        ObjectForExport exportObj = originalTree.getObjectForExport();
        
        //get EBI
        BI_Tree biTree = generate_BI_Tree (exportObj.tree.leafNodes);
      
        
        //finish solving original problem
        originalTree.solve(NUM_CYCLES    , null);
        originalTree.end();
        
        IloCplex newCplex = exportObj.cplex;
        SearchTree newTree = new SearchTree ( newCplex ,  false );
        
        //start solving new tree
        newTree.solve(NUM_CYCLES  +NUM_CYCLES  , bestSolutionFound, biTree,    getVariables (newCplex) );
        
        
        System.out.println ("Test complete !" );
        
    }    
 
    
}
