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
 * Animator that automatically lays out an idea map.
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
    private static final double MAX_MOVE_TIME_SECS = 8.0;
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
    /**
     * Proportion of velocity lost each click as a result of air RESISTANCE.
     */
    static final double RESISTANCE = 0.1;
    /**
     * Minimum desirable angle between branches (events may cause angles
     * to be less than this - for example, in the case of the case of many
     * branches).
     */
    static final double MIN_BRANCH_ANGLE = Math.PI / 20;

    /**
     * Constructor of the animator for a given idea map.
     * @param newIdeaMap idea map to animate.
     */
    public MapMover(final IdeaMap newIdeaMap) {
        this.ideaMap = newIdeaMap;
    }

    /**
     * Start the adjustment of the map. This will
     * continue asynchronously.
     */
    public void startAdjust() {
        timeChanged = System.currentTimeMillis();
        this.ticker.start();
    }

    /**
     * Stop the adjustment of the idea map.
     */
    public void stopAdjust() {
        this.ticker.stop();
        this.maxSpeed = 0.0;
        ideaMap.repaint();
    }

    /**
     * Whether the idea map is currently being
     * animated.
     * @return true if being animated, false otherwise.
     */
    public boolean isRunning() {
        return ticker.isRunning();
    }

    /**
     * Sets a single branch to not be adjusted by
     * this map mover. Useful if the branch is being
     * manually adjusted at the time.
     * @param fixed branch to ignore with the animation.
     */
    public void setFixedBranch(final BranchView fixed) {
        this.fixedBranch = fixed;
    }

    /**
     * Called by the timer to update the animation.
     * @param evt event describing the latest tick
     * of the timer.
     */
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

    /**
     * Adjust the idea map slightly.
     */
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

    /**
     * Given the force on a given branch, calculate
     * its new angular velocity.
     *
     * @param force force being applied to the branch.
     * @param branch branch being considered.
     * @param p start point of the branch.
     * @return new velocity of the branch.
     */
    private double getNewVelocity(final Vertex force, final BranchView branch,
            final Position p) {
        Vertex p2 = getParticle(branch, new Position(ORIGIN, p.angle));
        double sideForce = (p2.y * force.x) + (-p2.x * force.y);
        double v = branch.getIdea().getV();
        v += sideForce / mass;
        v *= (1.0 - RESISTANCE);
        if (v > maxSpeed) {
            v = maxSpeed;
        }
        if (v < -maxSpeed) {
            v = -maxSpeed;
        }
        return v;
    }

    /**
     * Adjust the angles of the branches to make
     * sure that they don't go out of order, or
     * turn past the parent branch.
     *
     * @param parentView parent branch.
     */
    private void adjustAngles(final IdeaView parentView) {
        final List<BranchView> branches = parentView.getSubBranches();
        for (int i = 0; i < branches.size(); i++) {
            BranchView previousBranch = null;
            BranchView nextBranch = null;
            BranchView branch = branches.get(i);
            if (i > 0) {
                previousBranch = branches.get(i - 1);
            }
            if (i < branches.size() - 1) {
                nextBranch = branches.get(i + 1);
            }
            double newAngle = getNewAngle(parentView, previousBranch, branch,
                    nextBranch);
            if (branch != fixedBranch) {
                branch.getIdea().setAngle(newAngle);
            }
            adjustAngles(branch);
        }
    }

    /**
     * Get the new angle of a branch, given the branches
     * before and after it. This method may also adjust
     * the next and previous branches. This method mostly
     * makes sure the branch doesn't change position with
     * those around it.
     * @param parentView parent of the branch.
     * @param previousBranch branch that should be immediately
     * anti-clockwise to this one.
     * @param branch branch we are considering.
     * @param nextBranch branch that should be immediately
     * clockwise of the branch.
     * @return new angle the branch should be set to.
     */
    private double getNewAngle(final IdeaView parentView,
            final BranchView previousBranch, final BranchView branch,
            final BranchView nextBranch) {
        final List<BranchView> branches = parentView.getSubBranches();
        final double v = branch.getIdea().getV();
        double minDiffAngle = Math.PI / 2 / branches.size();

        double oldAngle = branch.getIdea().getAngle();
        double newAngle = oldAngle  + (branch.getIdea().getV()
        / branch.getIdea().getLength());
        if (previousBranch != null) {
            double previousAngle = previousBranch.getIdea().getAngle();
            if (previousAngle > newAngle - minDiffAngle) {
                previousBranch.getIdea().setAngle(newAngle - minDiffAngle);
                newAngle = previousAngle + minDiffAngle;
                double previousV = previousBranch.getIdea().getV();
                double diffV = v - previousV;
                if (diffV > 0) {
                    branch.getIdea().setV(diffV);
                    previousBranch.getIdea().setV(-diffV);
                } else {
                    branch.getIdea().setV(-diffV);
                    previousBranch.getIdea().setV(diffV);
                }
            }
        } else {
            double previousAngle = -Math.PI;
            if (previousAngle > newAngle - MIN_BRANCH_ANGLE) {
                newAngle = previousAngle + MIN_BRANCH_ANGLE;
                double previousV = 0.0;
                double diffV = v - previousV;
                if (diffV > 0) {
                    branch.getIdea().setV(diffV);
                } else {
                    branch.getIdea().setV(-diffV);
                }
            }
        }
        if (nextBranch != null) {
            double nextAngle = nextBranch.getIdea().getAngle();
            if (nextAngle < newAngle + minDiffAngle) {
                nextBranch.getIdea().setAngle(newAngle + minDiffAngle);
                newAngle = nextAngle - minDiffAngle;
                double nextV = nextBranch.getIdea().getV();
                double diffV = 0.0;
                if (diffV > 0) {
                    branch.getIdea().setV(-diffV);
                    nextBranch.getIdea().setV(diffV);
                } else {
                    branch.getIdea().setV(diffV);
                    nextBranch.getIdea().setV(-diffV);
                }
            }
        } else {
            double nextAngle = Math.PI;
            if (nextAngle < newAngle + MIN_BRANCH_ANGLE) {
                newAngle = nextAngle - MIN_BRANCH_ANGLE;
                double nextV = 0.0;
                double diffV = 0.0;
                if (diffV > 0) {
                    branch.getIdea().setV(-diffV);
                } else {
                    branch.getIdea().setV(diffV);
                }
            }
        }
        return newAngle;
    }

    /**
     * The force applied onto the end of the given
     * view by the sub-branches pushing against it.
     * @param view view being considered.
     * @param p location of the start point of the
     * sub-branches.
     * @return force applied to the end point of the view
     * as a 2D vector.
     */
    private Vertex endForce(final IdeaView view, final Position p) {
        final List<BranchView> branches = view.getSubBranches();
        if (branches.size() == 0) {
            return new Vertex(0.0, 0.0);
        }
        Vertex totalForce = new Vertex(0.0, 0.0);
        for (BranchView branch : branches) {
            Vertex particle = getParticle(branch, p);
            Vertex force = repulsion(particle, branch, p);
            totalForce = totalForce.add(force);
            branch.getIdea().setV(getNewVelocity(force, branch, p));
        }
        return totalForce;
    }

    /**
     * Repulsion force applied to the end-point of
     * the given view by the end-points of all the
     * other branches. This is the key to how the animation
     * works. Each branch has a particle at the end of
     * non-zero mass. Each of these particles repel each
     * other using an inverse-square force, kind of like
     * reverse gravity. The desire of each particle
     * to be as far away from the others as possible
     * causes the branches they are sitting on to move.
     * @param point position of the particle on the end of
     * the view.
     * @param view view holding the particle.
     * @param p location and angle of the view.
     * @return resultant force on the particle.
     */
    private Vertex repulsion(final Vertex point, final IdeaView view,
            final Position p) {
        Vertex force = new Vertex(0, 0);
        /*
         * Loop through all the other particles and add up the repulsive
         * forces.
         */
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
        /*
         * In addition to the repulsion force of all other particles,
         * we also need to add on the force of the sub-branches pushing
         * back on this one.
         */
        force = force.add(endForce(view, new Position(point,
                view.getIdea().getAngle() + p.angle)));
        return force;
    }


    /**
     * Create a list of particles at the ends of
     * all of the branches.
     * @param parentView view to start at.
     * @param start start point of the given view.
     */
    private void createParticles(final IdeaView parentView,
            final Position start) {
        List<BranchView> branches = parentView.getSubBranches();
        particles.add(ORIGIN);
        for (IdeaView view : branches) {
            Vertex location = getParticle(view, start);
            particles.add(location);
            Position nextStart = new Position(location,
                    start.angle + view.getIdea().getAngle());
            createParticles(view, nextStart);
        }
    }

    /**
     * Get the particle associated with the
     * given view.
     * @param view 
     * @param p 
     * @return 
     */
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

