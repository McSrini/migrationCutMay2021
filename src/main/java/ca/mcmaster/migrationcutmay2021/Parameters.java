/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021;

/**
 *
 * @author tamvadss
 */
public class Parameters {
        
    public static final String INPUT_MIP_NAME =  "neosrahue" + ".pre.sav";
    public static final int MIP_EMPHASIS_TO_USE =  2 ;
     
     
    //the follwoing parameters are largely constant
    
    public static final String OUTPUT_MIP_NAME = "withMigrationCuts_" + INPUT_MIP_NAME;       
    public static final String MIP_FOLDER =
             System.getProperty("os.name").toLowerCase().contains("win") ?   "F:\\temporary files here recovered\\":"";
    
    public static final String PRIORITY_LIST_FILENAME = MIP_FOLDER + "priorityList.ser";
    public static final String PRIORITY_LIST_ORIGINAL_VARS = MIP_FOLDER + "originalVars.ser";
        
    public static final int  HUGE_WORKMEM = 192 ;// gig   
    public static final int MAX_THREADS=   System.getProperty("os.name").toLowerCase().contains("win") ?   2: 32;
    public static final int SOLUTION_CYCLE_TIME_MINUTES= 60 ;
    //public static final int          MAX_SOLUTION_CYCLES = 5;
    public static final int NUM_CYCLES= 2 ;
    public static final  int BRANCHING_STRATGEY = 3 ;
    
    public static int PRIORITY_FOR_ORIGINAL_VARIABLES = -1;
    
}
