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
    
    public void testDummy() {
        
    }
}
