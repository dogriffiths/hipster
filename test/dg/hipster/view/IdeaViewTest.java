/*
 * IdeaViewTest.java
 * JUnit based test
 *
 * Created on August 28, 2006, 3:53 PM
 */

package dg.hipster.view;

import junit.framework.*;
import dg.hipster.model.Idea;
import java.util.List;

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
    
    /**
     * Test of length getters and setters, of class dg.hipster.view.IdeaView.
     */
    public void testGetSetLength() {
        IdeaView ideaView = new IdeaView();
        assertEquals("Length should be zero if no idea", 0.0,
                ideaView.getLength(), 0.0000001);
        
        ideaView.setLength(100.0);
        assertEquals("Should allow me to set length", 100.0,
                ideaView.getLength(), 0.0000001);
        
        ideaView.setIdea(null);
        
        assertEquals("New null idea should give length of 0.0", 0.0,
                ideaView.getLength(), 0.0000001);
        
        ideaView.setIdea(new Idea("fred"));
        
        assertEquals("New idea should give length of 10 * text length + 20",
                10.0 * 4 + 20,
                ideaView.getLength(), 0.0000001);
        
    }
    
    /**
     * Test of angle setters and getters, of class dg.hipster.view.IdeaView.
     */
    public void testGetSetAngle() {
        IdeaView ideaView = new IdeaView();
        
        assertEquals("0 Angle should be zero if no idea", 0.0,
                ideaView.getAngle(), 0.0000001);
        
        ideaView.setAngle(Math.PI);
        assertEquals("1 Angle should be reset to Pi", Math.PI,
                ideaView.getAngle(), 0.0000001);
        
        Idea ideaParent = new Idea("parent");
        Idea ideaChild0 = new Idea("Child 0");
        ideaParent.add(ideaChild0);
        ideaView.setIdea(ideaParent);
        assertEquals("1a should have set idea correctly", ideaParent,
                ideaView.getIdea());
        assertTrue("1b first idea should be root", ideaView.isRoot());
        List<IdeaView> subViews = ideaView.getSubViews();
        assertEquals("2 Should be one sub-view", 1, subViews.size());
        IdeaView subView0 = subViews.get(0);
        assertFalse("2a second idea should not be root", subView0.isRoot());
        assertEquals("3 First idea should have angle 0", 0.0, subView0.getAngle(),
                0.0000001);
        
        Idea ideaChild1 = new Idea("Child 1");
        ideaParent.add(ideaChild1);
        subViews = ideaView.getSubViews();
        assertEquals("4 Should be one sub-view", 2, subViews.size());
        IdeaView subView1 = subViews.get(1);
        assertEquals("5 Second idea should have angle Pi/2", Math.PI / 2,
                subView1.getAngle(), 0.0000001);
        
        // Reset back to idea
        ideaView.setIdea(ideaParent);
        subViews = ideaView.getSubViews();
        assertEquals("6 Should be two sub-views", 2, subViews.size());
        subView0 = subViews.get(0);
        subView1 = subViews.get(1);
        assertEquals("7 First idea should have angle -Pi/2", -Math.PI / 2,
                subView0.getAngle(), 0.0000001);
        assertEquals("8 Second idea should have angle Pi/2", Math.PI / 2,
                subView1.getAngle(), 0.0000001);
        
        // Now add a third
        Idea ideaChild2 = new Idea("Child 2");
        ideaParent.add(ideaChild2);

        // Reset back to idea
        ideaView.setIdea(ideaParent);
        subViews = ideaView.getSubViews();
        assertEquals("9 Should be three sub-views", 3, subViews.size());
        subView0 = subViews.get(0);
        subView1 = subViews.get(1);
        IdeaView subView2 = subViews.get(2);
        assertEquals("10 First idea should have angle -2*Pi/3", -2 * Math.PI / 3,
                subView0.getAngle(), 0.0000001);
        assertEquals("11 Second idea should have angle 0", 0.0,
                subView1.getAngle(), 0.0000001);
        assertEquals("12 Third idea should have angle 2*Pi/3", 2 * Math.PI / 3,
                subView2.getAngle(), 0.0000001);
        
        // Add a grandchild
        Idea ideaGrandChild0 = new Idea("Grand child 0");
        ideaChild2.add(ideaGrandChild0);
        
        // Reset back to idea
        ideaView.setIdea(ideaParent);
        subView2 = ideaView.getSubViews().get(2);
        subViews = subView2.getSubViews();
        assertEquals("13 Should be one sub-view", 1, subViews.size());
        IdeaView grandSubView0 = subViews.get(0);
        assertEquals("14 First grand-idea should have angle 0", 0.0,
                grandSubView0.getAngle(), 0.0000001);
        
        Idea grandChild1 = new Idea("Grand child 1");
        ideaChild2.add(grandChild1);
        Idea grandChild2 = new Idea("Grand child 2");
        ideaChild2.add(grandChild2);
        
        Idea greatGrandChild0 = new Idea("Great grand child 0");
        ideaGrandChild0.add(greatGrandChild0);
        Idea greatGreatGrandChild0 = new Idea("Great great grand child 0");
        greatGrandChild0.add(greatGreatGrandChild0);

        // Reset back to idea
        ideaView.setIdea(ideaParent);
        IdeaView greatGrandView = ideaView.getSubViews().get(2).getSubViews(
                ).get(0).getSubViews().get(0);
        assertEquals("15 Great grand child should have angle 0.0", 0.0,
                greatGrandView.getAngle());
        assertEquals("16 ideaView should have angles from -2Pi/3",
                -2 * Math.PI / 3, ideaView.getMinSubAngle());
        assertEquals("17 ideaView should have angles from -2Pi/3",
                -2 * Math.PI / 3, ideaView.getMinSubAngle());
        
        ideaView.setAngle(-3.0);
        assertEquals("18 Should let me set angle", -3.0, ideaView.getAngle());
    }
    
    public void testGetViewFor() {
        Idea idea0 = new Idea("idea0");
        IdeaView ideaView0 = new IdeaView(idea0);
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
        IdeaView ideaView0 = new IdeaView(idea0);
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
