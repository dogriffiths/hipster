/*
 * IdeaViewTest.java
 * JUnit based test
 *
 * Created on August 28, 2006, 3:53 PM
 */

package dg.hipster.view;

import junit.framework.*;
import dg.hipster.model.Idea;

/**
 *
 * @author davidg
 */
public class IdeaViewTest extends TestCase {
    
    public IdeaViewTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(IdeaViewTest.class);
        
        return suite;
    }
    
    public void testGetViewFor() {
        Idea idea0 = new Idea("idea0");
        IdeaView ideaView0 = new BranchView(idea0);
        assertEquals("Couldn't view when it was itself", ideaView0, ideaView0.getViewFor(idea0));
        Idea idea1 = new Idea("idea1");
        assertEquals("Falsely found a view", null, ideaView0.getViewFor(idea1));
        idea0.add(idea1);
        IdeaView ideaView1 = ideaView0.getSubViews().get(0);
        assertEquals("Found the wrong view", ideaView1, ideaView0.getViewFor(idea1));
        Idea idea2 = new Idea("idea2");
        idea1.add(idea2);
        IdeaView ideaView2 = ideaView1.getSubViews().get(0);
        assertEquals("Found the wrong grand child view", ideaView2, ideaView0.getViewFor(idea2));
    }
    
    public void testGetRootView() {
        Idea idea0 = new Idea("idea0");
        IdeaView ideaView0 = new CentreView(idea0);
        Idea idea1 = new Idea("idea1");
        idea0.add(idea1);
        IdeaView ideaView1 = ideaView0.getSubViews().get(0);
        Idea idea2 = new Idea("idea2");
        idea1.add(idea2);
        IdeaView ideaView2 = ideaView1.getSubViews().get(0);
        assertEquals("ideaView0's root should be ideaView0", ideaView0, ideaView0.getRootView());
        assertEquals("ideaView1's root should be ideaView0", ideaView0, ideaView1.getRootView());
        assertEquals("ideaView2's root should be ideaView0", ideaView0, ideaView2.getRootView());
    }
}
