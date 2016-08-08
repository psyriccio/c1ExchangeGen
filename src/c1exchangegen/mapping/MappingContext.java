/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.mapping;

import c1c.meta.generated.MetaObject;
import c1exchangegen.generated.Mapping;
import java.util.List;

/**
 *
 * @author psyriccio
 */
public class MappingContext {

    public static Mapping MAPPING;
    
    private List<MetaObject> processed;
    private List<MetaObject> dependencies;

    public MappingContext() {
    }

    public List<MetaObject> getProcessed() {
        return processed;
    }

    public List<MetaObject> getDependencies() {
        return dependencies;
    }
    
    
    public int compareObjects(MetaObject objIn, MetaObject objOut) {

        
        
        return 0;
    }
    
    
    
}
