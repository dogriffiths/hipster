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

import dg.hipster.BrowserLauncher;
import dg.hipster.Main;
import dg.hipster.Utilities;
import dg.hipster.io.ReaderException;
import dg.hipster.io.ReaderFactory;
import dg.hipster.io.WriterFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaDocument;
import dg.hipster.model.Settings;
import dg.inx.XMLMenuBar;
import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
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
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Main window of the application.
 *
 * @author davidg
 */
public final class Mainframe extends JFrame implements PropertyChangeListener,
        FocusListener, ClipboardOwner {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");

    /**
     * Main idea processor component.
     */
    private IdeaMap ideaMap;
    //private IdeaDocument document;

    /** Creates a new instance of Mainframe */
    public Mainframe() {
        super();

        Settings s = Settings.getInstance();
        setBounds(s.getWindowLeft(), s.getWindowTop(),
                s.getWindowWidth(), s.getWindowHeight());
        buildView();
        buildModel();
        try {
            newDocument();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        XMLMenuBar menuBar = new XMLMenuBar(this,
                "/dg/hipster/view/mainframeMenu.xml", resBundle);
        JMenu fileMenu = menuBar.getMenu("file");
        menuBar.createItem("saveAs", fileMenu, "saveAsDocument",
                KeyStroke.getKeyStroke(
                KeyEvent.VK_S,
                ActionEvent.SHIFT_MASK
                + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        JMenu editMenu = menuBar.getMenu("edit");
        if (!Main.isMac()) {
            fileMenu.addSeparator();
            menuBar.createItem("exit", fileMenu, "fileExit");
            menuBar.createItem("preferences", editMenu, "editPreferences");
            JMenu helpMenu = menuBar.getMenu("help");
            menuBar.createItem("manual", helpMenu, "helpManual",
                    KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
            menuBar.createItem("about", helpMenu, "helpAbout");
        } else {
            JMenu helpMenu = menuBar.getMenu("help");
            menuBar.createItem("manual", helpMenu, "helpManual",
                    KeyStroke.getKeyStroke(
                    KeyEvent.VK_SLASH,
                    ActionEvent.SHIFT_MASK
                    + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        JMenuItem itemRedo = editMenu.getItem(1);
        itemRedo.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_Z,
                ActionEvent.SHIFT_MASK
                + Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        return menuBar;
    }

    /**
     * Set up the data.
     */
    private void buildModel() {
        IdeaDocument doc = new IdeaDocument();
        doc.setIdea(new Idea("Untitled"));
        setDocument(doc);
    }

    public void setDocument(final IdeaDocument document) {
        document.addPropertyChangeListener(this);
        this.ideaMap.setDocument(document);
        this.documentUpdated();
        if (!document.isNeedsAdjustment()) {
            ideaMap.stopAdjust();
        } else {
            ideaMap.startAdjust();
        }
        resetView();
    }

    private IdeaDocument getDocument() {
        return this.ideaMap.getDocument();
    }

    public void setDirty(boolean dirty) {
        this.getRootPane().putClientProperty("windowModified",
                Boolean.valueOf(dirty));
    }

    public IdeaMap getIdeaMap() {
        return this.ideaMap;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this.getDocument()) {
            this.documentUpdated();
        }
    }

    private void documentUpdated() {
        String docTitle = this.getDocument().getTitle();
        if (Main.isMac()) {
            this.setTitle(docTitle);
        } else {
            this.setTitle(resBundle.getString("app.name") + " - "
                    + docTitle);
        }
        this.setDirty(this.getDocument().isDirty());
    }

    public void zoomIn() {
        ideaMap.getViewport().zoomIn();
    }

    public void zoomOut() {
        ideaMap.getViewport().zoomOut();
    }

    public void editSelected() {
        ideaMap.edit();
    }

    public void unEditSelected() {
        ideaMap.unEdit();
    }

    public void newDocument() throws IOException, ReaderException {
        if (!checkIfSave()) {
            return;
        }
        this.setDocument(new IdeaDocument());
        this.editSelected();
    }

    public void openDocument() throws IOException, ReaderException {
        if (!checkIfSave()) {
            return;
        }
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
        saveDocument(this.getDocument(), null);
    }

    public void saveDocument() throws IOException, ReaderException {
        saveDocument(this.getDocument(), this.getDocument().getCurrentFile());
    }

    public void saveDocument(IdeaDocument document, File f)
    throws IOException, ReaderException {
        File file = f;
        if (file == null) {
            String filename = document.getTitle();
            if (!filename.toUpperCase().endsWith(".OPML")) {
                filename += ".opml";
            }
            int pos = filename.lastIndexOf(File.separatorChar);
            FileDialog chooser = new FileDialog(this,
                    resBundle.getString("save.opml.file"),
                    FileDialog.SAVE);
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

    public boolean checkIfSave() throws IOException, ReaderException {
        ideaMap.stopAdjust();
        IdeaDocument doc = getDocument();
        if (doc != null) {
            if (doc.isDirty()) {
                int answer = JOptionPane.showConfirmDialog(this,
                        resBundle.getString("save_file_question"),
                        resBundle.getString("app.name"),
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    saveDocument();
                } else if (answer == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
            }
        }
        return true;
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
            try {
                Class HelpBook = Class.forName("dg.hipster.HelpBook");
                Method launchHelpViewer = HelpBook.getMethod(
                        "launchHelpViewer");
                launchHelpViewer.invoke(new Integer(10));
            } catch(Exception cnfe) {
                cnfe.printStackTrace();
            }
        } else {
            String pwd = System.getProperty("launch4j.exedir");
            if ((pwd == null) || (pwd.length() == 0)) {
                pwd = System.getProperty("hipster.library.dir");
            }
            if ((pwd == null) || (pwd.length() == 0)) {
                pwd = System.getProperty("user.dir");
            }
            String manualIndex = pwd + File.separatorChar + "manual"
                    + File.separatorChar + "English" + File.separatorChar
                    + "index.html";
            showUrlInWindows((new File(manualIndex)).toString());
        }
    }

    private void showUrlInWindows(String u) {
        try {
            BrowserLauncher.openURL((new File(u)).toURL().toString());
        } catch(MalformedURLException mfue) {
            mfue.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void homePage() {
        try {
            BrowserLauncher.openURL("http://code.google.com/p/hipster/");
        } catch(MalformedURLException mfue) {
            mfue.printStackTrace();
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void insertChild() {
        getIdeaMap().insertChild();
    }

    public void insertSibling() {
        getIdeaMap().insertSibling();
    }

    public void focusGained(final FocusEvent evt) {
    }

    public void focusLost(final FocusEvent evt) {
        this.unEditSelected();
    }
    /**
     * Reset the zoom and offset.
     */
    public void resetView() {
        ideaMap.getViewport().resetView();
    }

    /**
     * Centre the view.
     */
    public void centreView() {
        ideaMap.getViewport().centreView();
    }

    /**
     * Centre the view.
     */
    public void resetZoom() {
        ideaMap.getViewport().resetZoom();
    }

    /**
     * Toggle the properties panel.
     */
    public void togglePropertiesPanel() {
        this.ideaMap.setPropertiesVisible(!this.ideaMap.getPropertiesVisible());
    }

    public void copyIdea() {
        DataFlavor ideaFlavour = new DataFlavor(Idea.class, "Idea");
        getToolkit().getSystemClipboard().setContents(
                new IdeaSelection(getDocument().getSelected().clone()), this);
    }

    public void cutIdea() {
        DataFlavor ideaFlavour = new DataFlavor(Idea.class, "Idea");
        getToolkit().getSystemClipboard().setContents(
                new IdeaSelection(getDocument().getSelected().clone()), this);
        getDocument().deleteSelected();
    }

    public void pasteIdea() {
        DataFlavor ideaFlavour = new DataFlavor(Idea.class, "Idea");
        Clipboard cb = getToolkit().getSystemClipboard();
        boolean isIdea = false;
        // Weird hack because the ideaFlavour appears in the list of
        // available flavors, but isDataFlavorAvailable returns false...
        for (DataFlavor f : cb.getAvailableDataFlavors()) {
            if (f.equals(ideaFlavour)) {
                isIdea = true;
            }
        }
        try {
            if (isIdea){
                Idea idea = (Idea)getToolkit().getSystemClipboard(
                        ).getContents(this).getTransferData(ideaFlavour);
                getDocument().getSelected().add(idea.clone());
            } else if (cb.isDataFlavorAvailable(
                    DataFlavor.javaFileListFlavor)){
                boolean err = false;
                final Object os = getToolkit().getSystemClipboard(
                        ).getContents(this).getTransferData(
                        DataFlavor.javaFileListFlavor);
                File f = (File)((Collection)os).iterator().next();
                boolean isOpml = f.getName().toUpperCase().endsWith(".OPML");
                if (isOpml) {
                    ReaderFactory factory = ReaderFactory.getInstance();
                    IdeaDocument document = factory.read(f);
                    if (getDocument().getSelected() != null) {
                        int answer = JOptionPane.showConfirmDialog(ideaMap,
                                resBundle.getString("insert_drag_question"),
                                resBundle.getString("app.name"),
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            getDocument().getSelected().add(document.getIdea());
                        } else if (answer == JOptionPane.NO_OPTION) {
                            this.setDocument(document);
                        }
                    } else {
                        Main.getMainframe().setDocument(document);
                    }
                } else if (getDocument().getSelected() != null) {
                    Idea idea = new Idea(f.getName());
                    idea.setDescription(f.toString());
                    String url = Utilities.toStringUrl(f);
                    idea.setUrl(url);
                    getDocument().getSelected().add(idea);
                }
            } else if (cb.isDataFlavorAvailable(
                    DataFlavor.stringFlavor)){
                String s = getToolkit().getSystemClipboard(
                        ).getContents(this).getTransferData(
                        DataFlavor.stringFlavor).toString();
                boolean isURLFormatted = (s.indexOf(':') >= 2);
                if (isURLFormatted) {
                    // DnD from firefox introduces a \n followed by the anchor label (like "RSS feed")
                    // that appears on the page. Hence, use only the substring upto the \n
                    int linebreakIndex = s.indexOf('\n');
                    String insertText = null;
                    Idea idea = new Idea(s);
                    if (s.startsWith("http") && (linebreakIndex != -1)) {
                        insertText = s.substring(0,linebreakIndex);
                        idea.setText(s.substring(linebreakIndex + 1));
                    } else {
                        //DnD from IE and other browsers;use the string as it is.
                        insertText = s;
                    }
                    if (insertText.startsWith("x-yojimbo-item:")) {
                        idea.setText("Yojimbo item");
                    }
                    idea.setUrl(insertText);
                    getDocument().getSelected().add(idea);
                }
            }
        } catch (Exception e) {
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable transferable) {

    }

    /**
     * Undo the last change.
     */
    public void undo() {
        if (this.ideaMap.getDocument() != null) {
            this.ideaMap.getDocument().undo();
        }
    }

    /**
     * Redo the last undone change.
     */
    public void redo() {
        if (this.ideaMap.getDocument() != null) {
            this.ideaMap.getDocument().redo();
        }
    }
}

class IdeaSelection implements Transferable {
    private Idea idea;

    public IdeaSelection(Idea anIdea) {
        this.idea = anIdea;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {new DataFlavor(Idea.class, "Idea")};
    }

    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        return (dataFlavor.getDefaultRepresentationClass().equals(Idea.class));
    }

    public Idea getTransferData(DataFlavor dataFlavor) {
        return idea;
    }
}