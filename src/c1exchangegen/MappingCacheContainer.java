/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import c1exchangegen.generated.Mapping.Map;
import c1exchangegen.generated.Mapping.Map.Rule;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author psyriccio
 */
public class MappingCacheContainer {

    private HashMap<Object, List<Map>> mapCache;
    private HashMap<Object, List<Rule>> ruleCache;
    private HashMap<Rule, Map> reverseMapCache;

    public MappingCacheContainer() {
        this.mapCache = new HashMap<>();
        this.reverseMapCache = new HashMap<>();
        this.ruleCache = new HashMap<>();
    }

    public MappingCacheContainer(HashMap<Object, List<Map>> mapCache, HashMap<Object, List<Rule>> ruleCache, HashMap<Rule, Map> reverseMapCache) {
        this.mapCache = mapCache;
        this.ruleCache = ruleCache;
        this.reverseMapCache = reverseMapCache;
    }

    public HashMap<Object, List<Map>> getMapCache() {
        return mapCache;
    }

    public HashMap<Rule, Map> getReverseMapCache() {
        return reverseMapCache;
    }

    public HashMap<Object, List<Rule>> getRuleCache() {
        return ruleCache;
    }

    public void setMapCache(HashMap<Object, List<Map>> mapCache) {
        this.mapCache = mapCache;
    }

    public void setReverseMapCache(HashMap<Rule, Map> reverseMapCache) {
        this.reverseMapCache = reverseMapCache;
    }

    public void setRuleCache(HashMap<Object, List<Rule>> ruleCache) {
        this.ruleCache = ruleCache;
    }
    
}
