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

import dg.hipster.controller.UndoManager;
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
    /**
     * Undo controller.
     */
    private UndoManager undoManager = new UndoManager();

    private Idea idea;
    private File currentFile;
    private boolean dirty;
    private String title;
    private boolean needsAdjustment;
    private Idea selected;

    public IdeaDocument() {
        this.setCurrentFile(null);
        this.setIdea(new Idea(this.getTitle()));
        this.setDirty(false);
        this.setSelected(this.getIdea());
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
        undoManager.setIdea(newIdea);
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
        firePropertyChange("currentFile", oldFile, this.currentFile);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        boolean oldDirty = this.dirty;
        this.dirty = dirty;
        firePropertyChange("dirty", oldDirty, this.dirty);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String newTitle) {
        String oldTitle = this.title;
        this.title = newTitle;
        firePropertyChange("title", oldTitle, this.title);
    }

    public void ideaChanged(IdeaEvent ideaEvent) {
        this.setDirty(true);
        if (this.currentFile == null) {
            setTitle(this.getIdea().getText());
        }
        setNeedsAdjustment(true);
    }

    public void setNeedsAdjustment(boolean adjustment) {
        this.needsAdjustment = adjustment;
    }

    public boolean isNeedsAdjustment() {
        return this.needsAdjustment;
    }

    public Idea getSelected() {
        return selected;
    }

    public void setSelected(Idea newSelected) {
        Idea oldSelected = this.selected;
        if (this.selected != null) {
            this.selected.setSelected(false);
        }
        this.selected = newSelected;
        if (this.selected != null) {
            this.selected.setSelected(true);
        }
        firePropertyChange("selected", oldSelected, this.selected);
    }

    /**
     * Delete the currently selected idea (and consequently
     * its child ideas).
     */
    public void deleteSelected() {
        if (selected == null) {
            return;
        }
        Idea parent = this.idea.getParentFor(selected);
        if (parent == null) {
            return;
        }
        if (selected instanceof IdeaLink) {
            parent.removeLink((IdeaLink)selected);
            this.setSelected(parent);
            return;
        }
        Idea nextToSelect = null;
        Idea nextSibling = getNextSibling(selected);
        Idea previousSibling = getPreviousSibling(selected);
        if (nextSibling != null) {
            nextToSelect = nextSibling;
        } else if (previousSibling != null) {
            nextToSelect = previousSibling;
        } else {
            nextToSelect = parent;
        }
        parent.remove(selected);
        this.setSelected(nextToSelect);
    }

    public Idea getPreviousSibling(Idea i) {
        return getSibling(i, -1);
    }

    public Idea getNextSibling(Idea i) {
        return getSibling(i, +1);
    }

    public Idea getSibling(Idea i, int difference) {
        Idea parent = idea.getParentFor(i);
        if (parent == null) {
            return null;
        }
        int pos = parent.getSubIdeas().indexOf(i);
        int subCount = parent.getSubIdeas().size();
        int diff = difference % subCount;
        int siblingPos = pos + diff;
        if ((diff == 0) || (siblingPos < 0) || (siblingPos > (subCount - 1))) {
            return null;
        }
        siblingPos = (siblingPos + subCount) % subCount;
        return parent.getSubIdeas().get(siblingPos);
    }

    /**
     * Undo the last change.
     */
    public void undo() {
        if (undoManager != null) {
            undoManager.undo();
            this.setSelected(null);
        }
    }

    /**
     * Redo the last change undone.
     */
    public void redo() {
        if (undoManager != null) {
            undoManager.redo();
            this.setSelected(null);
        }
    }
}