/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.mapping;

import c1c.meta.C1;
import c1c.meta.generated.MetaObject;
import c1c.meta.generated.MetaObjectClass;
import c1c.meta.generated.impl.MetaObjectImpl;
import c1exchangegen.C1ExchangeGen;
import c1exchangegen.generated.Mapping.Map.Rule;
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
public class MappingNode implements TreeNode, NodeStateContainer {

    private static MappingContext context = new MappingContext();

    private static Logger log = (Logger) LoggerFactory.getLogger("c1ex.mapper");

    private MappingNode parent;
    private List<MappingInfoNode> infoChilds;
    private List<MappingNode> childs;
    private MetaObject inObject;
    private MetaObject outObject;
    private MappingMode mode;
    private String name;
    private NodeState state;

    public MappingNode(String name, String[] inObjFullNames, String[] outObjFullNames, List[] rulesArray) {

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
            List<Rule> rules = rulesArray[k];
            final MetaObject inObj = C1.findObjFullName(C1ExchangeGen.IN_CONF, inObjFullNames[k]).get();
            final MetaObject outObj = C1.findObjFullName(C1ExchangeGen.OUT_CONF, outObjFullNames[k]).get();
            rules.forEach((rule) -> {
                if (rule.getMode().equals("SKIP")) {
                    inObj.select(rule.getObject()).getSelection().forEach((MetaObject sel) -> sel.mark("SKIP"));
                    outObj.select(rule.getObject()).getSelection().forEach((MetaObject sel) -> sel.mark("SKIP"));
                }
                if (rule.getMode().equals("REMAP")) {
                    MetaObject from = inObj.select(rule.getObject()).getSelection().stream().findFirst().get();
                    MetaObject dst = inObj.selectVD(rule.getDst()).getSelection().stream().findFirst().get();
                    MetaObject where = dst.getParent();
                    where.getChildrens().remove(dst);
                    dst.setParent(null);
                    where.getChildrens().add(from);
                    from.setParent(where);
                }
            });
            childs.add(new MappingNode(this, inObj, outObj));
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

        if (inObject.isMarkedBy("SKIP")) {
            this.state = NodeState.Inactive;
            infoChilds.add(new MappingInfoNode(this, "!SKIP", inObject.isMarkedBy("SKIP"), NodeState.Inactive));
            return;
        }

        this.state = inObject.compareTo(outObject).isEquals() ? NodeState.Good : NodeState.Error;

        if (this.state == NodeState.Error) {
            MappingNode curpar = parent;
            while (curpar != null) {
                curpar.setState(NodeState.Warning);
                curpar = (MappingNode) curpar.getParent();
            }

        }
        if (this.state == NodeState.Good) {
            if (parent.getState() == NodeState.Normal) {
                parent.setState(NodeState.Good);
            }
        }

        log.info("Processing {} to {}", inObject.getFullName(), outObject.getFullName());

        infoChilds.add(new MappingInfoNode(this, "to", this.outObject));

        List<MetaObject> outCh = new ArrayList<>(this.outObject.getChildrens());
        List<MetaObject> inCh = new ArrayList<>();
        List<MetaObject> remOutCh = new ArrayList<>();

        inObject.getChildrens().forEach((in) -> {
            log.info("Processing {} child {} and adding subnodes", inObject.getName(), in.getName());
            if (in.isMarkedBy("SKIP")) {
                childs.add(new MappingNode(this, in, in.getEMPTY()));
            } else if (in.getObjClass() == MetaObjectClass.TypeDescription) {
                MetaObject out = outObject.getChildrens().stream()
                        .findFirst().get().asTypeDescription();
                childs.add(new MappingNode(this, in, out));
                remOutCh.add(out);
            } else {
                inCh.add(in);
                outCh.forEach((out) -> {
                    if (!remOutCh.contains(out) && out.getName().equals(in.getName())) {
                        log.info("Find map for name {} -> {}", in.toString(), out.toString());
                        remOutCh.add(out);
                        childs.add(new MappingNode(this, in, out));
                        inCh.remove(in);
                    }
                });
            }
        });

        outCh.removeAll(remOutCh);
        List<MetaObject> outSkp = outCh.stream().filter((itm) -> itm.isMarkedBy("SKIP")).collect(Collectors.toList());
        outCh.removeAll(outSkp);

        log.info("IN-object {} has {} unmapped sub-nodes", inObject.getName(), inCh.isEmpty() ? "no" : inCh.size());
        log.info("OUT-object {} has {} unmapped sub-nodes", outObject.getName(), outCh.isEmpty() ? "no" : outCh.size());

        if (!inCh.isEmpty() || !outCh.isEmpty()) {
            HashMap<String, Object> unmapped = new HashMap<>();
            unmapped.put("IN", inCh.isEmpty() ? "" : inCh);
            unmapped.put("OUT", outCh.isEmpty() ? "" : outCh);

            infoChilds.add(new MappingInfoNode(this, "!UNMAPPED", unmapped, NodeState.Error));
            this.setState(NodeState.Warning);
        }

        this.inObject.getTypeReferences().stream()
                .filter(
                        (tRef) -> MappingContext.MAPPING.getMaps().stream()
                        .noneMatch(
                                (map) -> map.getIn().equals(tRef.getFullName()))
                ).forEach((tRef) -> {
                    infoChilds.add(
                            new MappingInfoNode(this, "!NEED", tRef, NodeState.Error));
                });

        this.outObject.getTypeReferences().stream()
                .filter(
                        (tRef) -> MappingContext.MAPPING.getMaps().stream()
                        .noneMatch(
                                (map) -> map.getOut().equals(tRef.getFullName()))
                ).forEach((tRef) -> {
                    infoChilds.add(
                            new MappingInfoNode(this, "!NEED", tRef, NodeState.Error));
                });

        this.infoChilds.forEach((inf) -> {
            if (inf.getState() != NodeState.Error) {
                inf.setState(this.state);
                if (inf.children() != null) {
                    Collections.list(inf.children()).forEach((ch) -> {
                        if (((MappingInfoNode) ch).getState() != NodeState.Error) {
                            ((MappingInfoNode) ch).setState(this.state);
                        }
                    });
                }
            }
        });

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

    public MetaObject getInObject() {
        return inObject;
    }

    public MetaObject getOutObject() {
        return outObject;
    }

    public MappingMode getMode() {
        return mode;
    }

}
