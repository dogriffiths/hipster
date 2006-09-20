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
import java.io.File;

/**
 *
 * @author davidg
 */
public class IdeaDocument implements IdeaListener {
    private Mainframe mainframe;
    private IdeaMap ideaMap;
    private Idea idea;
    private File currentFile;
    private boolean dirty;
    
    public IdeaDocument() {
        
    }
    
    public void setIdea(Idea newIdea) {
        if (this.ideaMap != null) {
            Idea oldIdea = this.ideaMap.getIdea();
            if (oldIdea != null) {
                oldIdea.removeIdeaListener(this);
            }
            this.ideaMap.setIdea(newIdea);
        }
        this.idea = newIdea;
        newIdea.addIdeaListener(this);
    }
    
    public Idea getIdea() {
        return this.idea;
    }
    
    public File getCurrentFile() {
        return currentFile;
    }
    
    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        notifyMainframe();
    }
    
    public Mainframe getMainframe() {
        return mainframe;
    }
    
    public void setMainframe(Mainframe mainframe) {
        this.mainframe = mainframe;
        notifyMainframe();
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        notifyMainframe();
    }
    
    public void ideaChanged(IdeaEvent fe) {
        this.setDirty(true);
    }
    
    public IdeaMap getIdeaMap() {
        return ideaMap;
    }
    
    public void setIdeaMap(IdeaMap ideaMap) {
        this.ideaMap = ideaMap;
        this.ideaMap.setIdea(this.idea);
        notifyMainframe();
    }
    
    private void notifyMainframe() {
        if (this.mainframe != null) {
        this.mainframe.documentChanged();
        }
    }
}