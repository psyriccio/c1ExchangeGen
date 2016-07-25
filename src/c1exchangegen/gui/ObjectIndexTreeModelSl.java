/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1exchangegen.ObjectIndex;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author psyriccio
 */
public class ObjectIndexTreeModelSl implements TreeModel {

    private final ObjectIndex index;
    private final List<TreeModelListener> listeners;
    
    public ObjectIndexTreeModelSl(ObjectIndex index) {
        this.index = index;
        this.listeners = new ArrayList<>();
    }
    
    @Override
    public Object getRoot() {
        return index.getRootNode();
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((TreeNode) parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((TreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((TreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        // none;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((TreeNode) parent).getIndex((TreeNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        if(!listeners.contains(l)) listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }

    
}
