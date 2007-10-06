/*
 * UndoManagerTest.java
 * JUnit based test
 *
 * Created on October 8, 2006, 2:34 PM
 */

package dg.hipster.controller;

import dg.hipster.model.*;
import junit.framework.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;

/**
 *
 * @author davidg
 */
public class UndoManagerTest extends TestCase {
    private UndoManager undoManager;
    
    public UndoManagerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        undoManager = new UndoManager();
    }

    protected void tearDown() throws Exception {
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(UndoManagerTest.class);
        
        return suite;
    }

    /**
     * Test of getIdea method, of class dg.hipster.model.UndoManager.
     */
    public void testGetSetIdea() {
        Idea idea = new Idea("test");
        assertEquals("Idea should be null by default", null,
                undoManager.getIdea());
        undoManager.setIdea(idea);
        assertEquals("Idea should have changed", idea,
                undoManager.getIdea());
    }

    /**
     * Test of undo method, of class dg.hipster.model.UndoManager.
     */
    public void testUndoText() {
        Idea idea = new Idea("test");
        undoManager.setIdea(idea);
        assertEquals("Wrong initial text", "test", idea.getText());
        idea.setText("new text");
        assertEquals("Wrong new text", "new text", idea.getText());
        undoManager.undo();
        assertEquals("Undo did not work", "test", idea.getText());
    }

    /**
     * Test of undo method, of class dg.hipster.model.UndoManager.
     */
    public void testUndoNotes() {
        Idea idea = new Idea("test");
        idea.setNotes("original notes");
        undoManager.setIdea(idea);
        assertEquals("Wrong initial notes", "original notes", idea.getNotes());
        idea.setNotes("new notes0");
        idea.setNotes("new notes1");
        idea.setNotes("new notes2");
        idea.setText("new text");
        idea.setNotes("new notes3");
        assertEquals("Wrong new notes", "new notes3", idea.getNotes());
        undoManager.undo();
        assertEquals("First undo did not work", "new notes2", idea.getNotes());
        undoManager.undo();
        assertEquals("Second undo did not work", "new notes2", idea.getNotes());
        undoManager.undo();
        assertEquals("Third undo did not work", "new notes1", idea.getNotes());
        undoManager.undo();
        assertEquals("Fourth undo did not work", "new notes0", idea.getNotes());
        undoManager.undo();
        assertEquals("Fifth undo did not work", "original notes", idea.getNotes());
        undoManager.redo();
        assertEquals("First redo did not work", "new notes0", idea.getNotes());
        idea.setNotes("burble");
        undoManager.redo();
        assertEquals("Second redo should have changed nothing", "burble", idea.getNotes());
    }
    
    /**
     * Test undo a sub-ideas.
     */
    public void testSubIdeaUndo() {
        Idea idea = new Idea("root");
        Idea subIdea0 = new Idea("subIdea0");
        idea.add(subIdea0);
        undoManager.setIdea(idea);
        Idea subIdea1 = new Idea("subIdea1");
        idea.add(subIdea1);
        Idea subSubIdea0 = new Idea("subSubIdea0");
        subIdea1.add(subSubIdea0);
        assertEquals("Wrong text in subSubIdea0", "subSubIdea0", subSubIdea0.getText());
        subSubIdea0.setText("new text");
        assertEquals("Wrong new text in subSubIdea0", "new text", subSubIdea0.getText());
        undoManager.undo();
        assertEquals("Didn't undo text in subSubIdea0", "subSubIdea0", subSubIdea0.getText());
        assertEquals("Wrong number of child for subIdea1", 1, subIdea1.getSubIdeas().size());
        undoManager.undo();
        assertEquals("Wrong number of child for subIdea1 after undo", 0, subIdea1.getSubIdeas().size());
        undoManager.redo();
        assertEquals("Wrong number of child for subIdea1 after first redo", 1, subIdea1.getSubIdeas().size());
        subIdea1.add(new Idea("stuff"));
        assertEquals("Wrong number of child for subIdea1 before second redo", 2, subIdea1.getSubIdeas().size());
        undoManager.redo();
        assertEquals("Wrong number of child for subIdea1 after second redo", 2, subIdea1.getSubIdeas().size());
    }
    
    /**
     * Test undo works if a sub-tree of ideas is added.
     */
    public void testAddSubTree() {
        Idea idea = new Idea("root");
        Idea subIdea0 = new Idea("subIdea0");
        idea.add(subIdea0);
        undoManager.setIdea(idea);
        Idea subIdea1 = new Idea("subIdea1");
        Idea subSubIdea0 = new Idea("subSubIdea0");
        subIdea1.add(subSubIdea0);
        Idea subSubIdea1 = new Idea("subSubIdea1");
        subIdea1.add(subSubIdea1);
        idea.add(subIdea1);
        assertEquals("Wrong initial text", "subSubIdea0", subSubIdea0.getText());
        subSubIdea0.setText("new text");
        assertEquals("Wrong new text", "new text", subSubIdea0.getText());
        undoManager.undo();
        assertEquals("Didn't reset text", "subSubIdea0", subSubIdea0.getText());
    }
    
    /**
     * Test undo works if a sub-tree of is cut and paste elsewhere.
     */
    public void testUndoTransfer() {
        Idea idea = new Idea("root");
        undoManager.setIdea(idea);
        Idea subIdea0 = new Idea("subIdea0");
        idea.add(subIdea0);
        Idea subIdea1 = new Idea("subIdea1");
        Idea subSubIdea0 = new Idea("subSubIdea0");
        subIdea0.add(subSubIdea0);
        idea.add(subIdea1);
        subIdea0.remove(subSubIdea0);
        subIdea1.add(subSubIdea0);
        undoManager.undo();
        assertEquals("subIdea1 still has the sub idea attached", 0,
                subIdea1.getSubIdeas().size());
        undoManager.undo();
    }
}
