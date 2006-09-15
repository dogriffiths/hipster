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
import dg.hipster.controller.MainframeController;
import dg.hipster.model.Settings;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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
        controller = new MainframeController(this);
    }
    
    private JMenuBar menu;
    private JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem openItem;
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem exitItem;
    private JMenu viewMenu;
    private JMenuItem zoomInItem;
    private JMenuItem zoomOutItem;
    private JMenu helpMenu;
    private JMenuItem aboutItem;
    private MainframeController controller;
    
    /**
     * Lay the window out.
     */
    private void buildView() {
        ideaMap = new IdeaMap();
        this.getContentPane().add(getIdeaMap(), BorderLayout.CENTER);
        menu = new JMenuBar();
        fileMenu = new JMenu("File");
        menu.add(fileMenu);
        newItem = new JMenuItem("New");
        fileMenu.add(newItem);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        openItem = new JMenuItem("Open...");
        fileMenu.add(openItem);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveItem = new JMenuItem("Save");
        fileMenu.add(saveItem);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        saveAsItem = new JMenuItem("Save As...");
        fileMenu.add(saveAsItem);
        viewMenu = new JMenu("View");
        menu.add(viewMenu);
        zoomInItem = new JMenuItem("Make Text Bigger");
        viewMenu.add(zoomInItem);
        zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        zoomOutItem = new JMenuItem("Make Text Smaller");
        viewMenu.add(zoomOutItem);
        zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        if (!Main.isMac()) {
            exitItem = new JMenuItem("Exit");
            fileMenu.addSeparator();
            fileMenu.add(exitItem);
            helpMenu = new JMenu("Help");
            aboutItem = new JMenuItem("About " + resBundle.getString("app.name"));
            
            helpMenu.add(aboutItem);
        }
        this.setJMenuBar(getMenu());
    }
    
    private String currentFile;
    
    
    /**
     * Set up the data.
     */
    private void buildModel() {
    }
    
    public IdeaMap getIdeaMap() {
        return this.ideaMap;
    }
    
    public void setCurrentFile(String filename) {
        this.currentFile = filename;
        if (currentFile != null) {
            this.setTitle(resBundle.getString("app.name") + " - "
                    + currentFile);
        } else {
            this.setTitle(resBundle.getString("app.name"));
        }
    }
    
    public String getCurrentFile() {
        return this.currentFile;
    }
    
    public JMenuBar getMenu() {
        return menu;
    }
    
    public JMenu getFileMenu() {
        return fileMenu;
    }
    
    public JMenuItem getNewItem() {
        return newItem;
    }
    
    public JMenuItem getOpenItem() {
        return openItem;
    }
    
    public JMenuItem getSaveItem() {
        return saveItem;
    }
    
    public JMenuItem getSaveAsItem() {
        return saveAsItem;
    }
    
    public JMenuItem getZoomInItem() {
        return zoomInItem;
    }
    
    public JMenuItem getZoomOutItem() {
        return zoomOutItem;
    }
    
    public JMenuItem getExitItem() {
        return exitItem;
    }
    
    public JMenu getHelpMenu() {
        return helpMenu;
    }
    
    public JMenuItem getAboutItem() {
        return aboutItem;
    }
}
