/*
 * IdeaDocumentTest.java
 * JUnit based test
 *
 * Created on 23 October 2006, 15:16
 */

package dg.hipster.model;

import junit.framework.*;
import java.io.File;
import java.util.ResourceBundle;

/**
 *
 * @author dgriffiths
 */
public class IdeaDocumentTest extends TestCase {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    private IdeaDocument instance;
    
    public IdeaDocumentTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
        instance = new IdeaDocument();
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(IdeaDocumentTest.class);
        
        return suite;
    }
    
    /**
     * Test of setIdea method, of class dg.hipster.model.IdeaDocument.
     */
    public void testSetGetIdea() {
        Idea newIdea = new Idea("Test");
        
        instance.setIdea(newIdea);
        
        assertEquals("Didn't pick up new idea", newIdea, instance.getIdea());
    }
    
    /**
     * Test of getCurrentFile method, of class dg.hipster.model.IdeaDocument.
     */
    public void testSetGetCurrentFile() {
        assertEquals("Should be no file by default", null, instance.getCurrentFile());
        File f = new File("test.opml");
        instance.setCurrentFile(f);
        assertEquals("Didn't pick new current file", f, instance.getCurrentFile());
    }
    
    /**
     * Test of isDirty method, of class dg.hipster.model.IdeaDocument.
     */
    public void testSetIsDirty() {
        assertFalse("Should not be dirty by default", instance.isDirty());
        instance.getIdea().add(new Idea("New idea"));
        assertTrue("Adding a sub idea should make it dirty", instance.isDirty());
    }
    
    /**
     * Test of getTitle method, of class dg.hipster.model.IdeaDocument.
     */
    public void testGetSetTitle() {
        assertEquals("Should be null by default",
                resBundle.getString("untitled"), instance.getTitle());
        instance.setCurrentFile(new File("fred.opml"));
        assertTrue("Didn't pick up file name",
                instance.getTitle().endsWith("fred.opml"));
        instance.setTitle("newTitle");
        assertEquals("Didn't pick up new title",
                "newTitle", instance.getTitle());
    }
    
    /**
     * Test of setNeedsAdjustment method, of class dg.hipster.model.IdeaDocument.
     */
    public void testSetIsNeedsAdjustment() {
        assertFalse("By default does not need adjustment", instance.isNeedsAdjustment());
        instance.setNeedsAdjustment(true);
        assertTrue("Should now need adjustment", instance.isNeedsAdjustment());
    }
    
    /**
     * Test of getSelected method, of class dg.hipster.model.IdeaDocument.
     */
    public void testGetSelected() {
        assertEquals("Should have selected centre by default",
                instance.getIdea(), instance.getSelected());
        Idea idea2 = new Idea("Sub idea");
        instance.getIdea().add(idea2);
        instance.setSelected(idea2);
        assertEquals("Didn't pick up new selection",
                idea2, instance.getSelected());
    }
    
    /**
     * Test of deleteSelected method, of class dg.hipster.model.IdeaDocument.
     */
    public void testDeleteSelected() {
        assertEquals("Should have selected centre by default",
                instance.getIdea(), instance.getSelected());
        Idea idea2 = new Idea("Sub idea");
        instance.getIdea().add(idea2);
        instance.setSelected(idea2);
        assertEquals("Idea should one child", 1, instance.getIdea().getSubIdeas().size());
        instance.deleteSelected();
        assertEquals("Didn't delete idea", 0, instance.getIdea().getSubIdeas().size());
    }
    
    /**
     * Test of getPreviousSibling method, of class dg.hipster.model.IdeaDocument.
     */
    public void testGetPreviousSibling() {
        Idea idea0 = new Idea("Sub idea0");
        instance.getIdea().add(idea0);
        Idea idea1 = new Idea("Sub idea1");
        instance.getIdea().add(idea1);
        Idea idea2 = new Idea("Sub idea2");
        instance.getIdea().add(idea2);
        assertEquals("Didn't pick up previous sibling", idea0, instance.getPreviousSibling(idea1));
    }
    
    /**
     * Test of getNextSibling method, of class dg.hipster.model.IdeaDocument.
     */
    public void testGetNextSibling() {
        Idea idea0 = new Idea("Sub idea0");
        instance.getIdea().add(idea0);
        Idea idea1 = new Idea("Sub idea1");
        instance.getIdea().add(idea1);
        Idea idea2 = new Idea("Sub idea2");
        instance.getIdea().add(idea2);
        assertEquals("Didn't pick up previous sibling", idea2, instance.getNextSibling(idea1));
    }
    
    /**
     * Test of getSibling method, of class dg.hipster.model.IdeaDocument.
     */
    public void testGetSibling() {
        Idea idea0 = new Idea("Sub idea0");
        instance.getIdea().add(idea0);
        Idea idea1 = new Idea("Sub idea1");
        instance.getIdea().add(idea1);
        Idea idea2 = new Idea("Sub idea2");
        instance.getIdea().add(idea2);
        assertEquals("Didn't pick up -1 sibling", idea0, instance.getSibling(idea1, -1));
        assertEquals("Didn't pick up +1 sibling", idea2, instance.getSibling(idea1, +1));
        assertEquals("Didn't pick up -2 sibling", idea0, instance.getSibling(idea2, -2));
        assertEquals("Didn't pick up +2 sibling", idea2, instance.getSibling(idea0, +2));
        assertEquals("Didn't pick up +2 sibling", null, instance.getSibling(idea1, +2));
    }
    
    /**
     * Test of redo method, of class dg.hipster.model.IdeaDocument.
     */
    public void testUndoRedo() {
        Idea idea2 = new Idea("Sub idea");
        instance.getIdea().add(idea2);
        instance.setSelected(idea2);
        assertEquals("Idea should one child", 1, instance.getIdea().getSubIdeas().size());
        instance.deleteSelected();
        assertEquals("Idea should have been deleted", 0, instance.getIdea().getSubIdeas().size());
        instance.undo();
        assertEquals("Idea should be 1 child again", 1, instance.getIdea().getSubIdeas().size());
        instance.redo();
        assertEquals("Idea should be back to 0 child again", 0, instance.getIdea().getSubIdeas().size());
    }
    
}
