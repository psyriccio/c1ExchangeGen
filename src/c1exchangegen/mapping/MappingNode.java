/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.mapping;

import c1c.meta.C1;
import c1c.meta.generated.MetaObject;
import c1c.meta.generated.impl.MetaObjectImpl;
import c1exchangegen.C1ExchangeGen;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.tree.TreeNode;
import org.slf4j.LoggerFactory;

/**
 *
 * @author psyriccio
 */
@SuppressWarnings("FieldMayBeFinal")
public class MappingNode implements TreeNode {

    private static MappingContext context = new MappingContext();
    
    private static Logger log = (Logger) LoggerFactory.getLogger("c1ex.mapper");

    private MappingNode parent;
    private List<MappingInfoNode> infoChilds;
    private List<MappingNode> childs;
    private MetaObject inObject;
    private MetaObject outObject;
    private MappingMode mode;
    private String name;
    
    public MappingNode(String name, String[] inObjFullNames, String[] outObjFullNames) {

        log.info("Creating root mapping node {}, for {} to {}", name, inObjFullNames, outObjFullNames);

        this.name = name;
        this.parent = null;
        this.infoChilds = new ArrayList<>();
        this.childs = new ArrayList<>();
        this.inObject = MetaObjectImpl.EMPTY;
        this.outObject = MetaObjectImpl.EMPTY;
        this.mode = MappingMode.NULL;

        for (int k = 0; k < inObjFullNames.length; k++) {
            log.info("Map {} to {}", inObjFullNames[k], outObjFullNames[k]);
            childs.add(
                    new MappingNode(
                            this,
                            C1.findObjFullName(C1ExchangeGen.IN_CONF, inObjFullNames[k]).get(),
                            C1.findObjFullName(C1ExchangeGen.OUT_CONF, outObjFullNames[k]).get()
                    ));
        }

    }

    public MappingNode(MappingNode parent, MetaObject inObject, MetaObject outObject) {

        this.name = "";
        this.parent = parent;
        this.infoChilds = new ArrayList<>();
        this.childs = new ArrayList<>();
        this.inObject = inObject;
        this.outObject = outObject;
        this.mode = MappingMode.NULL;

        log.info("Processing {} to {}", inObject.getFullName(), outObject.getFullName());
                
        infoChilds.add(new MappingInfoNode(this, "to", this.outObject));

        List<MetaObject> outCh = new ArrayList<>(this.outObject.getChildrens());
        List<MetaObject> inCh = new ArrayList<>();
        List<MetaObject> remOutCh = new ArrayList<>();

        inObject.getChildrens().forEach((in) -> {
            log.info("Processing {} child {} and adding subnodes", inObject.getName(), in.getName());
            outCh.forEach((out) -> {
                if (!remOutCh.contains(out) && out.getName().equals(in.getName())) {
                    log.info("Find map for name {} -> {}", in.toString(), out.toString());
                    remOutCh.add(out);
                    childs.add(new MappingNode(this, in, out));
                }
            });
            inCh.add(in);
        });

        outCh.removeAll(remOutCh);
        
        log.info("IN-object {} has {} unmapped sub-nodes", inObject.getName(), inCh.isEmpty() ? "no" : inCh.size());
        log.info("OUT-object {} has {} unmapped sub-nodes", outObject.getName(), outCh.isEmpty() ? "no" : outCh.size());

        if(!inCh.isEmpty() || !outCh.isEmpty()) {
            HashMap<String, Object> unmapped = new HashMap<>();
            unmapped.put("IN", inCh.isEmpty() ? "" : inCh);
            unmapped.put("OUT", outCh.isEmpty() ? "" : outCh);
            
            infoChilds.add(new MappingInfoNode(this, "!UNMAPPED", unmapped));
        }
    }

    @Override
    public String toString() {
        return name.isEmpty()
                ? (inObject != null
                        ? (inObject.toString() + " :// " + inObject.getFullName())
                        : this.getClass().getSimpleName()
                        + "@" + Integer.toHexString(this.hashCode()))
                : name;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return childIndex >= infoChilds.size()
                ? childs.get(childIndex - infoChilds.size())
                : infoChilds.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return childs.size() + infoChilds.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return node instanceof MappingNode
                ? childs.indexOf(node)
                : infoChilds.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return true;
    }

    @Override
    public boolean isLeaf() {
        return childs.isEmpty() && infoChilds.isEmpty();
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(
                Lists.newArrayList(infoChilds, childs).stream()
                .flatMap((item) -> item.stream())
                .collect(Collectors.toList()));
    }

}
