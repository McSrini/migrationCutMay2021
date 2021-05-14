/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.migrationcutmay2021.tree;

import ca.mcmaster.migrationcutmay2021.migrationCut.VarBoundDirection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class LCANode extends TreeNode{
    public List<VarBoundDirection> offsetFromTreeRoot = new ArrayList<VarBoundDirection>();  //not MIP root, but root of LCA tree
    
}
