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

import dg.hipster.model.Idea;
import dg.hipster.model.IdeaEvent;
import dg.hipster.model.IdeaListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 *
 * @author davidg
 */
public class IdeaMap extends JComponent implements MapComponent {
    private static final double MAX_SPEED = 5.0;
    private static final double MAX_MOVE_TIME_SECS = 3.0;
    private final static Vertex ORIGIN = new Vertex(0.0, 0.0);
    
    private IdeaView rootView;
    private ActionListener modelUpdater = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            adjust();
        }
    };
    private Timer ticker = new Timer(50, modelUpdater);
    
    /** Creates a new instance of Fred */
    public IdeaMap() {
    }
    
    public void setIdea(Idea idea) {
        this.rootView = new IdeaView(idea);
        this.rootView.setParent(this);
        ticker.start();
    }
    
    public Idea getIdea() {
        return this.rootView.getIdea();
    }
    
    public void paintComponent(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        g.translate(size.width / 2, size.height / 2);
        rootView.paint(g);
    }
    
    long timeChanged = 0;
    
    List<Vertex> locations;
    private void adjust() {
        locations = new Vector<Vertex>();
        createLocations(rootView, new Position(ORIGIN, rootView.getAngle()));
        endForce(rootView, new Position(ORIGIN, 0.0));
        repaint();
    }
    
    private Vertex endForce(final IdeaView parentView, final Position p) {
        final List<IdeaView> views = parentView.getSubViews();
        if (views.size() == 0) {
            return new Vertex(0.0, 0.0);
        }
        double mass = 2000.0 / (locations.size()
        * Math.sqrt((double)locations.size()));
        Vertex totForce = new Vertex(0.0, 0.0);
        if (timeChanged == 0) {
            timeChanged = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        double maxSpeed = 0.0;
        if ((now - timeChanged) < (MAX_MOVE_TIME_SECS * 1000.0)) {
            maxSpeed = MAX_SPEED - ((now - timeChanged) * MAX_SPEED / 1000.0
                    / MAX_MOVE_TIME_SECS);
        } else {
            ticker.stop();
        }
        double minDiffAngle = Math.PI / 2 / views.size();
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
            Vertex point = getLocation(view, p);
            Vertex force = repulsion(point, view, mass, p);
            totForce = totForce.add(force);
            Vertex p2 = getLocation(view, new Position(ORIGIN, p.angle));
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
            view.setV(v);
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
                    v = view.getV();
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
                    v = view.getV();
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
                    v = view.getV();
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
                    v = view.getV();
                }
            }
            view.setAngle(newAngle);
        }
        return totForce;
    }

    private Vertex repulsion(final Vertex point, final IdeaView view, final double mass, final Position p) {
        Vertex force = new Vertex(0, 0);
        for(Vertex other: locations) {
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
    
    private void createLocations(IdeaView parentView, Position p) {
        List<IdeaView> views = parentView.getSubViews();
        locations.add(ORIGIN);
        for(IdeaView view: views) {
            Vertex location = getLocation(view, p);
            locations.add(location);
            createLocations(view, new Position(location, p.angle + view.getAngle()));
        }
    }
    
    private Vertex getLocation(IdeaView view, Position p) {
        double angle = view.getAngle() + p.angle;
        double length = view.getLength();
        double x = p.start.x + Math.sin(angle) * length;
        double y = p.start.y + Math.cos(angle) * length;
        return new Vertex(x, y);
    }
    
    public void repaintRequired() {
        timeChanged = System.currentTimeMillis();
        ticker.start();
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
        return (new Point2D.Double(x, y)).distanceSq(new Point2D.Double(other.x, other.y));
    }
    
    double distance(Vertex other) {
        return (new Point2D.Double(x, y)).distance(new Point2D.Double(other.x, other.y));
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
