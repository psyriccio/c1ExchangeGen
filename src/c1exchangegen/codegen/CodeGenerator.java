/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.codegen;

import freemarker.core.ParseException;
import freemarker.core.PlainTextOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

/**
 *
 * @author psyriccio
 */
public class CodeGenerator {

    public static HashMapBuilder buildHashMap() {
        return new HashMapBuilder();
    }

    public static class HashMapBuilder {

        private final HashMap<String, Object> map;

        public HashMapBuilder() {
            this.map = new HashMap<>();
        }

        public HashMapBuilder(HashMap<String, Object> map) {
            this.map = map;
        }

        public HashMapBuilder add(String name, Object value) {
            this.map.put(name, value);
            return this;
        }

        public HashMapBuilder addAll(HashMap<String, Object> map) {
            this.map.putAll(map);
            return this;
        }

        public HashMap<String, Object> done() {
            return this.map;
        }

    }

    private final Configuration fm;
    private final Template procTpl;
    private final Template sructConstructTpl;
    private final Template forEachTpl;

    public CodeGenerator() throws MalformedTemplateNameException, ParseException, IOException {
        this.fm = new Configuration(Configuration.VERSION_2_3_25);
        this.fm.setIncompatibleImprovements(Configuration.VERSION_2_3_25);
        this.fm.setClassForTemplateLoading(c1exchangegen.C1ExchangeGen.class, "templates/");
        this.fm.setDefaultEncoding("UTF-8");
        this.fm.setWhitespaceStripping(true);
        this.fm.setNumberFormat("0.###");
        this.fm.setOutputFormat(PlainTextOutputFormat.INSTANCE);
        this.fm.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.fm.setLogTemplateExceptions(true);
        procTpl = this.fm.getTemplate("proc.ftl");
        sructConstructTpl = this.fm.getTemplate("struct_construct.ftl");
        forEachTpl = this.fm.getTemplate("foreach.ftl");
    }

    public Configuration getFm() {
        return fm;
    }
    
    protected String processTemplate(String name, HashMap<String, Object> data) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        Template template = fm.getTemplate(name);
        template.process(data, writer);
        writer.flush();
        return writer.toString();
    }

    protected String processTemplate(Template template, HashMap<String, Object> data) throws TemplateException, IOException {
        StringWriter writer = new StringWriter();
        template.process(data, writer);
        writer.flush();
        return writer.toString();
    }

    public String proc(String name, String[] params, boolean exported, boolean isFunc, String[] lines) throws TemplateException, IOException {
        return processTemplate(
                procTpl,
                buildHashMap()
                .add("name", name)
                .add("params", params)
                .add("isExport", exported)
                .add("isFunc", isFunc)
                .add("lines", lines)
                .done());
    }

    public String structConstruct(String name, HashMap<String, Object> map) throws TemplateException, IOException {
        return processTemplate(
                sructConstructTpl,
                buildHashMap()
                .add("name", name)
                .add("map", map)
                .done());
    }

    public String forEach(String item, String container, String[] lines) throws TemplateException, IOException {
        return processTemplate(
                forEachTpl,
                buildHashMap()
                .add("item", item)
                .add("container", container)
                .add("lines", lines)
                .done());
    }

}
