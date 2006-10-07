/*
 * MacAppListener.java
 *
 * Created on July 19, 2006, 7:55 AM
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

package dg.hipster.controller;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import dg.hipster.Main;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.IdeaDocument;
import dg.hipster.view.GuiUtilities;
import java.io.File;

/**
 *
 * @author davidg
 */
public final class MacAppListener implements ApplicationListener {
    static {
        Application application = Application.getApplication();
        application.addApplicationListener(new MacAppListener());
        application.setEnabledPreferencesMenu(true);
        application.setEnabledAboutMenu(true);
    }


    public MacAppListener() {
    }

    public void handleAbout(ApplicationEvent event)  {
        Main.showAbout();
        event.setHandled(true);
    }
    public void handleOpenApplication(ApplicationEvent event) {
        if (event.getFilename() != null) {
            handleOpenFile(event);
        }
    }
    public void handleOpenFile(ApplicationEvent event) {
        File f = new File(event.getFilename());
        try {
            IdeaDocument document = ReaderFactory.getInstance().read(f);
            Main.getMainframe().setDocument(document);
        } catch(ReaderException re) {
            re.printStackTrace();
        }
        event.setHandled(true);
    }
    public void handlePreferences(ApplicationEvent event) {
        Main.showPreferences();
        event.setHandled(true);
    }
    public void handlePrintFile(ApplicationEvent event) {
    }
    public void handleQuit(ApplicationEvent event) {
        event.setHandled(Main.handleQuit());
    }
    public void handleReOpenApplication(ApplicationEvent event) {
    }
}