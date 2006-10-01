/*
 * IdeaTest.java
 * JUnit based test
 *
 * Created on August 24, 2006, 11:44 AM
 */

package dg.hipster.model;

import dg.hipster.view.BranchView;
import dg.hipster.view.CentreView;
import dg.hipster.view.IdeaView;
import java.util.List;
import junit.framework.*;

/**
 *
 * @author davidg
 */
public class IdeaTest extends TestCase {
    private Idea idea;
    
    public IdeaTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        idea = new Idea();
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(IdeaTest.class);
        
        return suite;
    }
    
    /**
     * Test adding and removing sub-ideas.
     */
    public void testAddRemove() {
        Idea subIdea0 = new Idea();
        
        idea.add(subIdea0);
        
        assertEquals("Should now be 1 sub-idea", 1,
                idea.getSubIdeas().size());
        
        assertEquals("Should have return the first sub idea in first place",
                subIdea0, idea.getSubIdeas().get(0));
        
        Idea subIdea1 = new Idea();
        
        idea.add(subIdea1);
        
        assertEquals("Should now be 2 sub-ideas", 2,
                idea.getSubIdeas().size());
        
        assertEquals("Should still return the first sub idea in first place",
                subIdea0, idea.getSubIdeas().get(0));
        
        assertEquals("Should now return the second sub idea in second place",
                subIdea1, idea.getSubIdeas().get(1));
        
        Idea subIdea2 = new Idea();
        
        idea.add(1, subIdea2);
        
        assertEquals("Should now be 3 sub-ideas", 3,
                idea.getSubIdeas().size());
        
        assertEquals("Should still return the first sub idea in first place",
                subIdea0, idea.getSubIdeas().get(0));
        
        assertEquals("Should now return the third sub idea in second place",
                subIdea2, idea.getSubIdeas().get(1));
        
        assertEquals("Should now return the second sub idea in third place",
                subIdea1, idea.getSubIdeas().get(2));
        
        idea.remove(subIdea0);
        idea.remove(subIdea1);
        
        assertEquals("Should now be just 1 sub-idea",
                1, idea.getSubIdeas().size());
        
        assertEquals("First should now be subIdea2",
                subIdea2, idea.getSubIdeas().get(0));
        
    }
    
    /**
     * Test adding/removing idea listeners.
     */
    public void testAddRemoveIdeaListener() {
        class MyIdeaListener implements IdeaListener {
            IdeaEvent ie;
            public void ideaChanged(IdeaEvent theIe) {
                ie = theIe;
            }
        };
        
        MyIdeaListener listener0 = new MyIdeaListener();
        
        idea.addIdeaListener(listener0);
        
        assertEquals("Listener event should initially be null", null,
                listener0.ie);
        
        Idea subIdea0 = new Idea();
        
        idea.add(subIdea0);
        
        assertEquals("Listener should have received an ADDED command",
                "ADDED", listener0.ie.getCommand());
        
        assertEquals("Listener should have 2 paras",
                2, listener0.ie.getParas().length);
        
        assertEquals("1st Para should be the idea added",
                subIdea0, listener0.ie.getParas()[0]);
        
        assertEquals("2nd Para should be position added",
                0, listener0.ie.getParas()[1]);
        
        listener0.ie = null;
        
        MyIdeaListener listener1 = new MyIdeaListener();
        
        idea.addIdeaListener(listener1);
        
        Idea subIdea1 = new Idea();
        
        idea.add(subIdea1);
        
        assertEquals("First listener should have been informed",
                subIdea1, listener0.ie.getParas()[0]);
        
        assertEquals("Second listener should have been informed",
                subIdea1, listener1.ie.getParas()[0]);
        
        listener0.ie = null;
        
        idea.removeIdeaListener(listener0);
        
        Idea subIdea2 = new Idea();
        
        idea.add(subIdea2);
        
        assertEquals("First listener should not have been informed",
                null, listener0.ie);
        
        assertEquals("Second listener should have been informed",
                subIdea2, listener1.ie.getParas()[0]);
        
    }
    
    /**
     * Test of getSubIdeas method, of class dg.hipster.model.Idea.
     */
    public void testGetSubIdeas() {
        assertEquals("Should now be 0 sub-ideas", 0,
                idea.getSubIdeas().size());
        
        Idea subIdea0 = new Idea();
        
        idea.add(subIdea0);
        
        assertEquals("Should now be 1 sub-idea", 1,
                idea.getSubIdeas().size());
        
        assertEquals("Should have return the first sub idea in first place",
                subIdea0, idea.getSubIdeas().get(0));
        
        idea.getSubIdeas().add(new Idea("ignore"));
        
        assertEquals("Should still be 1 sub-idea", 1,
                idea.getSubIdeas().size());
        
    }
    
    /**
     * Test of getText method, of class dg.hipster.model.Idea.
     */
    public void testGetSetText() {
        assertEquals("Text should initially be zero-length", "", idea.getText());
        idea.setText("Some text");
        assertEquals("Text should have been updated", "Some text", idea.getText());
    }
    
    public void testAddLink() {
        Idea idea2 = new Idea("idea2");
        assertEquals("Wrong number of links by default", 0, idea.getLinks().size());
        assertEquals("Wrong number of links on idea2 by default", 0, idea2.getLinks().size());
        idea.addLink(idea);
        assertEquals("Shouldn't allow a link to itself", 0, idea.getLinks().size());
        idea.addLink(idea2);
        assertEquals("Wrong number of links", 1, idea.getLinks().size());
        assertEquals("Wrong number of links on idea2", 0, idea2.getLinks().size());
        idea.removeLink(new Idea("none"));
        assertEquals("Wrong number of links after remove", 1, idea.getLinks().size());
        assertEquals("Wrong number of links idea2 after remove", 0, idea2.getLinks().size());
        idea.removeLink(idea2);
        assertEquals("Wrong number of links after remove 2", 0, idea.getLinks().size());
        assertEquals("Wrong number of links idea2 after remove 2", 0, idea2.getLinks().size());
        idea.addBiLink(idea2);
        assertEquals("Wrong number of links after bi-link", 1, idea.getLinks().size());
        assertEquals("Wrong number of links on idea2 after bi-link", 1, idea2.getLinks().size());
        idea2.removeLink(idea);
        assertEquals("Wrong number of links after remove bi-link", 0, idea.getLinks().size());
        assertEquals("Wrong number of links on idea2 after remove bi-link", 0, idea2.getLinks().size());
    }
    
    public void testToString() {
        assertEquals("Should be blank initially", "[]", idea.toString());
        idea.setText("testRoot");
        assertEquals("Should have picked up name", "testRoot[]", idea.toString());
        Idea subIdea0 = new Idea("subIdea0");
        Idea subIdea1 = new Idea("subIdea1");
        idea.add(subIdea0);
        idea.add(subIdea1);
        assertEquals("Should include subs", "testRoot[subIdea0[], subIdea1[]]",
                idea.toString());
    }
    
    /**
     * Test of length getters and setters, of class dg.hipster.view.IdeaView.
     */
    public void testGetSetLength() {
        Idea idea = new Idea();
        assertEquals("New idea should give length of 0.0", 0.0,
                idea.getLength(), 0.0000001);
        
        idea.setLength(100.0);
        assertEquals("Should allow me to set length", 100.0,
                idea.getLength(), 0.0000001);
        
//        idea = new Idea("fred");
//        
//        assertEquals("New idea should give length of 10 * text length + 20",
//                10.0 * 4 + 20,
//                idea.getLength(), 0.0000001);
        
    }
    
    /**
     * Test of angle setters and getters, of class dg.hipster.view.IdeaView.
     */
    public void testGetSetAngle() {
        Idea idea = new Idea();
        
        assertEquals("0 Angle should be zero by default", 0.0,
                idea.getAngle(), 0.0000001);
        
        idea.setAngle(Math.PI);
        assertEquals("1 Angle should be reset to Pi", Math.PI,
                idea.getAngle(), 0.0000001);
        
        Idea ideaParent = new Idea("parent");
        Idea ideaChild0 = new Idea("Child 0");
        ideaParent.add(ideaChild0);
        idea = ideaParent;
        List<Idea> subIdeas = idea.getSubIdeas();
        assertEquals("2 Should be one sub-idea", 1, subIdeas.size());
        Idea subIdea0 = subIdeas.get(0);
        assertEquals("3 First idea should have angle 0", 0.0, subIdea0.getAngle(),
                0.0000001);
        
        Idea ideaChild1 = new Idea("Child 1");
        ideaParent.add(ideaChild1);
        subIdeas = idea.getSubIdeas();
        assertEquals("4 Should be one sub-idea", 2, subIdeas.size());
        Idea subIdea1 = subIdeas.get(1);
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
}
