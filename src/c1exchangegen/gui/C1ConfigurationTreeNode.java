/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1c.meta.generated.Catalog;
import c1c.meta.generated.Conf;
import c1c.meta.generated.Document;
import c1c.meta.generated.MetaObject;
import c1c.meta.generated.impl.MetaRef;
import c1c.meta.generated.impl.MetaVertualDirectory;
import c1exchangegen.mapping.NodeStateContainer;
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
public class C1ConfigurationTreeNode implements TreeNode, NodeStateContainer {

    private final C1ConfigurationTreeNode parent;
    private final List<C1ConfigurationTreeNode> childs;
    private final MetaObject obj;
    private NodeState state;

    public C1ConfigurationTreeNode(C1ConfigurationTreeNode parent, MetaObject obj) {
        
        this.state = NodeState.Normal;
        //this.state = (NodeState.values()[Math.round((float) (Math.random()*(NodeState.values().length-1)))]);
        
        this.parent = parent;
        this.childs = new ArrayList<>();
        this.obj = obj;
                
        if(obj instanceof Conf) {
        
            List<MetaObject> sublistEnums = obj.asConf().getEna().stream().map((ena) -> (MetaObject) ena).collect(Collectors.toList());
            List<MetaObject> sublistCatalogs = obj.asConf().getCatalogs().stream().map((cat) -> (MetaObject) cat).collect(Collectors.toList());
            List<MetaObject> sublistDocuments = obj.asConf().getDocuments().stream().map((doc) -> (MetaObject) doc).collect(Collectors.toList());
            MetaVertualDirectory ena = new MetaVertualDirectory("Перечисления", "Перечисления", obj, sublistEnums);
            MetaVertualDirectory catalogs = new MetaVertualDirectory("Справочники", "Справочники", obj, sublistCatalogs);
            MetaVertualDirectory documents = new MetaVertualDirectory("Документы", "Документы", obj, sublistDocuments);
            
            C1ConfigurationTreeNode dirEna = new C1ConfigurationTreeNode(this, ena);
            C1ConfigurationTreeNode dirCat = new C1ConfigurationTreeNode(this, catalogs);
            C1ConfigurationTreeNode dirDoc = new C1ConfigurationTreeNode(this, documents);
            
            childs.add(dirEna);
            childs.add(dirCat);
            childs.add(dirDoc);
        
        } else if(obj instanceof Catalog) {

            List<MetaObject> sublistProperties = obj.asCatalog().getProperties().stream().map((pr) -> (MetaObject) pr).collect(Collectors.toList());
            List<MetaObject> sublistTabSects = obj.asCatalog().getTabularSections().stream().map((ts) -> (MetaObject) ts).collect(Collectors.toList());
            MetaVertualDirectory props = new MetaVertualDirectory("Свойства", "Свойства", obj, sublistProperties);
            MetaVertualDirectory tabs = new MetaVertualDirectory("Табличные части", "Табличные части", obj, sublistTabSects);
            
            C1ConfigurationTreeNode dirProps = new C1ConfigurationTreeNode(this, props);
            C1ConfigurationTreeNode dirTabs = new C1ConfigurationTreeNode(this, tabs);
            
            childs.add(dirProps);
            childs.add(dirTabs);
            
        } else if(obj instanceof Document) {

            List<MetaObject> sublistProperties = obj.asDocument().getProperties().stream().map((pr) -> (MetaObject) pr).collect(Collectors.toList());
            List<MetaObject> sublistTabSects = obj.asDocument().getTabularSections().stream().map((ts) -> (MetaObject) ts).collect(Collectors.toList());
            MetaVertualDirectory props = new MetaVertualDirectory("Свойства", "Свойства", obj, sublistProperties);
            MetaVertualDirectory tabs = new MetaVertualDirectory("Табличные части", "Табличные части", obj, sublistTabSects);
            
            C1ConfigurationTreeNode dirProps = new C1ConfigurationTreeNode(this, props);
            C1ConfigurationTreeNode dirTabs = new C1ConfigurationTreeNode(this, tabs);
            
            childs.add(dirProps);
            childs.add(dirTabs);
            
        } else {
            obj.getChildrens().stream().forEach((cobj) -> {
                childs.add(new C1ConfigurationTreeNode(this, cobj));
            });
        }
    }

    public C1ConfigurationTreeNode(C1ConfigurationTreeNode parent) {
        this.state = NodeState.Normal;
        //this.state = (NodeState.values()[Math.round((float) (Math.random()*(NodeState.values().length-1)))]);
        this.parent = parent;
        this.childs = new ArrayList<>();
        this.obj = new MetaRef(parent.getObj());
    }

    @Override
    public NodeState getState() {
        return state;
    }

    @Override
    public void setState(NodeState state) {
        this.state = state;
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
