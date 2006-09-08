/*
 * IdeaTest.java
 * JUnit based test
 *
 * Created on August 24, 2006, 11:44 AM
 */

package dg.hipster.model;

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
}
