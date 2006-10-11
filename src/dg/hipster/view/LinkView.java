/*
 * LinkView.java
 *
 * Created on 10 October 2006, 21:17
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

import dg.hipster.Utilities;
import dg.hipster.model.IdeaLink;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author dgriffiths
 */
public class LinkView extends IdeaView {
    private static int NUM_POINTS = 11;
    private Point2D[] linePoints;
    
    /** Creates a new instance of LinkView */
    public LinkView(IdeaLink aLink) {
        super(aLink);
    }
    
    public boolean hits(Point2D p) {
        initPoints();
        for (int i = 0; i < linePoints.length - 1; i++) {
            double strokeWidth = BranchView.DEFAULT_STROKE_WIDTH / 2;
            if (Utilities.hitsLine(p, linePoints[i], linePoints[i + 1],
                    strokeWidth)) {
                return true;
            }
        }
        return false;
    }
    
    public IdeaLink getLink() {
        return (IdeaLink)super.getIdea();
    }
    
    public void setLink(IdeaLink link) {
        super.setIdea(link);
    }
    
    
    public void paintLink(final Graphics g) {
        initPoints();
        IdeaLink link = getLink();
        BranchView fromBranch = (BranchView) getRootView().getViewFor(
                link.getFrom());
        Stroke oldStroke = ((Graphics2D)g).getStroke();
        float strokeWidth = BranchView.DEFAULT_STROKE_WIDTH / 2;
        Stroke stroke = new BasicStroke(strokeWidth,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        if (this.isSelected()) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.GRAY);
        }
        ((Graphics2D)g).setStroke(stroke);
        for(int i = 0; i < linePoints.length - 1; i++){
            ((Graphics2D)g).draw(new Line2D.Double(linePoints[i],
                    linePoints[i + 1]));
        }
        ((Graphics2D)g).setStroke(oldStroke);
        int circleRadius = (int)(BranchView.DEFAULT_STROKE_WIDTH * 3 / 4);
        Point2D midPoint = fromBranch.getMidPoint();
        g.fillOval((int)midPoint.getX() - circleRadius,
                (int)midPoint.getY() - circleRadius,
                circleRadius * 2, circleRadius * 2);
    }
    
    private void initPoints() {
        IdeaLink link = getLink();
        BranchView fromBranch = (BranchView) getRootView().getViewFor(
                link.getFrom());
        BranchView branch = (BranchView) getRootView().getViewFor(link.getTo());
        Point2D start0 = fromBranch.getFromPoint();
        Point2D end0 = fromBranch.getToPoint();
        Point2D start1 = branch.getFromPoint();
        Point2D end1 = branch.getEndPoint();
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
            createBezier(p);
        }
    }
    
    private void createBezier(final Point[] coordlist) {
        linePoints = new Point2D.Double[NUM_POINTS];
        linePoints[0] = new Point2D.Double(coordlist[0].x, coordlist[0].y);
        double k = 1.0 / (linePoints.length - 1);
        double t = k;
        for(int count = 1; count < linePoints.length; count++){
            linePoints[count] = new Point2D.Double(
                    ((coordlist[0].x
                    + t * (-coordlist[0].x * 3
                    + t * (3 * coordlist[0].x
                    - coordlist[0].x * t)))
                    + t * (3*coordlist[1].x
                    + t * (-6*coordlist[1].x
                    + coordlist[1].x * 3 * t))
                    + t * t * (coordlist[2].x * 3
                    - coordlist[2].x * 3 * t)
                    + coordlist[3].x*t * t * t),
                    (((coordlist[0].y
                    + t * (-coordlist[0].y * 3
                    + t * (3 * coordlist[0].y
                    - coordlist[0].y * t)))
                    + t * (3 * coordlist[1].y
                    + t * (-6*coordlist[1].y
                    + coordlist[1].y * 3 * t))
                    + t * t *(coordlist[2].y * 3
                    -coordlist[2].y * 3 * t)
                    + coordlist[3].y * t * t * t)));
            t += k;
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
}
