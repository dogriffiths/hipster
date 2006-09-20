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
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.io.WriterFactory;
import dg.hipster.model.Idea;
import dg.hipster.controller.IdeaDocument;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.Mainframe;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author davidg
 */
public final class MainframeController {
    private Mainframe mainframe;
    
    public MainframeController(Mainframe aMainframe) {
        this.mainframe = aMainframe;
        mainframe.getItem("new").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    newDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("open").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    openDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("save").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("saveAs").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveAsDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("zoomIn").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    zoomIn();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("insertChild").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    mainframe.getDocument().getIdeaMap().getController().insertChild();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("insertSibling").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    mainframe.getDocument().getIdeaMap().getController().insertIdea();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("zoomOut").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    zoomOut();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        if (!Main.isMac()) {
            mainframe.getItem("preferences").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editPreferences();
                }
            });
            mainframe.getItem("exit").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fileExit();
                }
            });
            mainframe.getItem("about").addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    helpAbout();
                }
            });
        }
    }
    
    public void newDocument() {
        IdeaDocument document = new IdeaDocument();
        Idea idea = new Idea("New idea");
        document.setIdea(idea);
        mainframe.setDocument(document);
        mainframe.editSelected();
    }
    
    public void openDocument() throws IOException, ReaderException {
        FileDialog chooser = new FileDialog(mainframe, "Open OPML file",
                FileDialog.LOAD);
        
        chooser.setVisible(true);
        
        String filename = chooser.getFile();
        
        if (filename != null) {
            String absPath = chooser.getDirectory() + chooser.getFile();
            ReaderFactory factory = ReaderFactory.getInstance();
            IdeaDocument document = factory.read(new File(absPath));
            mainframe.setDocument(document);
        }
    }
    
    public void saveAsDocument() throws IOException, ReaderException {
        IdeaDocument document = mainframe.getDocument();
        File oldFile = document.getCurrentFile();
        document.setCurrentFile(null);
        saveDocument();
        if (document.getCurrentFile() == null) {
            document.setCurrentFile(oldFile);
        }
    }
    
    public void saveDocument() throws IOException, ReaderException {
        IdeaDocument document = mainframe.getDocument();
        if (document.getCurrentFile() == null) {
            FileDialog chooser = new FileDialog(mainframe, "Save OPML file",
                    FileDialog.SAVE);
            
            chooser.setVisible(true);
            
            if (chooser.getFile() != null) {
                document.setCurrentFile(new File(chooser.getDirectory()
                + chooser.getFile()));
            }
        }
        
        
        if (document.getCurrentFile() != null) {
            Idea idea = document.getIdea();
            WriterFactory.getInstance().write(document.getCurrentFile(),
                    document);
        }
    }
    
    public void zoomIn() {
        mainframe.zoomIn();
    }
    
    public void zoomOut() {
        mainframe.zoomOut();
    }
    
    public void fileExit() {
        System.exit(0);
    }
    
    public void editPreferences() {
        Main.showPreferences();
    }
    
    public void helpAbout() {
        Main.showAbout();
    }
}
