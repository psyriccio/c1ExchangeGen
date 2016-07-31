/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1c.meta.generated.Conf;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author psyriccio
 */
public class C1ConfigurationTreeModel implements TreeModel {

    private final Conf conf;
    private C1ConfigurationTreeNode thisNode;
    
    public C1ConfigurationTreeModel(Conf conf) {
        this.conf = conf;
        this.thisNode = new C1ConfigurationTreeNode(null, conf);
    }
    
    @Override
    public Object getRoot() {
        return thisNode;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((C1ConfigurationTreeNode) parent).getChildAt(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((C1ConfigurationTreeNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return ((C1ConfigurationTreeNode) node).isLeaf();
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
        //none
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((C1ConfigurationTreeNode) parent).getIndex((TreeNode) child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        //
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        //
    }
    
}
