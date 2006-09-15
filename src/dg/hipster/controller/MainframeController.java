/*
 * MainframeController.java
 *
 * Created on September 15, 2006, 7:21 AM
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

import dg.hipster.Main;
import dg.hipster.io.IdeaReader;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.Idea;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.Mainframe;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author davidg
 */
public class MainframeController {
    private Mainframe mainframe;
    
    public MainframeController(Mainframe aMainframe) {
        this.mainframe = aMainframe;
        mainframe.getNewItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    newDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getOpenItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    openDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getSaveItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getSaveAsItem().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveAsDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        if (!Main.isMac()) {
            mainframe.getExitItem().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fileExit();
                }
            });
            mainframe.getAboutItem().addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    helpAbout();
                }
            });
        }
    }
    
    public void newDocument() {
        Idea idea = new Idea("New idea");
        IdeaMap ideaMap = mainframe.getIdeaMap();
        ideaMap.setIdea(idea);
        ideaMap.getController().editIdeaView(ideaMap.getRootView());
        mainframe.setCurrentFile(null);
    }
    
    public void openDocument() throws IOException, ReaderException {
        FileDialog chooser = new FileDialog(mainframe, "Open OPML file",
                FileDialog.LOAD);
        
        chooser.setVisible(true);
        
        String filename = chooser.getFile();
        
        if (filename != null) {
            String absPath = chooser.getDirectory() + chooser.getFile();
            ReaderFactory factory = ReaderFactory.getInstance();
            IdeaReader reader = factory.read(new File(absPath));
            IdeaMap ideaMap = mainframe.getIdeaMap();
            ideaMap.setIdea(reader.getIdea());
            mainframe.setCurrentFile(absPath);
        }
    }
    
    public void saveAsDocument() throws IOException, ReaderException {
        String oldFile = mainframe.getCurrentFile();
        mainframe.setCurrentFile(null);
        saveDocument();
        if (mainframe.getCurrentFile() == null) {
            mainframe.setCurrentFile(oldFile);
        }
    }
    
    public void saveDocument() throws IOException, ReaderException {
        if (mainframe.getCurrentFile() == null) {
            FileDialog chooser = new FileDialog(mainframe, "Save OPML file",
                    FileDialog.SAVE);
            
            chooser.setVisible(true);
            
            if (chooser.getFile() != null) {
                mainframe.setCurrentFile(chooser.getDirectory()
                + chooser.getFile());
            }
        }
        
        
        if (mainframe.getCurrentFile() != null) {
            IdeaMap ideaMap = mainframe.getIdeaMap();
            Idea idea = ideaMap.getIdea();
            Writer out = new FileWriter(mainframe.getCurrentFile());
            save(idea, out);
            out.flush();
            out.close();
        }
    }
    
    public void fileExit() {
        System.exit(0);
    }
    
    public void helpAbout() {
        Main.showAbout();
    }
    
    private void save(Idea idea, Writer out) throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
        out.write("<opml version=\"1.0\">\n");
        
        out.write("     <head>\n");
        out.write("          <title/>\n");
        out.write("     </head>\n");
        
        out.write("     <body>\n");
        
        saveIdea(idea, out);
        
        out.write("     </body>\n");
        out.write("</opml>\n");
    }
    
    private void saveIdea(Idea idea, Writer out) throws IOException {
        out.write("<outline text=\"" + idea.getText() + "\">\n");
        for (Idea subIdea: idea.getSubIdeas()) {
            saveIdea(subIdea, out);
        }
        out.write("</outline>\n");
    }
}
