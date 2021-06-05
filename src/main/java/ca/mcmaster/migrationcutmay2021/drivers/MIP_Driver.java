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
public class MIP_Driver {
    
    public static void main(String[] args) throws Exception{
        
        SearchTree originalTree = new SearchTree (null, false);
        double bestSolutionFound = originalTree.solve(NUM_CYCLES, null);
        ObjectForExport exportObj = originalTree.getObjectForExport();
        
        CutGenerator cg = new  CutGenerator (  exportObj.cplex);
        cg.prohibitAll( exportObj.tree.getFathomedBranches());
        exportObj.cplex.exportModel( MIP_FOLDER + OUTPUT_MIP_NAME);
        
        //for testing, you can inspect the LP file
        exportObj.cplex.exportModel( MIP_FOLDER + OUTPUT_MIP_NAME+".lp");
        
        cg .createPriorityList();
        
        //finish solving original problem
        originalTree.solve(NUM_CYCLES   , null);
        originalTree.end();
        
        IloCplex newCplex = new IloCplex();
        newCplex.importModel( MIP_FOLDER + OUTPUT_MIP_NAME);
        boolean areIntegersPresent = areIntegerVariablesPresent(newCplex) ;
        PRIORITY_FOR_ORIGINAL_VARIABLES = areIntegersPresent? ONE : ZERO;
        SearchTree newTree = new SearchTree ( newCplex ,  true /* areIntegersPresent */ );
        //start solving new tree
        newTree.solve(NUM_CYCLES   , bestSolutionFound);
        //continue solving new tree
        newTree.solve(NUM_CYCLES   , null);
        
        System.out.println ("Test complete !" );
        
    }    
        
    private static boolean areIntegerVariablesPresent (IloCplex cplex) throws IloException{
        boolean areIntegerVariablesPresent = false;
        
        Map<String, IloNumVar> variablesInThisModel = getVariables (  cplex) ;
        
        for (IloNumVar var: variablesInThisModel .values()){
            if (var.getType().equals( IloNumVarType.Int)){
                areIntegerVariablesPresent = true;
                System.out.println("MIP has integer variables ") ;
                break;
            }
        }
        return areIntegerVariablesPresent ;
    }
    
}
