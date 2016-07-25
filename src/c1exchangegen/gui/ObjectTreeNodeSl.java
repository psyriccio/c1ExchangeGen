/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1exchangegen.ObjectIndex;
import static c1exchangegen.ObjectIndex.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;
import javax.swing.tree.TreeNode;

/**
 *
 * @author psyriccio
 */
public class ObjectTreeNodeSl implements TreeNode {

    private final ObjectIndex index;
    private final Object object;
    
    public ObjectTreeNodeSl(ObjectIndex index, Object object) {
        this.index = index;
        this.object = object;
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        return index.getIndexTreeNodes().get(
            getRef(index.getSlaves(object).get(childIndex))
        );
    }

    @Override
    public int getChildCount() {
        return index.getSlaves(object).size();
    }

    @Override
    public TreeNode getParent() {
        return index.getIndexTreeNodes().get(getRef(ObjectIndex.getParent(object))
        );
    }

    @Override
    public int getIndex(TreeNode node) {
        return index.getSlaves(object).stream()
                .map((obj) -> index.getIndexTreeNodes()
                                .get(getRef(obj))
                ).collect(Collectors.toList()).indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return this == index.getRootNode() ? true : !index.getSlaves(object).isEmpty();
    }

    @Override
    public boolean isLeaf() {
        return this == index.getRootNode() ? false : index.getSlaves(object).isEmpty();
    }

    @Override
    public Enumeration children() {
        return 
                Collections.enumeration(
                    index.getSlaves(object).stream()
                            .map((obj) -> index.getIndexTreeNodes().get(getRef(obj)))
                            .collect(Collectors.toList())
                );
    }

    @Override
    public String toString() {
        return getDescription(object) + " (" + getClassSuffix(object) + ")";
    }
    
    
}
