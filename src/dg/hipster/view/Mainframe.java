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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
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
    private MainframeController controller;
    private MenuManager menuMgr = new MenuManager();
    private String currentFile;
    
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

    /**
     * Lay the window out.
     */
    private void buildView() {
        ideaMap = new IdeaMap();
        this.getContentPane().add(getIdeaMap(), BorderLayout.CENTER);
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
             {"view", new Object[][]{
                  {"zoomIn", KeyEvent.VK_PLUS},
                  {"zoomOut", KeyEvent.VK_MINUS}
              }}
        });
        if (!Main.isMac()) {
            JMenu fileMenu = getMenu("file");
            fileMenu.addSeparator();
            menuMgr.createItem("exit", fileMenu);
            menuMgr.createMenu("help", menu, new Object[][]{
                {"about"}
            });
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
}
