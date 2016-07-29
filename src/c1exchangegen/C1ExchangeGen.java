/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen;

import c1c.meta.C1;
import c1c.meta.generated.Conf;
import java.io.File;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import c1exchangegen.codegen.CodeGenerator;
import c1exchangegen.gui.C1ConfigurationTreeModel;
import c1exchangegen.gui.ConfigurationForm;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Date;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTreeCellRenderer;
import org.pushingpixels.substance.api.skin.GraphiteSkin;

/**
 *
 * @author psyriccio
 */
public class C1ExchangeGen {

    public static Logger log = LoggerFactory.getLogger("~");

    public static Conf IN_CONF;
    public static Conf OUT_CONF;

    public static void exceptionConsumed(Exception ex) {
        log.error("Exception consumed: ", ex);
        if(ex instanceof JAXBException) {
            JAXBException jex = (JAXBException) ex;
            log.error(":", jex);
            log.error("{} \n {} \n {} \n", jex.getClass(), jex.getErrorCode(), jex.getMessage());
            log.error("{} \n {} \n {} \n", jex.getCause(), jex.getLinkedException(), jex.getStackTrace());
            if(ex instanceof IllegalAnnotationException) {
                IllegalAnnotationException aex = (IllegalAnnotationException) jex;
                log.error("::", aex);
                log.error(aex.toString());
            }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JAXBException, UnsupportedLookAndFeelException, TemplateException, IOException {

        C1.setExceptionsConsumer(C1ExchangeGen::exceptionConsumed);
        
        if (args.length == 0) {
            args = "map;./alucom.xml;./resurs.xml;./map.xml".split(";");
        }

        if (args.length != 4) {
            System.err.println("usage: c1ExchangeGen [command] [in] [out] [params...]");
            System.runFinalization();
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

            log.info("Loading models...");
            Conf inConf = C1.loadConfiguration(
                    new File(args[1]))
                    .orElseThrow(
                            () -> {
                                return new RuntimeException("Cant load configuration (1)");
                            });

            log.info("Loaded configuration (in): '" + inConf.getName() + ":" + "" + "', contains " + Integer.toString(inConf.getChildrens().size()) + " objects");

            Conf outConf = C1.loadConfiguration(
                    new File(args[2]))
                    .orElseThrow(
                            () -> {
                                return new RuntimeException("Cant load configuration (2)");
                            });

            log.info("Loaded configuration (out): '" + outConf.getName() + ":" + "" + "', contains " + Integer.toString(outConf.getChildrens().size()) + " objects in root");

            IN_CONF = inConf;
            OUT_CONF = outConf;

            log.info("Building index...");
            //ObjectIndex inIdx = new ObjectIndex(inConf);
            //ObjectIndex outIdx = new ObjectIndex(outConf);
            log.info("Index builded. Total object count: " + C1.getALL(inConf).size() + C1.getALL(outConf).size());

            if (args[0].equalsIgnoreCase("gui")) {
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        GraphiteSkin graphiteSkin = new GraphiteSkin();
                        SubstanceLookAndFeel.setSkin(graphiteSkin);
                        new ConfigurationForm(new C1ConfigurationTreeModel(IN_CONF), new C1ConfigurationTreeModel(OUT_CONF), null).setVisible(true);
                    }
                });
            }

        }
    }

}