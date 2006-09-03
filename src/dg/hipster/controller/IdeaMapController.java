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

import dg.hipster.view.IdeaMap;
import dg.hipster.view.IdeaView;
import dg.hipster.view.MapComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import javax.swing.Timer;

/**
 *
 * @author davidg
 */
public class IdeaMapController implements ActionListener, KeyListener,
        FocusListener {
    private static final double MAX_SPEED = 5.0;
    private static final double MAX_MOVE_TIME_SECS = 3.0;
    private final static Vertex ORIGIN = new Vertex(0.0, 0.0);
    private IdeaMap ideaMap;
    private Timer ticker = new Timer(50, this);
    long timeChanged = 0;
    
    /** Creates a new instance of IdeaMapController */
    public IdeaMapController(IdeaMap newIdeaMap) {
        this.ideaMap = newIdeaMap;
        this.ideaMap.setFocusable(true);
        this.ideaMap.addFocusListener(this);
        this.ideaMap.addKeyListener(this);
    }
    
    public void repaintRequired() {
        timeChanged = System.currentTimeMillis();
        ticker.start();
    }

    public void actionPerformed(ActionEvent evt) {
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
    
    public void focusGained(FocusEvent evt) {
    }
    
    public void focusLost(FocusEvent evt) {
    }
    
    public void keyReleased(KeyEvent evt) {
    }
    
    public void keyTyped(KeyEvent evt) {
    }
    
    public void keyPressed(KeyEvent evt) {
        switch(evt.getKeyCode()) {
            case KeyEvent.VK_UP:
                selectPrevious();
                break;
            case KeyEvent.VK_DOWN:
                selectNext();
                break;
            case KeyEvent.VK_LEFT:
                selectParent();
                break;
            case KeyEvent.VK_RIGHT:
                selectFirstChild();
                break;
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                deleteSelected();
                break;
            default:
                // Do nothing
        }
    }
    
    private void selectNext() {
        selectSibling(+1);
    }
    
    private void selectPrevious() {
        selectSibling(-1);
    }
    
    private void selectSibling(int diff) {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        IdeaView previous = selected.getSibling(diff);
        if (previous == null) {
            return;
        }
        this.ideaMap.setSelectedView(previous);
    }
    
    private void selectFirstChild() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        List<IdeaView> subViews = selected.getSubViews();
        if (subViews.size() == 0) {
            return;
        }
        this.ideaMap.setSelectedView(subViews.get(0));
    }
    
    private void selectParent() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        MapComponent parent = selected.getParent();
        if (!(parent instanceof IdeaView)) {
            return;
        }
        IdeaView parentView = (IdeaView)parent;
        this.ideaMap.setSelectedView(parentView);
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
        IdeaView parentView = (IdeaView)parent;
        parentView.remove(selected);
        this.ideaMap.setSelectedView(parentView);
    }
    
    private double mass = 0.0;
    private double maxSpeed = 0.0;
    
    List<Vertex> particles;
    private void adjust() {
        IdeaView rootView = ideaMap.getRootView();
        particles = new Vector<Vertex>();
        createParticles(rootView, new Position(ORIGIN, rootView.getAngle()));
        mass = 2000.0 / (particles.size()
        * Math.sqrt((double)particles.size()));
        endForce(rootView, new Position(ORIGIN, 0.0));
        adjustAngles(rootView);
        ideaMap.repaint();
    }
    
    private Vertex endForce(final IdeaView parentView, final Position p) {
        final List<IdeaView> views = parentView.getSubViews();
        if (views.size() == 0) {
            return new Vertex(0.0, 0.0);
        }
        Vertex totForce = new Vertex(0.0, 0.0);
        for (IdeaView view: views) {
            Vertex particle = getParticle(view, p);
            Vertex force = repulsion(particle, view, p);
            totForce = totForce.add(force);
            view.setV(getNewVelocity(force, view, p));
        }
        return totForce;
    }
    
    private double getNewVelocity(final Vertex force, final IdeaView view,
            final Position p) {
        Vertex p2 = getParticle(view, new Position(ORIGIN, p.angle));
        double sideForce = (p2.y * force.x) + (-p2.x * force.y);
        double v = view.getV();
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
        final List<IdeaView> views = parentView.getSubViews();
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
            view.setAngle(newAngle);
            adjustAngles(view);
        }
    }
    
    private double getNewAngle(final IdeaView parentView,
            final IdeaView previousView, final IdeaView view,
            final IdeaView nextView) {
        final List<IdeaView> views = parentView.getSubViews();
        final double v = view.getV();
        double minDiffAngle = Math.PI / 2 / views.size();
        
        double oldAngle = view.getAngle();
        double newAngle = oldAngle  + (view.getV() / view.getLength());
        if (previousView != null) {
            double previousAngle = previousView.getAngle();
            if (previousAngle > newAngle - minDiffAngle) {
                previousView.setAngle(newAngle - minDiffAngle);
                newAngle = previousAngle + minDiffAngle;
                double previousV = previousView.getV();
                double diffV = v - previousV;
                if (diffV > 0) {
                    view.setV(diffV);
                    previousView.setV(-diffV);
                } else {
                    view.setV(-diffV);
                    previousView.setV(diffV);
                }
            }
        } else {
            double previousAngle = -Math.PI;
            if (parentView.isRoot()) {
                previousAngle = views.get(views.size() - 1).getAngle()
                - 2 * Math.PI;
            }
            if (previousAngle > newAngle - minDiffAngle) {
                newAngle = previousAngle + minDiffAngle;
                double previousV = 0.0;
                double diffV = v - previousV;
                if (diffV > 0) {
                    view.setV(diffV);
                } else {
                    view.setV(-diffV);
                }
            }
        }
        if (nextView != null) {
            double nextAngle = nextView.getAngle();
            if (nextAngle < newAngle + minDiffAngle) {
                nextView.setAngle(newAngle + minDiffAngle);
                newAngle = nextAngle - minDiffAngle;
                double nextV = nextView.getV();
                double diffV = 0.0;
                if (diffV > 0) {
                    view.setV(-diffV);
                    nextView.setV(diffV);
                } else {
                    view.setV(diffV);
                    nextView.setV(-diffV);
                }
            }
        } else {
            double nextAngle = Math.PI;
            if (parentView.isRoot()) {
                nextAngle = views.get(0).getAngle() +  2 * Math.PI;
            }
            if (nextAngle < newAngle + minDiffAngle) {
                newAngle = nextAngle - minDiffAngle;
                double nextV = 0.0;
                double diffV = 0.0;
                if (diffV > 0) {
                    view.setV(-diffV);
                } else {
                    view.setV(diffV);
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
                view.getAngle() + p.angle)));
        return force;
    }
    
    private void createParticles(IdeaView parentView, Position start) {
        List<IdeaView> views = parentView.getSubViews();
        particles.add(ORIGIN);
        for(IdeaView view: views) {
            Vertex location = getParticle(view, start);
            particles.add(location);
            Position nextStart = new Position(location,
                    start.angle + view.getAngle());
            createParticles(view, nextStart);
        }
    }
    
    private Vertex getParticle(IdeaView view, Position p) {
        double angle = view.getAngle() + p.angle;
        double length = view.getLength();
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
