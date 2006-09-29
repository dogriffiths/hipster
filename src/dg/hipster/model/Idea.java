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
 * * Neither the name of David Griffiths nor the names of his contributors
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
public final class Idea {
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
     * List of sub-ideas.
     */
    private Vector<Idea> links = new Vector<Idea>();
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
        notify("ADDED", subIdea, subIdeas.size() - 1);
    }

    /**
     * Add a sub-idea to this idea at the given position.
     * @param pos position in the list of sub-ideas to insert
     * @param subIdea sub-idea to add
     */
    public synchronized void add(int pos, Idea subIdea) {
        subIdeas.add(pos, subIdea);
        notify("ADDED", subIdea, pos);
    }

    /**
     * Remove the given sub-idea from this idea. No
     * exception is raised if the idea given is
     * <em>not</em> a sub-idea of this idea.
     * @param subIdea sub-idea to be removed.
     */
    public synchronized void remove(Idea subIdea) {
        subIdeas.remove(subIdea);
        notify("REMOVED", subIdea);
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

    /**
     * Notify all listeners that something has changed. The parameters
     * provide more detail about the change.
     * @param command Text string describing the command. See source for
     * details.
     * @param paras objects providing more information about the change
     */
    private void notify(String command, Object... paras) {
        for (IdeaListener listener: listeners) {
            listener.ideaChanged(new IdeaEvent(this, command, paras));
        }
    }

    /**
     * Get a list of the sub-ideas for this idea.
     *@return list of idea objects
     */
    public List<Idea> getSubIdeas() {
        return (List<Idea>)subIdeas.clone();
    }

    /**
     * Get the short text description of this idea.
     * @return short text string, using for idea maps
     */
    public String getText() {
        return text;
    }

    /**
     * Specify the short text description of this idea.
     * @param text string to use
     */
    public void setText(String text) {
        this.text = text;
        notify("CHANGED");
    }
    
    /**
     * Add a one-way link to another idea.
     *@param other idea to link to.
     */
    public void addLink(Idea other) {
        if (!this.equals(other)) {
            links.add(other);
        }
    }
    
    /**
     * Add a two-way link to another idea.
     *@param other idea to link to.
     */
    public void addBiLink(Idea other) {
        if (other == null) {
            return;
        }
        this.addLink(other);
        other.addLink(this);
    }
    
    /**
     * Remove a link to another idea.
     *@param other idea to link to.
     */
    public void removeLink(Idea other) {
        if (other == null) {
            return;
        }
        if (links.contains(other)) {
            System.out.println("removing " + other + " from " + this);
            links.remove(other);
        }
        if (other.links.contains(this)) {
            System.out.println("removing0 " + this + " from " + other);
            other.links.remove(this);
        }
    }
    
    /**
     * Get the list of links that this idea is connected to.
     */
    public List<Idea> getLinks() {
        return (List<Idea>)links.clone();
    }

    /**
     * String version of this idea. It will be the short text
     * description, followed by a recursive list of the
     * sub-ideas.
     * @return string describing this idea and its sub-ideas.
     */
    public String toString() {
        return this.text + subIdeas.toString();
    }
}

