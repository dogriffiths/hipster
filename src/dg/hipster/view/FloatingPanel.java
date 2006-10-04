/*
 * FloatingPanel.java
 *
 * Created on October 4, 2006, 8:04 PM
 *
 * Copyright (c) 2006, David Griffiths
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this Vector of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this Vector of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the David Griffiths nor the names of his contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package dg.hipster.view;

import dg.hipster.controller.FloatingPanelController;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * Floating transparent panel that is used in the map area.
 *
 * @author davidg
 */
public final class FloatingPanel extends JPanel  {
    /**
     * 63 per cent transparent black. Used for background
     * of the map area, and all over-laid panes.
     */
    private static Color SHADED = new Color(0, 0, 0, 95);
    /**
     * Completely transparent.
     */
    private static Color CLEAR = new Color(0, 0, 0, 0);
    /**
     * Section of the pane that content should be
     * added to.
     */
    private JPanel contentPane;
    /**
     * Text that will appear at the top of the panel.
     */
    private JLabel caption;
    /**
     * Object handling the interaction logic of this
     * panel.
     */
    private FloatingPanelController controller;
    /**
     * Radius of the corners.
     */
    private static int RADIUS = 20;
    /**
     * Edges of the panel.
     */
    private Shape boundary;
    
    /**
     * Default constructor.
     */
    FloatingPanel() {
        setBackground(new Color(0, 0, 0, 0));
        controller = new FloatingPanelController(this);
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);
        caption = new JLabel("Test");
        caption.setBackground(SHADED);
        caption.setForeground(Color.WHITE);
        this.add(caption);
        contentPane = new JPanel();
        contentPane.setBackground(CLEAR);
        contentPane.setForeground(Color.WHITE);
        this.add(contentPane);
        layout.putConstraint(SpringLayout.WEST, caption, 3,
                SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, caption, 3,
                SpringLayout.NORTH, this);
        layout.putConstraint(SpringLayout.NORTH, contentPane, 3,
                SpringLayout.SOUTH, caption);
        layout.putConstraint(SpringLayout.WEST, contentPane, 3,
                SpringLayout.WEST, this);
    }
    
    /**
     * Text that will appear at the top of the panel.
     * @param text string to use at the top of the panel.
     */
    public void setCaption(String text) {
        caption.setText(text);
    }
    
    /**
     * Text that will appear at the top of the panel.
     * @return text at the top of the panel.
     */
    public String getCaption() {
        return caption.getText();
    }
    
    /**
     * Draw the component on the given graphics object.
     * @param g object to draw on.
     */
    public void paintComponent(Graphics g) {
        if (getBoundary() != null) {
            ((Graphics2D)g).clip(getBoundary());
        }
        g.setColor(SHADED);
        Dimension size = getSize();
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(Color.GRAY);
        g.drawLine(0, 0, size.width, 0);
        g.setColor(Color.BLACK);
        g.drawLine(0, size.height - 1, size.width, size.height - 1);
    }
    
    /**
     * Resize the panel, depending upon the size
     * of the content.
     */
    public void auto() {
        Dimension panSize = contentPane.getPreferredSize();
        setSize(panSize.width + 6, panSize.height + 25);
    }
    
    /**
     * Resize the panel.
     * @param width new width.
     * @param height new height.
     */
    public void setSize(int width, int height) {
        super.setSize(width, height);
        boundary = new RoundRectangle2D.Double(0, 0, width, height, RADIUS,
                RADIUS);
    }
    
    /**
     * Section of the pane that content should be
     * added to.
     * @return panel that component should be added to.
     */
    public JPanel getContentPane() {
        return contentPane;
    }

    /**
     * Outline boundary of the panel.
     * @return boundary.
     */
    public Shape getBoundary() {
        return boundary;
    }
}
