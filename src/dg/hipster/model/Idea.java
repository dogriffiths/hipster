/*
 * Idea.java
 *
 * Created on August 24, 2006, 11:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.model;

import dg.hipster.Utilities;
import java.util.Vector;

/**
 *
 * @author davidg
 */
public class Idea {
    private String text = "";
    private Vector<Idea> subIdeas = new Vector<Idea>();
    private Vector<IdeaListener> listeners = new Vector<IdeaListener>();
    
    public Idea() {
        
    }
    
    public Idea(String text) {
        setText(text);
    }
    
    public synchronized void add(Idea subIdea) {
        subIdeas.add(subIdea);
        notify("ADDED", new Object[] {subIdea});
    }
    
    public synchronized void remove(Idea subIdea) {
        subIdeas.remove(subIdea);
        notify("REMOVED", new Object[] {subIdea});
    }
    
    public void addIdeaListener(IdeaListener al) {
        listeners.add(al);
    }
    
    public void removeIdeaListener(IdeaListener al) {
        listeners.remove(al);
    }
    
    private void notify(String command, Object[] paras) {
        for (IdeaListener listener: listeners) {
            listener.ideaChanged(new IdeaEvent(this, command, paras));
        }
    }
    
    public synchronized Vector<Idea> getSubIdeas() {
        return (Vector<Idea>)subIdeas.clone();
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return this.text + subIdeas.toString();
    }
}

