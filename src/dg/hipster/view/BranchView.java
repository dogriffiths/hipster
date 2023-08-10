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

import dg.hipster.Utilities;
import dg.hipster.model.Idea;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 *
 * @author davidg
 */
public class BranchView extends IdeaView {
//    public static final Color[] COLOURS = {new Color(255, 20, 20), Color.ORANGE,
    public static final Color[] COLOURS = {new Color(255, 20, 20), Color.ORANGE,
    new Color(20, 200, 20), Color.BLUE};
    private double thickness;
    private Point2D fromPoint;
    private Point2D toPoint;
    /**
     * Default width of the stroke used to render branches.
     */
//    public static final float DEFAULT_STROKE_WIDTH = 20.0f;
    public static final float DEFAULT_STROKE_WIDTH = 4.0f;

    /** Creates a new instance of BranchView */
    public BranchView() {
        this(null);
    }

    public BranchView(Idea anIdea) {
        super(anIdea);
    }

    void paint(final Graphics g, final int depth, final IdeaMap map) {
        System.out.println("XXXXXXX depth = " + depth + " max depth = " + MAX_DEPTH);
        if (depth > MAX_DEPTH) {
            return;
        }
//        Font font  = new Font("Loopiejuice", Font.PLAIN, 22);
        Font font  = new Font("Loopiejuice", Font.PLAIN, 44);
        g.setFont(font);
        double a = getRealAngle();
        Point c = new Point((int) fromPoint.getX(), (int) fromPoint.getY());
        Point s = new Point((int) toPoint.getX(), (int) toPoint.getY());
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
//        strokeWidth = 4.0f;
        strokeWidth = 6.0f;
        Stroke stroke = new BasicStroke(strokeWidth,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        ((Graphics2D)g).setStroke(stroke);
        this.thickness = strokeWidth;
//        g.setColor(lower);
//        g.drawLine(c.x, c.y + 1, s.x, s.y + 1);
//        g.setColor(upper);
//        g.drawLine(c.x, c.y - 1, s.x, s.y - 1);
        g.setColor(colour);
        g.drawLine(c.x, c.y, s.x, s.y);

        int xdiff = s.x - c.x;
        int ydiff = s.y - c.y;

        float splits = 64.0f;
        for (int i = 0; i < splits; i++) {
            float v = ((float) i / splits);
            Stroke strokeA = new BasicStroke(strokeWidth * (1.0f + v),
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
            ((Graphics2D)g).setStroke(strokeA);
            double w = 0.2 * v;
            g.drawLine(c.x + (int)(xdiff * w), c.y + (int)(ydiff * w), s.x - (int)(xdiff * w), s.y - (int)(ydiff * w));
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
        return Utilities.hitsLine(p, fromPoint, toPoint, thickness);
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

    void initFromTo(final Point c, final double initAngle) {
        double a = this.getIdea().getAngle() + initAngle;
        this.setRealAngle(a);
        double len = this.getIdea().getLength();
        Point2D p = new Point2D.Double(Math.sin(a) * len,
                Math.cos(a) * len);
        Point s = this.getView(c, p);
        for (BranchView subView : this.getSubBranches()) {
            subView.initFromTo(s, a);
        }
        this.setFromPoint(c);
        this.setToPoint(s);
    }

    public Point2D getMidPoint() {
        return new Point2D.Double(
                (fromPoint.getX() + toPoint.getX()) / 2,
                (fromPoint.getY() + toPoint.getY()) / 2
                );
    }
}
