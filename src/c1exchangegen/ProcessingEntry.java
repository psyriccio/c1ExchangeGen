/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

/**
 *
 * @author psyriccio
 */
public class ProcessingEntry {

    private final Object inObject;
    private final Object outObject;
    private MappingMode mappingKind;
    private final Object inOwner;
    private final Object outOwner;
    private final ProcessingEntry parent;

    public ProcessingEntry(ProcessingEntry parent, Object inObject, Object outObject, Object inOwner, Object outOwner, MappingMode mappingKind) {
        this.inObject = inObject;
        this.outObject = outObject;
        this.mappingKind = mappingKind;
        this.inOwner = inOwner;
        this.outOwner = outOwner;
        this.parent = parent;
    }

    public Object getInObject() {
        return inObject;
    }
    
    public MappingMode getMappingKind() {
        return mappingKind;
    }

    public void setMappingKind(MappingMode mappingKind) {
        this.mappingKind = mappingKind;
    }

    public Object getOutObject() {
        return outObject;
    }

    public Object getInOwner() {
        return inOwner;
    }

    public Object getOutOwner() {
        return outOwner;
    }

    public ProcessingEntry getParent() {
        return parent;
    }
    
}


