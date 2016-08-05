/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.mapping;

/**
 *
 * @author psyriccio
 */
public interface NodeStateContainer {

    public enum NodeState {
        Normal, Good, Warning, Error, Inactive
    }

    public MappingNode.NodeState getState();
    public void setState(MappingNode.NodeState value);
    
}
