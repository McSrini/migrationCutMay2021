/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.migrationCut;

import static ca.mcmaster.migrationcutmay2021.Constants.*;

/**
 *
 * @author tamvadss
 */
public class VarBoundDirection {
    
    public String varName ;
    public int bound= - BILLION;
    public Boolean isUpperBound = null;
    
    
    
    public VarBoundDirection ( String varName ,     int bound, boolean isUpperBound){
        this. varName= varName;
        this.bound=bound;
        this.isUpperBound=isUpperBound;
    }
    
    public void printMe (){
        System.out.println("VarBoundDirection: ("+ varName + ", "+bound + ", "+  isUpperBound + ")") ;
    }
    
    
}
