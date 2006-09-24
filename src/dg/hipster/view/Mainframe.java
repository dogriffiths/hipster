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
import dg.hipster.controller.MainframeController;
import dg.hipster.model.IdeaDocument;
import dg.hipster.model.Settings;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
public final class Mainframe extends JFrame implements PropertyChangeListener {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    /**
     * Main idea processor component.
     */
    private MainframeController controller;
    private MenuManager menuMgr = new MenuManager();
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
        controller = new MainframeController(this);
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
                 {"new", KeyEvent.VK_N},
                 {"-"},
                 {"open", KeyEvent.VK_O},
                 {"save", KeyEvent.VK_S},
                 {"saveAs"}
             }},
             {"edit", new Object[][] {
                  {"insertChild"},
                  {"insertSibling"},
              }},
              {"view", new Object[][]{
                   {"zoomIn", KeyEvent.VK_PLUS},
                   {"zoomOut", KeyEvent.VK_MINUS}
               }},
               {"help", new Object[][]{
                }}
        });
        if (!Main.isMac()) {
            JMenu fileMenu = getMenu("file");
            fileMenu.addSeparator();
            menuMgr.createItem("exit", fileMenu);
            JMenu editMenu = getMenu("edit");
            menuMgr.createItem("preferences", editMenu);
            JMenu helpMenu = getMenu("help");
            menuMgr.createItem("manual", helpMenu,
                    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
            menuMgr.createItem("about", helpMenu);
        } else {
            JMenu helpMenu = getMenu("help");
            menuMgr.createItem("manual", helpMenu, KeyStroke.getKeyStroke(
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
}
