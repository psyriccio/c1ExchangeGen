//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.20 at 05:47:12 PM MSK 
//


package c1exchangegen;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element name="rule" maxOccurs="unbounded" form="unqualified">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="from" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="to" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rule"
})
@XmlRootElement(name = "Mapping")
public class Mapping {

    protected List<Mapping.Rule> rule;
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Gets the value of the rule property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rule property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRule().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mapping.Rule }
     * 
     * 
     */
    public List<Mapping.Rule> getRule() {
        if (rule == null) {
            rule = new ArrayList<Mapping.Rule>();
        }
        return this.rule;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="from" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="to" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Rule {

        @XmlAttribute(name = "from", required = true)
        protected String from;
        @XmlAttribute(name = "to", required = true)
        protected String to;

        /**
         * Gets the value of the from property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getFrom() {
            return from;
        }

        /**
         * Sets the value of the from property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setFrom(String value) {
            this.from = value;
        }

        /**
         * Gets the value of the to property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTo() {
            return to;
        }

        /**
         * Sets the value of the to property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTo(String value) {
            this.to = value;
        }

    }

}
