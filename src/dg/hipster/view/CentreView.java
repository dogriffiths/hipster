/*
 * CentreView.java
 *
 * Created on September 30, 2006, 8:09 AM
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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Central view in a mind-map.
 * @author davidg
 */
public class CentreView extends IdeaView {
    public static int ROOT_RADIUS_X = 70;
    public static int ROOT_RADIUS_Y = 40;

    /** Creates a new instance of CentreView */
    public CentreView() {
        this(null);
    }

    public CentreView(Idea anIdea) {
        super(anIdea);
    }

    public void paint(Graphics g, IdeaMap map) {
        initFromTo();
//        for (BranchView branch : this.getSubViews()) {
            this.paintLinks(g);
//        }
        paintBranches(g, new Point(0, 0), this, getIdea().getAngle(), 0, map);
        Color colour = Color.WHITE;
        if (this.isSelected()) {
            colour = invert(colour);
        }
        g.setColor(colour);
        g.fillOval(-ROOT_RADIUS_X, -ROOT_RADIUS_Y, ROOT_RADIUS_X * 2,
                ROOT_RADIUS_Y * 2);
        colour = Color.BLACK;
        if (this.isSelected()) {
            colour = invert(colour);
        }
        g.setColor(colour);
        g.drawOval(-ROOT_RADIUS_X, -ROOT_RADIUS_Y, ROOT_RADIUS_X * 2,
                ROOT_RADIUS_Y * 2);
        drawString((Graphics2D)g, getIdea().getText(), new Point(0, 0), 4,
                getIdea().getAngle(), this.isEditing(), map);
    }

    boolean hits(Point2D p) {
        int x = (int) p.getX() * ROOT_RADIUS_Y / ROOT_RADIUS_X;
        int y = (int) p.getY();
        return ((x * x) + (y * y)) < (ROOT_RADIUS_Y * ROOT_RADIUS_Y);
    }

    private void initFromTo() {
        for (BranchView subView : getSubViews()) {
            double a = subView.getIdea().getAngle();
            Point start = new Point(
                    (int) (Math.sin(a) * this.ROOT_RADIUS_X),
                    (int) (-Math.cos(a) * this.ROOT_RADIUS_Y)
                    );
            subView.initFromTo(start, 0.0);
        }
    }
}
