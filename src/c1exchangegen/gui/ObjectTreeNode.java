/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1exchangegen.ObjectIndex;
import static c1exchangegen.ObjectIndex.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.tree.TreeNode;

/**
 *
 * @author psyriccio
 */
public class ObjectTreeNode implements TreeNode {

    private final ObjectIndex index;
    private final Object object;
    private List<TreeNode> childsCache = null;
    private TreeNode parent;
    
    private void initCache() {
        childsCache = index.getChilds(object).stream()
                        .map((obj) -> index.getIndexTreeNodes().get(getRef(obj)))
                        .collect(Collectors.toList());
        parent = index.getIndexTreeNodes().get(getRef(ObjectIndex.getOwner(object))
        );
    }
    
    public ObjectTreeNode(ObjectIndex index, Object object) {
        this.index = index;
        this.object = object;
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        if(childsCache == null) initCache();
        return childsCache.get(childIndex);
    }

    @Override
    public int getChildCount() {
        if(childsCache == null) initCache();
        return childsCache.size();
    }

    @Override
    public TreeNode getParent() {
        if(childsCache == null) initCache();
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        if(childsCache == null) initCache();
        return childsCache.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        if(childsCache == null) initCache();
        return this == index.getRootNode() ? true : !childsCache.isEmpty();
    }

    @Override
    public boolean isLeaf() {
        if(childsCache == null) initCache();
        return this == index.getRootNode() ? false : childsCache.isEmpty();
    }

    @Override
    public Enumeration children() {
        if(childsCache == null) initCache();
        return Collections.enumeration(childsCache);
    }

    @Override
    public String toString() {
        return getDescription(object) + " (" + getClassSuffix(object) + ")";
    }
    
    
}
