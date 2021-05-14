/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip;

/**
 *
 * @author tamvadss
 */
public class NodeAttachment {
    public NodeAttachment parentNode = null;
    
    //condition used to create down branch child , up branch condition can be inferred
    public String branchingVarName = null;
    public Double upperBound = null;  
    
    public boolean amITheDownBranchChild = false;
    
   
    
}

