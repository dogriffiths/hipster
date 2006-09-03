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

import dg.hipster.controller.IdeaMapController;
import dg.hipster.model.Idea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author davidg
 */
public class IdeaMap extends JComponent implements MapComponent {
    private IdeaMapController controller;
    private IdeaView rootView;
    private IdeaView selected;

    /** Creates a new instance of Fred */
    public IdeaMap() {
        controller = new IdeaMapController(this);
    }
    
    public void setIdea(Idea idea) {
        this.rootView = new IdeaView(idea);
        this.rootView.setParent(this);
        rootView.setSelected(true);
        this.selected = rootView;
        this.repaintRequired();
    }
    
    public Idea getSelected() {
        return this.selected.getIdea();
    }

    public void setSelected(Idea selectedIdea) {
        setSelectedView(findIdeaViewFor(rootView, selectedIdea));
    }
    
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
    
    public IdeaView getSelectedView() {
        return this.selected;
    }
    
    public void setSelectedView(IdeaView newSelectedView) {
        if (this.selected != null) {
            this.selected.setSelected(false);
        }
        this.selected = newSelectedView;
        if (this.selected != null) {
            this.selected.setSelected(true);
        }
    }
    
    public Idea getIdea() {
        return this.rootView.getIdea();
    }
    
    public IdeaView getRootView() {
        return this.rootView;
    }
    
    public void paintComponent(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        g.translate(size.width / 2, size.height / 2);
        rootView.paint(g);
    }
    
    public void repaintRequired() {
        controller.repaintRequired();
    }
}
