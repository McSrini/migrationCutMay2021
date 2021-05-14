/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip;

import ca.mcmaster.migrationcutmay2021.tree.Tree;
import ilog.cplex.IloCplex;

/**
 *
 * @author tamvadss
 */
public class ObjectForExport {
    
    //cplex to export
    public IloCplex cplex;
    
    //get fathomed branches from this tree, and add migration cuts to the cplex object above before exporting
    public Tree tree;
    
}