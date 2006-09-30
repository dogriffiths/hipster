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
    private static Color[] COLOURS = {new Color(255, 20, 20), Color.ORANGE,
    new Color(20, 200, 20), Color.CYAN};
    private double thickness;
    private Point2D fromPoint;
    private Point2D toPoint;
    
    /** Creates a new instance of BranchView */
    public BranchView() {
        this(null);
    }
    
    public BranchView(Idea anIdea) {
        super(anIdea);
    }
    
    void paint(final Graphics g, final Point c2,
            final double initAngle, final int depth,
            final IdeaMap map, final CentreView rootView, final IdeaView aView) {
        Point c = new Point(c2.x, c2.y);
        double a = this.getAngle() + initAngle;
        setRealAngle(a);
        double len = this.getLength();
        Point2D p = new Point2D.Double(Math.sin(a) * len,
                Math.cos(a) * len);
        if (aView instanceof CentreView) {
            c.x += (int) (Math.sin(a) * rootView.ROOT_RADIUS_X);
            c.y -= (int) (Math.cos(a) * rootView.ROOT_RADIUS_Y);
        }
        Point s = getView(c, p);
        paintBranches(g, s, this, a, depth + 1, map, rootView);
        Color colour = COLOURS[depth % COLOURS.length];
        if (this.isSelected()) {
            colour = invert(colour);
        }
        Color upper = colour.brighter().brighter();
        Color lower = colour.darker().darker();
        setFromPoint(c);
        setToPoint(s);
        Stroke oldStroke = ((Graphics2D)g).getStroke();
        float strokeWidth = 20.0f - (depth * 2);
        if (strokeWidth < 10.0f) {
            strokeWidth = 10.0f;
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
        Point2D start0 = s;
        Point2D end0 = c;
        Point2D mid0 = new Point2D.Double(
                (start0.getX() + end0.getX()) / 2,
                (start0.getY() + end0.getY()) / 2
                );
        for (Idea link: this.getIdea().getLinks()) {
            IdeaView linkView = rootView.getViewFor(link);
            if ((linkView != null) && (linkView instanceof BranchView)) {
                BranchView branch = (BranchView)linkView;
                Point2D start1 = branch.getFromPoint();
                Point2D end1 = branch.getEndPoint();
                if ((start1 != null) && (end1 != null)
                && (start0 != null) && (end0 != null)) {
                    Point2D mid1 = new Point2D.Double(
                            (start1.getX() + end1.getX()) / 2,
                            (start1.getY() + end1.getY()) / 2
                            );
                    g.setColor(Color.GRAY);
                    g.drawLine((int)mid0.getX(), (int)mid0.getY(),
                            (int)mid1.getX(), (int)mid1.getY());
                }
            }
        }
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
        
        double minX = Math.min(vx0, vx1) - thickness;
        double maxX = Math.max(vx0, vx1) + thickness;
        double minY = Math.min(vy0, vy1) - thickness;
        double maxY = Math.max(vy0, vy1) + thickness;
        
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
    
    public Point2D getMidPoint() {
        return new Point2D.Double(
                (fromPoint.getX() + toPoint.getX()) / 2,
                (fromPoint.getY() + toPoint.getY()) / 2
                );
    }
}