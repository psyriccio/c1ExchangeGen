//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.27 at 08:41:24 AM MSK 
//


package c1exchangegen.generated.impl;

import javax.xml.bind.annotation.XmlRegistry;
import c1exchangegen.generated.Mapping;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the c1exchangegen.generated.impl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: c1exchangegen.generated.impl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Mapping }
     * 
     */
    public MappingImpl createMapping() {
        return new MappingImpl();
    }

    /**
     * Create an instance of {@link Mapping.Map }
     * 
     */
    public MappingImpl.MapImpl createMappingMap() {
        return new MappingImpl.MapImpl();
    }

    /**
     * Create an instance of {@link Mapping.Map.Rule }
     * 
     */
    public MappingImpl.MapImpl.RuleImpl createMappingMapRule() {
        return new MappingImpl.MapImpl.RuleImpl();
    }

}
