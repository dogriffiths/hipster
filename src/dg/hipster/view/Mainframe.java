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

import dg.hipster.io.IdeaReader;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.Settings;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
        JMenuItem openMenu = new JMenuItem("Open");
        fileMenu.add(openMenu);
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
        saveMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                try {
                    saveDocument();
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.setJMenuBar(menu);
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
        }
    }
    
    public void saveDocument() throws IOException, ReaderException {
        FileDialog chooser = new FileDialog(this, "Save OPML file", FileDialog.SAVE);
        
        chooser.setVisible(true);
        
        String filename = chooser.getFile();
        
        if (filename != null) {
            String absPath = chooser.getDirectory() + chooser.getFile();

            
            Idea idea = this.ideaMap.getIdea();
            Writer out = new FileWriter(absPath);
            save(idea, out);
            out.flush();
            out.close();
            
            
            this.setTitle(resBundle.getString("app.name") + " - "
                    + absPath);
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
    
    /**
     * Set up the data.
     */
    private void buildModel() {
//        ReaderFactory factory = ReaderFactory.getInstance();
//        try {
//            IdeaReader reader = factory.read(new File("etc/test.opml"));
//            ideaMap.setIdea(reader.getIdea());
//        } catch(ReaderException re) {
//            re.printStackTrace();
//        }
        
        Idea idea = new Idea("Persistence");
        ideaMap.setIdea(idea);
        Idea mistakes = new Idea("Mistakes");
        Idea platforms = new Idea("Platforms");
        mistakes.add(platforms);
        Idea attempts = new Idea("Attempts");
        platforms.add(attempts);
        Idea continual = new Idea("Continual");
        attempts.add(continual);
        Idea further = new Idea("Further");
        attempts.add(further);
        Idea enjoyed = new Idea("Enjoyed");
        attempts.add(enjoyed);
        Idea thousands = new Idea("Thousands");
        mistakes.add(thousands);
        Idea making = new Idea("Making");
        mistakes.add(making);
        Idea progress = new Idea("Progress");
        mistakes.add(progress);
        Idea learning = new Idea("Learning");
        idea.add(learning);
        Idea love = new Idea("Love");
        learning.add(love);
        love.add(mistakes);
        idea.add(mistakes);
        
        
        
        
//        final int lines = 35;
//        final Idea idea = new Idea("Test pattern");
//        ideaMap.setIdea(idea);
//        (new Thread(){public void run() {
//            for (int i = 0; i < lines; i++) {
//                Idea fred2 = new Idea("      i = " + i);
//                synchronized(idea) {
//                    idea.add(fred2);
//                }
//                //try { Thread.sleep(100);} catch(Exception e){}
//            }
//
//            Idea sub = idea.getSubIdeas().get(0);
//
//            Idea subIdea0 = null;
//            for (int i = 0; i < 4; i++) {
//                subIdea0 = new Idea("i = " + i);
//                sub.add(subIdea0);
//                try { Thread.sleep(1000);} catch(Exception e){}
//            }
//            try { Thread.sleep(10000);} catch(Exception e){}
//
//            Idea s2 = subIdea0;
//            for (int i = 0; i < 6; i++) {
//                Idea subIdea2 = new Idea("i = " + i);
//                s2.add(subIdea2);
//                try { Thread.sleep(1000);} catch(Exception e){}
//            }
//        }}).start();
    }
}
