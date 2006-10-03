/*
 * IdeaMapTest.java
 * JUnit based test
 *
 * Created on 03 October 2006, 13:53
 */

package dg.hipster.view;

import junit.framework.*;
import dg.hipster.controller.IdeaMapController;
import dg.hipster.model.Idea;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author dgriffiths
 */
public class IdeaMapTest extends TestCase {
    
    public IdeaMapTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IdeaMapTest.class);
        
        return suite;
    }

    public void testAngleBetween() {
        IdeaMap instance = new IdeaMap();
        Point2D fromP = new Point2D.Double(0.0, 0.0);
        Point2D toP = new Point2D.Double(0.0, 1.0);
        assertEquals("Straight down should be Pi", Math.PI,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(1.0, 0.0);
        assertEquals("Straight right should be Pi/2", Math.PI / 2,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-1.0, 0.0);
        assertEquals("Straight left should be -Pi/2", -Math.PI / 2,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(0.0, -1.0);
        assertEquals("Straight up should be 0.0", 0.0,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(1.0, -1.0);
        assertEquals("North East should be Pi/4", Math.PI / 4,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(1.0, 1.0);
        assertEquals("South East should be 3Pi/4", 3 * Math.PI / 4,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-1.0, 1.0);
        assertEquals("South West should be -3Pi/4", -3 * Math.PI / 4,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-1.0, -1.0);
        assertEquals("North West should be -Pi/4", -Math.PI / 4,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-Math.sin(Math.PI/6), -Math.cos(Math.PI/6));
        assertEquals("-Pi/6 should be -Pi/6", -Math.PI / 6,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-Math.sin(Math.PI/3), -Math.cos(Math.PI/3));
        assertEquals("-Pi/3 should be -Pi/3", -Math.PI / 3,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.sin(Math.PI/3), -Math.cos(Math.PI/3));
        assertEquals("North East Pi/3 should be Pi/3", Math.PI / 3,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.sin(2*Math.PI/3), -Math.cos(2*Math.PI/3));
        assertEquals("South East Pi/6 should be 2Pi/3", 2 * Math.PI / 3,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.sin(-2*Math.PI/3), -Math.cos(-2*Math.PI/3));
        assertEquals("South West Pi/6 should be -2Pi/3", -2 * Math.PI / 3,
                instance.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.cos(Math.PI/20), Math.sin(Math.PI/20));
        assertEquals("South East Pi/20 should be Pi/2 + Pi/20", (Math.PI / 2) + (Math.PI / 20),
                instance.getAngleBetween(fromP, toP), 0.0000001);
    }
}
