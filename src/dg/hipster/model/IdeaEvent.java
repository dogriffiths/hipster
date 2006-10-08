/*
 * IdeaEvent.java
 *
 * Created on August 24, 2006, 11:41 AM
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

import java.awt.event.ActionEvent;

/**
 *
 * @author davidg
 */
public final class IdeaEvent extends ActionEvent {
    public static final int ADDED = 0;
    public static final int REMOVED = 1;
    public static final int ADDED_LINK = 2;
    public static final int REMOVED_LINK = 3;
    public static final int CHANGED = 4;
    private Idea idea;
    private Object param;
    
    public IdeaEvent(Object aSource, int id, String aCommand) {
        this(aSource, id, aCommand, null, null);
    }
    
    public IdeaEvent(Object aSource, int id, String aCommand, Idea anIdea) {
        this(aSource, id, aCommand, anIdea, null);
    }
    
    public IdeaEvent(Object aSource, int id, String aCommand, Idea anIdea,
            Object aParam) {
        super(aSource, id, aCommand);
        this.setIdea(anIdea);
        this.setParam(aParam);
    }

    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public Object getParam() {
        return param;
    }

    public void setParam(Object param) {
        this.param = param;
    }
}