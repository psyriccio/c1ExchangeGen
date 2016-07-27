/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import static c1exchangegen.ObjectIndex.*;
import static c1exchangegen.C1ExchangeGen.log;
import c1exchangegen.generated.Mapping;
import c1exchangegen.generated.Mapping.Map;
import c1exchangegen.generated.Mapping.Map.Rule;
import c1exchangegen.generated.ObjectFactory;
import c1meta.CatalogObjectObj;
import c1meta.CatalogObjectProperty;
import c1meta.CatalogObjectValue;
import c1meta.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author psyriccio
 */
public class ObjectComparator {

    public static final ArrayList<Rule> EMPTY_RULE_LIST = new ArrayList<>();
    public static final ObjectFactory OBJ = new ObjectFactory();
    
    protected static String descrStatic(String prefix, String propName, Object in, Object inVal, Object out, Object outVal) {
        return prefix
                + " "
                + propName
                + " : "
                + getFullDescription(in)
                + ", "
                + getFullDescription(out)
                + " : "
                + inVal.toString()
                + " | "
                + outVal.toString();
    }

    public static ComparationResult invokeSupplier(Supplier<ComparationResult> supp) {
        return supp.get();
    }

    public static Predicate<Mapping.Map.Rule> ruleMapPredicate(Object obj) {
        return obj == EMPTY ? (Rule rule) -> false
                : (Rule rule) -> (rule.getObject().equals(getFullDescription(obj)))
                || rule.getObject().equals(getDescription(obj))
                || (rule.getObject().endsWith("\\*")
                ? (getDescription(obj).startsWith(rule.getObject().replaceAll("\\*", ""))
                || getDescription(obj).startsWith(rule.getObject().replaceAll("\\*", ""))) : false)
                || (rule.getObject().startsWith("\\*")
                ? (getDescription(obj).endsWith(rule.getObject().replaceAll("\\*", ""))
                || getDescription(obj).endsWith(rule.getObject().replaceAll("\\*", ""))) : false)
                || (rule.getObject().startsWith("regexp:"))
                ? getDescription(obj).matches(rule.getObject().replace("regexp:", "")) : false;
    }

    public static Predicate<Mapping.Map> mapPredicateIn(Object obj) {
        return obj == EMPTY ? (Map map) -> false
                : (Map map) -> (map.getIn().equals(getFullDescription(obj)))
                || map.getIn().equals(getDescription(obj))
                || ((getDescription(obj).startsWith(map.getIn())
                || getDescription(obj).startsWith(map.getIn())))
                || (map.getIn().startsWith("\\*")
                ? (getDescription(obj).endsWith(map.getIn())
                || getDescription(obj).endsWith(map.getIn())) : false)
                || (map.getIn().startsWith("regexp:"))
                ? getDescription(obj).matches(map.getIn().replace("regexp:", "")) : false;
    }

    public static Predicate<Mapping.Map> mapPredicateOut(Object obj) {
        return obj == EMPTY ? (Map map) -> false
                : (Map map) -> (map.getOut().equals(getFullDescription(obj)))
                || map.getOut().equals(getDescription(obj))
                || ((getDescription(obj).startsWith(map.getOut())
                || getDescription(obj).startsWith(map.getOut())))
                || (getDescription(obj).endsWith(map.getOut())
                || getDescription(obj).endsWith(map.getOut()))
                || (map.getOut().startsWith("regexp:"))
                ? getDescription(obj).matches(map.getOut().replace("regexp:", "")) : false;
    }

    public static MappingMode decodeMode(Mapping.Map mapping, Object obj) {
        return MappingMode.valueOf(
                (mapping.getRules().stream()
                .filter((ruleMapPredicate(obj)))
                .map((rule) -> rule.getMode())
                .findFirst().orElse("NULL")).toUpperCase());
    }

    public static MappingMode decodeMode(List<Rule> rules, Object obj) {
        return MappingMode.valueOf(
                (rules.stream()
                .filter((ruleMapPredicate(obj)))
                .map((rule) -> rule.getMode())
                .findFirst().orElse("NULL")).toUpperCase());
    }

    public static MappingMode decodeModeNoCheck(List<Rule> rules, Object obj) {
        return MappingMode.valueOf(
                (rules.stream()
                .map((rule) -> rule.getMode())
                .findFirst().orElse("NULL")).toUpperCase());

    }

    public static List<Rule> parseInRules(Mapping mapping, Object in, MappingCacheContainer cacheContainer) {

        String descrIn = getFullDescription(in);
        String nameIn = getDescription(in);
        String pathIn = descrIn.replace("." + nameIn, "");
        List<Rule> rulesIn = new ArrayList<>();

        HashMap<Object, List<Map>> mapCache = new HashMap<>();
        HashMap<Object, List<Rule>> ruleCache = new HashMap<>();
        HashMap<Rule, Map> reverseMapCache = new HashMap<>();

        mapping.getMaps().stream()
                .forEach((map) -> {

                    if (map.getIn().equals(descrIn)
                          || descrIn.startsWith(map.getIn())) {

                        MappingMode pmode = map.getMode() != null && !map.getMode().isEmpty()
                                ? MappingMode.valueOf(map.getMode().toUpperCase()) : null;

                        if (pmode != null) {
                            Rule prule = OBJ.createMappingMapRule();
                            prule.setMode(map.getMode());
                            prule.setObject("*");
                            rulesIn.add(prule);
                        }
                    }

                    rulesIn.addAll(
                            map.getRules().stream()
                            .filter((rule)
                                    -> ((map.getIn().startsWith("#regex:")
                                    ? pathIn.matches(map.getIn().replace("#regex:", ""))
                                    : pathIn.startsWith(map.getIn()))
                                    && (rule.getObject().startsWith("#regex:")
                                    ? nameIn.matches(rule.getObject().replace("#regex:", ""))
                                    : (rule.getObject().endsWith("*")
                                    ? nameIn.startsWith(rule.getObject().replace("*", ""))
                                    : nameIn.equals(rule.getObject())))))
                            .peek((rule) -> {
                                List<Map> lst = mapCache.getOrDefault(in, new ArrayList<>());
                                lst.add(map);
                                mapCache.put(in, lst);
                                reverseMapCache.put(rule, map);
                                List<Rule> rlst = ruleCache.getOrDefault(in, new ArrayList<>());
                                rlst.add(rule);
                                ruleCache.put(in, rlst);
                            })
                            .collect(Collectors.toList()));
                });

        if (cacheContainer != null) {
            cacheContainer.setMapCache(mapCache);
            cacheContainer.setReverseMapCache(reverseMapCache);
            cacheContainer.setRuleCache(ruleCache);
        }

        return rulesIn;

    }

    public static List<Rule> parseOutRules(Mapping mapping, Object out, MappingCacheContainer cacheContainer) {

        String descrOut = getFullDescription(out);
        String nameOut = getDescription(out);
        String pathOut = descrOut.replace("." + nameOut, "");
        List<Rule> rulesOut = new ArrayList<>();
        HashMap<Object, List<Map>> mapCache = new HashMap<>();
        HashMap<Object, List<Rule>> ruleCache = new HashMap<>();
        HashMap<Rule, Map> reverseMapCache = new HashMap<>();

        mapping.getMaps().stream()
                .forEach((map) -> {
                    rulesOut.addAll(
                            map.getRules().stream()
                            .filter((rule)
                                    -> ((map.getOut().startsWith("#regex:")
                                    ? pathOut.matches(map.getOut().replace("#regex:", ""))
                                    : (map.getOut().endsWith("*")
                                    ? pathOut.startsWith(map.getOut().replace("*", ""))
                                    : pathOut.equals(map.getOut())))
                                    && (rule.getObject().startsWith("#regex:")
                                    ? nameOut.matches(rule.getObject().replace("#regex:", ""))
                                    : (rule.getObject().endsWith("*")
                                    ? nameOut.startsWith(rule.getObject().replace("*", ""))
                                    : nameOut.equals(rule.getObject())))))
                            .peek((rule) -> {
                                List<Map> lst = mapCache.getOrDefault(out, new ArrayList<>());
                                lst.add(map);
                                mapCache.put(out, lst);
                                reverseMapCache.put(rule, map);
                                List<Rule> rlst = ruleCache.getOrDefault(out, new ArrayList<>());
                                rlst.addAll(rlst);
                                ruleCache.put(out, rlst);
                            })
                            .collect(Collectors.toList()));
                });

        if (cacheContainer != null) {
            mapCache.forEach((key, val) -> cacheContainer.getMapCache().computeIfAbsent(key, (nkey) -> new ArrayList<>()).addAll(val));
            ruleCache.forEach((key, val) -> cacheContainer.getRuleCache().computeIfAbsent(key, (nkey) -> new ArrayList<>()).addAll(val));
            reverseMapCache.forEach((key, val) -> cacheContainer.getReverseMapCache().put(key, val));
        }

        return rulesOut;

    }

    public static ComparationResult doWithModeCheck(Mapping mapping, Object in, Object out, Supplier<ComparationResult> supp) {

        List<Rule> rulesIn = parseInRules(mapping, in, null);
        List<Rule> rulesOut = parseOutRules(mapping, out, null);

        MappingMode modeIn = decodeModeNoCheck(rulesIn, in);
        MappingMode modeOut = decodeModeNoCheck(rulesOut, out);

        if (modeIn == MappingMode.SKIP || modeOut == MappingMode.SKIP) {
            log.debug("MODE: {}, {}", modeIn.name(), modeOut.name());
            log.info("'SKIP' rule. Skipped mapping for {} - {}, EQU assumed", getFullDescription(in), getFullDescription(out));
            return new ComparationResult(
                    ComparationResult.Status.EQU,
                    ComparationResultsItem.makeOneAsList(
                            in, in, ComparationResult.DiffKind.PROPERTY,
                            descrStatic("ASSUME", "MODE", in, modeIn, out, modeOut)));
        }
        log.debug("No mapping rules founded");
        return supp.get();
    }

    protected class ChainChecker {

        private ComparationResult.Status status;
        private List<ComparationResultsItem> items;
        private List<ComparationResult> results;

        public ChainChecker() {
            this.status = ComparationResult.Status.EQU;
            this.items = new ArrayList<>();
            this.results = new ArrayList<>();
        }

        public ChainChecker(ComparationResult.Status status, List<ComparationResultsItem> items) {
            this.status = status;
            this.items = items;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, ComparationResult result) {
            if (pdc.test(this)) {
                results.add(result);
                log.debug("Added checking result: " + results.get(results.size() - 1).toString());
            }
            return this;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, Supplier<ComparationResult> supp) {
            if (pdc.test(this)) {
                results.add(supp.get());
                log.debug("Added checking result: " + results.get(results.size() - 1).toString());
            }
            return this;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, ComparationResult result, ComparationResult negResult) {
            results.add(
                    pdc.test(this) ? result : negResult
            );
            log.debug("Added checking result: " + results.get(results.size() - 1).toString());
            return this;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, ComparationResult result, Supplier<ComparationResult> negSupp) {
            results.add(
                    pdc.test(this) ? result : negSupp.get()
            );
            log.debug("Added checking result: " + results.get(results.size() - 1).toString());
            return this;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, ComparationResult result, Consumer<Boolean> predicateResultConsumer) {
            predicateResultConsumer.andThen((val) -> {
                if (val) {
                    results.add(result);
                    log.debug("Added checking result: " + results.get(results.size() - 1).toString());
                }
            }).accept(pdc.test(this));
            return this;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, ComparationResult.Status status, ComparationResultsItem item) {
            if (pdc.test(this)) {
                this.status = this.status.ordinal() < status.ordinal() ? status : this.status;
                items.add(item);
                log.debug("Added checking result: {}, {}", this.status.name(), item.toString());
            }
            return this;
        }

        public ChainChecker check(Predicate<ChainChecker> pdc, ComparationResult.Status status, ComparationResultsItem item, Consumer<Boolean> predicateResultConsumer) {
            predicateResultConsumer.andThen((val) -> {
                if (val) {
                    this.status = this.status.ordinal() < status.ordinal() ? status : this.status;
                    items.add(item);
                    log.debug("Added checking result: {}, {}", this.status.name(), item.toString());
                }
            }).accept(pdc.test(this));
            return this;
        }

        public ChainChecker add(ComparationResult result) {
            results.add(result);
            log.debug("Added checking result: " + result.toString());
            return this;
        }

        public ChainChecker add(Supplier<ComparationResult> suppl) {
            ComparationResult result = suppl.get();
            log.debug("Added checking result: " + result.toString());
            return this.add(result);
        }

        public ComparationResult done() {
            ComparationResult.Status subStatus = results.stream()
                    .map((res) -> res.getStatus())
                    .reduce(status, (acc, item) -> acc.ordinal() > item.ordinal() ? item : acc);

            status = status.ordinal() > subStatus.ordinal() ? subStatus : status;

            items = results.stream()
                    .map((res) -> res.getResultItems())
                    .reduce(
                            items,
                            (acc, item) -> Stream.concat(acc.stream(), item.stream()).collect(Collectors.toList())
                    );

            ComparationResult result = new ComparationResult(status, items);
            log.debug("Closing ChainChecker with result: {}", result.toString());
            return result;

        }

    }

    public static final String[] PRIME_TYPES_ARRAY = new String[]{"Число", "Строка", "Дата", "Булево", "ХранилищеЗначения", "УникальныйИдентификатор"};
    public static final List<String> PRIME_TYPES_LIST = Arrays.asList(PRIME_TYPES_ARRAY);

    public static final String[] CLASS_TYPES_ARRAY = new String[]{"Справочник", "Документ", "Перечисление"};
    public static final List<String> CLASS_TYPES_LIST = Arrays.asList(CLASS_TYPES_ARRAY);

    public static String getObjectPairKey(Object in, Object out) {
        return getRef(in) + "|" + getRef(out);
    }

    public static String getObjectPairKey(Types in, Types out) {
        return getDescription(in) + "|" + getDescription(out);
    }

    public static String getObjectPairKey(ObjectIndex indexIn, String inDescr, ObjectIndex indexOut, String outDescr) {
        return getRef(indexIn.getIndexDescription().get(inDescr)) + "|" + getRef(indexOut.getIndexDescription().get(outDescr));
    }

    public static String getObjectPairKey(String inRef, String outRef) {
        return inRef + "|" + outRef;
    }

    public static MappingMode checkMapping(Mapping mapping, Object in, Object out) {
        return MappingMode.valueOf(
                (mapping.getMaps().stream()
                .filter(
                        (rule) -> rule.getIn().equals(getDescription(in))
                        && rule.getOut().equals(getDescription(out))
                ).map((rule) -> rule.getMode())
                .findFirst().orElse("NULL").toUpperCase())
        );
    }

    public static Mapping selectMapping(Mapping mapping, Object obj) {
        Mapping result = (new ObjectFactory()).createMapping();
        result.getMaps().addAll(
                mapping.getMaps().stream()
                .filter(
                        (rule) -> rule.getIn().equals(getDescription(obj))
                        || rule.getOut().equals(getDescription(obj))
                ).collect(Collectors.toList())
        );

        return result;

    }

    private final ObjectIndex inIndex;
    private final ObjectIndex outIndex;
    private final HashMap<String, ComparationResult> comparedDeepCache;
    private final HashMap<String, ComparationResult> doneCache;
    private final Mapping glMapping;
    private final Mapping.Map mapping;
    private final HashMap<Object, MappingMode> modeCache;
    private final HashMap<Object, List<Map>> mapCacheIn;
    private final HashMap<Object, List<Map>> mapCacheOut;
    private final HashMap<Object, List<Rule>> ruleCacheIn;
    private final HashMap<Object, List<Rule>> ruleCacheOut;
    private final HashMap<Rule, Map> reverseMapCacheIn;
    private final HashMap<Rule, Map> reverseMapCacheOut;
    private boolean ruleCacheInited = false;

    protected ComparationResult viaCache(String key, ComparationResult res) {
        ComparationResult cached = comparedDeepCache.putIfAbsent(key, res);
        if (cached == null) {
            log.debug("Cahced: {} => {}", key, res.toString());
        }
        if (res.getStatus() != ComparationResult.Status.EQU) {
            log.info("{}", res.toString());
            log.info("!ERROR! : Non EQU status cached! Stopping...");
            log.info("        Mapping results:");
            if (doneCache.isEmpty()) {
                log.info(" --- empty --- ");
            }
            doneCache.forEach((dkey, done) -> {
                log.info("   key:" + dkey);
                String[] refs = dkey.split("[\\|]");
                log.debug("ref[0]: {}, ref[1]: {}", refs[0], refs[1]);
                log.info("   " + getFullDescription(deRefStatic(refs[0]).orElse(EMPTY)) + " -> " + getFullDescription(deRefStatic(refs[1]).orElse(EMPTY)));
                log.info("      " + getName(deRefStatic(refs[0]).orElse(EMPTY)) + " -> " + getName(deRefStatic(refs[0]).orElse(EMPTY)));
                done.getResultItems().forEach((reslt) -> {
                    log.info(reslt.toString());
                });
            });
            doneCache.forEach((dkey, done) -> {
                done.getResultItems().forEach((rres) -> {
                    System.out.println(getFullDescription(rres.getObjectIn()) + "\n => " + getFullDescription(rres.getObjectOut()));
                });
            });
            System.exit(res.getStatus().ordinal());
        } else {
            doneCache.put(key, res);
        }
        return cached == null ? res : cached;
    }

    protected String descr(String prefix, String propName, Object in, Object inVal, Object out, Object outVal) {
        return prefix
                + " "
                + propName
                + " : "
                + getDescription(in)
                + ", "
                + getDescription(out)
                + " : "
                + inVal.toString()
                + " | "
                + outVal.toString();
    }

    public ObjectComparator(Mapping glMapping, Mapping.Map mapping, ObjectIndex inIndex, ObjectIndex outIndex) {
        this.inIndex = inIndex;
        this.outIndex = outIndex;
        this.comparedDeepCache = new HashMap<>();
        this.doneCache = new HashMap<>();
        this.glMapping = glMapping;
        this.mapping = mapping;
        this.modeCache = new HashMap<>();
        this.mapCacheIn = new HashMap<>();
        this.ruleCacheIn = new HashMap<>();
        this.reverseMapCacheIn = new HashMap<>();
        this.mapCacheOut = new HashMap<>();
        this.ruleCacheOut = new HashMap<>();
        this.reverseMapCacheOut = new HashMap<>();

        log.info("Building mapping cache...");

    }

    public void initRuleCache() {

        inIndex.getIndexRefs().values().stream().forEach((obj) -> {
            MappingCacheContainer cacheContainerIn = new MappingCacheContainer();
            parseInRules(glMapping, obj, cacheContainerIn);
            cacheContainerIn.getMapCache().forEach((key, lst) -> {
                List<Map> olst = mapCacheIn.getOrDefault(key, new ArrayList<>());
                olst.addAll(lst);
                mapCacheIn.put(key, olst);
            });
            cacheContainerIn.getRuleCache().forEach((key, lst) -> {
                List<Rule> olst = ruleCacheIn.getOrDefault(key, new ArrayList<>());
                olst.addAll(lst);
                ruleCacheIn.put(key, olst);
            });
            cacheContainerIn.getReverseMapCache().forEach((rule, map) -> {
                reverseMapCacheIn.put(rule, map);
            });
        });

        outIndex.getIndexRefs().values().stream().forEach((obj) -> {
            MappingCacheContainer cacheContainerOut = new MappingCacheContainer();
            parseOutRules(glMapping, obj, cacheContainerOut);
            cacheContainerOut.getMapCache().forEach((key, lst) -> {
                List<Map> olst = mapCacheOut.getOrDefault(key, new ArrayList<>());
                olst.addAll(lst);
                mapCacheOut.put(key, olst);
            });
            cacheContainerOut.getRuleCache().forEach((key, lst) -> {
                List<Rule> olst = ruleCacheOut.getOrDefault(key, new ArrayList<>());
                olst.addAll(lst);
                ruleCacheOut.put(key, olst);
            });
            cacheContainerOut.getReverseMapCache().forEach((rule, map) -> {
                reverseMapCacheOut.put(rule, map);
            });
        });

        ruleCacheInited = true;

    }

    public MappingMode getCachedMode(Object obj) {
        return Optional.ofNullable(modeCache.get(obj)).orElseGet(() -> {

            List<Rule> rules
                    = inIndex.isPresentInIndex(obj)
                    ? parseInRules(glMapping, obj, null)
                    : parseOutRules(glMapping, obj, null);

            MappingMode mode = decodeModeNoCheck(rules, obj);
            modeCache.put(obj, mode);

            return mode;

        });
    }

    public HashMap<Object, List<Map>> getMapCacheIn() {
        return mapCacheIn;
    }

    public HashMap<Object, List<Map>> getMapCacheOut() {
        return mapCacheOut;
    }

    public HashMap<Object, List<Rule>> getRuleCacheIn() {
        return ruleCacheIn;
    }

    public HashMap<Object, List<Rule>> getRuleCacheOut() {
        return ruleCacheOut;
    }

    public HashMap<Rule, Map> getReverseMapCacheIn() {
        return reverseMapCacheIn;
    }

    public HashMap<Rule, Map> getReverseMapCacheOut() {
        return reverseMapCacheOut;
    }

    public Object[] checkRemaps(Object in, Object out) {

        Optional<Object[]> rems = getRuleCacheIn().keySet().stream()
                .filter((key) -> 
                        getFullDescription(in).startsWith(getFullDescription(key)))
                .map((key) -> 
                        getRuleCacheIn().get(key))
                .flatMap((lst) -> 
                        (lst.stream()))
                .collect(Collectors.toList()).stream()
                .filter((rule) -> 
                        rule.getMode().toUpperCase().equals(MappingMode.REMAP.name()))
                .filter((rule) -> 
                        getFullDescription(in).startsWith(reverseMapCacheIn.get(rule).getIn() + "." + rule.getObject()))
                .map(
                        (rule) -> new Object[]{
                            findObjectEx(
                                    inIndex, 
                                    getFullDescription(in)
                                            .replace(
                                                    reverseMapCacheIn.get(rule).getIn() + "." 
                                                            + rule.getObject(), 
                                                    reverseMapCacheIn.get(rule).getIn() + "." 
                                                            + rule.getIn())).orElse(in),
                            findObjectEx(
                                    outIndex, 
                                    getFullDescription(out)
                                            .replace(
                                                    reverseMapCacheIn.get(rule).getOut() + "." 
                                                            + rule.getObject(), 
                                                    reverseMapCacheIn.get(rule).getOut() + "." 
                                                            + rule.getOut())).orElse(out)
                        }).findFirst();

        return rems.orElse(new Object[]{in, out});

    }

    public ComparationResult compare(CatalogObjectObj inObj, CatalogObjectObj outObj) {

        if (!ruleCacheInited) {
            initRuleCache();
        }

        Object[] remaps = checkRemaps(inObj, outObj);
        CatalogObjectObj in = (CatalogObjectObj) remaps[0];
        CatalogObjectObj out = (CatalogObjectObj) remaps[1];

        final String cacheKey = getObjectPairKey(in, out);

        if (!in.equals(inObj)) {
            log.info("Process remap {} -> {}", getFullDescription(inObj), getFullDescription(in));
        }

        if (!out.equals(outObj)) {
            log.info("Process remap {} -> {}", getFullDescription(outObj), getFullDescription(out));
        }

        log.debug("Compare obj: {}, {}", in.getDescription(), out.getDescription());
        if (in == EMPTY || out == EMPTY) {
            log.info("Cant find mapping for one of the objects {}, {}", getFullDescription(in), getFullDescription(out));
            return doWithModeCheck(glMapping, in, out, () -> {
                return viaCache(
                        cacheKey,
                        new ComparationResult(
                                ComparationResult.Status.NO_MAP,
                                ComparationResultsItem.makeOneAsList(
                                        in, out,
                                        ComparationResult.DiffKind.TYPE,
                                        descr("MISS", "MAP", in, out, in, out))));
            });
        }

        final ComparationResult cached = comparedDeepCache.get(cacheKey);
        log.debug(
                "Cache: {}, {}",
                cacheKey,
                cached == null ? null
                        : cached.getStatus().name() + " ("
                        + cached.getResultItems().stream()
                        .map(
                                (itm) -> itm.getDiffKind().name()
                        ).reduce("", (acc, itm) -> (acc.isEmpty() ? itm : ", " + itm))
        );

        return doWithModeCheck(glMapping, in, out, () -> {
            return Optional.ofNullable(comparedDeepCache.get(cacheKey)).orElseGet(() -> {
                return viaCache(
                        cacheKey,
                        new ChainChecker().check(
                                (__) -> (!getClassSuffix(in).equals(getClassSuffix(out))),
                                ComparationResult.Status.DIFF,
                                ComparationResultsItem.makeOne(
                                        in, out,
                                        ComparationResult.DiffKind.TYPE,
                                        descr("DIFF", "Class", in, getClassSuffix(in), out, getClassSuffix(out)))
                        ).check(
                                (__) -> (!in.getType().equals(out.getType())),
                                ComparationResult.Status.DIFF,
                                ComparationResultsItem.makeOne(
                                        in, out,
                                        ComparationResult.DiffKind.TYPE,
                                        descr("DIFF", "Type", in, in.getType(), out, out.getType()))
                        ).add(() -> {
                            final ChainChecker subCheck = new ChainChecker();
                            final List<Object> outChilds = getChildsStatic(out);
                            getChildsStatic(in).forEach((inCh) -> {
                                subCheck.add(
                                        compare(
                                                inCh,
                                                outChilds.stream()
                                                .filter(
                                                        (outCh) -> getDescription(outCh).equals(getDescription(inCh))
                                                ).findFirst()
                                                .orElse(EMPTY)
                                        )
                                );
                            });
                            return subCheck.done();
                        }).done()
                );
            });
        });

    }

    public ComparationResult compare(CatalogObjectProperty inObj, CatalogObjectProperty outObj) {

        if (!ruleCacheInited) {
            initRuleCache();
        }

        Object[] remaps = checkRemaps(inObj, outObj);
        CatalogObjectProperty in = (CatalogObjectProperty) remaps[0];
        CatalogObjectProperty out = (CatalogObjectProperty) remaps[1];

        if (!in.equals(inObj)) {
            log.info("Process remap {} -> {}", getFullDescription(inObj), getFullDescription(in));
        }

        if (!out.equals(outObj)) {
            log.info("Process remap {} -> {}", getFullDescription(outObj), getFullDescription(out));
        }

        final String cacheKey = getObjectPairKey(in, out);

        log.debug("Compare props: {}, {}", in.getDescription(), out.getDescription());
        final ComparationResult cached = comparedDeepCache.get(cacheKey);
        log.debug(
                "Cache: {}, {}",
                cacheKey,
                cached == null ? null
                        : cached.getStatus().name() + " ("
                        + cached.getResultItems().stream()
                        .map(
                                (itm) -> itm.getDiffKind().name()
                        ).reduce("", (acc, itm) -> (acc.isEmpty() ? itm : ", " + itm))
        );

        if (getCachedMode(in) == MappingMode.NO_CHAIN
                || getCachedMode(out) == MappingMode.NO_CHAIN) {

            log.info("MODE: NO_CHAIN {} : {}, {}, {}", in, getCachedMode(in), out, getCachedMode(out));
            return doWithModeCheck(glMapping, in, out, () -> {
                return viaCache(
                        cacheKey,
                        new ComparationResult(
                                ComparationResult.Status.EQU,
                                ComparationResultsItem.makeOneAsList(
                                        in, out,
                                        ComparationResult.DiffKind.PROPERTY,
                                        descr("EQU:NO_CHAIN", "", in, in, out, out))
                        ));
            });
        }

        return doWithModeCheck(glMapping, in, out, () -> {
            return Optional.ofNullable(comparedDeepCache.get(cacheKey)).orElseGet(() -> {
                return viaCache(
                        cacheKey,
                        new ChainChecker().check(
                                (__) -> (!in.getKind().equals(out.getKind())),
                                ComparationResult.Status.DIFF,
                                ComparationResultsItem.makeOne(
                                        in, out,
                                        ComparationResult.DiffKind.PROPERTY,
                                        descr("DIFF", "Kind", in, in.getKind(), out, out.getKind()))
                        ).add(compareTypes(in, out, in.getTypes(), out.getTypes())
                        ).add(() -> {

                            ChainChecker slChk = new ChainChecker();
                            inIndex.getSlaves(in).stream().forEach((slaveObjIn) -> {
                                outIndex.getSlaves(out).forEach((slaveObjOut) -> {
                                    if (getFullDescription(slaveObjOut).equals(getFullDescription(slaveObjIn))) {
                                        slChk.add(compare(slaveObjIn, slaveObjOut));
                                    }
                                });
                            });
                            return slChk.done();
                        }).done()
                );
            });
        });

    }

    public ComparationResult compareTypes(Object inObj, Object outObj, Types in, Types out) {

        if (!ruleCacheInited) {
            initRuleCache();
        }

        final String cacheKey = getObjectPairKey(in, out);

        log.debug("Compare types: {} : {}, {} : {}", getDescription(inObj), in.getRow().get(0).getType(), getDescription(outObj), out.getRow().get(0).getType());
        final ComparationResult cached = comparedDeepCache.get(cacheKey);
        log.debug(
                "Cache: {}, {}",
                cacheKey,
                cached == null ? null
                        : cached.getStatus().name() + " ("
                        + cached.getResultItems().stream()
                        .map(
                                (itm) -> itm.getDiffKind().name()
                        ).reduce("", (acc, itm) -> (acc.isEmpty() ? itm : ", " + itm))
        );

        return doWithModeCheck(glMapping, in, out, () -> {
            return viaCache(cacheKey, Optional.ofNullable(comparedDeepCache.get(cacheKey)).orElseGet(() -> {
                return (new ChainChecker()).add(() -> {
                    List<String> inList = in.getRow().stream()
                            .map((row) -> row.getType()).sorted().collect(Collectors.toList());
                    List<String> outList = out.getRow().stream()
                            .map((row) -> row.getType()).sorted().collect(Collectors.toList());
                    ChainChecker subChain = new ChainChecker();

                    inList.stream().forEach((type) -> {
                        CatalogObjectObj typeObjIn = deRefAsObjStatic(type).orElse(EMPTY);
                        CatalogObjectObj typeObjOut = deRefAsObjStatic(outList.get(inList.indexOf(type))).orElse(EMPTY);
                        log.debug("Compare Types-part: {}, {}", typeObjIn.getType(), typeObjOut.getType());
                        ComparationResult cachedRes = comparedDeepCache.get(getObjectPairKey(typeObjIn, typeObjOut));
                        subChain.check(
                                (__) -> PRIME_TYPES_LIST.contains(typeObjIn.getType())
                                && PRIME_TYPES_LIST.contains(typeObjOut.getType())
                                && !typeObjIn.getType().equals(typeObjOut.getType()),
                                new ComparationResult(
                                        ComparationResult.Status.DIFF,
                                        ComparationResultsItem.makeOneAsList(
                                                in, out,
                                                ComparationResult.DiffKind.TYPE,
                                                descr("DIFF", "Types", in, typeObjIn.getType(), out, typeObjOut.getType())))
                        ).check(
                                (__) -> (CLASS_TYPES_LIST.contains(typeObjIn.getType())
                                && CLASS_TYPES_LIST.contains(typeObjOut.getType())),
                                () -> {
                                    return new ComparationResult(
                                            compare(typeObjIn, typeObjOut).getStatus(),
                                            ComparationResultsItem.makeOneAsList(
                                                    in, out,
                                                    ComparationResult.DiffKind.PROPERTY,
                                                    descr("CHAIN", "PROP", in, typeObjIn.getType(), out, typeObjOut.getType())));

                                }
                        );
                    });

//                    for (String type : outList) {
//                        subChain.check(
//                                (__) -> !type.equals(inList.get(outList.indexOf(type))),
//                                ComparationResult.Status.DIFF,
//                                new ComparationResultsItem(
//                                        new Object[]{in, out},
//                                        ComparationResult.DiffKind.TYPE,
//                                        descr("DIFF", "Types", inObj, in, outObj, out)
//                                )
//                        );
//
//                    }
                    return subChain.done();
                }).done();
            }));
        });

    }

    public ComparationResult compare(CatalogObjectValue inObj, CatalogObjectValue outObj) {

        if (!ruleCacheInited) {
            initRuleCache();
        }

        Object[] remaps = checkRemaps(inObj, outObj);
        CatalogObjectValue in = (CatalogObjectValue) remaps[0];
        CatalogObjectValue out = (CatalogObjectValue) remaps[1];

        if (!in.equals(inObj)) {
            log.info("Process remap {} -> {}", getFullDescription(inObj), getFullDescription(in));
        }

        if (!out.equals(outObj)) {
            log.info("Process remap {} -> {}", getFullDescription(outObj), getFullDescription(out));
        }

        final String cacheKey = getObjectPairKey(in, out);

        log.debug("Compare vals: {}, {}", in.getDescription(), out.getDescription());
        final ComparationResult cached = comparedDeepCache.get(cacheKey);
        log.debug(
                "Cache: {}, {}",
                cacheKey,
                cached == null ? null
                        : cached.getStatus().name() + " ("
                        + cached.getResultItems().stream()
                        .map(
                                (itm) -> itm.getDiffKind().name()
                        ).reduce("", (acc, itm) -> (acc.isEmpty() ? itm : ", " + itm))
        );

        return doWithModeCheck(glMapping, in, out, () -> {
            return Optional.ofNullable(comparedDeepCache.get(cacheKey)).orElseGet(() -> {
                return viaCache(
                        cacheKey,
                        new ChainChecker().check(
                                (__) -> !in.getCode().equals(out.getCode()),
                                ComparationResult.Status.DIFF,
                                ComparationResultsItem.makeOne(
                                        in, out,
                                        ComparationResult.DiffKind.PROPERTY,
                                        descr("DIFF", "Code", in, in.getCode(), out, out.getCode()))
                        ).add(compareTypes(in, out, in.getTypes(), out.getTypes())).done()
                );
            });
        });

    }

    public ComparationResult compare(Object inObj, Object outObj) {

        if (!ruleCacheInited) {
            initRuleCache();
        }

        Object[] remaps = checkRemaps(inObj, outObj);
        Object in = remaps[0];
        Object out = remaps[1];

        if (!in.equals(inObj)) {
            log.info("Process remap {} -> {}", getFullDescription(inObj), getFullDescription(in));
        }

        if (!out.equals(outObj)) {
            log.info("Process remap {} -> {}", getFullDescription(outObj), getFullDescription(out));
        }

        final String cacheKey = getObjectPairKey(in, out);

        final boolean classMatch = in.getClass().equals(out.getClass());

        log.debug(
                "Compare objects {} (ref: {}; type: {}) <=> {} (ref: {}; type: {})",
                getDescription(in), getRef(in), getTypesString(in),
                getDescription(out), getRef(out), getTypesString(out)
        );
        if (in == EMPTY || out == EMPTY) {
            log.info("Cant find mapping for one of the objects {}, {}", getFullDescription(in), getFullDescription(out));
            return doWithModeCheck(glMapping, in, out, () -> {
                return viaCache(
                        cacheKey,
                        new ComparationResult(
                                ComparationResult.Status.NO_MAP,
                                ComparationResultsItem.makeOneAsList(
                                        in, out,
                                        ComparationResult.DiffKind.TYPE,
                                        descr("MISS", "MAP", in, in, out, out))));
            });
        }
        log.debug("Classes of objects: {}, {}", in.getClass().getSimpleName(), out.getClass().getSimpleName());
        final ComparationResult cached = comparedDeepCache.get(cacheKey);
        log.debug(
                "Cache: {}, {}",
                cacheKey,
                cached == null ? null
                        : cached.getStatus().name() + " ("
                        + cached.getResultItems().stream()
                        .map(
                                (itm) -> itm.getDiffKind().name()
                        ).reduce("", (acc, itm) -> (acc.isEmpty() ? itm : ", " + itm))
        );

        return doWithModeCheck(glMapping, in, out, () -> {
            return Optional.ofNullable(comparedDeepCache.get(cacheKey)).orElseGet(() -> {
                return viaCache(
                        cacheKey,
                        new ChainChecker().check(
                                (__) -> !classMatch,
                                new ComparationResult(
                                        ComparationResult.Status.DIFF_CLASSES,
                                        ComparationResultsItem.makeOneAsList(
                                                in, out,
                                                ComparationResult.DiffKind.TYPE,
                                                descr("DIFF", "Class", in, in.getClass().getSimpleName(), out, out.getClass().getSimpleName()))),
                                () -> {
                                    String suff = getClassSuffix(out);
                                    log.debug("Class suffix: {}", suff);
                                    switch (suff) {
                                        case "Object":
                                            return viaCache(cacheKey, compare((CatalogObjectObj) in, (CatalogObjectObj) out));
                                        case "Property":
                                            return viaCache(cacheKey, compare((CatalogObjectProperty) in, (CatalogObjectProperty) out));
                                        case "Value":
                                            return viaCache(cacheKey, compare((CatalogObjectValue) in, (CatalogObjectValue) out));
                                        case "Types":
                                            return viaCache(cacheKey, compareTypes(EMPTY, EMPTY, (Types) in, (Types) out));
                                        default:
                                            return viaCache(
                                                    cacheKey,
                                                    new ComparationResult(
                                                            ComparationResult.Status.ERROR,
                                                            ComparationResultsItem.makeOneAsList(
                                                                    in, out,
                                                                    ComparationResult.DiffKind.TYPE,
                                                                    descr("ERROR", "ClassSuffix", EMPTY, "-", out, suff))));
                                    }
                                }
                        ).done()
                );
            });
        });

    }

    public ComparationResult compare(String inRef, String outRef) {

        if (!ruleCacheInited) {
            initRuleCache();
        }

        log.debug(
                "Compare objects {} (ref: {}) <=> {} (ref: {})",
                getDescription(deRefStatic(inRef).orElse(EMPTY)), inRef,
                getDescription(deRefStatic(outRef).orElse(EMPTY)), outRef
        );

        return doWithModeCheck(glMapping, deRefStatic(inRef).orElse(EMPTY), deRefStatic(outRef).orElse(EMPTY), () -> {
            return compare(deRefStatic(inRef).orElse(null), deRefStatic(outRef).orElse(null));
        });

    }

}
