/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c1exchangegen.gui;

import c1exchangegen.mapping.MappingNode;
import c1exchangegen.mapping.NodeStateContainer;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTree;
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

    private int checkByteBounds(int x) {
        if (x < 0) {
            return 0;
        }
        if (x > 255) {
            return 255;
        }
        return x;
    }

    private Color addColorComponent(Color cl, int red, int green, int blue, int alpha) {
        int rred = checkByteBounds(cl.getRed() + red);
        int rgreen = checkByteBounds(cl.getGreen() + green);
        int rblue = checkByteBounds(cl.getBlue() + blue);
        int raplha = checkByteBounds(cl.getAlpha() + alpha);
        return new Color(rred, rgreen, rblue, raplha);
    }

    private void applyNodeStateToLabel(JLabel label, MappingNode.NodeState state) {
        
        switch (state) {
            case Normal:
                break;
            case Good:
                label.setBackground(
                        addColorComponent(
                                label.getBackground(), 
                                -stsClAdd, stsClAdd, -stsClAdd, 0));
                break;
            case Warning:
                label.setBackground(
                        addColorComponent(
                                label.getBackground(), 
                                stsClAdd, stsClAdd, -stsClAdd, 0));
                break;
            case Error:
                label.setBackground(
                        addColorComponent(
                                label.getBackground(), 
                                stsClAdd, -stsClAdd, -stsClAdd, 0));
                break;
            //case Error:
            case Inactive:
                label.setBackground(
                        addColorComponent(
                                label.getBackground(), 
                                (stsClAdd), (stsClAdd), (stsClAdd), 0));
                label.setForeground(
                        addColorComponent(
                                label.getForeground(),
                                -(stsClAdd*2), -(stsClAdd*2), -(stsClAdd*2), 0));
                break;
        }

    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component defComp = defRenderer.getTreeCellRendererComponent(tree, value, leaf, expanded, leaf, row, hasFocus);
        JLabel label = (JLabel) defComp;
        if (value instanceof NodeStateContainer) {
            NodeStateContainer node = (NodeStateContainer) value;
            applyNodeStateToLabel(label, node.getState());
        }
        return label;
    }

}
