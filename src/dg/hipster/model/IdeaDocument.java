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

package dg.hipster.model;

import dg.hipster.view.IdeaMap;
import dg.hipster.view.Mainframe;
import dg.inx.AbstractModel;
import java.io.File;
import java.util.ResourceBundle;

/**
 *
 * @author davidg
 */
public class IdeaDocument extends AbstractModel implements IdeaListener {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    private Idea idea;
    private File currentFile;
    private boolean dirty;
    private String title;
    
    public IdeaDocument() {
        this.setCurrentFile(null);
        this.setIdea(new Idea(this.getTitle()));
    }
    
    public void setIdea(Idea newIdea) {
        Idea oldIdea = this.idea;
        if (oldIdea != null) {
            oldIdea.removeIdeaListener(this);
        }
        this.idea = newIdea;
        if (this.idea != null) {
            newIdea.addIdeaListener(this);
        }
        firePropertyChange("idea", oldIdea, this.idea);
    }
    
    public Idea getIdea() {
        return this.idea;
    }
    
    public File getCurrentFile() {
        return currentFile;
    }
    
    public void setCurrentFile(File newCurrentFile) {
        File oldFile = this.currentFile;
        this.currentFile = newCurrentFile;
        if (this.currentFile == null) {
            this.setTitle(resBundle.getString("untitled"));
        } else {
            this.setTitle(newCurrentFile.getAbsolutePath());
        }
        firePropertyChange("title", oldFile, this.currentFile);
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    public void setDirty(boolean dirty) {
        boolean oldDirty = this.dirty;
        this.dirty = dirty;
        firePropertyChange("title", oldDirty, this.dirty);
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(String newTitle) {
        String oldTitle = this.title;
        this.title = newTitle;
        firePropertyChange("title", oldTitle, this.title);
    }
    
    public void ideaChanged(IdeaEvent fe) {
        this.setDirty(true);
    }
}