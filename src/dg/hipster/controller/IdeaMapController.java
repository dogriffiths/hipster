/*
 * IdeaMapController.java
 *
 * Created on September 1, 2006, 12:09 PM
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

package dg.hipster.controller;

import dg.hipster.model.Idea;
import dg.hipster.view.BranchView;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.IdeaView;
import dg.hipster.view.MapComponent;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Object that can control an idea-map. It registers
 * itself to listen keyboard, mouse and other activity
 * and decides how the idea map should react.
 * @author davidg
 */
public final class IdeaMapController implements KeyListener, FocusListener,
        MouseListener, MouseMotionListener {
    /**
     * Idea map being controlled.
     */
    private IdeaMap ideaMap;
    /**
     * Point in idea-map relative screen space when the mouse was last
     * pressed. This is used to drag the result of dragging.
     */
    private Point downPoint;
    /**
     * The controller animating the map.
     */
    private MapMover mapMover;
    /**
     * Branch being dragged.
     */
    private BranchView draggedBranch;
    /**
     * Factor to zoom by with each mouse-wheel click.
     */
    static final double ZOOM_PER_CLICK = 0.8;

    /**
     * Creates a new instance of IdeaMapController.
     *@param newIdeaMap Idea map to control.
     */
    public IdeaMapController(final IdeaMap newIdeaMap) {
        this.ideaMap = newIdeaMap;
        this.ideaMap.setFocusTraversalKeysEnabled(false);
        this.ideaMap.setFocusable(true);
        this.ideaMap.addFocusListener(this);
        this.ideaMap.addKeyListener(this);
        this.ideaMap.addMouseListener(this);
        this.ideaMap.addMouseMotionListener(this);
        this.ideaMap.addMouseWheelListener(
                new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                ideaMap.zoom(Math.pow(ZOOM_PER_CLICK, rotation));
            }
        });
        this.ideaMap.getTextField().addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                unEditCurrent();
            }
        });
        this.ideaMap.getTextField().addFocusListener(new FocusListener() {
            public void focusLost(final FocusEvent fe) {
                unEditCurrent();
            }
            public void focusGained(final FocusEvent fe) {

            }
        });
        this.ideaMap.requestFocusInWindow();
        this.mapMover = new MapMover(this.ideaMap);
    }

    /**
     * Stop the automatic adjustment process.
     */
    public void stopAdjust() {
        mapMover.stopAdjust();
    }

    /**
     * Start the automatic adjustment process.
     */
    public void startAdjust() {
        mapMover.startAdjust();
    }

    /**
     * Switch the current idea view out of editing mode.
     */
    private void unEditCurrent() {
        this.ideaMap.unEdit();
    }

    /**
     * Called when a mouse is pressed and released on
     * the idea map.
     * @param evt event describing the mouse click.
     */
    public void mouseClicked(final MouseEvent evt) {
        Point2D p = this.ideaMap.getMapPoint(evt.getPoint());
        IdeaView hit = this.ideaMap.getViewAt(p);
        if (hit == null) {
            ideaMap.setSelected(null);
        }
    }


    /**
     * Called when a mouse is pressed on the idea map.
     * @param evt event describing the mouse press.
     */
    public void mousePressed(final MouseEvent evt) {
        this.ideaMap.unEdit();
        downPoint = evt.getPoint();
        Point2D p = this.ideaMap.getMapPoint(evt.getPoint());
        if (this.ideaMap != null) {
            boolean shouldEdit = (evt.getClickCount() == 2);
            selectIdeaViewAt(p, shouldEdit);
        }
    }

    private void selectIdeaViewAt(final Point2D p, final boolean shouldEdit) {
        IdeaView hit = this.ideaMap.getViewAt(p);
        if (hit != null) {
            this.ideaMap.setSelected(hit.getIdea());
            if (hit instanceof BranchView) {
                draggedBranch = (BranchView) hit;
                mapMover.setFixedBranch(draggedBranch);
            }
            ideaMap.getTextField().setText(hit.getIdea().getText());
            if (shouldEdit) {
                this.ideaMap.edit();
            }
        }
    }

    /**
     * Called when a mouse is released over the idea map.
     * @param evt event describing the mouse release.
     */
    public void mouseReleased(final MouseEvent evt) {
        if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
            Point2D p = this.ideaMap.getMapPoint(evt.getPoint());
            createLinkTo(p);
        }
        draggedBranch = null;
        mapMover.setFixedBranch(null);
        this.ideaMap.clearRubberBand();
    }

    /**
     * Called when a mouse exits the idea map.
     * @param evt event describing the mouse exit.
     */
    public void mouseExited(final MouseEvent evt) {

    }

    /**
     * Called when a mouse enters the idea map.
     * @param evt event describing the mouse entry.
     */
    public void mouseEntered(final MouseEvent evt) {

    }

    /**
     * Called when a mouse moves over the idea map.
     * @param evt event describing the mouse movement.
     */
    public void mouseMoved(final MouseEvent evt) {

    }

    /**
     * Called when a mouse is dragged over the idea map.
     * @param evt event describing the mouse drag.
     */
    public synchronized void mouseDragged(final MouseEvent evt) {
        if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0) {
            if (draggedBranch != null) {
                this.ideaMap.dragBranchTo(draggedBranch, evt.getPoint());
            } else {
                dragMapTo(evt.getPoint());
                return;
            }
        } else {
            this.ideaMap.drawLinkRubberBand(evt.getPoint());
        }
    }

    private void dragMapTo(final Point p) {
        if ((p == null) || (downPoint == null)) {
            return;
        }
        int xDiff = p.x - downPoint.x;
        int yDiff = p.y - downPoint.y;
        Point offset = ideaMap.getOffset();
        if (offset == null) {
            offset = new Point(0, 0);
        }
        ideaMap.setOffset(new Point(offset.x + xDiff,
                offset.y + yDiff));
        downPoint = p;
    }

    public void focusGained(final FocusEvent evt) {
    }

    public void focusLost(final FocusEvent evt) {
    }

    public void keyReleased(final KeyEvent evt) {
    }

    public void keyTyped(final KeyEvent evt) {
    }

    public void keyPressed(final KeyEvent evt) {
        switch(evt.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (mapMover.isRunning()) {
                    stopAdjust();
                } else {
                    startAdjust();
                }
                break;
            case KeyEvent.VK_UP:
                selectUp();
                break;
            case KeyEvent.VK_DOWN:
                selectDown();
                break;
            case KeyEvent.VK_LEFT:
                selectLeft();
                break;
            case KeyEvent.VK_RIGHT:
                selectRight();
                break;
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                this.ideaMap.deleteSelected();
                break;
            case KeyEvent.VK_ESCAPE:
                this.ideaMap.unEdit();
                break;
            case KeyEvent.VK_ENTER:
                if (evt.getModifiers() != 0) {
                    this.ideaMap.edit();
                } else {
                    this.ideaMap.insertSibling();
                }
                break;
            case KeyEvent.VK_TAB:
                this.ideaMap.insertChild();
                break;
            default:
                // Do nothing
        }
    }

    private void selectDown() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Westerly
        double y = Double.NEGATIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint: viewMap.keySet()) {
            if (endPoint.getY() > y) {
                y = endPoint.getY();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.setSelected(nextView.getIdea());
        }
    }

    private void selectUp() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Westerly
        double y = Double.POSITIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getY() < y) {
                y = endPoint.getY();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.setSelected(nextView.getIdea());
        }
    }

    private void selectSibling(final int diff) {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        IdeaView previous = selected.getSibling(diff);
        if (previous == null) {
            return;
        }
        this.ideaMap.setSelected(previous.getIdea());
    }

    private void selectRight() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Easterly
        double x = Double.NEGATIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getX() > x) {
                x = endPoint.getX();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.setSelected(nextView.getIdea());
        }
    }

    private void selectLeft() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Westerly
        double x = Double.POSITIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getX() < x) {
                x = endPoint.getX();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.setSelected(nextView.getIdea());
        }
    }

    private Map<Point2D, IdeaView> endPoints(final IdeaView ideaView) {
        Map<Point2D, IdeaView> results = new HashMap<Point2D, IdeaView>();
        List<BranchView> subViews = ideaView.getSubViews();
        // Add all the sub-views
        results.put(ideaView.getEndPoint(), ideaView);
        for (IdeaView subView : subViews) {
            results.put(subView.getEndPoint(), subView);
        }
        IdeaView previousSibling = ideaView.getPreviousSibling();
        if (previousSibling != null) {
            results.put(previousSibling.getEndPoint(), previousSibling);
        }
        IdeaView nextSibling = ideaView.getNextSibling();
        if (nextSibling != null) {
            results.put(nextSibling.getEndPoint(), nextSibling);
        }
        MapComponent parent = ideaView.getParent();
        if (parent instanceof IdeaView) {
            IdeaView parentView = (IdeaView) parent;
            MapComponent grandParent = parentView.getParent();
            if (grandParent instanceof IdeaView) {
                IdeaView grandParentView = (IdeaView) grandParent;
                results.put(grandParentView.getEndPoint(), parentView);
            } else {
                results.put(parentView.getEndPoint(), parentView);
            }
        }
        return results;
    }

    private void createLinkTo(final Point2D p) {
        if (this.ideaMap != null) {
            IdeaView hit = this.ideaMap.getViewAt(p);
            if (hit != null) {
                Idea selectedIdea = this.ideaMap.getSelected();
                if (selectedIdea != null) {
                    Idea hitIdea = hit.getIdea();
                    selectedIdea.addLink(hitIdea);
                }
            }
        }
    }
}
