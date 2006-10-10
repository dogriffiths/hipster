/*
 * BranchView.java
 *
 * Created on September 30, 2006, 7:55 AM
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

import dg.hipster.model.Idea;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Point2D;

/**
 *
 * @author davidg
 */
public class BranchView extends IdeaView {
    public static final Color[] COLOURS = {new Color(255, 20, 20), Color.ORANGE,
    new Color(20, 200, 20), Color.CYAN};
    private double thickness;
    private Point2D fromPoint;
    private Point2D toPoint;
    /**
     * Default width of the stroke used to render branches.
     */
    public static final float DEFAULT_STROKE_WIDTH = 20.0f;
    
    /** Creates a new instance of BranchView */
    public BranchView() {
        this(null);
    }
    
    public BranchView(Idea anIdea) {
        super(anIdea);
    }
    
    void paint(final Graphics g, final int depth, final IdeaMap map) {
        double a = getRealAngle();
        Point c = new Point((int)fromPoint.getX(), (int)fromPoint.getY());
        Point s = new Point((int)toPoint.getX(), (int)toPoint.getY());
        paintBranches(g, s, this, a, depth + 1, map);
        Color colour = COLOURS[depth % COLOURS.length];
        if (this.isSelected()) {
            colour = invert(colour);
        }
        Color upper = colour.brighter().brighter();
        Color lower = colour.darker().darker();
        Stroke oldStroke = ((Graphics2D)g).getStroke();
        float strokeWidth = DEFAULT_STROKE_WIDTH - (depth * 2);
        if (strokeWidth < (DEFAULT_STROKE_WIDTH / 2)) {
            strokeWidth = DEFAULT_STROKE_WIDTH / 2;
        }
        Stroke stroke = new BasicStroke(strokeWidth,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        ((Graphics2D)g).setStroke(stroke);
        this.thickness = strokeWidth;
        g.setColor(lower);
        g.drawLine(c.x, c.y + 1, s.x, s.y + 1);
        g.setColor(upper);
        g.drawLine(c.x, c.y - 1, s.x, s.y - 1);
        g.setColor(colour);
        g.drawLine(c.x, c.y, s.x, s.y);
        ((Graphics2D)g).setStroke(oldStroke);
        if (this.isSelected()) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        Point midp = new Point((c.x + s.x) / 2, (c.y + s.y) / 2);
        double textAngle = (Math.PI / 2.0) - a;
        drawString((Graphics2D)g, this.getIdea().getText(), midp, 4,
                textAngle, this.isEditing(), map);
    }
    
    boolean hits(Point2D p) {
        Point2D fromPoint = this.getFromPoint();
        Point2D toPoint = this.getToPoint();
        if ((fromPoint == null) || (toPoint == null)) {
            return false;
        }
        double vx0 = fromPoint.getX();
        double vy0 = fromPoint.getY();
        double vx1 = toPoint.getX();
        double vy1 = toPoint.getY();
        double vx2 = p.getX();
        double vy2 = p.getY();
        
        double minX = Math.min(vx0, vx1) - thickness / 2;
        double maxX = Math.max(vx0, vx1) + thickness / 2;
        double minY = Math.min(vy0, vy1) - thickness / 2;
        double maxY = Math.max(vy0, vy1) + thickness / 2;
        
        if ((vx2 > maxX) || (vx2 < minX)) {
            return false;
        }
        if ((vy2 > maxY) || (vy2 < minY)) {
            return false;
        }
        
        // Calculate magnitude of the normal to the line-segment
        double magNormal = Math.sqrt(
                ((vx1 - vx0) * (vx1 - vx0)) + ((vy1 - vy0) * (vy1 - vy0))
                );
        
        // Calculate (signed) distance of the point from the line-segment
        double distance = (
                ((vx2 - vx0) * (vy0 - vy1)) + ((vy2 - vy0) * (vx1 - vx0))
                ) / magNormal;
        
        // Check if the
        if (Math.abs(distance) <= (thickness / 2)) {
            return true;
        }
        return false;
    }
    
    void setFromPoint(Point2D f) {
        this.fromPoint = f;
    }
    
    public Point2D getFromPoint() {
        return this.fromPoint;
    }
    
    void setToPoint(Point2D t) {
        this.toPoint = t;
    }
    
    public Point2D getToPoint() {
        return this.toPoint;
    }
    
    private void paintBezier(Graphics g, Point[] coordlist) {
        double x1,x2,y1,y2;
        x1 = coordlist[0].x;
        y1 = coordlist[0].y;
        double k = 0.025;
        for(double t = k;t <= 1 + k;t += k){
            x2=(coordlist[0].x + t * (-coordlist[0].x * 3 + t * (3 * coordlist[0].x
                    - coordlist[0].x * t)))+t*(3*coordlist[1].x+t*(-6*coordlist[1].x+
                    coordlist[1].x*3*t))+t*t*(coordlist[2].x*3-coordlist[2].x*3*t)+
                    coordlist[3].x*t*t*t;
            y2=(coordlist[0].y+t*(-coordlist[0].y*3+t*(3*coordlist[0].y-
                    coordlist[0].y*t)))+t*(3*coordlist[1].y+t*(-6*coordlist[1].y+
                    coordlist[1].y*3*t))+t*t*(coordlist[2].y*3-coordlist[2].y*3*t)+
                    coordlist[3].y*t*t*t;
            g.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
            x1 = x2;
            y1 = y2;
        }
    }
    
    void initFromTo(final Point c, final double initAngle) {
        double a = this.getIdea().getAngle() + initAngle;
        this.setRealAngle(a);
        double len = this.getIdea().getLength();
        Point2D p = new Point2D.Double(Math.sin(a) * len,
                Math.cos(a) * len);
        Point s = this.getView(c, p);
        for (BranchView subView : this.getSubViews()) {
            subView.initFromTo(s, a);
        }
        this.setFromPoint(c);
        this.setToPoint(s);
    }
    
    
    //
    // Links code
    //
    
    void paintLinks(final Graphics g) {
        IdeaView rootView = getRootView();
        Stroke oldStroke = ((Graphics2D)g).getStroke();
        float strokeWidth = DEFAULT_STROKE_WIDTH / 2;
        Stroke stroke = new BasicStroke(strokeWidth,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        for (Idea link: this.getIdea().getLinks()) {
            IdeaView linkView = rootView.getViewFor(link);
            if ((linkView != null) && (linkView instanceof BranchView)) {
                BranchView branch = (BranchView) linkView;
                g.setColor(Color.GRAY);
                Point2D start1 = branch.getFromPoint();
                Point2D end1 = branch.getEndPoint();
                ((Graphics2D)g).setStroke(stroke);
                drawCurve(g, fromPoint, toPoint, start1, end1);
                ((Graphics2D)g).setStroke(oldStroke);
            }
        }
        for (BranchView branch : this.getSubViews()) {
            branch.paintLinks(g);
        }
    }
    
    private void drawCurve(final Graphics g, final Point2D start0,
            final Point2D end0, final Point2D start1, final Point2D end1) {
        if ((start1 != null) && (end1 != null)
        && (start0 != null) && (end0 != null)) {
            Point[] p = new Point[4];
            
            Point s0 = intPoint(start0);
            Point s1 = intPoint(start1);
            Point e0 = intPoint(end0);
            Point e1 = intPoint(end1);
            
            Point v0 = minus(s0, e0);
            Point v1 = minus(s1, e1);
            Point n0 = normal(v0);
            Point n1 = normal(v1);
            
            p[0] = mid(s0, e0);
            p[3] = mid(s1, e1);
            
            p[1] = plus(p[0], n0);
            if (dot(minus(s0, p[1]), n0) * dot(minus(s0, s1), n0) < 0) {
                p[1] = minus(p[0], n0);
            }
            
            p[2] = plus(p[3], n1);
            if (dot(minus(s1, p[2]), n1) * dot(minus(s1, s0), n1) < 0) {
                p[2] = minus(p[3], n1);
            }
            paintBezier(g, p);
        }
    }
    
    private static Point normal(Point p) {
        return new Point(-p.y, p.x);
    }
    
    private static int dot(Point p0, Point p1) {
        return p0.x * p1.x + p0.y * p1.y;
    }
    
    private static Point plus(Point p0, Point p1) {
        return new Point(p0.x + p1.x, p0.y + p1.y);
    }
    
    private static Point minus(Point p0, Point p1) {
        return new Point(p0.x - p1.x, p0.y - p1.y);
    }
    
    private static Point intPoint(Point2D p) {
        return new Point((int) p.getX(), (int) p.getY());
    }
    
    private static Point mid(Point p0, Point p1) {
        return new Point((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);
    }
    
    public Point2D getMidPoint() {
        return new Point2D.Double(
                (fromPoint.getX() + toPoint.getX()) / 2,
                (fromPoint.getY() + toPoint.getY()) / 2
                );
    }
}
