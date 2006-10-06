/*
 * IdeaTest.java
 * JUnit based test
 *
 * Created on August 24, 2006, 11:44 AM
 */

package dg.hipster.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
        idea.setText("idea");
        Idea subIdea0 = new Idea("subIdea0");
        
        IdeaListenerImpl i = new IdeaListenerImpl();
        idea.addIdeaListener(i);
        
        assertEquals("Should be no event yet", null, i.ideaEvent);
        
        idea.add(subIdea0);
        
        assertEquals("Should have fired add event", "ADDED",
                i.ideaEvent.getCommand());
        assertEquals("Should have fired add event", subIdea0,
                i.ideaEvent.getParas()[0]);
        assertEquals("Should have fired add event", 0,
                i.ideaEvent.getParas()[1]);
        
        i.ideaEvent = null;
        
        assertEquals("Should now be 1 sub-idea", 1,
                idea.getSubIdeas().size());
        
        assertEquals("Should have return the first sub idea in first place",
                subIdea0, idea.getSubIdeas().get(0));
        
        Idea subIdea1 = new Idea("subIdea1");
        
        idea.add(subIdea1);
        
        assertEquals("Should now be 2 sub-ideas", 2,
                idea.getSubIdeas().size());
        
        assertEquals("Should still return the first sub idea in first place",
                subIdea0, idea.getSubIdeas().get(0));
        
        assertEquals("Should now return the second sub idea in second place",
                subIdea1, idea.getSubIdeas().get(1));
        
        Idea subIdea2 = new Idea("subIdea2");
        
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
        assertEquals("Should have fired add event", "REMOVED",
                i.ideaEvent.getCommand());
        i.ideaEvent = null;
        
        idea.remove(subIdea1);
        assertEquals("Should have fired add event", "REMOVED",
                i.ideaEvent.getCommand());
        assertEquals("Should have fired add event", subIdea1,
                i.ideaEvent.getParas()[0]);
        i.ideaEvent = null;
        
        assertEquals("Should now be just 1 sub-idea",
                1, idea.getSubIdeas().size());
        
        assertEquals("First should now be subIdea2",
                subIdea2, idea.getSubIdeas().get(0));
        
        Idea subSubIdea0 = new Idea("subSubIdea0");
        
        subIdea2.add(subSubIdea0);
        assertEquals("Should have fired add event", "ADDED",
                i.ideaEvent.getCommand());
        assertEquals("Should have fired add event", subSubIdea0,
                i.ideaEvent.getParas()[0]);
        assertEquals("Should have fired add event", 0,
                i.ideaEvent.getParas()[1]);
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
                3, listener0.ie.getParas().length);
        
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
        IdeaListenerImpl i = new IdeaListenerImpl();
        idea.addIdeaListener(i);
        PropertyChangeListenerImpl p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener(p);
        assertEquals("Text should initially be zero-length", "", idea.getText());
        idea.setText("Some text");
        assertEquals("Text should have been updated", "Some text", idea.getText());
        assertEquals("Should have sent an idea listener event", "CHANGED", i.ideaEvent.getCommand());
        i.ideaEvent = null;
        assertEquals("Should have sent a general property change event", 
                p.propertyChangeEvent.getPropertyName(), 
                "text");
        p.propertyChangeEvent = null;
        idea.removePropertyChangeListener(p);
        idea.setText("Some more text");
        assertEquals("Should not have sent a PCE", 
                null, 
                p.propertyChangeEvent);
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
        IdeaListenerImpl i = new IdeaListenerImpl();
        idea.addIdeaListener(i);
        PropertyChangeListenerImpl p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener(p);
        assertEquals("New idea should give length of 0.0", 0.0,
                idea.getLength(), 0.0000001);
        
        idea.setLength(100.0);
        assertEquals("Should allow me to set length", 100.0,
                idea.getLength(), 0.0000001);
        assertEquals("Should not have sent an idea listener event", null,
                i.ideaEvent);
        assertEquals("Should have sent a general property change event", 
                p.propertyChangeEvent.getPropertyName(), 
                "length");
    }
    
    /**
     * Test of angle setters and getters, of class dg.hipster.view.IdeaView.
     */
    public void testGetSetAngle() {
        IdeaListenerImpl i = new IdeaListenerImpl();
        idea.addIdeaListener(i);
        PropertyChangeListenerImpl p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener(p);
        
        assertEquals("0 Angle should be zero by default", 0.0,
                idea.getAngle(), 0.0000001);
        
        idea.setAngle(Math.PI);
        assertEquals("1 Angle should be reset to Pi", Math.PI,
                idea.getAngle(), 0.0000001);
        assertEquals("Should not have sent an idea listener event", null,
                i.ideaEvent);
        assertEquals("Should have sent a general property change event", 
                p.propertyChangeEvent.getPropertyName(), 
                "angle");
        idea.removePropertyChangeListener(p);
        p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener("angle", p);
        idea.setAngle(-1.0);
        assertEquals("Should have sent a specific property change event", 
                -1.0,
                p.propertyChangeEvent.getNewValue());
        idea.removePropertyChangeListener(p);
        p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener("text", p);
        idea.setAngle(Math.PI);
        assertEquals("Should not have sent a specific property change event for another attribute", 
                null,
                p.propertyChangeEvent);
        
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
    }
    
    /**
     * Test of getV method, of class dg.hipster.model.Idea.
     */
    public void testGetSetV() {
        IdeaListenerImpl i = new IdeaListenerImpl();
        idea.addIdeaListener(i);
        PropertyChangeListenerImpl p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener(p);
        
        assertEquals("0 v should be zero by default", 0.0,
                idea.getV(), 0.0000001);
        
        idea.setV(1.4);
        assertEquals("1 Angle should be reset to 1.4", 1.4,
                idea.getV(), 0.0000001);
        assertEquals("Should not have sent an idea listener event", null,
                i.ideaEvent);
        assertEquals("Should have sent a general property change event", 
                p.propertyChangeEvent.getPropertyName(), 
                "v");
    }
    
    /**
     * Test of getNotes method, of class dg.hipster.model.Idea.
     */
    public void testGetSetNotes() {
        IdeaListenerImpl i = new IdeaListenerImpl();
        idea.addIdeaListener(i);
        PropertyChangeListenerImpl p = new PropertyChangeListenerImpl();
        idea.addPropertyChangeListener(p);
        assertEquals("Notes should initially be zero-length", "", idea.getNotes());
        idea.setNotes("A note");
        assertEquals("Notes should have been updated", "A note", idea.getNotes());
        assertEquals("Should have sent an idea listener event", "CHANGED", i.ideaEvent.getCommand());
    }
    
//    /**
//     * Test of getLinks method, of class dg.hipster.model.Idea.
//     */
//    public void testGetLinks() {
//        System.out.println("getLinks");
//        
//        Idea instance = new Idea();
//        
//        List<Idea> expResult = null;
//        List<Idea> result = instance.getLinks();
//        assertEquals(expResult, result);
//        
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
//    /**
//     * Test of addBiLink method, of class dg.hipster.model.Idea.
//     */
//    public void testAddBiLink() {
//        System.out.println("addBiLink");
//        
//        Idea other = null;
//        Idea instance = new Idea();
//        
//        instance.addBiLink(other);
//        
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//    
//    /**
//     * Test of removeLink method, of class dg.hipster.model.Idea.
//     */
//    public void testRemoveLink() {
//        System.out.println("removeLink");
//        
//        Idea other = null;
//        Idea instance = new Idea();
//        
//        instance.removeLink(other);
//        
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}

class IdeaListenerImpl implements IdeaListener {
    IdeaEvent ideaEvent;
    
    public IdeaListenerImpl() {
        
    }
    
    public void ideaChanged(IdeaEvent fe) {
        this.ideaEvent = fe;
    }
}

class PropertyChangeListenerImpl implements PropertyChangeListener {
    PropertyChangeEvent propertyChangeEvent;
    
    public PropertyChangeListenerImpl() {
        
    }
    
    public void propertyChange(PropertyChangeEvent pce) {
        this.propertyChangeEvent = pce;
    }
}