/*
 * IdeaMapControllerTest.java
 * JUnit based test
 *
 * Created on September 10, 2006, 7:46 AM
 */

package dg.hipster.controller;

import junit.framework.*;
import dg.hipster.model.Idea;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.IdeaView;
import dg.hipster.view.MapComponent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.Timer;

/**
 *
 * @author davidg
 */
public class IdeaMapControllerTest extends TestCase {
    
    public IdeaMapControllerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(IdeaMapControllerTest.class);
        
        return suite;
    }

    public void testAngleBetween() {
        Point2D fromP = new Point2D.Double(0.0, 0.0);
        Point2D toP = new Point2D.Double(0.0, 1.0);
        assertEquals("Straight down should be Pi", Math.PI,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(1.0, 0.0);
        assertEquals("Straight right should be Pi/2", Math.PI / 2,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-1.0, 0.0);
        assertEquals("Straight left should be -Pi/2", -Math.PI / 2,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(0.0, -1.0);
        assertEquals("Straight up should be 0.0", 0.0,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(1.0, -1.0);
        assertEquals("North East should be Pi/4", Math.PI / 4,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(1.0, 1.0);
        assertEquals("South East should be 3Pi/4", 3 * Math.PI / 4,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-1.0, 1.0);
        assertEquals("South West should be -3Pi/4", -3 * Math.PI / 4,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-1.0, -1.0);
        assertEquals("North West should be -Pi/4", -Math.PI / 4,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-Math.sin(Math.PI/6), -Math.cos(Math.PI/6));
        assertEquals("-Pi/6 should be -Pi/6", -Math.PI / 6,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(-Math.sin(Math.PI/3), -Math.cos(Math.PI/3));
        assertEquals("-Pi/3 should be -Pi/3", -Math.PI / 3,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.sin(Math.PI/3), -Math.cos(Math.PI/3));
        assertEquals("North East Pi/3 should be Pi/3", Math.PI / 3,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.sin(2*Math.PI/3), -Math.cos(2*Math.PI/3));
        assertEquals("South East Pi/6 should be 2Pi/3", 2 * Math.PI / 3,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.sin(-2*Math.PI/3), -Math.cos(-2*Math.PI/3));
        assertEquals("South West Pi/6 should be -2Pi/3", -2 * Math.PI / 3,
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
        toP = new Point2D.Double(Math.cos(Math.PI/20), Math.sin(Math.PI/20));
        assertEquals("South East Pi/20 should be Pi/2 + Pi/20", (Math.PI / 2) + (Math.PI / 20),
                IdeaMapController.getAngleBetween(fromP, toP), 0.0000001);
    }
}
