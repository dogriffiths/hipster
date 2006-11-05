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

import dg.hipster.Utilities;
import dg.inx.AbstractModel;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Represents an idea that may have sub-ideas.
 *
 * @author davidg
 */
public class Idea extends AbstractModel implements IdeaListener {
    /**
     * Length of a branch idea.
     */
    private double length;
    /**
     * Clockwise angle from the angle defined by the parent.
     */
    private double angle;
    /**
     * Angular velocity.
     */
    private double v;
    /**
     * Short text version of the idea. Used for the text
     * on idea maps.
     */
    private String text = "";
    /**
     * Description - probably used to over-ride shorter name in exports.
     */
    private String description = "";
    /**
     * URL to hyperlink to.
     */
    private String url = "";
    /**
     * Start date for this idea.
     */
    private Date startDate;
    /**
     * Start date for this idea.
     */
    private Date endDate;
    /**
     * List of sub-ideas.
     */
    private Vector<Idea> subIdeas = new Vector<Idea>();
    /**
     * List of sub-ideas.
     */
    private Vector<IdeaLink> links = new Vector<IdeaLink>();
    /**
     * List of objects that are observing this idea.
     */
    private List<IdeaListener> listeners = new Vector<IdeaListener>();
    /**
     * Longer notes.
     */
    private String notes = "";
    /**
     * Whether this idea is currently selected.
     */
    private boolean selected;

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
            notify(new IdeaEvent(this, IdeaEvent.CHANGED, "CHANGED"));
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
     * @param newLength length of the idea's representation.
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
     * @param newAngle angle in radians.
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
     * @param newV the velocity.
     */
    public void setV(double newV) {
        double oldV = this.v;
        this.v = newV;
        if (Math.abs(oldV - newV) > 0.0000001) {
            this.firePropertyChange("v", oldV, newV);
        }
    }

    /**
     * Longer notes fields.
     * @return Long text string for use in the notes
     * field of this idea's properties palette.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Longer notes fields.
     * @param newNotes Long text string for use in the notes
     * field of this idea's properties palette.
     */
    public void setNotes(String newNotes) {
        String oldNotes = this.notes;
        if ((this.notes == null) || (!this.notes.equals(newNotes))) {
            this.notes = newNotes;
            this.firePropertyChange("notes", oldNotes, newNotes);
            notify(new IdeaEvent(this, IdeaEvent.CHANGED, "CHANGED"));
        }
    }

    /**
     * URL associated with this idea.
     * @return URL of more info.
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL connected with this idea.
     * @param newUrl associated URL.
     */
    public void setUrl(String newUrl) {
        String oldUrl = this.url;
        if ((this.url == null) || (!this.url.equals(newUrl))) {
            this.url = newUrl;
            this.firePropertyChange("url", oldUrl, newUrl);
        }
    }

    /**
     * Longer text field. Might be used in exports in
     * preference to the text field.
     * @return Longer text field.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Longer text field. Might be used in exports in
     * preference to the text field.
     * @param newDescription Longer text field.
     */
    public void setDescription(String newDescription) {
        String oldDescription = this.description;
        if ((this.description == null)
        || (!this.description.equals(newDescription))) {
            this.description = newDescription;
            this.firePropertyChange("description", oldDescription,
                    newDescription);
        }
    }

    /**
     * End date for the period this idea is valid.
     * @return idea's end date.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * End date for the period this idea is valid.
     * @param newEndDate idea's end date.
     */
    public void setEndDate(Date newEndDate) {
        Date oldEndDate = this.endDate;
        if ((this.endDate == null)
        || (!this.endDate.equals(newEndDate))) {
            this.endDate = newEndDate;
            this.firePropertyChange("endDate", oldEndDate,
                    newEndDate);
        }
    }

    /**
     * Start date for the period this idea is valid.
     * @return idea's start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Start date for the period this idea is valid.
     * @param newStartDate idea's start date.
     */
    public void setStartDate(Date newStartDate) {
        Date oldStartDate = this.startDate;
        if ((this.startDate == null)
        || (!this.startDate.equals(newStartDate))) {
            this.startDate = newStartDate;
            this.firePropertyChange("startDate", oldStartDate,
                    newStartDate);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean newSelected) {
        boolean oldSelected = this.selected;
        this.selected = newSelected;
        if (oldSelected != newSelected) {
            this.firePropertyChange("selected", oldSelected, newSelected);
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
    public List<IdeaLink> getLinks() {
        return (List<IdeaLink>)links.clone();
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
        notify(new IdeaEvent(this, IdeaEvent.ADDED, "ADDED",
                subIdea, subIdeas.size() - 1));
    }

    /**
     * Add a sub-idea to this idea at the given position.
     * @param pos position in the list of sub-ideas to insert
     * @param subIdea sub-idea to add
     */
    public synchronized void add(int pos, Idea subIdea) {
        subIdeas.add(pos, subIdea);
        subIdea.addIdeaListener(this);
        notify(new IdeaEvent(this, IdeaEvent.ADDED, "ADDED",
                subIdea, pos));
    }

    /**
     * Remove the given sub-idea from this idea. No
     * exception is raised if the idea given is
     * <em>not</em> a sub-idea of this idea.
     * @param subIdea sub-idea to be removed.
     */
    public synchronized void remove(Idea subIdea) {
        int pos = subIdeas.indexOf(subIdea);
        subIdeas.remove(subIdea);
        subIdea.removeIdeaListener(this);
        notify(new IdeaEvent(this, IdeaEvent.REMOVED, "REMOVED",
                subIdea, pos));
    }

    /**
     * Add a one-way link to another idea.
     *@param other idea to link to.
     */
    public void addLink(IdeaLink other) {
        if ((other != null) && (!this.equals(other.getTo()))
        && (this.equals(other.getFrom()))) {
            links.add(other);
            notify(new IdeaEvent(this, IdeaEvent.ADDED_LINK, "ADDED_LINK",
                    other));
        }
    }

    /**
     * Remove a link to another idea.
     *@param other idea to link to.
     */
    public void removeLink(IdeaLink other) {
        if (other == null) {
            return;
        }
        if (links.contains(other)) {
            links.remove(other);
            notify(new IdeaEvent(this, IdeaEvent.REMOVED_LINK, "REMOVED_LINK",
                    other));
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
        if (!listeners.contains(ideaListener)) {
            listeners.add(ideaListener);
        }
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
    private void notify(IdeaEvent ideaEvent) {
        for (IdeaListener listener: listeners) {
            listener.ideaChanged(ideaEvent);
        }
    }

    public void ideaChanged(IdeaEvent ideaEvent) {
        notify(ideaEvent);
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
        idea.setDescription(getDescription());
        idea.setStartDate(getStartDate());
        idea.setEndDate(getEndDate());
        idea.setUrl(getUrl());
        idea.setV(getV());
        for (Idea subIdea : this.subIdeas) {
            idea.add(subIdea.clone());
        }
        return idea;
    }

    /**
     * Find the parent for the given idea, starting at this idea.
     *@param idea - idea we are looking for.
     *@return parent of the given idea, if it is in the tree of ideas
     * growing from this one.
     */
    public Idea getParentFor(Idea idea) {
        if (this == idea) {
            return null;
        }
        for (Idea subIdea : this.subIdeas) {
            if (subIdea == idea) {
                return this;
            }
            Idea descendent = subIdea.getParentFor(idea);
            if (descendent != null) {
                return descendent;
            }
        }
        for (IdeaLink link : this.links) {
            if (link == idea) {
                return this;
            }
        }
        return null;
    }
}

