/*
 * IdeaMap.java
 *
 * Created on August 31, 2006, 6:03 PM
 *
 * Copyright (c) 2006, David Griffiths
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of David Griffiths nor the names of his contributors
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

import dg.hipster.controller.IdeaMapController;
import dg.hipster.model.Idea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * Main component for displaying the mind maps.
 * @author davidg
 */
public final class IdeaMap extends JComponent implements MapComponent {
    /**
     * Proportion that the image will be scaled in and out each
     * time the {@link #zoomIn()} and {@link #zoomOut()} methods are called.
     */
    public final static double SCALE_FACTOR = 1.5;
    /**
     * Controller object for this idea map.
     */
    private IdeaMapController controller;
    /**
     * Idea that will appear at the centre of the map.
     */
    private CentreView rootView;
    /**
     * Currently selected idea branch (if any).
     */
    private IdeaView selected;
    /**
     * Text field that appears at the top of the component.
     */
    private JTextField text;
    /**
     * Amount this map is scaled.
     */
    private double zoom = 1.0;
    
    /** Creates a new instance of Fred */
    public IdeaMap() {
        text = new JTextField("");
        setLayout(new BorderLayout());
        add(text, BorderLayout.NORTH);
        controller = new IdeaMapController(this);
    }
    
    /**
     * Text field that appears at the top of the component.
     * @return Text field that appears at the top of the component.
     */
    public JTextField getTextField() {
        return this.text;
    }
    
    /**
     * Set the central newIdea of the map.
     *
     * @param newIdea Idea that will be displayed at the centre.
     */
    public void setIdea(Idea newIdea) {
        Idea oldIdea = getIdea();
        if ((newIdea != null) && (!newIdea.equals(oldIdea))) {
            this.rootView = new CentreView(newIdea);
            this.rootView.setParent(this);
            rootView.setSelected(true);
            this.selected = rootView;
            text.setText(newIdea.getText());
            text.setEnabled(false);
        }
    }
    
    /**
     * Currently selected idea branch (if any).
     * @return Currently selected idea branch (if any).
     */
    public Idea getSelected() {
        return this.selected.getIdea();
    }
    
    /**
     * Currently selected idea branch (if any).
     * @param selectedIdea Currently selected idea branch (if any).
     */
    public void setSelected(Idea selectedIdea) {
        setSelectedView(findIdeaViewFor(rootView, selectedIdea));
    }
    
    /**
     * Find the view (if any) that represents the given idea.
     * Start the search at the given view, and search all of
     * it's sub-views.
     * @param parentView View to start the search at.
     * @param idea idea we are looking for.
     * @return idea-view representing the idea, or null
     * if none are found.
     */
    private IdeaView findIdeaViewFor(IdeaView parentView, Idea idea) {
        if (idea == null) {
            return null;
        }
        if (parentView.getIdea().equals(idea)) {
            return parentView;
        }
        for (IdeaView subView: parentView.getSubViews()) {
            IdeaView ideaView = findIdeaViewFor(subView, idea);
            if (ideaView != null) {
                return ideaView;
            }
        }
        return null;
    }
    
    /**
     * Currently selected idea branch (if any).
     * @return Currently selected idea branch (if any).
     */
    public IdeaView getSelectedView() {
        return this.selected;
    }
    
    /**
     * Select the given view.
     * @param newSelectedView View to select.
     */
    public void setSelectedView(IdeaView newSelectedView) {
        if (this.selected != null) {
            this.selected.setSelected(false);
        }
        this.selected = newSelectedView;
        if (this.selected != null) {
            this.selected.setSelected(true);
        }
    }
    
    /**
     * The idea represented at the centre of this map.
     * @return central idea.
     */
    public Idea getIdea() {
        if (this.rootView != null) {
            return this.rootView.getIdea();
        }
        return null;
    }
    
    /**
     * The idea-view at the centre of this map.
     * @return idea-view at the centre of this map.
     */
    public IdeaView getRootView() {
        return this.rootView;
    }
    
    /**
     * Paint the map part of the component (the text-field
     * will paint itself).
     * @param gOrig Graphics object to draw on.
     */
    public void paintComponent(Graphics gOrig) {
        Graphics g = gOrig.create();
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        g.translate(size.width / 2, size.height / 2);
        ((Graphics2D)g).scale(zoom, zoom);
        if (rootView != null) {
            rootView.paint(g, this);
        }
        g.dispose();
    }
    
    /**
     * Amount this map is scaled.
     * @return  Amount this map is scaled.
     */
    public double getZoom() {
        return zoom;
    }
    
    /**
     * Scale this map up by {@link #SCALE_FACTOR}.
     */
    public void zoomIn() {
        zoom *= SCALE_FACTOR;
        repaintRequired();
    }
    
    /**
     * Scale this map down by {@link #SCALE_FACTOR}.
     */
    public void zoomOut() {
        zoom /= SCALE_FACTOR;
        repaintRequired();
    }
    
    /**
     * Call for a repaint of this map.
     */
    public void repaintRequired() {
        controller.repaintRequired();
    }
    
    /**
     * Call for a repaint of this map.
     */
    public void startAdjust() {
        controller.startAdjust();
    }
    
    /**
     * Get the controller for this map.
     */
    public IdeaMapController getController() {
        return this.controller;
    }
}
