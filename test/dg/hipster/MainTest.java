/*
 * MainTest.java
 * JUnit based test
 *
 * Created on September 8, 2006, 7:16 AM
 */

package dg.hipster;

import junit.framework.*;
import dg.hipster.model.Settings;
import dg.hipster.view.AboutBox;
import dg.hipster.view.Mainframe;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;

/**
 *
 * @author davidg
 */
public class MainTest extends TestCase {
    
    public MainTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(MainTest.class);
        
        return suite;
    }

    public void testConstructor() {
        Main main = new Main();
    }
}
