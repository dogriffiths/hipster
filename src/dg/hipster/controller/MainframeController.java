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

import dg.hipster.Main;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.io.WriterFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaDocument;
import dg.hipster.view.Mainframe;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

/**
 *
 * @author davidg
 */
public final class MainframeController implements FocusListener {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
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
                    mainframe.getIdeaMap().getController().insertChild();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        mainframe.getItem("insertSibling").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    mainframe.getIdeaMap().getController().insertIdea();
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
        mainframe.getItem("manual").addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                helpManual();
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
        mainframe.addFocusListener(this);
    }
    
    
    public void focusGained(final FocusEvent evt) {
    }
    
    public void focusLost(final FocusEvent evt) {
        mainframe.unEditSelected();
    }
    
    public void newDocument() {
        IdeaDocument document = new IdeaDocument();
        mainframe.setDocument(document);
    }
    
    public void openDocument() throws IOException, ReaderException {
        FileDialog chooser = new FileDialog(mainframe,
                resBundle.getString("open.opml.file"),
                FileDialog.LOAD);
        
        chooser.setFilenameFilter(new FilenameFilter(){
            public boolean accept(File directory, String file) {
                String filename = file.toUpperCase();
                return filename.endsWith(".OPML");
            }
        });
        
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
        saveDocument(document, null);
    }
    
    public void saveDocument() throws IOException, ReaderException {
        IdeaDocument document = this.mainframe.getDocument();
        saveDocument(document, document.getCurrentFile());
    }
    
    public void saveDocument(IdeaDocument document, File f)
    throws IOException, ReaderException {
        File file = f;
        if (file == null) {
            FileDialog chooser = new FileDialog(mainframe,
                    resBundle.getString("save.opml.file"),
                    FileDialog.SAVE);
            String filename = document.getTitle();
            if (!filename.toUpperCase().endsWith(".OPML")) {
                filename += ".opml";
            }
            int pos = filename.lastIndexOf(File.separatorChar);
            chooser.setFile(filename.substring(pos + 1));
            
            chooser.setVisible(true);
            
            if (chooser.getFile() != null) {
                file = new File(chooser.getDirectory() + chooser.getFile());
            }
        }
        
        
        if (file != null) {
            Idea idea = document.getIdea();
            WriterFactory.getInstance().write(file, document);
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
    
    public void helpManual() {
        if (Main.isMac()) {
            System.out.println("calling the manual");
            try {
                Class HelpBook = Class.forName("dg.hipster.HelpBook");
                Method launchHelpViewer = HelpBook.getMethod(
                        "launchHelpViewer");
                launchHelpViewer.invoke(new Integer(10));
            } catch(Exception cnfe) {
                cnfe.printStackTrace();
            }
        } else if (Main.isWindows()) {
            String pwd = System.getProperty("user.dir");
            String manualIndex = pwd + File.separatorChar + "manual"
                    + File.separatorChar + "English" + File.separatorChar
                    + "index.html";
            showUrlInWindows((new File(manualIndex)).toString());
        }
    }
    
    private void showUrlInWindows(String u) {
        System.out.println("u = " + u);
        
        if (u.indexOf('@') != -1) {
            u = "mailto:" + u;
        } else if (u.startsWith("http")) {
        } else if (u.substring(1, 2).indexOf(':') != -1) {
        } else {
            u = "http://" + u;
        }
        
        try {
            Process process = null;
            
            process = Runtime.getRuntime().exec("explorer " + u);
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
