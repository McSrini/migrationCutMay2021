/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021;

import org.apache.log4j.Level;

/**
 *
 * @author tamvadss
 */
public class Constants {
        
    public static final int ZERO = 0;
    public static final int ONE = 1; 
    public static final int   TWO= 2;
    public static final int        THREE =3;
    public static final int        FOUR =4;
    public static final int        FIVE =5;
    public static final int        SIXTY =60;
    public static final int THOUSAND =  1000;
    public static final int MILLION = 1000*1000;
    public static final int BILLION = 1000*1000*1000;
    
    public static   int COUNTER_FOR_CUT_VARIABLES = 0;
    public static  final String CUT_BINARY_VARIABLE_PREFIX= "McCUT____VAR__";
    public static  final String CUT_INTEGER_VARIABLE_PREFIX= "McCUT____INT__";
    
    
    public static   final String LOG_FOLDER="./logs"+"/"; 
    public static   final String LOG_FILE_EXTENSION = ".log";
    public static   final Level LOGGING_LEVEL= Level.INFO ;    
    
    public static final String MIPROOT_NODE_ID = "Node0";
    
}
