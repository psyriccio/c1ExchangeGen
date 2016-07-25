/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import c1exchangegen.gui.ObjectTreeNode;
import c1meta.CatalogObjectConf;
import c1meta.CatalogObjectObj;
import c1meta.CatalogObjectProperty;
import c1meta.CatalogObjectValue;
import c1meta.Conf;
import c1meta.Owner;
import c1meta.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.tree.TreeNode;

/**
 *
 * @author psyriccio
 */
public class ObjectIndex {

    public static final CatalogObjectObj EMPTY = new CatalogObjectObj();
    public static final Owner NO_OWNER = new Owner();
    
    protected static HashMap<String, Object> indexRefsStatic = new HashMap<>();
    protected static HashMap<String, Class> indexClassStatic = new HashMap<>();
    protected static HashMap<String, Object> indexDescriptionStatic = new HashMap<>();

    public static String getClassSuffix(Object obj) {
        return obj instanceof Types ? "Types"
                : obj instanceof CatalogObjectConf ? "Conf" 
                : obj instanceof CatalogObjectObj ? "Object"
                : obj instanceof CatalogObjectProperty ? "Property"
                : obj instanceof CatalogObjectValue ? "Value" : "UNKNOWN";
                        
    }

    public static String getRef(Object obj) {
        return obj == null ? "00000000-0000-0000-0000-000000000000"
                : obj instanceof CatalogObjectObj ? ((CatalogObjectObj) obj).getRef()
                        : obj instanceof CatalogObjectProperty ? ((CatalogObjectProperty) obj).getRef()
                                : obj instanceof CatalogObjectValue ? ((CatalogObjectValue) obj).getRef()
                                        : obj instanceof CatalogObjectConf ? ((CatalogObjectConf) obj).getRef()
                                                : obj instanceof Types ? ((Types) obj).getRow().stream().map((row) -> row.getType()).reduce(obj.toString() + "|Types:", (acc, itm) -> acc.isEmpty() ? acc + itm : ", " + acc + itm)
                                                        : obj.getClass().getName() + "@" + Integer.toHexString(obj.hashCode());

    }

    public static String getName(Object obj) {
        return obj instanceof CatalogObjectObj ? ((CatalogObjectObj) obj).getName()
                : obj instanceof CatalogObjectProperty ? ((CatalogObjectProperty) obj).getDescription()
                        : ((CatalogObjectValue) obj).getDescription();
    }

    public static String getTypesString(Object obj) {
        return obj instanceof CatalogObjectObj ? ((CatalogObjectObj) obj).getType()
                : obj instanceof CatalogObjectProperty ? ((CatalogObjectProperty) obj).getTypes().getRow().stream()
                        .map((row) -> getDescription(deRefStatic(row.getType()).orElse(EMPTY))
                        ).reduce("", (acc, item) -> acc + (acc.isEmpty() ? item : ", " + item))
                        : ((CatalogObjectValue) obj).getTypes().getRow().stream()
                        .map((
                                row) -> getDescription(deRefStatic(row.getType()).orElse(EMPTY))
                        ).reduce("", (acc, item) -> acc + (acc.isEmpty() ? item : ", " + item));
    }

    public static String getDescription(Object obj) {
        return obj == EMPTY ? "<EMPTY>"
                : obj instanceof CatalogObjectObj ? ((CatalogObjectObj) obj).getDescription()
                        : obj instanceof CatalogObjectProperty ? ((CatalogObjectProperty) obj).getDescription()
                                : obj instanceof CatalogObjectValue ? ((CatalogObjectValue) obj).getDescription()
                                        : obj instanceof Types
                                                ? ((Types) obj).getRow().stream()
                                                .map((row) -> getDescription((deRefStatic(row.getType()).orElse(EMPTY))))
                                                .reduce(
                                                        obj.toString() + "|Types:",
                                                        (acc, itm) -> acc.endsWith(":")
                                                        ? acc + itm : ", " + acc + itm) : obj.toString();
    }

    public static String getFullDescription(Object obj) {
        String fDesc = obj == EMPTY ? "<EMPTY>" : "";
        Object cObj = obj;
        while (cObj != EMPTY) {
            fDesc = fDesc.isEmpty() ? getDescription(cObj) + fDesc : getDescription(cObj) + "." + fDesc;
            cObj = getOwner(cObj).orElse(EMPTY);
        }
        return fDesc;
    }

    public static Optional<Object> getOwner(Object obj) {
        return obj instanceof CatalogObjectObj ? deRefStatic(Optional.ofNullable(((CatalogObjectObj) obj).getOwner()).orElse(NO_OWNER).getContent())
                : obj instanceof CatalogObjectProperty ? deRefStatic(((CatalogObjectProperty) obj).getOwner().getContent())
                        : obj instanceof CatalogObjectValue ? deRefStatic(((CatalogObjectValue) obj).getOwner().getContent())
                                : obj instanceof CatalogObjectConf ? Optional.of(EMPTY)
                                        : Optional.empty();
    }

    public static Optional<Object> getParent(Object obj) {
        return obj instanceof CatalogObjectObj ? deRefStatic(((CatalogObjectObj) obj).getParent())
                : obj instanceof CatalogObjectProperty ? deRefStatic(((CatalogObjectProperty) obj).getParent())
                        : obj instanceof CatalogObjectValue ? deRefStatic(((CatalogObjectValue) obj).getParent())
                                : obj instanceof CatalogObjectConf ? Optional.of(EMPTY)
                                        : Optional.empty();
    }

    public static Optional<Object> getParentOrOwner(Object obj) {
        return Optional.of(getParent(obj).orElse(getOwner(obj).orElse(EMPTY)));
    }

    public static List<Object> getChildsStatic(Object parent) {
        final String ref = getRef(parent);
        return getIndexRefsStatic().values().stream()
                .filter((obj) -> (getRef(getOwner(obj).orElse(null)).equals(ref)))
                .collect(Collectors.toList());
    }

    public static List<Object> getSlavesStatic(Object parent) {
        final String ref = getRef(parent);
        return getIndexRefsStatic().values().stream()
                .filter((obj) -> (getRef(getParent(obj).orElse(getOwner(obj).orElse(obj))).equals(ref)))
                .collect(Collectors.toList());
    }

    public static List<Object> getSlavesAndChildsSatic(Object parent) {
        List<Object> res = getSlavesStatic(parent);
        res.addAll(getChildsStatic(parent));
        return res;
    }

    public static HashMap<String, Object> getIndexRefsStatic() {
        return indexRefsStatic;
    }

    public static HashMap<String, Class> getIndexClassStatic() {
        return indexClassStatic;
    }

    public static HashMap<String, Object> getIndexDescriptionStatic() {
        return indexDescriptionStatic;
    }

    public static Optional<Object> deRefStatic(String ref) {
        return Optional.ofNullable(indexRefsStatic.get(ref));
    }

    public static Optional<CatalogObjectObj> deRefAsObjStatic(String ref) {
        Object obj = indexRefsStatic.get(ref);
        return Optional.ofNullable((CatalogObjectObj) (obj instanceof CatalogObjectObj ? obj : null));
    }

    public static Optional<CatalogObjectProperty> deRefAsPropertyStatic(String ref) {
        Object obj = indexRefsStatic.get(ref);
        return Optional.ofNullable((CatalogObjectProperty) (obj instanceof CatalogObjectProperty ? obj : null));
    }

    public static Optional<CatalogObjectValue> deRefAsValueStatic(String ref) {
        Object obj = indexRefsStatic.get(ref);
        return Optional.ofNullable((CatalogObjectValue) (obj instanceof CatalogObjectValue ? obj : null));
    }

    public static Optional<Object> findObject(ObjectIndex index, String description) {
        return description.startsWith("ref:")
                ? Optional.of(getIndexRefsStatic().get(description.replace("ref:", "")))
                : Optional.of(index.getIndexDescription().get(description));
    }

    public static List<Object> findMatches(ObjectIndex index, String regex) {
        return index.getIndexDescription().keySet().stream()
                .filter((key) -> (key.matches(regex)))
                .map((key) -> index.getIndexDescription().get(key))
                .collect(Collectors.toList());
    }

    private final Conf conf;
    private final TreeNode rootNode;
    private final HashMap<String, Object> indexRefs;
    private final HashMap<String, Class> indexClass;
    private final HashMap<String, Object> indexDescription;
    private final HashMap<String, TreeNode> indexTreeNodes;

    public Conf getConf() {
        return conf;
    }

    public TreeNode getRootNode() {
        return rootNode;
    }

    public static CatalogObjectObj getEmpty() {
        return EMPTY;
    }

    public HashMap<String, Object> getIndexRefs() {
        return indexRefs;
    }

    public HashMap<String, Class> getIndexClass() {
        return indexClass;
    }

    public HashMap<String, Object> getIndexDescription() {
        return indexDescription;
    }

    public HashMap<String, TreeNode> getIndexTreeNodes() {
        return indexTreeNodes;
    }

    public ObjectIndex(Conf conf) {
        this.conf = conf;
        this.indexRefs = new HashMap<>();
        this.indexClass = new HashMap<>();
        this.indexDescription = new HashMap<>();
        this.indexTreeNodes = new HashMap<>();
        this.rootNode = new ObjectTreeNode(this, conf.getCatalogObjectConf());
        conf.getObjects().stream().map((obj) -> {
            if (obj instanceof CatalogObjectObj) {
                CatalogObjectObj cObj = (CatalogObjectObj) obj;
                indexRefs.put(cObj.getRef(), obj);
                indexClass.put(cObj.getRef(), CatalogObjectObj.class);
                indexTreeNodes.put(cObj.getRef(), new ObjectTreeNode(this, obj));
            }
            return obj;
        }).map((obj) -> {
            if (obj instanceof CatalogObjectProperty) {
                CatalogObjectProperty cObj = (CatalogObjectProperty) obj;
                indexRefs.put(cObj.getRef(), obj);
                indexClass.put(cObj.getRef(), CatalogObjectProperty.class);
                indexTreeNodes.put(cObj.getRef(), new ObjectTreeNode(this, obj));
                indexRefs.put(getRef(cObj.getTypes()), cObj.getTypes());
                indexClass.put(getRef(cObj.getTypes()), Types.class);
                indexTreeNodes.put(getRef(cObj.getTypes()), new ObjectTreeNode(this, cObj.getTypes()));
            }
            return obj;
        }).filter((obj) -> (obj instanceof CatalogObjectValue)).forEach((obj) -> {
            CatalogObjectValue cObj = (CatalogObjectValue) obj;
            indexRefs.put(cObj.getRef(), obj);
            indexClass.put(cObj.getRef(), CatalogObjectValue.class);
            indexTreeNodes.put(cObj.getRef(), new ObjectTreeNode(this, obj));
            indexRefs.put(getRef(cObj.getTypes()), cObj.getTypes());
            indexClass.put(getRef(cObj.getTypes()), Types.class);
            indexTreeNodes.put(getRef(cObj.getTypes()), new ObjectTreeNode(this, cObj.getTypes()));
        });
        indexRefsStatic.putAll(indexRefs);
        indexClassStatic.putAll(indexClass);
        indexRefs.forEach((ref, obj) -> {
            if (obj instanceof CatalogObjectObj) {
                CatalogObjectObj cObj = (CatalogObjectObj) obj;
                indexDescription.put(cObj.getDescription(), obj);
            }
        });
        indexRefs.forEach((ref, obj) -> {
            if (obj instanceof CatalogObjectProperty) {
                CatalogObjectProperty cObj = (CatalogObjectProperty) obj;
                indexDescription.put(getDescription(getOwner(cObj).orElse(EMPTY)) + "." + getDescription(obj), obj);
                indexDescription.put(getDescription(cObj.getTypes()), cObj.getTypes());
            }
        });
        indexRefs.forEach((ref, obj) -> {
            if (obj instanceof CatalogObjectValue) {
                CatalogObjectValue cObj = (CatalogObjectValue) obj;
                indexDescription.put(getDescription(getOwner(cObj).orElse(EMPTY)) + "." + getDescription(obj), obj);
                indexDescription.put(getDescription(cObj.getTypes()), cObj.getTypes());
            }
        });
        //indexDescriptionStatic.putAll(indexRefs);
    }

    public boolean isPresentInIndex(Object obj) {
        return indexRefs.containsValue(obj);
    }
    
    public Optional<Object> deRef(String ref) {
        return Optional.ofNullable(indexRefs.get(ref));
    }

    public Optional<CatalogObjectObj> deRefAsObj(String ref) {
        Object obj = indexRefs.get(ref);
        return Optional.ofNullable((CatalogObjectObj) (obj instanceof CatalogObjectObj ? obj : null));
    }

    public Optional<CatalogObjectProperty> deRefAsProperty(String ref) {
        Object obj = indexRefs.get(ref);
        return Optional.ofNullable((CatalogObjectProperty) (obj instanceof CatalogObjectProperty ? obj : null));
    }

    public Optional<CatalogObjectValue> deRefAsValue(String ref) {
        Object obj = indexRefs.get(ref);
        return Optional.ofNullable((CatalogObjectValue) (obj instanceof CatalogObjectValue ? obj : null));
    }

    public List<Object> getChilds(Object parent) {
        final String ref = parent == conf.getCatalogObjectConf() ? "00000000-0000-0000-0000-000000000000" : getRef(parent);
        List<Object> lst = getIndexRefs().values().stream()
                .filter((obj) -> (getRef(getOwner(obj).orElse(null)).equals(ref)))
                .collect(Collectors.toList());
        if (parent == conf.getCatalogObjectConf()) {
            lst.addAll(getIndexRefs().values().stream()
                    .filter((obj) -> (getRef(getOwner(obj).orElse(null)).equals(getRef(conf.getCatalogObjectConf()))))
                    .collect(Collectors.toList())
            );
        }

        return lst;

    }

    public List<Object> getSlaves(Object parent) {
        final String ref = parent == conf.getCatalogObjectConf() ? "00000000-0000-0000-0000-000000000000" : getRef(parent);
        List<Object> lst = getIndexRefs().values().stream()
                .filter((obj) -> (getRef(getParent(obj).orElse(getOwner(obj).orElse(null))).equals(ref)))
                .collect(Collectors.toList());
        if (parent == conf.getCatalogObjectConf()) {
            lst.addAll(getIndexRefs().values().stream()
                    .filter((obj) -> (getRef(getParent(obj).orElse(getOwner(obj).orElse(null))).equals(getRef(conf.getCatalogObjectConf()))))
                    .collect(Collectors.toList())
            );
        }

        return lst;

    }

    public List<Object> getSlavesAndChilds(Object parent) {
        List<Object> res = getSlaves(parent);
        res.addAll(getChilds(parent));
        return res;
    }

}
