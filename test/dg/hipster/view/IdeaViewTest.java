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
        IdeaView ideaView1 = ideaView0.getSubBranches().get(0);
        assertEquals("Found the wrong view", ideaView1, ideaView0.getViewFor(idea1));
        Idea idea2 = new Idea("idea2");
        idea1.add(idea2);
        IdeaView ideaView2 = ideaView1.getSubBranches().get(0);
        assertEquals("Found the wrong grand child view", ideaView2, ideaView0.getViewFor(idea2));
    }
    
    public void testGetRootView() {
        Idea idea0 = new Idea("idea0");
        IdeaView ideaView0 = new CentreView(idea0);
        Idea idea1 = new Idea("idea1");
        idea0.add(idea1);
        IdeaView ideaView1 = ideaView0.getSubBranches().get(0);
        Idea idea2 = new Idea("idea2");
        idea1.add(idea2);
        IdeaView ideaView2 = ideaView1.getSubBranches().get(0);
        assertEquals("ideaView0's root should be ideaView0", ideaView0, ideaView0.getRootView());
        assertEquals("ideaView1's root should be ideaView0", ideaView0, ideaView1.getRootView());
        assertEquals("ideaView2's root should be ideaView0", ideaView0, ideaView2.getRootView());
    }
    
    
//        assertEquals("5 Second idea should have angle Pi/2", Math.PI / 2,
//                subIdea1.getAngle(), 0.0000001);
//
//        // Reset back to idea
//        idea = ideaParent;
//        subIdeas = idea.getSubIdeas();
//        assertEquals("6 Should be two sub-ideas", 2, subIdeas.size());
//        subIdea0 = subIdeas.get(0);
//        subIdea1 = subIdeas.get(1);
//        assertEquals("7 First idea should have angle -Pi/2", -Math.PI / 2,
//                subIdea0.getAngle(), 0.0000001);
//        assertEquals("8 Second idea should have angle Pi/2", Math.PI / 2,
//                subIdea1.getAngle(), 0.0000001);
//
//        // Now add a third
//        Idea ideaChild2 = new Idea("Child 2");
//        ideaParent.add(ideaChild2);
//
//        // Reset back to idea
//        idea = ideaParent;
//        subIdeas = idea.getSubIdeas();
//        assertEquals("9 Should be three sub-ideas", 3, subIdeas.size());
//        subIdea0 = subIdeas.get(0);
//        subIdea1 = subIdeas.get(1);
//        Idea subIdea2 = subIdeas.get(2);
//        assertEquals("10 First idea should have angle -2*Pi/3", -2 * Math.PI / 3,
//                subIdea0.getAngle(), 0.0000001);
//        assertEquals("11 Second idea should have angle 0", 0.0,
//                subIdea1.getAngle(), 0.0000001);
//        assertEquals("12 Third idea should have angle 2*Pi/3", 2 * Math.PI / 3,
//                subIdea2.getAngle(), 0.0000001);
//
//        // Add a grandchild
//        Idea ideaGrandChild0 = new Idea("Grand child 0");
//        ideaChild2.add(ideaGrandChild0);
//
//        // Reset back to idea
//        idea = ideaParent;
//        subIdea2 = idea.getSubIdeas().get(2);
//        subIdeas = subIdea2.getSubIdeas();
//        assertEquals("13 Should be one sub-view", 1, subIdeas.size());
//        Idea grandSubIdea0 = subIdeas.get(0);
//        assertEquals("14 First grand-idea should have angle 0", 0.0,
//                grandSubIdea0.getAngle(), 0.0000001);
//
//        Idea grandChild1 = new Idea("Grand child 1");
//        ideaChild2.add(grandChild1);
//        Idea grandChild2 = new Idea("Grand child 2");
//        ideaChild2.add(grandChild2);
//
//        Idea greatGrandChild0 = new Idea("Great grand child 0");
//        ideaGrandChild0.add(greatGrandChild0);
//        Idea greatGreatGrandChild0 = new Idea("Great great grand child 0");
//        greatGrandChild0.add(greatGreatGrandChild0);
//
//        // Reset back to idea
//        idea = ideaParent;
//        Idea greatGrandIdea = idea.getSubIdeas().get(2).getSubIdeas(
//                ).get(0).getSubIdeas().get(0);
//        assertEquals("15 Great grand child should have angle 0.0", 0.0,
//                greatGrandIdea.getAngle());
//
//        idea.setAngle(-3.0);
//        assertEquals("18 Should let me set angle", -3.0, idea.getAngle());
}
