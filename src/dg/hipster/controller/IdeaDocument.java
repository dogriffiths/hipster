/*
 * IdeaDocument.java
 *
 * Created on September 20, 2006, 3:19 PM
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

import dg.hipster.model.*;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.Mainframe;

/**
 *
 * @author davidg
 */
public class IdeaDocument implements IdeaListener {
    private Mainframe mainframe;
    private IdeaMap ideaMap;
    private String currentFile;
    private boolean dirty;
    
    public IdeaDocument() {
        
    }
    
    public void setIdea(Idea idea) {
        Idea oldIdea = this.ideaMap.getIdea();
        if (oldIdea != null) {
            oldIdea.removeIdeaListener(this);
        }
        this.ideaMap.setIdea(idea);
        idea.addIdeaListener(this);
    }
    
    public Idea getIdea() {
        return this.ideaMap.getIdea();
    }
    
    public String getCurrentFile() {
        return currentFile;
    }
    
    public void setCurrentFile(String currentFile) {
        this.currentFile = currentFile;
        this.getMainframe().documentChanged();
    }
    
    public Mainframe getMainframe() {
        return mainframe;
    }
    
    public void setMainframe(Mainframe mainframe) {
        this.mainframe = mainframe;
        this.mainframe.documentChanged();
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        this.getMainframe().documentChanged();
    }
    
    public void ideaChanged(IdeaEvent fe) {
        this.setDirty(true);
    }

    public IdeaMap getIdeaMap() {
        return ideaMap;
    }

    public void setIdeaMap(IdeaMap ideaMap) {
        this.ideaMap = ideaMap;
        this.mainframe.documentChanged();
    }
}