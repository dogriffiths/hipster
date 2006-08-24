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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Vector;
import javax.swing.JComponent;

/**
 *
 * @author davidg
 */
public class Fred extends JComponent {
    private static final double MAX_SPEED = 10.0;
    private static final double MAX_MOVE_TIME_SECS = 3.0;
    private final static Point2D.Double ORIGIN = new Point2D.Double(0.0, 0.0);
    
    private Fred2 root;
    private Fred2View rootView;
    
    /** Creates a new instance of Fred */
    public Fred() {
        buildModel();
    }
    
    private void buildModel() {
        root = new Fred2();
        rootView = new Fred2View(root);
        final int lines = 15;
        (new Thread(){public void run() {
            for (int i = 0; i < lines; i++) {
                Fred2 fred2 = new Fred2();
                synchronized(root) {
                    root.add(fred2);
                    timeAdded = System.currentTimeMillis();
                }
                //try { Thread.sleep(100);} catch(Exception e){}
            }
            
            Fred2 sub = root.getSubIdeas().get(0);
            
            Fred2 subIdea0 = null;
            for (int i = 0; i < 4; i++) {
                subIdea0 = new Fred2();
                sub.add(subIdea0);
                timeAdded = System.currentTimeMillis();
                try { Thread.sleep(1000);} catch(Exception e){}
            }
            
            Fred2 s2 = subIdea0;
            for (int i = 0; i < 6; i++) {
                Fred2 subIdea2 = new Fred2();
                s2.add(subIdea2);
                timeAdded = System.currentTimeMillis();
                try { Thread.sleep(1000);} catch(Exception e){}
            }
        }}).start();
    }
    
    public void paintComponent(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        Vector<Fred2View> views = rootView.getSubViews();
        Point c = new Point(size.width / 2, size.height / 2);
        displayIdeas(g, 0.0, c, views);
        adjustModel();
        repaint(100);
    }
    
    
    private void displayIdeas(final Graphics g, final double initAngle, final Point c, final Vector<Fred2View> views) {
        synchronized(views) {
            for (Fred2View view: views) {
                double a = view.getAngle() + initAngle;
                double len = view.getLength();
                Point2D p = new Point2D.Double(Math.sin(a) * len, Math.cos(a) * len);
                Point s = getView(c, p);
                g.drawLine(c.x, c.y, s.x, s.y);
                Point midp = new Point((c.x + s.x) / 2, (c.y + s.y) / 2);
                double textAngle = (Math.PI / 2.0) - a;
                if ((a < 0) || (a > Math.PI)) {
                    textAngle += Math.PI;
                }
                drawString((Graphics2D)g, view.getIdea().getText(), midp, 4, textAngle);
                displayIdeas(g, view.getAngle(), s, view.getSubViews());
            }
        }
    }
    
    public void transform(Graphics2D graphics2d, double orientation, int x, int y) {
        graphics2d.transform(
                AffineTransform.getRotateInstance(
                -orientation, x, y
                )
                );
    }
    public void drawString(Graphics2D graphics2d, String string, Point p, int alignment,
            double orientation) {
        
        int xc = p.x;
        int yc = p.y;
        
        AffineTransform transform = graphics2d.getTransform();
        
        transform(graphics2d, orientation, xc, yc);
        
        FontMetrics fm = graphics2d.getFontMetrics();
        double realTextWidth = fm.stringWidth(string);
        double realTextHeight = fm.getHeight();
        
        int offsetX = (int) (realTextWidth * (double) (alignment % 3) / 2.0);
        int offsetY = (int) (-realTextHeight * (double) (alignment / 3) / 2.0);
        
        if ((graphics2d != null) && (string != null)) {
            graphics2d.drawString(string, xc - offsetX, yc - offsetY);
        }
        
        graphics2d.setTransform(transform);
    }
    
    private Point getView(final Point c, final Point2D p) {
        int sx = c.x + (int)p.getX();
        int sy = c.y - (int)p.getY();
        return new Point(sx, sy);
    }
    long timeAdded = 0;
    
    Vector<Point2D> points;
    private void adjustModel() {
        points = new Vector<Point2D>();
        Vector<Fred2View> views = rootView.getSubViews();
        createPoints(views, ORIGIN, 0.0);
        tweakIdeas(views, ORIGIN, 0.0, false);
    }
    
    private Point2D tweakIdeas(final Vector<Fred2View> views, final Point2D c, final double initAngle, final boolean hasParent) {
        if (views.size() == 0) {
            return new Point2D.Double(0.0, 0.0);
        }
        double mass = 2000.0 / (points.size() * Math.sqrt((double)points.size()));
        double totForceX = 0.0;
        double totForceY = 0.0;
        if (timeAdded == 0) {
            timeAdded = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        double maxSpeed = 0.0;
        if ((now - timeAdded) < (MAX_MOVE_TIME_SECS * 1000.0)) {
            maxSpeed = MAX_SPEED - ((now - timeAdded) * MAX_SPEED / 1000.0 / MAX_MOVE_TIME_SECS);
        }
        synchronized(views) {
            double minDiffAngle = Math.PI / 2 / views.size();
            for (int i = 0; i < views.size(); i++) {
                Fred2View previousView = null;
                Fred2View nextView = null;
                Fred2View view = views.get(i);
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
                Point2D tf = tweakIdeas(view.getSubViews(), point, view.getAngle(), true);
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
                        previousAngle = views.get(views.size() - 1).getAngle() - 2 * Math.PI;
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
    
    private void createPoints(Vector<Fred2View> views, Point2D c, double initAngle) {
        points.add(ORIGIN);
        synchronized(views) {
            for(Fred2View view: views) {
                Point2D point = getPoint(view, c, initAngle);
                points.add(point);
                createPoints(view.getSubViews(), point, view.getAngle());
            }
        }
    }
    
    private Point2D getPoint(Fred2View view, Point2D c, double initAngle) {
        double angle = view.getAngle() + initAngle;
        double length = view.getLength();
        double x = c.getX() + Math.sin(angle) * length;
        double y = c.getY() + Math.cos(angle) * length;
        return new Point2D.Double(x, y);
    }
    
    private static class Fred2 {
        private String text = "hipster";
        private Vector<Fred2> subIdeas = new Vector<Fred2>();
        private Vector<Fred2Listener> listeners = new Vector<Fred2Listener>();
        
        public Fred2() {
            
        }
        
        public synchronized void add(Fred2 subIdea) {
            subIdeas.add(subIdea);
            notify("ADDED", new Object[] {subIdea});
        }
        
        public synchronized void remove(Fred2 subIdea) {
            subIdeas.remove(subIdea);
            notify("REMOVED", new Object[] {subIdea});
        }
        
        public void addFred2Listener(Fred2Listener al) {
            listeners.add(al);
        }
        
        public void removeFred2Listener(Fred2Listener al) {
            listeners.remove(al);
        }
        
        private void notify(String command, Object[] paras) {
            for (Fred2Listener listener: listeners) {
                listener.fred2Changed(new Fred2Event(this, command, paras));
            }
        }
        
        public synchronized Vector<Fred2> getSubIdeas() {
            return subIdeas;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
    }
    
    
    
    
    
    private static class Fred2View implements Fred2Listener {
        private double length;
        private double angle;
        private double v;
        private Vector<Fred2View> subViews = new Vector<Fred2View>();
        private Fred2 idea;
        
        public Fred2View(Fred2 anIdea) {
            this.idea = anIdea;
            anIdea.addFred2Listener(this);
        }
        
        public double getLength() {
            return length;
        }
        
        public void setLength(double length) {
            this.length = length;
        }
        
        public double getAngle() {
            return angle;
        }
        
        public void setAngle(double angle) {
            this.angle = angle;
        }
        public void fred2Changed(Fred2Event fe) {
            String cmd = fe.getCommand();
            if ("ADDED".equals(cmd)) {
                Fred2 subIdea = (Fred2)fe.getParas()[0];
                Fred2View subView = new Fred2View(subIdea);
                 subView.setLength(100);
                subView.setAngle(Math.PI / 4);
               add(subView);
            } else if ("REMOVED".equals(cmd)) {
                Fred2 subIdea = (Fred2)fe.getParas()[0];
                for (int i = 0; i < subViews.size(); i++) {
                    Fred2 idea = (Fred2)subViews.get(i).getIdea();
                    if (idea.equals(subIdea)) {
                        subViews.remove(i);
                        break;
                    }
                }
            }
        }
        
        public synchronized void add(Fred2View subView) {
            subViews.add(subView);
        }
        
        public synchronized void remove(Fred2View subView) {
            subViews.remove(subView);
        }
        
        public synchronized Vector<Fred2View> getSubViews() {
            return subViews;
        }
        
        public double getV() {
            return v;
        }
        
        public void setV(double v) {
            this.v = v;
        }
        
        public Fred2 getIdea() {
            return idea;
        }
    }
    
}

interface Fred2Listener {
    public void fred2Changed(Fred2Event fe);
}

class Fred2Event {
    private Object source;
    private String command;
    private Object[] paras;
    
    public Fred2Event(Object aSource, String aCommand,
            Object[] theParas) {
        this.source = aSource;
        this.command = aCommand;
        this.paras = theParas;
    }
    
    public Object getSource() {
        return source;
    }
    
    public void setSource(Object source) {
        this.source = source;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public Object[] getParas() {
        return paras;
    }
    
    public void setParas(Object[] paras) {
        this.paras = paras;
    }
}