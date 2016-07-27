/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import java.util.UUID;

/**
 *
 * @author psyriccio
 */
public class MappingSchemaBase {
    
    private final UUID uid;

    public MappingSchemaBase() {
        this.uid = UUID.randomUUID();
    }

    public MappingSchemaBase(UUID uid) {
        this.uid = uid;
    }

    public UUID getUid() {
        return uid;
    }
    
}
