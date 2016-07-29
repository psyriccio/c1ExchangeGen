/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1c.meta.generated.Conf;
import c1c.meta.generated.MetaObject;
import c1c.meta.generated.impl.MetaRef;
import c1c.meta.generated.impl.MetaVertualDirectory;
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
public class C1ConfigurationTreeNode implements TreeNode {

    private final C1ConfigurationTreeNode parent;
    private final List<C1ConfigurationTreeNode> childs;
    private final MetaObject obj;

    public C1ConfigurationTreeNode(C1ConfigurationTreeNode parent, MetaObject obj) {
        this.parent = parent;
        this.childs = new ArrayList<>();
        this.obj = obj;
        if(obj instanceof Conf) {
        
            List<MetaObject> sublistCatalogs = obj.asConf().getCatalogs().stream().map((cat) -> (MetaObject) cat).collect(Collectors.toList());
            List<MetaObject> sublistDocuments = obj.asConf().getDocuments().stream().map((doc) -> (MetaObject) doc).collect(Collectors.toList());
            MetaVertualDirectory catalogs = new MetaVertualDirectory("Справочники", "Справочники", obj, sublistCatalogs);
            MetaVertualDirectory documents = new MetaVertualDirectory("Документы", "Документы", obj, sublistDocuments);
            
            C1ConfigurationTreeNode dirCat = new C1ConfigurationTreeNode(this, catalogs);
            C1ConfigurationTreeNode dirDoc = new C1ConfigurationTreeNode(this, documents);
            
            childs.add(dirCat);
            childs.add(dirDoc);
            
        } else {
            obj.getChildrens().stream().forEach((cobj) -> {
                childs.add(new C1ConfigurationTreeNode(this, cobj));
            });
        }
    }

    public C1ConfigurationTreeNode(C1ConfigurationTreeNode parent) {
        this.parent = parent;
        this.childs = new ArrayList<>();
        this.obj = new MetaRef(parent.getObj());
    }
    
    @Override
    public TreeNode getChildAt(int childIndex) {
        return childs.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childs.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return childs.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return childs.isEmpty();
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(childs);
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    protected MetaObject getObj() {
        return obj;
    }
    
}
