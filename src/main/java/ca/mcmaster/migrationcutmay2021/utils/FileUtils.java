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
import static ilog.concert.IloNumVarType.Float;
import ilog.cplex.IloCplex;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.exit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class FileUtils {
        
    public static void applyVarPriority (IloCplex cplex) throws Exception{
           
        //all the original variables get a priority value of 1
        
        File file = new File( PRIORITY_LIST_ORIGINAL_VARS ); 
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(PRIORITY_LIST_ORIGINAL_VARS );
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<String>  recreatedVarPriorityList = (List<String>) ois.readObject();
            ois.close();
            fis.close();

            Map<String, IloNumVar> varMap = CplexUtils. getVariables (  cplex);
            for (String varname :  recreatedVarPriorityList){
                IloNumVar priorityVar = varMap.get (varname);
                
                if (null!=priorityVar) {
                    
                    cplex.setPriority( priorityVar, PRIORITY_FOR_ORIGINAL_VARIABLES  );
                    System.out.println("Var "+ varname + " "+ PRIORITY_FOR_ORIGINAL_VARIABLES + " "+ priorityVar.getType()) ;
                }else {
                    System.out.println("Var "+ varname + " missing from exported file") ;
                }
            }
                      
        }else {
            System.err.println("cannot find var priority list ! ") ;
            exit (1);
        }
        
                 
        file = new File(PRIORITY_LIST_FILENAME ); 
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(PRIORITY_LIST_FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            List<String>  recreatedVarPriorityList = (List<String>) ois.readObject();
            ois.close();
            fis.close();

            int priorityValue = BILLION;
            Map<String, IloNumVar> varMap = CplexUtils. getVariables (  cplex);
            for (String varname :  recreatedVarPriorityList){
                IloNumVar priorityVar = varMap.get (varname);
                if (null!=priorityVar) {
                    cplex.setPriority( priorityVar, -- priorityValue  );
                    System.out.println("Var "+ varname + " "+ priorityValue) ;
                }else {
                    System.out.println("Var "+ varname + " missing from exported file") ;
                }
            }
            
        }else {
            System.err.println("cannot find var priority list ! ") ;
            exit (1);
        }
               
    }
    
      public static void savePriorityListToDisk (List<String> varPriorityList, String filename ) throws Exception {
        
        //for (String str : varPriorityList){
            //System.out.println(str) ;
        //}
        
        FileOutputStream fos =                     new FileOutputStream(filename );
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(varPriorityList);
        oos.close();
        fos.close();
        
        FileInputStream fis = new FileInputStream(filename );
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<String>  recreatedVarPriorityList = (List<String>) ois.readObject();
        ois.close();
        fis.close();
        
        //System.out.println() ;
        //System.out.println("Printing recreated Map") ;
        //for (String str : recreatedVarPriorityList){
            //System.out.println(str) ;
        //}
    }
 
     
}
