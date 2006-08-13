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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author davidg
 */
public class Fred extends JComponent {
    private static final double MAX_SPEED = 10.0;
    private static final double MAX_MOVE_TIME_SECS = 3.0;
    private final static Point2D.Double ORIGIN = new Point2D.Double(0.0, 0.0);
    
    private List<Fred2> rootIdeas;
    
    /** Creates a new instance of Fred */
    public Fred() {
        buildModel();
    }
    
    private void buildModel() {
        rootIdeas = new ArrayList<Fred2>();
        final int lines = 15;
        (new Thread(){public void run() {
            for (int i = 0; i < lines; i++) {
                Fred2 fred2 = new Fred2();
            fred2.setLength(100 + 100 * i / lines);
//            fred2.setLength(100 + 100 * i / lines + ((int)Math.random() * 100));
//                fred2.setLength(100 + (int)(Math.random() * 200));
//            fred2.setLength(200);
                fred2.setAngle(i * Math.PI / lines);
                synchronized(rootIdeas) {
                    rootIdeas.add(fred2);
                    timeAdded = System.currentTimeMillis();
                }
                //try { Thread.sleep(100);} catch(Exception e){}
            }
            List<Fred2> subs = rootIdeas.get(0).getSubIdeas();
            
            Fred2 subIdea0 = null;
            for (int i = 0; i < 4; i++) {
                subIdea0 = new Fred2();
                subIdea0.setLength(100);
                subIdea0.setAngle(Math.PI / 4);
                synchronized(subs) {
                    subs.add(subIdea0);
                    timeAdded = System.currentTimeMillis();
                }
                try { Thread.sleep(1000);} catch(Exception e){}
            }
            
            List<Fred2> subs2 = subIdea0.getSubIdeas();
            for (int i = 0; i < 6; i++) {
                Fred2 subIdea2 = new Fred2();
                subIdea2.setLength(100);
                subIdea2.setAngle(Math.PI / 4);
                synchronized(subs2) {
                    subs2.add(subIdea2);
                    timeAdded = System.currentTimeMillis();
                }
                try { Thread.sleep(1000);} catch(Exception e){}
            }
        }}).start();
    }
    
    public void paintComponent(Graphics g) {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        Dimension size = getSize();
        List<Fred2> views = rootIdeas;
        Point c = new Point(size.width / 2, size.height / 2);
        displayIdeas(g, 0.0, c, views);
        adjustModel();
//               try { Thread.sleep(100);} catch(Exception e){}
        repaint(100);
    }
    
    
    private void displayIdeas(final Graphics g, final double initAngle, final Point c, final List<Fred2> ideas) {
        synchronized(ideas) {
            for (Fred2 idea: ideas) {
                double a = idea.getAngle() + initAngle;
                double len = idea.getLength();
                Point2D p = new Point2D.Double(Math.sin(a) * len, Math.cos(a) * len);
                Point s = getView(c, p);
                g.drawLine(c.x, c.y, s.x, s.y);
                Point midp = new Point((c.x + s.x) / 2, (c.y + s.y) / 2);
                double textAngle = (Math.PI / 2.0) - a;
                if ((a < 0) || (a > Math.PI)) {
                    textAngle += Math.PI;
                }
                drawString((Graphics2D)g, idea.getText(), midp, 4, textAngle);
                displayIdeas(g, idea.getAngle(), s, idea.getSubIdeas());
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
    
    List<Point2D> points;
    private void adjustModel() {
        points = new ArrayList<Point2D>();
        createPoints(rootIdeas, ORIGIN, 0.0);
        List<Fred2> ideas = rootIdeas;
        tweakIdeas(ideas, ORIGIN, 0.0, false);
    }
    
    private Point2D tweakIdeas(final List<Fred2> ideas, final Point2D c, final double initAngle, final boolean hasParent) {
        if (ideas.size() == 0) {
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
        synchronized(ideas) {
            double minDiffAngle = Math.PI / 2 / ideas.size();
            for (int i = 0; i < ideas.size(); i++) {
                Fred2 previousIdea = null;
                Fred2 nextIdea = null;
                Fred2 idea = ideas.get(i);
                if (i > 0) {
                    previousIdea = ideas.get(i - 1);
                }
                if (i < ideas.size() - 1) {
                    nextIdea = ideas.get(i + 1);
                }
                Point2D point = getPoint(idea, c, initAngle);
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
                Point2D p2 = getPoint(idea, ORIGIN, initAngle);
                Point2D tf = tweakIdeas(idea.getSubIdeas(), point, idea.getAngle(), true);
                forceX += tf.getX();
                forceY += tf.getY();
                double sideForce = (p2.getY() * forceX) + (-p2.getX() * forceY);
                totForceX += forceX;
                totForceY += forceY;
                double v = idea.getV();
                v += sideForce / mass;
                v *= 0.90;
                if (v > maxSpeed) {
                    v = maxSpeed;
                }
                if (v < -maxSpeed) {
                    v = -maxSpeed;
                }
                idea.setV(v);
                double oldAngle = idea.getAngle();
                double newAngle = oldAngle  + (idea.getV() / idea.getLength());
                if (previousIdea != null) {
                    double previousAngle = previousIdea.getAngle();
                    if (previousAngle > newAngle - minDiffAngle) {
                        previousIdea.setAngle(newAngle - minDiffAngle);
                        newAngle = previousAngle + minDiffAngle;
                        double previousV = previousIdea.getV();
                        double diffV = v - previousV;
                        if (diffV > 0) {
                            idea.setV(diffV);
                            previousIdea.setV(-diffV);
                        } else {
                            idea.setV(-diffV);
                            previousIdea.setV(diffV);
                        }
                        v = idea.getV();
                    }
                } else {
                    double previousAngle = -Math.PI;
                    if (!hasParent) {
                        previousAngle = ideas.get(ideas.size() - 1).getAngle() - 2 * Math.PI;
                    }
                    if (previousAngle > newAngle - minDiffAngle) {
                        newAngle = previousAngle + minDiffAngle;
                        double previousV = 0.0;
                        double diffV = v - previousV;
                        if (diffV > 0) {
                            idea.setV(diffV);
                        } else {
                            idea.setV(-diffV);
                        }
                        v = idea.getV();
                    }
                }
                if (nextIdea != null) {
                    double nextAngle = nextIdea.getAngle();
                    if (nextAngle < newAngle + minDiffAngle) {
                        nextIdea.setAngle(newAngle + minDiffAngle);
                        newAngle = nextAngle - minDiffAngle;
                        double nextV = nextIdea.getV();
                        double diffV = 0.0;
                        if (diffV > 0) {
                            idea.setV(-diffV);
                            nextIdea.setV(diffV);
                        } else {
                            idea.setV(diffV);
                            nextIdea.setV(-diffV);
                        }
                        v = idea.getV();
                    }
                } else {
                    double nextAngle = Math.PI;
                    if (!hasParent) {
                        nextAngle = ideas.get(0).getAngle() +  2 * Math.PI;
                    }
                    if (nextAngle < newAngle + minDiffAngle) {
                        newAngle = nextAngle - minDiffAngle;
                        double nextV = 0.0;
                        double diffV = 0.0;
                        if (diffV > 0) {
                            idea.setV(-diffV);
                        } else {
                            idea.setV(diffV);
                        }
                        v = idea.getV();
                    }
                }
                idea.setAngle(newAngle);
            }
        }
        return new Point2D.Double(totForceX, totForceY);
    }
    
    private void createPoints(List<Fred2> ideas, Point2D c, double initAngle) {
        points.add(ORIGIN);
        synchronized(ideas) {
            for(Fred2 idea: ideas) {
                Point2D point = getPoint(idea, c, initAngle);
                points.add(point);
                createPoints(idea.getSubIdeas(), point, idea.getAngle());
            }
        }
    }
    
    private Point2D getPoint(Fred2 idea, Point2D c, double initAngle) {
        double angle = idea.getAngle() + initAngle;
        double length = idea.getLength();
        double x = c.getX() + Math.sin(angle) * length;
        double y = c.getY() + Math.cos(angle) * length;
        return new Point2D.Double(x, y);
    }
    
    private static class Fred2 {
        private double length;
        private double angle;
        private double v;
        private String text = "hipster";
        private List<Fred2> subIdeas = new ArrayList<Fred2>();
        
        public Fred2() {
            
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
        
        public List<Fred2> getSubIdeas() {
            return subIdeas;
        }
        
        public double getV() {
            return v;
        }
        
        public void setV(double v) {
            this.v = v;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
    
}
