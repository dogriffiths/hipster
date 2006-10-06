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

import dg.inx.AbstractModel;
import java.util.List;
import java.util.Vector;

/**
 * Represents an idea that may have sub-ideas.
 *
 * @author davidg
 */
public final class Idea extends AbstractModel implements IdeaListener {
    private double length;
    private double angle;
    private double v;
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
     * Longer notes.
     */
    private String notes = "";
    
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
    
    //
    // PROPERTIES
    //
    
    /**
     * Get the short text description of this idea.
     * @return short text string, using for idea maps
     */
    public String getText() {
        return text;
    }
    
    /**
     * Specify the short newText description of this idea.
     *
     * @param newText string to use
     */
    public void setText(String newText) {
        String oldText = this.text;
        if ((this.text == null) || (!this.text.equals(newText))) {
            this.text = newText;
            this.firePropertyChange("text", oldText, newText);
            notify("CHANGED");
        }
    }
    
    /**
     * The length of this idea.
     *@return length in points.
     */
    public double getLength() {
        return length;
    }
    
    /**
     * The length of this idea.
     *@param length length in points.
     */
    public void setLength(double newLength) {
        double oldLength = this.length;
        this.length = newLength;
        if (Math.abs(oldLength - newLength) > 0.0000001) {
            this.firePropertyChange("length", oldLength, newLength);
        }
    }
    
    /**
     * The angle of this idea relative to its parent.
     *@return angle in radians.
     */
    public double getAngle() {
        return angle;
    }
    
    /**
     * The angle of this idea relative to its parent.
     *@param angle angle in radians.
     */
    public void setAngle(double newAngle) {
        double oldAngle = this.angle;
        this.angle = newAngle;
        if (Math.abs(oldAngle - newAngle) > 0.0000001) {
            this.firePropertyChange("angle", oldAngle, newAngle);
        }
    }
    
    /**
     * The velocity of this idea's end-point during animation.
     *@return the velocity.
     */
    public double getV() {
        return v;
    }
    
    /**
     * The velocity of this idea's end-point during animation.
     *@param v the velocity.
     */
    public void setV(double newV) {
        double oldV = this.v;
        this.v = newV;
        if (Math.abs(oldV - newV) > 0.0000001) {
            this.firePropertyChange("v", oldV, newV);
        }
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String newNotes) {
        String oldNotes = this.notes;
        if ((this.notes == null) || (!this.notes.equals(newNotes))) {
            this.notes = newNotes;
            this.firePropertyChange("notes", oldNotes, newNotes);
            notify("CHANGED");
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
     * Get the list of links that this idea is connected to.
     * @return list of links to related ideas.
     */
    public List<Idea> getLinks() {
        return (List<Idea>)links.clone();
    }
    
    //
    // Collection methods.
    //
    
    /**
     * Add a sub-idea to this idea.
     * @param subIdea sub-idea to add
     */
    public synchronized void add(Idea subIdea) {
        subIdeas.add(subIdea);
        subIdea.addIdeaListener(this);
        notify("ADDED", subIdea, subIdeas.size() - 1, this);
    }
    
    /**
     * Add a sub-idea to this idea at the given position.
     * @param pos position in the list of sub-ideas to insert
     * @param subIdea sub-idea to add
     */
    public synchronized void add(int pos, Idea subIdea) {
        subIdeas.add(pos, subIdea);
        subIdea.addIdeaListener(this);
        notify("ADDED", subIdea, pos, this);
    }
    
    /**
     * Remove the given sub-idea from this idea. No
     * exception is raised if the idea given is
     * <em>not</em> a sub-idea of this idea.
     * @param subIdea sub-idea to be removed.
     */
    public synchronized void remove(Idea subIdea) {
        subIdeas.remove(subIdea);
        subIdea.removeIdeaListener(this);
        notify("REMOVED", subIdea, this);
    }
    
    /**
     * Add a one-way link to another idea.
     *@param other idea to link to.
     */
    public void addLink(Idea other) {
        if (!this.equals(other)) {
            links.add(other);
            notify("ADDED_LINK", this, other);
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
            links.remove(other);
            notify("REMOVED_LINK", this, other);
        }
        if (other.links.contains(this)) {
            other.links.remove(this);
            notify("REMOVED_LINK", other, this);
        }
    }
    
    //
    // Idea-listener methods.
    //
    
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
    
    public void ideaChanged(IdeaEvent fe) {
        notify(fe.getCommand(), fe.getParas());
    }
    
    //
    // Other methods.
    //
    
    /**
     * String version of this idea. It will be the short text
     * description, followed by a recursive list of the
     * sub-ideas.
     * @return string describing this idea and its sub-ideas.
     */
    public String toString() {
        return this.text + subIdeas.toString();
    }
    
    /**
     * Create a clone of this idea.
     */
    public Idea clone() {
        Idea idea = new Idea(getText());
        idea.setAngle(getAngle());
        idea.setLength(getLength());
        idea.setNotes(getNotes());
        idea.setV(getV());
        for (Idea subIdea : this.subIdeas) {
            idea.add(subIdea.clone());
        }
        return idea;
    }
}

