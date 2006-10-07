/*
 * Main.java
 *
 * Created on July 19, 2006, 6:44 AM
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

package dg.hipster;

import dg.hipster.model.IdeaDocument;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.Settings;
import dg.hipster.view.AboutBox;
import dg.hipster.view.GuiUtilities;
import dg.hipster.view.Mainframe;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.JFrame;

/**
 * Main class of the application.
 * @author davidg
 */
public final class Main {
    /**
     * The single instance of the application that will be created.
     */
    private static Main main;
    /**
     * The main window.
     */
    private Mainframe frame;
    /**
     * The &quot;About...&quot; box.
     */
    private AboutBox aboutBox = new AboutBox();
    /**
     * Internationalization strings.
     */
    private static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    static {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
    }
    
    /**
     * The method executed at application start.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        main = new Main();
        main.initView();
        main.initControllers();
        IdeaDocument document = new IdeaDocument();
        if (args.length > 0) {
            try {
                document = ReaderFactory.getInstance().read(new File(args[0]));
            } catch (ReaderException re) {
                GuiUtilities.showDebug(re.getMessage());
            }
        }
        getMainframe().setDocument(document);
        main.setVisible(true);
    }
    
    /**
     * Default constructor of the application. Left as package-private
     * for unit-testing.
     */
    Main() {
    }
    
    /**
     * Set up the interface.
     */
    private void initView() {
        frame = new Mainframe();
    }
    
    /**
     * Connect the controllers to the interface.
     */
    private void initControllers() {
        if (frame != null) {
            frame.setDefaultCloseOperation(
                    JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(final WindowEvent e) {
                    if (handleQuit()) {
                        System.exit(0);
                    }
                }
            });
        }
        if (isMac()) {
            loadMacPlugins();
        }
    }
    
    /**
     * Load plugins for integration with the Macintosh environment.
     */
    private void loadMacPlugins() {
        try {
            Class.forName("dg.hipster.controller.MacAppListener");
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
    
    /**
     * Make the application visible. Will put the app into edit
     * mode.
     * @param visible true to become visible, false otherwise.
     */
    public void setVisible(final boolean visible) {
        if (frame != null) {
            frame.setVisible(visible);
            frame.editSelected();
        }
    }
    
    /**
     * True if we are running on a Mac.
     * @return true if the current platform is a Mac, false otherwise
     */
    public static boolean isMac() {
        String osName = System.getProperty("os.name");
        return ((osName != null) && (osName.indexOf("Mac") != -1));
    }
    
    /**
     * True if we are running on Windows.
     * @return true if the current platform is Windows, false otherwise
     */
    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return ((osName != null) && (osName.indexOf("Windows") != -1));
    }
    
    /**
     * Display the about box.
     */
    public static void showAbout() {
        main.aboutBox.setVisible(true);
    }
    
    /**
     * Display the preferences dialog.
     */
    public static void showPreferences() {
        GuiUtilities.showInfo("preferences.placeholder");
    }
    
    /**
     * Called when the application being closed down.
     */
    public static boolean handleQuit() {
        Rectangle bounds = main.frame.getBounds();
        Settings.getInstance().setWindowLeft(bounds.x);
        Settings.getInstance().setWindowTop(bounds.y);
        Settings.getInstance().setWindowWidth(bounds.width);
        Settings.getInstance().setWindowHeight(bounds.height);
        try {
            return getMainframe().checkIfSave();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Get the main application frme.
     * @return main application frame.
     */
    public static Mainframe getMainframe() {
        return main.frame;
    }
}
