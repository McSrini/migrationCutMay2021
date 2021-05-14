/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.utils;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import static ca.mcmaster.migrationcutmay2021.Parameters.*;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author tamvadss
 */
public class CplexUtils {
    
    public static void setConfiguration (IloCplex cplex) throws IloException{
                 
        cplex.setParam( IloCplex.Param.MIP.Strategy.File,  ZERO);
        cplex.setParam( IloCplex.Param.MIP.Strategy.HeuristicFreq , -ONE);
        cplex.setParam( IloCplex.Param.MIP.Strategy.VariableSelect , BRANCHING_STRATGEY );
        cplex.setParam( IloCplex.Param.WorkMem, THOUSAND * HUGE_WORKMEM) ;
        cplex.setParam(IloCplex.Param.Emphasis.MIP, MIP_EMPHASIS_TO_USE) ;         
                
    }
            
    public static Map<String, IloNumVar> getVariables (IloCplex cplex) throws IloException{
        Map<String, IloNumVar> result = new HashMap<String, IloNumVar>();
        IloLPMatrix lpMatrix = (IloLPMatrix)cplex.LPMatrixIterator().next();
        IloNumVar[] variables  =lpMatrix.getNumVars();
        for (IloNumVar var :variables){
            result.put(var.getName(),var ) ;
        }
        return result;
    }
    
    /**
     * 
     *  Update variable bounds as specified    
    */
    public static   void updateVariableBounds(IloNumVar var, double newBound, boolean isUpperBound   )      throws IloException{
 
        if (isUpperBound){
            if ( var.getUB() > newBound ){
                //update the more restrictive upper bound
                var.setUB( newBound );
                //System.out.println(" var " + var.getName() + " set upper bound " + newBound ) ;
            }
        }else{
            if ( var.getLB() < newBound){
                //update the more restrictive lower bound
                var.setLB(newBound);
                //System.out.println(" var " + var.getName() + " set lower bound " + newBound ) ;
            }
        }  

    } 
}
