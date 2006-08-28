/*
 * Fred.java
 *
 * Created on July 20, 2006, 9:49 AM
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

import dg.hipster.io.IdeaReader;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaEvent;
import dg.hipster.model.IdeaListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Vector;
import javax.swing.JComponent;

/**
 *
 * @author davidg
 */
public class Fred extends JComponent implements IdeaListener {
    private static final double MAX_SPEED = 2.0;
    private static final double MAX_MOVE_TIME_SECS = 15.0;
    private final static Point2D.Double ORIGIN = new Point2D.Double(0.0, 0.0);
    
    private Idea rootIdea;
    private IdeaView rootView;
    
    /** Creates a new instance of Fred */
    public Fred() {
        buildModel();
    }
    
    private void buildModel() {
        ReaderFactory factory = ReaderFactory.getInstance();
        try {
            IdeaReader reader = factory.read(new File("etc/test.opml"));
            rootIdea = reader.getIdea();
            rootView = new IdeaView(rootIdea);
        } catch(ReaderException re) {
            re.printStackTrace();
        }
        
//        rootIdea = new Idea("Persistence");
//        rootView = new IdeaView(rootIdea);
//
//
//
//        Idea mistakes = new Idea("Mistakes");
//        Idea platforms = new Idea("Platforms");
//        mistakes.add(platforms);
//        Idea attempts = new Idea("Attempts");
//        platforms.add(attempts);
//        Idea continual = new Idea("Continual");
//        attempts.add(continual);
//        Idea further = new Idea("Further");
//        attempts.add(further);
//        Idea enjoyed = new Idea("Enjoyed");
//        attempts.add(enjoyed);
//        Idea thousands = new Idea("Thousands");
//        mistakes.add(thousands);
//        Idea making = new Idea("Making");
//        mistakes.add(making);
//        Idea progress = new Idea("Progress");
//        mistakes.add(progress);
//        Idea learning = new Idea("Learning");
//        rootIdea.add(learning);
//        Idea love = new Idea("Love");
//        learning.add(love);
//        love.add(mistakes);
//        rootIdea.add(mistakes);
        
        
        
        
//        final int lines = 35;
//        rootIdea = new Idea("Test pattern");
//        rootView = new IdeaView(rootIdea);
//        (new Thread(){public void run() {
//            for (int i = 0; i < lines; i++) {
//                Idea fred2 = new Idea("      i = " + i);
//                synchronized(rootIdea) {
//                    rootIdea.add(fred2);
//                    timeChanged = System.currentTimeMillis();
//                }
//                //try { Thread.sleep(100);} catch(Exception e){}
//            }
//
//            Idea sub = rootIdea.getSubIdeas().get(0);
//
//            Idea subIdea0 = null;
//            for (int i = 0; i < 4; i++) {
//                subIdea0 = new Idea("i = " + i);
//                sub.add(subIdea0);
//                timeChanged = System.currentTimeMillis();
//                try { Thread.sleep(1000);} catch(Exception e){}
//            }
//            try { Thread.sleep(10000);} catch(Exception e){}
//
//            Idea s2 = subIdea0;
//            for (int i = 0; i < 6; i++) {
//                Idea subIdea2 = new Idea("i = " + i);
//                s2.add(subIdea2);
//                timeChanged = System.currentTimeMillis();
//                try { Thread.sleep(1000);} catch(Exception e){}
//            }
//        }}).start();
    }
    
    public void paintComponent(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        g.translate(size.width / 2, size.height / 2);
        rootView.paint(g);
        adjustModel();
        repaint(100);
    }
    
    public void ideaChanged(IdeaEvent e) {
        timeChanged = System.currentTimeMillis();
    }
    
    
    long timeChanged = 0;
    
    Vector<Point2D> points;
    private void adjustModel() {
        points = new Vector<Point2D>();
        Vector<IdeaView> views = rootView.getSubViews();
        createPoints(rootView, ORIGIN, rootView.getAngle());
        tweakIdeas(views, ORIGIN, 0.0, false);
    }
    
    private Point2D tweakIdeas(final Vector<IdeaView> views, final Point2D c,
            final double initAngle, final boolean hasParent) {
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
                Point2D point = getPoint(view, c, initAngle);
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
                Point2D p2 = getPoint(view, ORIGIN, initAngle);
                Point2D tf = tweakIdeas(view.getSubViews(), point,
                        view.getAngle() + initAngle, true);
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
    
    private void createPoints(IdeaView parentView, Point2D c, double initAngle) {
        Vector<IdeaView> views = parentView.getSubViews();
        points.add(ORIGIN);
        synchronized(views) {
            for(IdeaView view: views) {
                Point2D point = getPoint(view, c, initAngle);
                points.add(point);
                createPoints(view, point, initAngle + view.getAngle());
            }
        }
    }
    
    private Point2D getPoint(IdeaView view, Point2D c, double initAngle) {
        double angle = view.getAngle() + initAngle;
        double length = view.getLength();
        double x = c.getX() + Math.sin(angle) * length;
        double y = c.getY() + Math.cos(angle) * length;
        return new Point2D.Double(x, y);
    }
}
