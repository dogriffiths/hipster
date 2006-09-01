/*
 * Idea.java
 *
 * Created on August 24, 2006, 11:38 AM
 *
 * Copyright (c) 2006, David Griffiths
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the David Griffiths nor the names of his contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.

 */

package dg.hipster.model;

import java.util.List;
import java.util.Vector;

/**
 * Represents an idea that may have sub-ideas.
 *
 * @author davidg
 */
public class Idea {
    /**
     * Short text version of the idea. Used for the text
     * on idea maps.
     */
    private String text = "";
    /**
     * List of sub-ideas.
     */
    private Vector<Idea> subIdeas = new Vector<Idea>();
    /**
     * List of objects that are observing this idea.
     */
    private List<IdeaListener> listeners = new Vector<IdeaListener>();
    
    /**
     * No args constructor.
     */
    public Idea() {
        
    }
    
    /**
     * Constructor for an idea with the given text.
     * @param text zhort text description of the idea
     */
    public Idea(String text) {
        setText(text);
    }
    
    /**
     * Add a sub-idea to this idea.
     * @param subIdea sub-idea to add
     */
    public synchronized void add(Idea subIdea) {
        subIdeas.add(subIdea);
        notify("ADDED", new Object[] {subIdea});
    }
    
    /**
     * Remove the given sub-idea from this idea. No
     * exception is raised if the idea given is
     * <em>not</em> a sub-idea of this idea.
     * @param subIdea sub-idea to be removed.
     */
    public synchronized void remove(Idea subIdea) {
        subIdeas.remove(subIdea);
        notify("REMOVED", new Object[] {subIdea});
    }
    
    /**
     * Add an idea listener to this idea. The idea listener
     * will be informed when anything changes about this
     * idea.
     * @param ideaListener idea listener in question.
     */
    public void addIdeaListener(IdeaListener ideaListener) {
        listeners.add(ideaListener);
    }
    
    /**
     * The given idea-listener will no longer be
     * notified of changes to this idea. No exception
     * will be thrown if the given idea-listener is
     * not currently listening to this idea.
     * @param ideaListener idea-listener in question
     */
    public void removeIdeaListener(IdeaListener ideaListener) {
        listeners.remove(ideaListener);
    }
    
    private void notify(String command, Object[] paras) {
        for (IdeaListener listener: listeners) {
            listener.ideaChanged(new IdeaEvent(this, command, paras));
        }
    }
    
    public synchronized List<Idea> getSubIdeas() {
        return (List<Idea>)subIdeas.clone();
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
