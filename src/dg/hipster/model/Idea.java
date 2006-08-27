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

