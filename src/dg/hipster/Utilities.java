/*
 * Utilities.java
 *
 * Created on August 24, 2006, 11:54 AM
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

package dg.hipster;

import java.awt.geom.Point2D;


/**
 * Functions that may be useful in various classes.
 * @author dgriffiths
 */
public final class Utilities {
    
    /**
     * Creates a new instance of Utilities.
     */
    private Utilities() {
    }
    
    /**
     * Whether two objects are equal, without throwing
     * a null pointer exception.
     * @param object0 first object we are comparing
     * @param object1 second object we are comparing
     * @return true if they are the same, false otherwise
     */
    public static boolean areEqual(final Object object0, final Object object1) {
        if ((object0 == null) && (object1 == null)) {
            return true;
        }
        if ((object0 == null) || (object1 == null)) {
            return false;
        }
        return object0.equals(object1);
    }
    
    /**
     * Whether a point is on a line of a given thickness.
     *@param p point in question.
     *@param fromPoint one end of line.
     *@param toPoint other end of line.
     *@param thickness thickness of the line.
     */
    public static boolean hitsLine(final Point2D p, final Point2D fromPoint,
            final Point2D toPoint, final double thickness) {
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
}
