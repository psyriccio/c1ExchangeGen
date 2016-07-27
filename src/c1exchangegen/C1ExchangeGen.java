/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import c1exchangegen.generated.Mapping;
import c1meta.Conf;
import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static c1exchangegen.ObjectIndex.*;
import c1exchangegen.codegen.CodeGenerator;
import c1exchangegen.gui.ConfigurationForm;
import c1exchangegen.gui.ObjectIndexTreeModel;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.GraphiteSkin;

/**
 *
 * @author psyriccio
 */
public class C1ExchangeGen {

    public static Logger log = LoggerFactory.getLogger("~");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JAXBException, UnsupportedLookAndFeelException, TemplateException, IOException {

        if (args.length == 0) {
            args = "map;./alucom.xml;./resurs.xml;./map.xml".split(";");
        }

        if (args.length != 4) {
            System.err.println("usage: c1ExchangeGen [command] [in] [out] [params...]");
            System.exit(1);
        }

        if (args[0].equals("test")) {
            CodeGenerator codeGen = new CodeGenerator();
            System.out.println(
                    codeGen.proc(
                            "Тест",
                            new String[]{"Пар1", "Пар2", "Пар3"},
                            true, true,
                            new String[]{
                                "//Тест тестовая функция, сгенерирована c1ExchangeGen",
                                codeGen.forEach(
                                        "СтрСтр",
                                        "Список",
                                        new String[]{
                                            "СтрСтр.Поле = \"Олололо\";"
                                        }),
                                "Сообщить(\"Тест! c1ExchangeGen работает!\" + Пар1 + Пар2 + Пар3);",
                                "Предупреждение(\"Олололо!\");",
                                codeGen.structConstruct(
                                        "Стрк",
                                        CodeGenerator.buildHashMap()
                                        .add("Тест1", "это просто строка")
                                        .add("Тест2", 20.0)
                                        .add("ЗначениеПараметра1", "~!Пар1")
                                        .add(
                                                "ВложСтрукт",
                                                CodeGenerator.buildHashMap()
                                                .add("Тест1", true)
                                                .add("Тест2", new Date())
                                                .add(
                                                        "ЕщёВложение",
                                                        CodeGenerator.buildHashMap()
                                                        .add("Глубоко1", "так губоко еще никто не забирался")
                                                        .add("ЗакончиласьФантазия", true)
                                                        .done()
                                                ).done()
                                        ).done()
                                )
                            }
                    ));
        }

        if (args[0].equalsIgnoreCase("map") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("match") || args[0].equalsIgnoreCase("gui")) {

            log.info("Initialize JAXB contexts...");
            JAXBContext xmlContext = JAXBContext.newInstance("c1meta");
            Unmarshaller xmlUnm = xmlContext.createUnmarshaller();
            JAXBContext mapContext = JAXBContext.newInstance("c1exchangegen.generated");
            Unmarshaller mapUnm = mapContext.createUnmarshaller();

            log.info("Loading models...");
            Conf inConf = (Conf) xmlUnm.unmarshal(new File(args[1]));
            log.info("Loaded configuration (in): '" + inConf.getCatalogObjectConf().getDescription() + ":" + inConf.getCatalogObjectConf().getVersion() + "', contains " + Integer.toString(inConf.getObjects().size()) + " objects");

            Conf outConf = (Conf) xmlUnm.unmarshal(new File(args[2]));
            log.info("Loaded configuration (out): '" + outConf.getCatalogObjectConf().getDescription() + ":" + outConf.getCatalogObjectConf().getVersion() + "', contains " + Integer.toString(outConf.getObjects().size()) + " objects");

            log.info("Building index...");
            ObjectIndex inIdx = new ObjectIndex(inConf);
            ObjectIndex outIdx = new ObjectIndex(outConf);
            log.info("Index builded. Total object count: " + Integer.toString(ObjectIndex.getIndexRefsStatic().size()));

            if (args[0].equalsIgnoreCase("match")) {
                List<Object> lstIn = ObjectIndex.findMatches(inIdx, args[3]);
                List<Object> lstOut = ObjectIndex.findMatches(outIdx, args[3]);
                lstIn.forEach((obj) -> {
                    log.info(
                            "in: {}, class: {}, ref: {}, owner: {}, types: {}",
                            getDescription(obj),
                            getClassSuffix(obj),
                            getRef(obj),
                            getDescription(getOwner(obj).orElse(ObjectIndex.EMPTY)),
                            getTypesString(obj)
                    );
                });
                lstOut.forEach((obj) -> {
                    log.info(
                            "out: {}, class: {}, ref: {}, owner: {}, types: {}",
                            getDescription(obj),
                            getClassSuffix(obj),
                            getRef(obj),
                            getDescription(getOwner(obj).orElse(ObjectIndex.EMPTY)),
                            getTypesString(obj)
                    );
                });
                System.exit(0);
            }

            Mapping mapping = (Mapping) mapUnm.unmarshal(new File(args[3]));
            log.info("Loaded mapping file! (contains " + Integer.toString(mapping.getMap().size()) + " mapping entries)");

            if (args[0].equalsIgnoreCase("list")) {
                inIdx.getIndexDescription().forEach((descr, obj) -> {
                    log.info(
                            "in: {}, class: {}, ref: {}, owner: {}, types: {}",
                            getDescription(obj),
                            getClassSuffix(obj),
                            getRef(obj),
                            getDescription(getOwner(obj).orElse(ObjectIndex.EMPTY)),
                            getTypesString(obj)
                    );
                });
                inIdx.getIndexDescription().forEach((descr, obj) -> {
                    log.info(
                            "out: {}, class: {}, ref: {}, owner: {}, types: {}",
                            getDescription(obj),
                            getClassSuffix(obj),
                            getRef(obj),
                            getDescription(getOwner(obj).orElse(ObjectIndex.EMPTY)),
                            getTypesString(obj)
                    );
                });
                System.exit(0);
            }

            if (args[0].equalsIgnoreCase("gui")) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GraphiteSkin graphiteSkin = new GraphiteSkin();
                        SubstanceLookAndFeel.setSkin(graphiteSkin);
                        //
                        new ConfigurationForm(new ObjectIndexTreeModel(inIdx)).setVisible(true);
                    }
                });
            }

            ProcessingRegistry reg = new ProcessingRegistry();
            ArrayList<ProcessingEntry> prcBuf = new ArrayList<ProcessingEntry>();

            for (Mapping.Map map : mapping.getMap()) {
                ObjectComparator comparator = new ObjectComparator(mapping, map, inIdx, outIdx);
                Object in = ObjectIndex.findObject(inIdx, map.getIn()).orElse(EMPTY);
                Object out = ObjectIndex.findObject(outIdx, map.getOut()).orElse(EMPTY);
                ComparationResult result = comparator.compare(in, out);
                log.info("{} ({}) <=> {} ({}) : {}", map.getIn(), in, map.getOut(), out, result.getStatus());
                result.getResultItems().forEach((resItem) -> {
                    log.info(
                            "    {} ({}) : {} :> {}",
                            getDescription(resItem.getObjectIn()) + ", "
                            + getDescription(resItem.getObjectOut()),
                            resItem.getWhere(),
                            resItem.getDiffKind(),
                            resItem.getDescription()
                    );
                });

            }

        }

//        for(Object obj : objects) {
//            if(obj instanceof CatalogObjectObj) {
//                CatalogObjectObj cObj = (CatalogObjectObj) obj;
//                System.out.println("Obj: " + cObj.getName());
//            }
//            if(obj instanceof CatalogObjectProperty) {
//                CatalogObjectProperty cObj = (CatalogObjectProperty) obj;
//                System.out.println("Prop: " + cObj.getDescription() + ", " + cObj.getKind() + " in " + cObj.getOwner().getContent());
//            }
//            if(obj instanceof CatalogObjectValue) {
//                CatalogObjectValue cObj = (CatalogObjectValue) obj;
//                System.out.println("Val: " + cObj.getDescription() + ", " + cObj.getTypes().toString() + " in " + cObj.getOwner().getContent());
//            }
//        }
    }

}
