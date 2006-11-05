/*
 * Viewport.java
 *
 * Created on 23 October 2006, 14:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.view;

import dg.inx.AbstractModel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Current area viewed by the idea map.
 * @author dgriffiths
 */
public class Viewport extends AbstractModel {
    /**
     * Proportion that the image will be scaled in and out each
     * time the {@link #zoomIn()} and {@link #zoomOut()} methods are called.
     */
    public final static double SCALE_FACTOR = 1.5;

    /**
     * Amount this map is scaled.
     */
    private double zoom;
    /**
     * Amount to offset the map by.
     */
    private Point offset;

    public Viewport() {
        setOffset(new Point(0, 0));
        setZoom(1.0);
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        double oldZoom = this.zoom;
        this.zoom = zoom;
        firePropertyChange("zoom", oldZoom, this.zoom);
    }

    /**
     * Amount that the current display is offset.
     * @return offset point.
     */
    public Point getOffset() {
        return offset;
    }

    /**
     * Amount the current display is offset.
     * @param offset point to offset by.
     */
    public void setOffset(Point offset) {
        Point oldOffset = this.offset;
        this.offset = offset;
        firePropertyChange("offset", oldOffset, this.offset);
    }

    public void transform(Graphics g) {
        if (offset != null) {
            g.translate(offset.x, offset.y);
        }
        ((Graphics2D)g).scale(zoom, zoom);
    }

    /**
     * Reset the zoom and offset.
     */
    public void resetView() {
        centreView();
        resetZoom();
    }

    /**
     * Centre the view.
     */
    public void centreView() {
        setOffset(new Point(0, 0));
    }

    /**
     * Centre the view.
     */
    public void resetZoom() {
        setOffset(new Point((int)(offset.x / zoom), (int)(offset.y / zoom)));
        setZoom(1.0);
    }

    /**
     * 2D point in map space corresponding to the given point in screen space.
     *@param p Point in screen space.
     *@return Point2D in map space.
     */
    public Point2D getMapPoint(Dimension size, Point p) {
        double x = p.x - (size.width / 2);
        double y = p.y - (size.height / 2);
        if (offset != null) {
            x -= offset.x;
            y -= offset.y;
        }
        double z = getZoom();
        x /= z;
        y /= z;
        return new Point2D.Double(x, y);
    }

    /**
     * Point in screen space corresponding to the given point in map space.
     *@param p Point2D in map space.
     *@return Point in screen space.
     */
    public Point getScreenPoint(Dimension size, Point2D p) {
        double x = p.getX();
        double y = p.getY();
        double z = getZoom();
        x *= z;
        y *= z;
        if (offset != null) {
            x += offset.x;
            y += offset.y;
        }
        x += (size.width / 2);
        y += (size.height / 2);
        return new Point((int) x, (int) y);
    }

    /**
     * Scale this map up by the given factor.
     * @param factor scaling factor - 1.0 for normal view.
     */
    public void zoom(double factor) {
        setZoom(getZoom() * factor);
        setOffset(new Point((int)(offset.x * factor),
                (int)(offset.y * factor)));
    }

    /**
     * Scale this map up by {@link #SCALE_FACTOR}.
     */
    public void zoomIn() {
        zoom(SCALE_FACTOR);
    }

    /**
     * Scale this map down by {@link #SCALE_FACTOR}.
     */
    public void zoomOut() {
        zoom(1.0 / SCALE_FACTOR);
    }
}