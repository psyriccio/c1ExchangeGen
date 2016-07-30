/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.mapping;

import c1c.meta.generated.MetaObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.tree.TreeNode;

/**
 *
 * @author psyriccio
 */
@SuppressWarnings("FieldMayBeFinal")
public class MappingInfoNode implements TreeNode {

    private TreeNode parent;
    private List<MappingInfoNode> childs;
    private String header;
    private Object data;
    private String dataDescription;

    public MappingInfoNode(TreeNode parent, String header, Object data) {
        this.parent = parent;
        this.childs = null;
        this.header = header;
        this.data = data;
        this.dataDescription = "";

        if (data instanceof HashMap) {
            this.childs = new ArrayList<>();
            HashMap<Object, Object> dt = (HashMap<Object, Object>) data;
            dt.keySet().forEach((key) -> {
                Object obj = dt.get(key);
                childs.add(new MappingInfoNode(this, key.toString(), obj));
            });
            dataDescription = "HashMap";
            return;
        }
        
        if(data instanceof List) {
            this.childs = new ArrayList<>();
            List<Object> dt = (List<Object>) data;
            int k = 0;
            for (Object obj : dt) {
                childs.add(new MappingInfoNode(this, Integer.toString(k), obj));
                k++;
            }
            dataDescription = "List";
            return;
            
        }

        if (data.getClass().isArray()) {
            this.childs = new ArrayList<>();
            Object[] dt = (Object[]) data;
            int k = 0;
            for (Object obj : dt) {
                childs.add(new MappingInfoNode(this, Integer.toString(k), obj));
                k++;
            }
            dataDescription = "Array";
            return;
        }

        for (Class intrf : data.getClass().getInterfaces()) {
            if (intrf.equals(Iterable.class)) {
                this.childs = new ArrayList<>();
                Iterable<Object> iterObj = (Iterable<Object>) data;
                int k = 0;
                for (Object obj : iterObj) {
                    childs.add(new MappingInfoNode(this, Integer.toString(k), obj));
                    k++;
                }
                dataDescription = "Iterable";
                return;
            }
        }

        String fullName = "";
        try {
            fullName = ((MetaObject) data).getFullName();
        } catch (Exception ex) {
            // do nothing
        }
        
        dataDescription = data.toString() + (fullName.isEmpty() ? "" : " :// " + fullName);

    }

    @Override
    public String toString() {
        return header + ": " + dataDescription;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return childs == null ? null : childs.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childs == null ? 0 : childs.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return childs == null ? -1 : childs.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return childs == null ? true : childs.isEmpty();
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(childs);
    }

}
