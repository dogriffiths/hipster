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

package dg.hipster.view;

import dg.hipster.Main;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.io.WriterFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaDocument;
import dg.hipster.model.Settings;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Main window of the application.
 *
 * @author davidg
 */
public final class Mainframe extends JFrame implements PropertyChangeListener,
        FocusListener {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    /**
     * Main idea processor component.
     */
    private MenuManager menuMgr = new MenuManager(this);
    private IdeaMap ideaMap;
    private IdeaDocument document;
    
    /** Creates a new instance of Mainframe */
    public Mainframe() {
        super();
        
        Settings s = Settings.getInstance();
        setBounds(s.getWindowLeft(), s.getWindowTop(),
                s.getWindowWidth(), s.getWindowHeight());
        buildView();
        buildModel();
        setDocument(new IdeaDocument());
    }
    
    /**
     * Lay the window out.
     */
    private void buildView() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.ideaMap = new IdeaMap();
        this.getContentPane().add(ideaMap, BorderLayout.CENTER);
        this.setJMenuBar(createMenu());
        this.setIconImage(createIcon());
    }
    
    private Image createIcon() {
        String imageName = "/dg/hipster/resource/hipster_icon.png";
        java.net.URL url = getClass().getResource(imageName);
        if (url == null) {
            throw new RuntimeException("Unable to find picture " + imageName);
        }
        return Toolkit.getDefaultToolkit().getImage(url);
    }
    
    private JMenuBar createMenu() {
        JMenuBar menu = new JMenuBar();
        menuMgr.createMenus(menu, new Object[][]{
            {"file", new Object[][] {
                 {"new", "newDocument", KeyEvent.VK_N},
                 {"-"},
                 {"open", "openDocument", KeyEvent.VK_O},
                 {"save", "saveDocument", KeyEvent.VK_S},
                 {"saveAs", "saveAsDocument"}
             }},
             {"edit", new Object[][] {
                  {"insertChild", "insertChild"},
                  {"insertSibling", "insertSibling"},
              }},
              {"view", new Object[][]{
                   {"zoomIn", "zoomIn", KeyEvent.VK_PLUS},
                   {"zoomOut", "zoomOut", KeyEvent.VK_MINUS}
               }},
               {"help", new Object[][]{
                }}
        });
        if (!Main.isMac()) {
            JMenu fileMenu = getMenu("file");
            fileMenu.addSeparator();
            menuMgr.createItem("exit", fileMenu, "fileExit");
            JMenu editMenu = getMenu("edit");
            menuMgr.createItem("preferences", editMenu, "editPreferences");
            JMenu helpMenu = getMenu("help");
            menuMgr.createItem("manual", helpMenu, "helpManual",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
            menuMgr.createItem("about", helpMenu, "helpAbout");
        } else {
            JMenu helpMenu = getMenu("help");
            menuMgr.createItem("manual", helpMenu, "helpManual",
                    KeyStroke.getKeyStroke(
                    KeyEvent.VK_SLASH,
                    ActionEvent.SHIFT_MASK
                    + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        return menu;
    }
    
    public JMenu getMenu(String name) {
        return menuMgr.getMenu(name);
    }
    
    public JMenuItem getItem(String name) {
        return menuMgr.getItem(name);
    }
    
    /**
     * Set up the data.
     */
    private void buildModel() {
    }
    
    public void setDocument(final IdeaDocument newDocument) {
        this.document = newDocument;
        this.document.addPropertyChangeListener(this);
        this.ideaMap.setIdea(this.document.getIdea());
        this.editSelected();
        this.setDirty(this.document.isDirty());
        this.setTitle(this.document.getTitle());
    }
    
    public IdeaDocument getDocument() {
        return this.document;
    }
    
    public void setDirty(boolean dirty) {
        this.getRootPane().putClientProperty("windowModified",
                Boolean.valueOf(dirty));
    }
    
    public IdeaMap getIdeaMap() {
        return this.ideaMap;
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this.document) {
            if (Main.isMac()) {
                this.setTitle(this.document.getTitle());
            } else {
                this.setTitle(resBundle.getString("app.name") + " - "
                        + this.document.getTitle());
            }
            if ("idea".equals(evt.getPropertyName())) {
                this.ideaMap.setIdea(this.document.getIdea());
            }
            this.setDirty(this.document.isDirty());
        }
    }
    
    public void zoomIn() {
        ideaMap.zoomIn();
    }
    
    public void zoomOut() {
        ideaMap.zoomOut();
    }
    
    public void editSelected() {
        ideaMap.getController().editIdeaView(ideaMap.getRootView());
    }
    
    public void unEditSelected() {
        ideaMap.getController().unEditIdeaView(ideaMap.getRootView());
    }
    
    public void newDocument() {
        IdeaDocument document = new IdeaDocument();
        this.setDocument(document);
    }
    
    public void openDocument() throws IOException, ReaderException {
        FileDialog chooser = new FileDialog(this,
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
            this.setDocument(document);
        }
    }
    
    public void saveAsDocument() throws IOException, ReaderException {
        saveDocument(document, null);
    }
    
    public void saveDocument() throws IOException, ReaderException {
        saveDocument(document, document.getCurrentFile());
    }
    
    public void saveDocument(IdeaDocument document, File f)
    throws IOException, ReaderException {
        File file = f;
        if (file == null) {
            FileDialog chooser = new FileDialog(this,
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
    
    public void insertChild() {
        getIdeaMap().getController().insertChild();
    }
    
    public void insertSibling() {
        getIdeaMap().getController().insertIdea();
    }
    
    public void focusGained(final FocusEvent evt) {
    }
    
    public void focusLost(final FocusEvent evt) {
        this.unEditSelected();
    }
}
