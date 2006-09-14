/*
 * Mainframe.java
 *
 * Created on July 19, 2006, 7:41 AM
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

package dg.hipster.view;

import dg.hipster.Main;
import dg.hipster.io.IdeaReader;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.Settings;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Main window of the application.
 *
 * @author davidg
 */
public class Mainframe extends JFrame {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    /**
     * Main idea processor component.
     */
    private IdeaMap ideaMap;
    
    /** Creates a new instance of Mainframe */
    public Mainframe() {
        setTitle(resBundle.getString("app.name"));
        
        Settings s = Settings.getInstance();
        setBounds(s.getWindowLeft(), s.getWindowTop(),
                s.getWindowWidth(), s.getWindowHeight());
        buildView();
        buildModel();
    }
    
    /**
     * Lay the window out.
     */
    private void buildView() {
        ideaMap = new IdeaMap();
        this.getContentPane().add(ideaMap, BorderLayout.CENTER);
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        menu.add(fileMenu);
        JMenuItem newMenu = new JMenuItem("New");
        fileMenu.add(newMenu);
        newMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    newDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        JMenuItem openMenu = new JMenuItem("Open...");
        fileMenu.add(openMenu);
        openMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    openDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        JMenuItem saveMenu = new JMenuItem("Save");
        fileMenu.add(saveMenu);
        saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        JMenuItem saveAsMenu = new JMenuItem("Save As...");
        fileMenu.add(saveAsMenu);
        saveAsMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveAsDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        if (!Main.isMac()) {
            JMenuItem itemExit = new JMenuItem("Exit");
            itemExit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fileExit();
                }
            });
            fileMenu.addSeparator();
            fileMenu.add(itemExit);
        }
        if (!Main.isMac()) {
            JMenu helpMenu = new JMenu("Help");
            JMenuItem about = new JMenuItem("About " + resBundle.getString("app.name"));
            about.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    helpAbout();
                }
            });
            
            helpMenu.add(about);
        }
        this.setJMenuBar(menu);
    }
    
    public void newDocument() {
        Idea idea = new Idea("New idea");
        this.ideaMap.setIdea(idea);
        this.ideaMap.getController().editIdeaView(this.ideaMap.getRootView());
        currentFile = null;
        this.setTitle(resBundle.getString("app.name"));
    }
    
    public void openDocument() throws IOException, ReaderException {
        FileDialog chooser = new FileDialog(this, "Open OPML file", FileDialog.LOAD);
        
        chooser.setVisible(true);
        
        String filename = chooser.getFile();
        
        if (filename != null) {
            String absPath = chooser.getDirectory() + chooser.getFile();
            ReaderFactory factory = ReaderFactory.getInstance();
            IdeaReader reader = factory.read(new File(absPath));
            ideaMap.setIdea(reader.getIdea());
            this.setTitle(resBundle.getString("app.name") + " - "
                    + absPath);
            currentFile = absPath;
        }
    }
    
    private String currentFile;
    
    public void saveAsDocument() throws IOException, ReaderException {
        String oldFile = currentFile;
        currentFile = null;
        saveDocument();
        if (currentFile == null) {
            currentFile = oldFile;
        }
    }
    
    public void saveDocument() throws IOException, ReaderException {
        if (currentFile == null) {
            FileDialog chooser = new FileDialog(this, "Save OPML file", FileDialog.SAVE);
            
            chooser.setVisible(true);
            
            if (chooser.getFile() != null) {
                currentFile = chooser.getDirectory() + chooser.getFile();
            }
        }
        
        
        if (currentFile != null) {
            Idea idea = this.ideaMap.getIdea();
            Writer out = new FileWriter(currentFile);
            save(idea, out);
            out.flush();
            out.close();
            
            this.setTitle(resBundle.getString("app.name") + " - "
                    + currentFile);
        }
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
    
    public void fileExit() {
        System.exit(0);
    }
    
    public void helpAbout() {
        Main.showAbout();
    }
    
    /**
     * Set up the data.
     */
    private void buildModel() {
    }
}
