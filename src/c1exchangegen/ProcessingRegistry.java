/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static c1exchangegen.ObjectIndex.getRef;
import static c1exchangegen.C1ExchangeGen.log;
import static c1exchangegen.ObjectIndex.getDescription;

/**
 *
 * @author psyriccio
 */
public class ProcessingRegistry {

    private final HashMap<String, ProcessingEntry> processedRefs;
    private final List<ProcessingEntry> entries;

    public ProcessingRegistry() {
        this.processedRefs = new HashMap<>();
        this.entries = new ArrayList<>();
    }
    
    public List<ProcessingEntry> getEntries() {
        return entries;
    }

    public HashMap<String, ProcessingEntry> getProcessedRefs() {
        return processedRefs;
    }

    public void add(ProcessingEntry entry) {
        if(processedRefs.containsKey(getRef(entry.getInObject()))) {
            log.warn("Reference already present in processing registry {}, ref: {}", getDescription(entry.getInObject()), getRef(entry.getInObject()));
        }
        if(processedRefs.containsKey(getRef(entry.getOutObject()))) {
            log.warn("Reference already present in processing registry {}, ref: {}", getDescription(entry.getOutObject()), getRef(entry.getOutObject()));
        }
        processedRefs.put(getRef(entry.getInObject()), entry);
        processedRefs.put(getRef(entry.getOutObject()), entry);
        entries.add(entry);
    }
    
}
