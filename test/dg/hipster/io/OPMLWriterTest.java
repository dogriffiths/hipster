/*
 * OPMLWriterTest.java
 * JUnit based test
 *
 * Created on September 18, 2006, 4:32 PM
 */

package dg.hipster.io;

import dg.hipster.model.IdeaDocument;
import junit.framework.*;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaLink;
import static dg.hipster.io.OPMLWriter.DATE_FORMAT;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

/**
 *
 * @author davidg
 */
public class OPMLWriterTest extends TestCase {
    
    public OPMLWriterTest(String testName) {
        super(testName);
    }
    
    protected void setUp() throws Exception {
    }
    
    protected void tearDown() throws Exception {
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(OPMLWriterTest.class);
        
        return suite;
    }
    
    /**
     * Test of write method, of class dg.hipster.io.OPMLWriter.
     */
    public void testWrite() throws Exception {
        Idea idea = new Idea("R&D");
        Idea subIdea0WithQuotes = new Idea("\"Idea\"");
        idea.add(subIdea0WithQuotes);
        Idea subIdea1 = new Idea("Test");
        idea.add(subIdea1);
        idea.setStartDate(DATE_FORMAT.parse("Sat, 21 Oct 2006 07:47:00 BST"));
        idea.setEndDate(DATE_FORMAT.parse("Sun, 22 Oct 2006 07:37:00 BST"));
        subIdea0WithQuotes.addLink(new IdeaLink(subIdea0WithQuotes, subIdea1));
        
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        
        OPMLWriter instance = new OPMLWriter(new OutputStreamWriter(out));
        
        IdeaDocument document = new IdeaDocument();
        document.setIdea(idea);
        
        instance.write(document);
        
        String result = out.toString();
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<opml version=\"1.0\">"
                + "<head>"
                + "<title/>"
                + "</head>"
                + "<body>"
                + "<outline angle=\"0.0\""
                + " endDate=\"Sun, 22 Oct 2006 07:37:00 BST\""
                + " id=\"0\""
                + " startDate=\"Sat, 21 Oct 2006 07:47:00 BST\""
                + " text=\"R&amp;D\">"
                + "<outline angle=\"0.0\" id=\"1\" text=\"&quot;Idea&quot;\">"
                + "<outline text=\"Test\" type=\"link\" url=\"#2\"/>"
                + "</outline>"
                + "<outline angle=\"0.0\" id=\"2\" text=\"Test\"/>"
                + "</outline>"
                + "</body>"
                + "</opml>";
        
        // TODO review the generated test code and remove the default call to fail.
        assertEquals("Wrong XML output", expected, result);
    }
    
}
