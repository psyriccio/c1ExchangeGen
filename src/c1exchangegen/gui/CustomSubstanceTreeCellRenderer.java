/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.TreeCellRenderer;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultTreeCellRenderer;

/**
 *
 * @author psyriccio
 */
public class CustomSubstanceTreeCellRenderer implements TreeCellRenderer {
    
    private final int stsClAdd = 20;
    private final SubstanceDefaultTreeCellRenderer defRenderer;

    public CustomSubstanceTreeCellRenderer() {
        defRenderer = new SubstanceDefaultTreeCellRenderer();
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component defComp = defRenderer.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus);
        JLabel label = (JLabel) defComp;
        if(value instanceof C1ConfigurationTreeNode) {
            C1ConfigurationTreeNode node = (C1ConfigurationTreeNode) value;
            label.setToolTipText(node.getObj().getFullName());
            if(node.getState() == C1ConfigurationTreeNode.NodeState.Good) {
                Color bg = label.getBackground();
                label.setBackground(
                        new Color(
                                bg.getRed() >= stsClAdd ? bg.getRed() - stsClAdd : 0, 
                                bg.getGreen() <= 255-stsClAdd ? bg.getGreen() + stsClAdd : 255, 
                                bg.getBlue() >= stsClAdd ? bg.getBlue() - stsClAdd : 0, 
                                bg.getAlpha()));
            }
            if(node.getState() == C1ConfigurationTreeNode.NodeState.Warning) {
                Color bg = label.getBackground();
                label.setBackground(
                        new Color(
                                bg.getRed() <= 255-stsClAdd ? bg.getRed() + stsClAdd : 255, 
                                bg.getGreen() <= 255-stsClAdd ? bg.getGreen() + stsClAdd : 255, 
                                bg.getBlue() >= stsClAdd ? bg.getBlue() - stsClAdd : 0, 
                                bg.getAlpha()));
            }
            if(node.getState() == C1ConfigurationTreeNode.NodeState.Error) {
                Color bg = label.getBackground();
                label.setBackground(
                        new Color(
                                bg.getRed() <= 255-stsClAdd ? bg.getRed() + stsClAdd : 255, 
                                bg.getGreen() >= stsClAdd ? bg.getGreen() - stsClAdd : 0, 
                                bg.getBlue() >= stsClAdd ? bg.getBlue() - stsClAdd : 0, 
                                bg.getAlpha()));
            }
        }
        return label;
    }
    
}
