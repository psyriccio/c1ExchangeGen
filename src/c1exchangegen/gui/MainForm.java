/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1c.meta.C1;
import c1exchangegen.PlaceKind;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.awt.FileDialog;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeModel;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.output.WriterOutputStream;
import org.pushingpixels.substance.api.SubstanceConstants;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.slf4j.LoggerFactory;

/**
 *
 * @author psyriccio
 */
public class MainForm extends javax.swing.JFrame {

    public static Logger log = (Logger) LoggerFactory.getLogger("c1Ex.Form");

    protected void appendToLogArea(CharSequence csq) {
        jLogArea.setText(jLogArea.getText() + csq);
    }

    protected void appendToLogArea(char c) {
        jLogArea.setText(jLogArea.getText() + c);
    }

    /**
     * Creates new form ConfigurationForm
     *
     * @param logger
     */
    public MainForm(Logger logger) throws UnsupportedEncodingException {
        initComponents();
        log.setLevel(Level.ALL);
        log.setAdditive(true);

        System.setOut(
                new PrintStream(
                        new WriterOutputStream(
                                new JTextAreaWriter(jLogArea), "UTF-8"), 
                        true, 
                        "UTF-8")
        );
        
//        System.setOut(
//                new PrintStream(
//                        new WriterOutputStream(
//                                new StringWriter(
//                                        new AppendableOutputStream(
//                                                new Appendable() {
//                                            @Override
//                                            public Appendable append(CharSequence csq) throws IOException {
//                                                appendToLogArea(csq);
//                                                return this;
//                                            }
//
//                                            @Override
//                                            public Appendable append(CharSequence csq, int start, int end) throws IOException {
//                                                appendToLogArea(csq.subSequence(start, end));
//                                                return this;
//                                            }
//
//                                            @Override
//                                            public Appendable append(char c) throws IOException {
//                                                appendToLogArea(c);
//                                                return this;
//                                            }
//                                        })), "UTF-8"), true, "UTF-8"));
        log.info("GUI module started");
    }

    public void setModels(TreeModel modelIn, TreeModel modelOut, TreeModel modelRes) {
        log.info("Setting models to view");
        if (modelIn != null) {
            log.info("Setting 'IN'-model");
            this.jConfTree1.putClientProperty(SubstanceLookAndFeel.FOCUS_KIND, SubstanceConstants.FocusKind.NONE);
            this.jConfTree1.putClientProperty(SubstanceLookAndFeel.TREE_SMART_SCROLL_ANIMATION_KIND, false);
            jConfTree1.setModel(modelIn);
        }
        if (modelOut != null) {
            log.info("Setting 'OUT'-model");
            this.jConfTree2.putClientProperty(SubstanceLookAndFeel.FOCUS_KIND, SubstanceConstants.FocusKind.NONE);
            this.jConfTree2.putClientProperty(SubstanceLookAndFeel.TREE_SMART_SCROLL_ANIMATION_KIND, false);
            jConfTree2.setModel(modelOut);
        }
        if (modelRes != null) {
            log.info("Setting 'RESULT'-model");
            this.jTreeResult.putClientProperty(SubstanceLookAndFeel.FOCUS_KIND, SubstanceConstants.FocusKind.NONE);
            this.jTreeResult.putClientProperty(SubstanceLookAndFeel.TREE_SMART_SCROLL_ANIMATION_KIND, false);
            jTreeResult.setModel(modelRes);
        }
    }

    protected void doLoadFileAction(PlaceKind place) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new FileNameExtensionFilter("*.xml", "xml"));
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setMultiSelectionEnabled(false);
        if(jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            c1exchangegen.C1ExchangeGen.startLoadWorker(jfc.getSelectedFile(), place);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBarMain = new javax.swing.JToolBar();
        jBtnLoadIn = new javax.swing.JButton();
        jBtnLoadOut = new javax.swing.JButton();
        jBtnLoadMapping = new javax.swing.JButton();
        jSP_Log_Main = new javax.swing.JSplitPane();
        jSP_InOut_Res = new javax.swing.JSplitPane();
        jSP_In_Out = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jConfTree1 = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        jConfTree2 = new javax.swing.JTree();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTreeResult = new javax.swing.JTree();
        jScrollPane4 = new javax.swing.JScrollPane();
        jLogArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("c1ExchangeGen");
        setPreferredSize(new java.awt.Dimension(1600, 800));

        jToolBarMain.setFloatable(false);
        jToolBarMain.setRollover(true);
        jToolBarMain.setMaximumSize(new java.awt.Dimension(88, 48));
        jToolBarMain.setMinimumSize(new java.awt.Dimension(88, 48));
        jToolBarMain.setPreferredSize(new java.awt.Dimension(88, 48));

        jBtnLoadIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/c1exchangegen/icons/doc_import-6951412645.png"))); // NOI18N
        jBtnLoadIn.setToolTipText("Load 'IN'-model");
        jBtnLoadIn.setFocusTraversalPolicyProvider(true);
        jBtnLoadIn.setFocusable(false);
        jBtnLoadIn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBtnLoadIn.setIconTextGap(1);
        jBtnLoadIn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jBtnLoadIn.setMaximumSize(new java.awt.Dimension(36, 36));
        jBtnLoadIn.setMinimumSize(new java.awt.Dimension(36, 36));
        jBtnLoadIn.setPreferredSize(new java.awt.Dimension(36, 36));
        jBtnLoadIn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBtnLoadIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLoadInActionPerformed(evt);
            }
        });
        jToolBarMain.add(jBtnLoadIn);

        jBtnLoadOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/c1exchangegen/icons/doc_export-6942615315.png"))); // NOI18N
        jBtnLoadOut.setToolTipText("Load 'OUT'-model");
        jBtnLoadOut.setFocusable(false);
        jBtnLoadOut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBtnLoadOut.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jBtnLoadOut.setMaximumSize(new java.awt.Dimension(36, 36));
        jBtnLoadOut.setMinimumSize(new java.awt.Dimension(36, 36));
        jBtnLoadOut.setPreferredSize(new java.awt.Dimension(36, 36));
        jBtnLoadOut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jBtnLoadOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLoadOutActionPerformed(evt);
            }
        });
        jToolBarMain.add(jBtnLoadOut);

        jBtnLoadMapping.setIcon(new javax.swing.ImageIcon(getClass().getResource("/c1exchangegen/icons/share-6957213308.png"))); // NOI18N
        jBtnLoadMapping.setToolTipText("Load mapping rules");
        jBtnLoadMapping.setFocusable(false);
        jBtnLoadMapping.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jBtnLoadMapping.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jBtnLoadMapping.setMaximumSize(new java.awt.Dimension(36, 36));
        jBtnLoadMapping.setMinimumSize(new java.awt.Dimension(36, 36));
        jBtnLoadMapping.setPreferredSize(new java.awt.Dimension(36, 36));
        jBtnLoadMapping.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBarMain.add(jBtnLoadMapping);

        jSP_Log_Main.setDividerLocation(600);
        jSP_Log_Main.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSP_Log_Main.setResizeWeight(1.0);
        jSP_Log_Main.setToolTipText("");
        jSP_Log_Main.setContinuousLayout(true);

        jSP_InOut_Res.setDividerLocation(1000);

        jSP_In_Out.setDividerLocation(500);

        jConfTree1.setModel(null);
        jScrollPane2.setViewportView(jConfTree1);

        jSP_In_Out.setLeftComponent(jScrollPane2);

        jConfTree2.setModel(null);
        jConfTree2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(jConfTree2);
        jConfTree2.getAccessibleContext().setAccessibleName("jTree");

        jSP_In_Out.setRightComponent(jScrollPane1);

        jSP_InOut_Res.setLeftComponent(jSP_In_Out);

        jTreeResult.setModel(null);
        jTreeResult.setToolTipText("");
        jScrollPane3.setViewportView(jTreeResult);

        jSP_InOut_Res.setRightComponent(jScrollPane3);

        jSP_Log_Main.setTopComponent(jSP_InOut_Res);

        jLogArea.setEditable(false);
        jLogArea.setColumns(20);
        jLogArea.setFont(new java.awt.Font("Lucida Console", 0, 15)); // NOI18N
        jLogArea.setRows(5);
        jLogArea.setTabSize(4);
        jLogArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLogAreaMouseClicked(evt);
            }
        });
        jLogArea.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jLogAreaComponentResized(evt);
            }
        });
        jScrollPane4.setViewportView(jLogArea);

        jSP_Log_Main.setRightComponent(jScrollPane4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSP_Log_Main, javax.swing.GroupLayout.DEFAULT_SIZE, 1596, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBarMain, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBarMain, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSP_Log_Main, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jLogAreaComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jLogAreaComponentResized
        jLogArea.setCaretPosition(jLogArea.getText().length());
    }//GEN-LAST:event_jLogAreaComponentResized

    private void jLogAreaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLogAreaMouseClicked
        if(evt.getClickCount() > 1) jLogArea.setCaretPosition(jLogArea.getText().length());
    }//GEN-LAST:event_jLogAreaMouseClicked

    private void jBtnLoadInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLoadInActionPerformed
        doLoadFileAction(PlaceKind.PLACE_IN);
    }//GEN-LAST:event_jBtnLoadInActionPerformed

    private void jBtnLoadOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLoadOutActionPerformed
        doLoadFileAction(PlaceKind.PLACE_OUT);
    }//GEN-LAST:event_jBtnLoadOutActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnLoadIn;
    private javax.swing.JButton jBtnLoadMapping;
    private javax.swing.JButton jBtnLoadOut;
    private javax.swing.JTree jConfTree1;
    private javax.swing.JTree jConfTree2;
    private javax.swing.JTextArea jLogArea;
    private javax.swing.JSplitPane jSP_InOut_Res;
    private javax.swing.JSplitPane jSP_In_Out;
    private javax.swing.JSplitPane jSP_Log_Main;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar jToolBarMain;
    private javax.swing.JTree jTreeResult;
    // End of variables declaration//GEN-END:variables

}
