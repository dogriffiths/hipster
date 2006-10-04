/*
 * MapMover.java
 *
 * Created on 02 October 2006, 23:39
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
 * * Neither the name of David Griffiths nor the names of his contributors
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

package dg.hipster.controller;

import dg.hipster.view.BranchView;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.IdeaView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import javax.swing.Timer;

/**
 *
 * @author dgriffiths
 */

final class MapMover implements ActionListener {

    /**
     * Maximum speed the particles may move at.
     */
    private static final double MAX_SPEED = 5.0;
    /**
     * Maximum time in the seconds that the arrangement animation
     * will run before needing to be restarted.
     */
    private static final double MAX_MOVE_TIME_SECS = 23.0;
    /**
     * Pause between animation updates. 40 = 20 fps.
     */
    private static final int REFRESH_PAUSE = 50; // milliseconds
    /**
     * Milliseconds in a second.
     */
    private static final int MILLIS_PER_SEC = 1000;
    /**
     * Origin of the map coordinate system.
     */
    private static final Vertex ORIGIN = new Vertex(0.0, 0.0);
    /**
     * Total mass of the particle system.
     */
    private static final double TOTAL_MASS = 2000.0;
    /**
     * Timer that runs the animation.
     */
    private Timer ticker = new Timer(REFRESH_PAUSE, this);
    /**
     * System time of last update of the idea map.
     */
    private long timeChanged = 0;
    /**
     * Mass of a typical particle.
     */
    private double mass = 0.0;
    /**
     * Maximum speed of particles at a given point in time. Will
     * typically be lower than MAX_SPEED as the animation is
     * slowed down the longer it runs.
     */
    private double maxSpeed = 0.0;
    /**
     * Particles corresponding to the end points of the
     * branches.
     */
    private List<Vertex> particles;
    /**
     * Idea map we are moving.
     */
    private IdeaMap ideaMap;
    /**
     * Idea view that will not be animated (useful if, for example, something
     * else such as the mouse is controlling it).
     */
    private BranchView fixedBranch;

    public MapMover(IdeaMap newIdeaMap) {
        this.ideaMap = newIdeaMap;
    }

    public void startAdjust() {
        timeChanged = System.currentTimeMillis();
        this.ticker.start();
    }

    public void stopAdjust() {
        this.ticker.stop();
        this.maxSpeed = 0.0;
        ideaMap.repaint();
    }

    public boolean isRunning() {
        return ticker.isRunning();
    }

    public void setFixedBranch(BranchView fixed) {
        this.fixedBranch = fixed;
    }

    public void actionPerformed(final ActionEvent evt) {
        if (timeChanged == 0) {
            timeChanged = System.currentTimeMillis();
        }
        long now = System.currentTimeMillis();
        maxSpeed = 0.0;
        if ((now - timeChanged) < (MAX_MOVE_TIME_SECS * MILLIS_PER_SEC)) {
            maxSpeed = MAX_SPEED - ((now - timeChanged)
            * MAX_SPEED / MILLIS_PER_SEC / MAX_MOVE_TIME_SECS);
        } else {
            ticker.stop();
        }
        adjust();
    }

    private void adjust() {
        IdeaView rootView = ideaMap.getRootView();
        particles = new Vector<Vertex>();
        createParticles(rootView, new Position(ORIGIN,
                rootView.getIdea().getAngle()));
        mass = TOTAL_MASS / (particles.size()
        * Math.sqrt((double) particles.size()));
        endForce(rootView, new Position(ORIGIN, 0.0));
        adjustAngles(rootView);
        ideaMap.repaint();
    }

    private double getNewVelocity(final Vertex force, final IdeaView view,
            final Position p) {
        Vertex p2 = getParticle(view, new Position(ORIGIN, p.angle));
        double sideForce = (p2.y * force.x) + (-p2.x * force.y);
        double v = view.getIdea().getV();
        v += sideForce / mass;
        v *= 0.90;
        if (v > maxSpeed) {
            v = maxSpeed;
        }
        if (v < -maxSpeed) {
            v = -maxSpeed;
        }
        return v;
    }

    private void adjustAngles(final IdeaView parentView) {
        final List<BranchView> views = parentView.getSubViews();
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
            double newAngle = getNewAngle(parentView, previousView, view,
                    nextView);
            if (view != fixedBranch) {
                view.getIdea().setAngle(newAngle);
            }
            adjustAngles(view);
        }
    }

    private double getNewAngle(final IdeaView parentView,
            final IdeaView previousView, final IdeaView view,
            final IdeaView nextView) {
        final List<BranchView> views = parentView.getSubViews();
        final double v = view.getIdea().getV();
        double minDiffAngle = Math.PI / 2 / views.size();

        double oldAngle = view.getIdea().getAngle();
        double newAngle = oldAngle  + (view.getIdea().getV()
        / view.getIdea().getLength());
        if (previousView != null) {
            double previousAngle = previousView.getIdea().getAngle();
            if (previousAngle > newAngle - minDiffAngle) {
                previousView.getIdea().setAngle(newAngle - minDiffAngle);
                newAngle = previousAngle + minDiffAngle;
                double previousV = previousView.getIdea().getV();
                double diffV = v - previousV;
                if (diffV > 0) {
                    view.getIdea().setV(diffV);
                    previousView.getIdea().setV(-diffV);
                } else {
                    view.getIdea().setV(-diffV);
                    previousView.getIdea().setV(diffV);
                }
            }
        } else {
            double previousAngle = -Math.PI;
            double md = Math.PI / 20;
            if (previousAngle > newAngle - md) {
                newAngle = previousAngle + md;
                double previousV = 0.0;
                double diffV = v - previousV;
                if (diffV > 0) {
                    view.getIdea().setV(diffV);
                } else {
                    view.getIdea().setV(-diffV);
                }
            }
        }
        if (nextView != null) {
            double nextAngle = nextView.getIdea().getAngle();
            if (nextAngle < newAngle + minDiffAngle) {
                nextView.getIdea().setAngle(newAngle + minDiffAngle);
                newAngle = nextAngle - minDiffAngle;
                double nextV = nextView.getIdea().getV();
                double diffV = 0.0;
                if (diffV > 0) {
                    view.getIdea().setV(-diffV);
                    nextView.getIdea().setV(diffV);
                } else {
                    view.getIdea().setV(diffV);
                    nextView.getIdea().setV(-diffV);
                }
            }
        } else {
            double nextAngle = Math.PI;
            double md = Math.PI / 20;
            if (nextAngle < newAngle + md) {
                newAngle = nextAngle - md;
                double nextV = 0.0;
                double diffV = 0.0;
                if (diffV > 0) {
                    view.getIdea().setV(-diffV);
                } else {
                    view.getIdea().setV(diffV);
                }
            }
        }
        return newAngle;
    }




    private Vertex endForce(final IdeaView parentView, final Position p) {
        final List<BranchView> views = parentView.getSubViews();
        if (views.size() == 0) {
            return new Vertex(0.0, 0.0);
        }
        Vertex totForce = new Vertex(0.0, 0.0);
        for (IdeaView view: views) {
            Vertex particle = getParticle(view, p);
            Vertex force = repulsion(particle, view, p);
            totForce = totForce.add(force);
            view.getIdea().setV(getNewVelocity(force, view, p));
        }
        return totForce;
    }

    private Vertex repulsion(final Vertex point, final IdeaView view,
            final Position p) {
        Vertex force = new Vertex(0, 0);
        for (Vertex other : particles) {
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
                view.getIdea().getAngle() + p.angle)));
        return force;
    }


    /**
     *
     * @param parentView
     * @param start
     */
    private void createParticles(final IdeaView parentView,
            final Position start) {
        List<BranchView> views = parentView.getSubViews();
        particles.add(ORIGIN);
        for (IdeaView view : views) {
            Vertex location = getParticle(view, start);
            particles.add(location);
            Position nextStart = new Position(location,
                    start.angle + view.getIdea().getAngle());
            createParticles(view, nextStart);
        }
    }

    private Vertex getParticle(final IdeaView view, final Position p) {
        double angle = view.getIdea().getAngle() + p.angle;
        double length = view.getIdea().getLength();
        double x = p.start.x + Math.sin(angle) * length;
        double y = p.start.y + Math.cos(angle) * length;
        return new Vertex(x, y);
    }
}

final class Position {
    Vertex start;
    double angle;
    Position(final Vertex aStart, final double anAngle) {
        this.start = aStart;
        this.angle = anAngle;
    }
}

final class Vertex {
    double x;
    double y;
    Vertex(final double anX, final double aY) {
        this.x = anX;
        this.y = aY;
    }

    Vertex add(final Vertex other) {
        return new Vertex(this.x + other.x, this.y + other.y);
    }

    Vertex subtract(final Vertex other) {
        return new Vertex(this.x - other.x, this.y - other.y);
    }

    double distanceSq(final Vertex other) {
        return (new Point2D.Double(x, y)).distanceSq(
                new Point2D.Double(other.x, other.y));
    }

    double distance(final Vertex other) {
        return (new Point2D.Double(x, y)).distance(
                new Point2D.Double(other.x, other.y));
    }

    Vertex scale(final double factor) {
        return new Vertex(this.x * factor, this.y * factor);
    }

    void trim(final double min, final double max) {
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

