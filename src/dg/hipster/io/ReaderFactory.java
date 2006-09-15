/*
 * ReaderFactory.java
 *
 * Created on August 27, 2006, 11:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.io;

import dg.hipster.model.Idea;
import java.io.File;
import java.io.FileInputStream;

/**
 *
 * @author davidg
 */
public class ReaderFactory {
    private static ReaderFactory instance = new ReaderFactory();
    
    /** Creates a new instance of ReaderFactory */
    private ReaderFactory() {
    }
    
    public static ReaderFactory getInstance() {
        return instance;
    }
    
    public Idea read(File f) throws ReaderException {
        try {
            IdeaReader reader = null;
            if (f.getName().toLowerCase().endsWith(".opml")) {
                reader = new OPMLReader(new FileInputStream(f));
            }
            if (reader != null) {
                return reader.getIdea();
            }
        } catch (Exception e) {
            throw new ReaderException("Unable to read file " + f, e);
        }
        throw new ReaderException("Unknown file type for " + f, null);
    }
}
