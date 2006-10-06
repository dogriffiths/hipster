/*
 * FloatingPanelController.java
 *
 * Created on October 4, 2006, 8:06 PM
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

package dg.hipster.controller;

import dg.hipster.view.FloatingPanel;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Object to control the interactions with a
 * floting panel.
 * @author davidg
 */
public final class FloatingPanelController implements MouseListener,
        MouseMotionListener {
    /**
     * Floating panel being controlled.
     */
    private FloatingPanel floatingPanel;
    /**
     * Point where the mouse was last pressed.
     */
    private Point downPoint;
    /**
     * Where the floating panel was positioned relative
     * to its parent, when the mouse was first clicked.
     */
    private Point start;

    /**
     * Constructor for a controller of a given floating
     * panel.
     * @param aFloatingPanel panel to control.
     */
    public FloatingPanelController(final FloatingPanel aFloatingPanel) {
        this.floatingPanel = aFloatingPanel;
        this.floatingPanel.addMouseListener(this);
        this.floatingPanel.addMouseMotionListener(this);
    }

    /**
     * Called when a mouse enters the panel.
     * @param evt mouse event describing the mouse entry.
     */
    public void mouseEntered(final MouseEvent evt) {
    }

    /**
     * Called when a mouse exits the panel.
     * @param evt mouse event describing the mouse exit.
     */
    public void mouseExited(final MouseEvent evt) {
    }

    /**
     * Called when a mouse is pressed and released
     * at a point on the panel.
     * @param evt mouse event describing the mouse click.
     */
    public void mouseClicked(final MouseEvent evt) {
    }

    /**
     * Called when a mouse moves over the panel.
     * @param evt mouse event describing the mouse move.
     */
    public void mouseMoved(final MouseEvent evt) {
    }

    /**
     * Called when a mouse is released over the panel.
     * @param evt mouse event describing the mouse release.
     */
    public void mouseReleased(final MouseEvent evt) {
        downPoint = null;
    }

    /**
     * Called when a mouse is pressed on the panel.
     * @param evt mouse event describing the mouse press.
     */
    public void mousePressed(final MouseEvent evt) {
        downPoint = evt.getPoint();
        if (!this.floatingPanel.getBoundary().contains(downPoint)) {
            downPoint = null;
            return;
        }
        start = this.floatingPanel.getLocation();
        downPoint.x += start.x;
        downPoint.y += start.y;
    }

    /**
     * Called when a mouse is dragged over the panel.
     * @param evt mouse event describing the mouse drag.
     */
    public void mouseDragged(final MouseEvent evt) {
        if (downPoint == null) {
            return;
        }
        Point p = evt.getPoint();
        Point s = this.floatingPanel.getLocation();
        int xDiff = s.x + p.x - downPoint.x;
        int yDiff = s.y + p.y - downPoint.y;
        this.floatingPanel.setLocation(new Point(start.x + xDiff,
                start.y + yDiff));
    }
}
