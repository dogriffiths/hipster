/*
 * IdeaListener.java
 *
 * Created on August 24, 2006, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.model;

import dg.hipster.model.IdeaEvent;

/**
 *
 * @author davidg
 */
public interface IdeaListener {
    public void ideaChanged(IdeaEvent fe);
}
