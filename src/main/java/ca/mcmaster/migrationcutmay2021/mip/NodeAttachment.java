/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.mip;

import static ca.mcmaster.migrationcutmay2021.Constants.*;
import ca.mcmaster.migrationcutmay2021.mip.embeddedBI.BI_Tree;

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
    
    public BI_Tree biTree = null;
    
     
    
    public int numLeafsinDownBranch = ZERO;
    public int numLeafsinUpBranch = ZERO;
    public int num_nonLeafsDown = ZERO;
    public int num_nonLeafsUp = ZERO;
    public NodeAttachment downChild = null;
    public NodeAttachment upChild = null;
    
    public boolean isPerfect (){
        boolean cond1 =  (ONE + num_nonLeafsDown + num_nonLeafsUp +ONE == numLeafsinDownBranch+numLeafsinUpBranch);
        boolean cond2 = ZERO== numLeafsinDownBranch+numLeafsinUpBranch;
        return cond1 || cond2;
    }
    
    public boolean isParentOfSingleCHild (){
        boolean isParentOfSingleChild = this.downChild==null && this.upChild!=null ;
        isParentOfSingleChild = isParentOfSingleChild || (this.downChild!=null && this.upChild==null ) ;
        return isParentOfSingleChild;
    }
}

