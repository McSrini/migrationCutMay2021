/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip.embeddedBI;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class CompoundBI {
    public List <String> vars = new ArrayList<String> () ;
    public List <Integer> bounds = new ArrayList<Integer> () ;
    public List <Boolean> isDownBranch = new ArrayList<Boolean> () ;
    
    public void add (String var, int bound, boolean isDown) {
        vars.add(var);
        bounds.add (bound);
        isDownBranch.add (isDown) ;
    }
    
    public int size (){
        return vars.size();
    }
    
    //toString()
    public void printMe () {
        int size = vars.size();
        for (int index = ZERO; index < size ; index ++){
            System.out.println("("+vars.get(index) + ", "+ bounds.get(index)+ ", "+ isDownBranch.get(index)+")") ;
        }
    }
}
