/*
 * UndoManager.java
 *
 * Created on October 8, 2006, 2:11 PM
 *
 * Copyright (c) 2006, David Griffiths
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this Vector of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this Vector of conditions and the following disclaimer in the documentation
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

package dg.hipster.controller;

import dg.hipster.model.Idea;
import dg.hipster.model.IdeaEvent;
import dg.hipster.model.IdeaListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Stack;

/**
 * Class for undoing operations on ideas.
 * @author davidg
 */
public class UndoManager implements IdeaListener, PropertyChangeListener {
    /**
     * Root idea of the document being undone.
     */
    private Idea idea;
    /**
     * Stack of events that will be undone in reverse
     * order.
     */
    private Stack events;

    /**
     * Creates a new instance of UndoManager.
     */
    public UndoManager() {
        events = new Stack();
    }

    /**
     * Called when ideas are amended. We are only
     * interested in ADDED and REMOVED events, because
     * CHANGED events will already be picked up from
     * the property change listener interface.
     * @param ideaEvent event recording the change that
     * has occurred to the idea.
     */
    public void ideaChanged(IdeaEvent ideaEvent) {
        switch(ideaEvent.getID()) {
            case IdeaEvent.ADDED:
                Idea newIdea = ideaEvent.getIdea();
                startListeningTo(newIdea);
                storeEvent(ideaEvent);
                break;
            case IdeaEvent.REMOVED:
                Idea oldIdea = ideaEvent.getIdea();
                stopListeningTo(oldIdea);
                storeEvent(ideaEvent);
                break;
            default:
                // Do nothing
        }
    }

    /**
     * Called when a property changes on an idea.
     * @param propertyChangeEvent event describing the change.
     */
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        storeEvent(propertyChangeEvent);
    }

    /**
     * Place an event upon a stack - unless it is
     * already there. The reason for the check is
     * to prevent ideas that have erroneously been
     * subscribed to more than once generating
     * multiple events.
     * @param event event to store.
     */
    private void storeEvent(final Object event) {
        if (events.isEmpty() || (events.peek() != event)) {
            events.push(event);
        }
    }

    /**
     * Root idea being watched.
     * @return idea being watched.
     */
    public Idea getIdea() {
        return idea;
    }

    /**
     * Root idea being watched.
     * @param newIdea Root idea being watched.
     */
    public void setIdea(Idea newIdea) {
        Idea oldIdea = this.idea;
        this.idea = newIdea;
        if (oldIdea != null) {
            oldIdea.removeIdeaListener(this);
            stopListeningTo(oldIdea);
        }
        if (newIdea != null) {
            newIdea.addIdeaListener(this);
            startListeningTo(newIdea);
        }
    }

    /**
     * Stop listening to the property changes
     * of an idea - usually
     * because it has been deleted.
     * @param anIdea idea to stop listening to.
     */
    private void stopListeningTo(Idea anIdea) {
        if (anIdea != null) {
            anIdea.removePropertyChangeListener("text", this);
            anIdea.removePropertyChangeListener("notes", this);
            for (Idea subIdea : anIdea.getSubIdeas()) {
                stopListeningTo(subIdea);
            }
        }
    }

    /**
     * Start listening to the property changes of
     * an idea.
     * @param anIdea idea to listen to.
     */
    private void startListeningTo(Idea anIdea) {
        if (anIdea != null) {
            anIdea.addPropertyChangeListener("text", this);
            anIdea.addPropertyChangeListener("notes", this);
            for (Idea subIdea : anIdea.getSubIdeas()) {
                startListeningTo(subIdea);
            }
        }
    }

    /**
     * Undo the last event recorded.
     */
    public void undo() {
        if (events.isEmpty()) {
            return;
        }
        Object lastEvent = events.pop();
        if (lastEvent instanceof IdeaEvent) {
            undo((IdeaEvent)lastEvent);
        } else if (lastEvent instanceof PropertyChangeEvent) {
            undo((PropertyChangeEvent)lastEvent);
        }
        // Now remove the event that would have resulted from the undo
        events.pop();
    }

    /**
     * Undo the specified idea-event.
     * @param ideaEvent idea-event to undo.
     */
    private void undo(IdeaEvent ideaEvent) {
        switch(ideaEvent.getID()) {
            case IdeaEvent.ADDED:
                Idea parent0 = (Idea)ideaEvent.getSource();
                parent0.remove(ideaEvent.getIdea());
                break;
            case IdeaEvent.REMOVED:
                Idea parent1 = (Idea)ideaEvent.getSource();
                parent1.add((Integer)ideaEvent.getParam(),
                        ideaEvent.getIdea());
                break;
            default:
                throw new RuntimeException("Cannot undo: " + ideaEvent);
        }
    }

    /**
     * Undo the specified property-change-event.
     * @param propertyChangeEvent event to undo.
     */
    private void undo(PropertyChangeEvent propertyChangeEvent) {
        String propertyName = propertyChangeEvent.getPropertyName();
        Idea idea = (Idea)propertyChangeEvent.getSource();
        if (propertyName.equals("text")) {
            idea.setText((String)propertyChangeEvent.getOldValue());
        } else if (propertyName.equals("notes")) {
            idea.setNotes((String)propertyChangeEvent.getOldValue());
        }
    }
}
