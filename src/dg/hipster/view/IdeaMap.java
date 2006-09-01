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
    private final static Point2D.Double ORIGIN = new Point2D.Double(0.0, 0.0);
    
    private IdeaView rootView;
    private ActionListener modelUpdater = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            adjustModel();
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
    
    List<Point2D> points;
    private void adjustModel() {
        points = new Vector<Point2D>();
        createPoints(rootView, new Position(ORIGIN, rootView.getAngle()));
        tweakIdeas(rootView, new Position(ORIGIN, 0.0));
        repaint();
    }
    
    private Point2D tweakIdeas(final IdeaView parentView, final Position p) {
        final List<IdeaView> views = parentView.getSubViews();
        final boolean hasParent = (parentView.getParent() instanceof IdeaView);
        if (views.size() == 0) {
            return new Point2D.Double(0.0, 0.0);
        }
        double mass = 2000.0 / (points.size()
        * Math.sqrt((double)points.size()));
        double totForceX = 0.0;
        double totForceY = 0.0;
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
        synchronized(views) {
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
                Point2D point = getPoint(view, p);
                double forceX = 0.0;
                double forceY = 0.0;
                for(Point2D other: points) {
                    double dirX = point.getX() - other.getX();
                    double dirY = point.getY() - other.getY();
                    double dSquare = point.distanceSq(other);
                    if (dSquare > 0.000001) {
                        double unitFactor = point.distance(other);
                        forceX += (dirX / unitFactor) * (mass * mass / dSquare);
                        if (forceX > 1.0) {
                            forceX = 1.0;
                        }
                        if (forceX < -1.0) {
                            forceX = -1.0;
                        }
                        forceY += (dirY / unitFactor) * (mass * mass / dSquare);
                        if (forceY > 1.0) {
                            forceY = 1.0;
                        }
                        if (forceY < -1.0) {
                            forceY = -1.0;
                        }
                    }
                }
                Point2D p2 = getPoint(view, new Position(ORIGIN, p.angle));
                Point2D tf = tweakIdeas(view, new Position(point,
                        view.getAngle() + p.angle));
                forceX += tf.getX();
                forceY += tf.getY();
                double sideForce = (p2.getY() * forceX) + (-p2.getX() * forceY);
                totForceX += forceX;
                totForceY += forceY;
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
                    if (!hasParent) {
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
                    if (!hasParent) {
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
        }
        return new Point2D.Double(totForceX, totForceY);
    }
    
    private void createPoints(IdeaView parentView, Position p) {
        List<IdeaView> views = parentView.getSubViews();
        points.add(ORIGIN);
        for(IdeaView view: views) {
            Point2D point = getPoint(view, p);
            points.add(point);
            createPoints(view, new Position(point, p.angle + view.getAngle()));
        }
    }
    
    private Point2D getPoint(IdeaView view, Position p) {
        double angle = view.getAngle() + p.angle;
        double length = view.getLength();
        double x = p.start.getX() + Math.sin(angle) * length;
        double y = p.start.getY() + Math.cos(angle) * length;
        return new Point2D.Double(x, y);
    }
    
    public void repaintRequired() {
        timeChanged = System.currentTimeMillis();
        ticker.start();
    }
}

class Position {
    Point2D start;
    double angle;
    Position(Point2D aStart, double anAngle) {
        this.start = aStart;
        this.angle = anAngle;
    }
}