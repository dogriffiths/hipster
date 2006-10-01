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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.Timer;

/**
 *
 * @author davidg
 */
public final class IdeaMapController implements ActionListener, KeyListener,
        FocusListener, MouseListener, MouseMotionListener {
    private static final double MAX_SPEED = 5.0;
    private static final double MAX_MOVE_TIME_SECS = 23.0;
    private final static Vertex ORIGIN = new Vertex(0.0, 0.0);
    private IdeaMap ideaMap;
    private Timer ticker = new Timer(50, this);
    long timeChanged = 0;
    
    /** Creates a new instance of IdeaMapController */
    public IdeaMapController(final IdeaMap newIdeaMap) {
        this.ideaMap = newIdeaMap;
        this.ideaMap.setFocusTraversalKeysEnabled(false);
        this.ideaMap.setFocusable(true);
        this.ideaMap.addFocusListener(this);
        this.ideaMap.addKeyListener(this);
        this.ideaMap.addMouseListener(this);
        this.ideaMap.addMouseMotionListener(this);
        this.ideaMap.getTextField().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unEditCurrent();
            }
        });
        this.ideaMap.getTextField().addFocusListener(new FocusListener() {
            public void focusLost(FocusEvent fe) {
                unEditCurrent();
            }
            public void focusGained(FocusEvent fe) {
                
            }
        });
        this.ideaMap.requestFocusInWindow();
    }
    
    private void unEditCurrent() {
        ideaMap.requestFocusInWindow();
        IdeaView current = ideaMap.getSelectedView();
        if (current != null) {
            unEditIdeaView(current);
        }
    }
    
    public void mouseClicked(final MouseEvent evt) {
        
    }
    
    public void mousePressed(final MouseEvent evt) {
        IdeaView selected = this.ideaMap.getSelectedView();
        if (selected != null) {
            selected.getIdea().setText(ideaMap.getTextField().getText());
            ideaMap.getTextField().setEnabled(false);
            selected.setEditing(false);
        }
        Dimension size = this.ideaMap.getSize();
        double x = evt.getX() - (size.width / 2);
        double y = evt.getY() - (size.height / 2);
        double z = ideaMap.getZoom();
        x /= z;
        y /= z;
        Point2D p = new Point2D.Double(x, y);
        if ((this.ideaMap != null) && (this.ideaMap.getRootView() != null)) {
            IdeaView hit = this.ideaMap.getRootView().getViewAt(p);
            if (hit != null) {
                this.ideaMap.setSelectedView(hit);
                ideaMap.getTextField().setText(hit.getIdea().getText());
                if (evt.getClickCount() == 2) {
                    editIdeaView(hit);
                }
            }
        }
    }
    
    public void mouseReleased(final MouseEvent evt) {
        if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
            Dimension size = this.ideaMap.getSize();
            double x = evt.getX() - (size.width / 2);
            double y = evt.getY() - (size.height / 2);
            double z = ideaMap.getZoom();
            x /= z;
            y /= z;
            Point2D p = new Point2D.Double(x, y);
            if ((this.ideaMap != null) && (this.ideaMap.getRootView() != null)) {
                IdeaView hit = this.ideaMap.getRootView().getViewAt(p);
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
    
    public void mouseExited(final MouseEvent evt) {
        
    }
    
    public void mouseEntered(final MouseEvent evt) {
        
    }
    
    public void mouseMoved(final MouseEvent evt) {
        
    }
    
    public synchronized void mouseDragged(final MouseEvent evt) {
        if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0) {
            IdeaView current = this.ideaMap.getSelectedView();
            if (current == null) {
                return;
            }
            if (current instanceof BranchView) {
                BranchView branch = (BranchView)current;
                Dimension size = this.ideaMap.getSize();
                double x = evt.getX() - (size.width / 2);
                double y = evt.getY() - (size.height / 2);
                double z = ideaMap.getZoom();
                x /= z;
                y /= z;
                Point2D p = new Point2D.Double(x, y);
                Point2D fromPoint = branch.getFromPoint();
                double angle = getAngleBetween(fromPoint, p);
                MapComponent parent = current.getParent();
                if (parent instanceof IdeaView) {
                    IdeaView parentView = (IdeaView) parent;
                    angle = angle - parentView.getRealAngle();
                }
                current.getIdea().setAngle(angle);
            }
        } else {
            IdeaView selectedView = ideaMap.getSelectedView();
            if ((selectedView != null)
            && (selectedView instanceof BranchView)) {
                BranchView branch = (BranchView)selectedView;
                Graphics2D g = (Graphics2D)ideaMap.getGraphics();
                g.setColor(Color.GRAY);
                Dimension size = ideaMap.getSize();
                Point2D p = branch.getMidPoint();
                int x = (int)p.getX() + (size.width / 2);
                int y = (int)p.getY() + (size.height / 2);
                Stroke oldStroke = g.getStroke();
                float strokeWidth = 20.0f;
                Stroke stroke = new BasicStroke(strokeWidth,
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
                g.setStroke(stroke);
                g.drawLine(x,
                        y, evt.getX(), evt.getY());
                g.setStroke(oldStroke);
                
            }
        }
    }
    
    static double getAngleBetween(final Point2D fromP, final Point2D toP) {
        double diffX = toP.getX() - fromP.getX();
        double diffY = toP.getY() - fromP.getY();
        double angle = 0.0;
        double tan = Math.abs(diffX) / Math.abs(diffY);
        angle = Math.atan(tan);
        if (diffY > 0) {
            angle = (Math.PI - angle);
        }
        if (diffX < 0) {
            angle *= -1;
        }
        return angle;
    }
    
    public void repaintRequired() {
        timeChanged = System.currentTimeMillis();
        ticker.start();
    }
    
    public void actionPerformed(final ActionEvent evt) {
        if (timeChanged == 0) {
            timeChanged = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        maxSpeed = 0.0;
        if ((now - timeChanged) < (MAX_MOVE_TIME_SECS * 1000.0)) {
            maxSpeed = MAX_SPEED - ((now - timeChanged) * MAX_SPEED / 1000.0
                    / MAX_MOVE_TIME_SECS);
        } else {
            ticker.stop();
        }
        adjust();
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
                ticker.stop();
                maxSpeed = 0.0;
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
                deleteSelected();
                break;
            case KeyEvent.VK_ESCAPE:
                IdeaView hit2 = this.ideaMap.getSelectedView();
                if (hit2 != null) {
                    unEditIdeaView(hit2);
                }
                break;
            case KeyEvent.VK_ENTER:
                if (evt.getModifiers() != 0) {
                    IdeaView hit = this.ideaMap.getSelectedView();
                    if (hit != null) {
                        editIdeaView(hit);
                    }
                } else {
                    insertIdea();
                }
                break;
            case KeyEvent.VK_TAB:
                insertChild();
                break;
            case KeyEvent.VK_EQUALS:
                if (evt.getModifiersEx() != 0) {
                    this.ideaMap.zoomIn();
                }
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
            selectIdeaView(nextView);
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
        for (Point2D endPoint: viewMap.keySet()) {
            if (endPoint.getY() < y) {
                y = endPoint.getY();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            selectIdeaView(nextView);
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
        selectIdeaView(previous);
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
        for (Point2D endPoint: viewMap.keySet()) {
            if (endPoint.getX() > x) {
                x = endPoint.getX();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            selectIdeaView(nextView);
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
        for (Point2D endPoint: viewMap.keySet()) {
            if (endPoint.getX() < x) {
                x = endPoint.getX();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            selectIdeaView(nextView);
        }
    }
    
    private Map<Point2D, IdeaView> endPoints(final IdeaView ideaView) {
        Map<Point2D, IdeaView> results = new HashMap<Point2D, IdeaView>();
        List<BranchView> subViews = ideaView.getSubViews();
        // Add all the sub-views
        results.put(ideaView.getEndPoint(), ideaView);
        for(IdeaView subView: subViews) {
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
    
    private void deleteSelected() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        MapComponent parent = selected.getParent();
        if (!(parent instanceof IdeaView)) {
            return;
        }
        IdeaView parentView = (IdeaView) parent;
        IdeaView nextToSelect = null;
        IdeaView nextSibling = selected.getNextSibling();
        IdeaView previousSibling = selected.getPreviousSibling();
        if (nextSibling != null) {
            nextToSelect = nextSibling;
        } else if (previousSibling != null) {
            nextToSelect = previousSibling;
        } else {
            nextToSelect = parentView;
        }
        parentView.getIdea().remove(selected.getIdea());
        selectIdeaView(nextToSelect);
    }
    
    int newCount = 0;
    public void insertIdea() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        MapComponent parent = selected.getParent();
        if (!(parent instanceof IdeaView)) {
            return;
        }
        IdeaView parentView = (IdeaView) parent;
        int pos = parentView.getSubViews().indexOf(selected);
        Idea newIdea = new Idea("New " + (newCount++));
        parentView.getIdea().add(pos + 1, newIdea);
        editIdeaView(selected.getNextSibling());
    }
    
    public void editIdeaView(final IdeaView selected) {
        selectIdeaView(selected);
        selected.setEditing(true);
        ideaMap.getTextField().setEnabled(true);
        ideaMap.getTextField().requestFocusInWindow();
        ideaMap.getTextField().selectAll();
    }
    
    public void unEditIdeaView(final IdeaView ideaView) {
        ideaView.getIdea().setText(ideaMap.getTextField().getText());
        ideaView.setEditing(false);
        ideaMap.getTextField().select(0, 0);
        ideaMap.getTextField().setEnabled(false);
    }
    
    private void selectIdeaView(final IdeaView selected) {
        this.ideaMap.setSelectedView(selected);
        ideaMap.getTextField().setText(selected.getIdea().getText());
    }
    
    public void insertChild() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Idea newIdea = new Idea("New " + (newCount++));
        selected.getIdea().add(0, newIdea);
        editIdeaView(selected.getSubViews().get(0));
    }
    
    private double mass = 0.0;
    private double maxSpeed = 0.0;
    
    List<Vertex> particles;
    private void adjust() {
        IdeaView rootView = ideaMap.getRootView();
        particles = new Vector<Vertex>();
        createParticles(rootView, new Position(ORIGIN, rootView.getIdea().getAngle()));
        mass = 2000.0 / (particles.size()
        * Math.sqrt((double) particles.size()));
        endForce(rootView, new Position(ORIGIN, 0.0));
        adjustAngles(rootView);
        ideaMap.repaint();
    }
    
    private Vertex endForce(final IdeaView parentView, final Position p) {
        final List<BranchView> views = parentView.getSubViews();
        if (views.size() == 0) {
            return new Vertex(0.0, 0.0);
        }
        Vertex totForce = new Vertex(0.0, 0.0);
        for (IdeaView view: views) {
            Vertex particle = getParticle(view, p);
            Vertex force = repulsion(particle, view, p);
            totForce = totForce.add(force);
            view.getIdea().setV(getNewVelocity(force, view, p));
        }
        return totForce;
    }
    
    private double getNewVelocity(final Vertex force, final IdeaView view,
            final Position p) {
        Vertex p2 = getParticle(view, new Position(ORIGIN, p.angle));
        double sideForce = (p2.y * force.x) + (-p2.x * force.y);
        double v = view.getIdea().getV();
        v += sideForce / mass;
        v *= 0.90;
        if (v > maxSpeed) {
            v = maxSpeed;
        }
        if (v < -maxSpeed) {
            v = -maxSpeed;
        }
        return v;
    }
    
    private void adjustAngles(final IdeaView parentView) {
        final List<BranchView> views = parentView.getSubViews();
        for (int i = 0; i < views.size(); i++) {
            IdeaView previousView = null;
            IdeaView nextView = null;
            IdeaView view = views.get(i);
            if (i > 0) {
                previousView = views.get(i - 1);
            }
            if (i < views.size() - 1) {
                nextView = views.get(i + 1);
            }
            double newAngle = getNewAngle(parentView, previousView, view,
                    nextView);
            view.getIdea().setAngle(newAngle);
            adjustAngles(view);
        }
    }
    
    private double getNewAngle(final IdeaView parentView,
            final IdeaView previousView, final IdeaView view,
            final IdeaView nextView) {
        final List<BranchView> views = parentView.getSubViews();
        final double v = view.getIdea().getV();
        double minDiffAngle = Math.PI / 2 / views.size();
        
        double oldAngle = view.getIdea().getAngle();
        double newAngle = oldAngle  + (view.getIdea().getV() / view.getIdea().getLength());
        if (previousView != null) {
            double previousAngle = previousView.getIdea().getAngle();
            if (previousAngle > newAngle - minDiffAngle) {
                previousView.getIdea().setAngle(newAngle - minDiffAngle);
                newAngle = previousAngle + minDiffAngle;
                double previousV = previousView.getIdea().getV();
                double diffV = v - previousV;
                if (diffV > 0) {
                    view.getIdea().setV(diffV);
                    previousView.getIdea().setV(-diffV);
                } else {
                    view.getIdea().setV(-diffV);
                    previousView.getIdea().setV(diffV);
                }
            }
        } else {
            double previousAngle = -Math.PI;
            if (previousAngle > newAngle - minDiffAngle) {
                newAngle = previousAngle + minDiffAngle;
                double previousV = 0.0;
                double diffV = v - previousV;
                if (diffV > 0) {
                    view.getIdea().setV(diffV);
                } else {
                    view.getIdea().setV(-diffV);
                }
            }
        }
        if (nextView != null) {
            double nextAngle = nextView.getIdea().getAngle();
            if (nextAngle < newAngle + minDiffAngle) {
                nextView.getIdea().setAngle(newAngle + minDiffAngle);
                newAngle = nextAngle - minDiffAngle;
                double nextV = nextView.getIdea().getV();
                double diffV = 0.0;
                if (diffV > 0) {
                    view.getIdea().setV(-diffV);
                    nextView.getIdea().setV(diffV);
                } else {
                    view.getIdea().setV(diffV);
                    nextView.getIdea().setV(-diffV);
                }
            }
        } else {
            double nextAngle = Math.PI;
            if (nextAngle < newAngle + minDiffAngle) {
                newAngle = nextAngle - minDiffAngle;
                double nextV = 0.0;
                double diffV = 0.0;
                if (diffV > 0) {
                    view.getIdea().setV(-diffV);
                } else {
                    view.getIdea().setV(diffV);
                }
            }
        }
        return newAngle;
    }
    
    private Vertex repulsion(final Vertex point, final IdeaView view,
            final Position p) {
        Vertex force = new Vertex(0, 0);
        for(Vertex other: particles) {
            Vertex dir = point.subtract(other);
            double dSquare = point.distanceSq(other);
            if (dSquare > 0.000001) {
                double unitFactor = point.distance(other);
                Vertex scaled = dir.scale(mass * mass / (dSquare * unitFactor));
                force = force.add(scaled);
                force.trim(-1.0, 1.0);
            }
        }
        force = force.add(endForce(view, new Position(point,
                view.getIdea().getAngle() + p.angle)));
        return force;
    }
    
    private void createParticles(final IdeaView parentView,
            final Position start) {
        List<BranchView> views = parentView.getSubViews();
        particles.add(ORIGIN);
        for(IdeaView view: views) {
            Vertex location = getParticle(view, start);
            particles.add(location);
            Position nextStart = new Position(location,
                    start.angle + view.getIdea().getAngle());
            createParticles(view, nextStart);
        }
    }
    
    private Vertex getParticle(IdeaView view, Position p) {
        double angle = view.getIdea().getAngle() + p.angle;
        double length = view.getIdea().getLength();
        double x = p.start.x + Math.sin(angle) * length;
        double y = p.start.y + Math.cos(angle) * length;
        return new Vertex(x, y);
    }
    
}

class Position {
    Vertex start;
    double angle;
    Position(Vertex aStart, double anAngle) {
        this.start = aStart;
        this.angle = anAngle;
    }
}

class Vertex {
    double x;
    double y;
    Vertex(double anX, double aY) {
        this.x = anX;
        this.y = aY;
    }
    
    Vertex add(Vertex other) {
        return new Vertex(this.x + other.x, this.y + other.y);
    }
    
    Vertex subtract(Vertex other) {
        return new Vertex(this.x - other.x, this.y - other.y);
    }
    
    double distanceSq(Vertex other) {
        return (new Point2D.Double(x, y)).distanceSq(
                new Point2D.Double(other.x, other.y));
    }
    
    double distance(Vertex other) {
        return (new Point2D.Double(x, y)).distance(
                new Point2D.Double(other.x, other.y));
    }
    
    Vertex scale(double factor) {
        return new Vertex(this.x * factor, this.y * factor);
    }
    
    void trim(double min, double max) {
        if (x < min) {
            x = min;
        }
        if (x > max) {
            x = max;
        }
        if (y < min) {
            y = min;
        }
        if (y > max) {
            y = max;
        }
    }
}
