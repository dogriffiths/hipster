/*
 * IdeaWriter.java
 *
 * Created on September 15, 2006, 7:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.io;

import dg.hipster.model.Idea;

/**
 *
 * @author davidg
 */
public interface IdeaWriter {
    public void write(Idea idea) throws Exception;
}
