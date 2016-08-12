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
import c1exchangegen.gui.C1ConfigurationTreeModel;
import c1exchangegen.gui.MainForm;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import com.sun.xml.bind.v2.runtime.IllegalAnnotationException;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import javax.swing.UnsupportedLookAndFeelException;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.GraphiteSkin;
import org.slf4j.LoggerFactory;
import static org.slf4j.LoggerFactory.getLogger;

/**
 *
 * @author psyriccio
 */
public class C1ExchangeGen {

    public static MainForm MAIN_FORM;

    public static Logger log = (Logger) getLogger("c1Ex");

    public static Conf IN_CONF;
    public static Conf OUT_CONF;

    public static void exceptionConsumed(Exception ex) {
        log.error("Exception consumed: ", ex);
        if (ex instanceof JAXBException) {
            JAXBException jex = (JAXBException) ex;
            log.error(":", jex);
            log.error("{} \n {} \n {} \n", jex.getClass(), jex.getErrorCode(), jex.getMessage());
            log.error("{} \n {} \n {} \n", jex.getCause(), jex.getLinkedException(), jex.getStackTrace());
            if (ex instanceof IllegalAnnotationException) {
                IllegalAnnotationException aex = (IllegalAnnotationException) jex;
                log.error("::", aex);
                log.error(aex.toString());
            }
        }
    }

    public static Thread startLoadWorker(File fl, PlaceKind place, Consumer<Integer> prcConsumer) {
        if (place != PlaceKind.PLACE_IN && place != PlaceKind.PLACE_OUT) {
            return null;
        }
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("Worker started {}", Thread.currentThread());
                try {
                    log.info("Loading model from {}", fl);
                    if (place == PlaceKind.PLACE_IN) {
                        c1exchangegen.C1ExchangeGen.IN_CONF = C1.loadConfiguration(fl, (prc) -> {
                            try {
                                java.awt.EventQueue.invokeAndWait(new Runnable() {
                                    @Override
                                    public void run() {
                                        prcConsumer.accept(prc);
                                    }
                                });
                            } catch (InterruptedException | InvocationTargetException ex) {
                                log.error("Exception: ", ex);
                            }
                        }).orElse(null);
                    } else {
                        c1exchangegen.C1ExchangeGen.OUT_CONF = C1.loadConfiguration(fl, (prc) -> {
                            try {
                                java.awt.EventQueue.invokeAndWait(new Runnable() {
                                    @Override
                                    public void run() {
                                        prcConsumer.accept(prc);
                                    }
                                });
                            } catch (InterruptedException | InvocationTargetException ex) {
                                log.error("Exception: ", ex);
                            }
                        }).orElse(null);
                    }
                } catch (JAXBException ex) {
                    log.error("Exception: ", ex);
                }
                if ((place == PlaceKind.PLACE_IN && c1exchangegen.C1ExchangeGen.IN_CONF == null)
                        || (place == PlaceKind.PLACE_OUT && c1exchangegen.C1ExchangeGen.OUT_CONF == null)) {
                    log.error("Eroor loading model");
                } else {
                    log.info("Model loaded, opening in editor...");
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            log.info("Setting model");
                            c1exchangegen.C1ExchangeGen.MAIN_FORM.setModels(
                                    place == PlaceKind.PLACE_IN
                                            ? new C1ConfigurationTreeModel(
                                                    c1exchangegen.C1ExchangeGen.IN_CONF)
                                            : null,
                                    place == PlaceKind.PLACE_OUT
                                            ? new C1ConfigurationTreeModel(
                                                    c1exchangegen.C1ExchangeGen.OUT_CONF)
                                            : null,
                                    null);
                        }
                    });
                    log.info("Worker done {}", Thread.currentThread());
                }
            }
        }, "WRK:" + Long.toHexString(Math.round((Math.random() * 1000000) + 65535)).substring(1, 3));
        log.info("Starting worker {}", worker);
        worker.start();
        return worker;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws JAXBException, UnsupportedLookAndFeelException, TemplateException, IOException, InterruptedException, InvocationTargetException {

        C1.setExceptionsConsumer(C1ExchangeGen::exceptionConsumed);

        // assume SLF4J is bound to logback in the current environment
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.print(lc);

        if (args.length == 0) {
            args = "gui;./alucom.xml;./resurs.xml;./map.xml".split(";");
        }

        if (args.length != 4) {
            System.err.println("usage: c1ExchangeGen [command] [in] [out] [params...]");
            System.runFinalization();
            System.exit(1);
        }

        if (args[0].equalsIgnoreCase("gui")) {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Thread.currentThread().setName("UI");
                    GraphiteSkin graphiteSkin = new GraphiteSkin();
                    SubstanceLookAndFeel.setSkin(graphiteSkin);
                    try {
                        MAIN_FORM = new MainForm(log);
                    } catch (UnsupportedEncodingException ex) {
                        log.error("Exception: ", ex);
                    }
                    MAIN_FORM.setVisible(true);
                }
            });
        }

        if (args[0].equalsIgnoreCase("map") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("match")) {

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
            log.info("Index builded. Total object count: {}", (C1.getALL(inConf).size() + C1.getALL(outConf).size()));

//            if(args[0].equalsIgnoreCase("gui")) {
//                java.awt.EventQueue.invokeLater(() -> {
//                    MAIN_FORM.setModels(new C1ConfigurationTreeModel(IN_CONF), new C1ConfigurationTreeModel(OUT_CONF), null);
//                });
//            }
        }
    }

}
