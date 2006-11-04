/*
 * IdeaMapController.java
 *
 * Created on September 1, 2006, 12:09 PM
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

import dg.hipster.BrowserLauncher;
import dg.hipster.Main;
import dg.hipster.Utilities;
import dg.hipster.io.ReaderFactory;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaDocument;
import dg.hipster.model.IdeaLink;
import dg.hipster.view.BranchView;
import dg.hipster.view.IdeaMap;
import dg.hipster.view.IdeaView;
import dg.hipster.view.MapComponent;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 * Object that can control an idea-map. It registers
 * itself to listen keyboard, mouse and other activity
 * and decides how the idea map should react.
 * @author davidg
 */
public final class IdeaMapController implements KeyListener, FocusListener,
        MouseListener, MouseMotionListener, PropertyChangeListener {
    /**
     * Internationalization strings.
     */
    private static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    /**
     * Idea map being controlled.
     */
    private IdeaMap ideaMap;
    /**
     * Point in idea-map relative screen space when the mouse was last
     * pressed. This is used to drag the result of dragging.
     */
    private Point downPoint;
    /**
     * The controller animating the map.
     */
    private MapMover mapMover;
    /**
     * Branch being dragged.
     */
    private BranchView draggedBranch;
    /**
     * Factor to zoom by with each mouse-wheel click.
     */
    static final double ZOOM_PER_CLICK = 0.8;
    
    /**
     * Creates a new instance of IdeaMapController.
     *@param newIdeaMap Idea map to control.
     */
    public IdeaMapController(final IdeaMap newIdeaMap) {
        this.ideaMap = newIdeaMap;
        this.ideaMap.setFocusTraversalKeysEnabled(false);
        this.ideaMap.setFocusable(true);
        this.ideaMap.addFocusListener(this);
        this.ideaMap.addKeyListener(this);
        this.ideaMap.addMouseListener(this);
        this.ideaMap.addMouseMotionListener(this);
        this.ideaMap.addMouseWheelListener(
                new MouseWheelListener() {
            public void mouseWheelMoved(final MouseWheelEvent e) {
                int rotation = e.getWheelRotation();
                ideaMap.getViewport().zoom(Math.pow(ZOOM_PER_CLICK, rotation));
            }
        });
        this.ideaMap.getTextField().addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                unEditCurrent();
            }
        });
        this.ideaMap.getTextField().addFocusListener(new FocusListener() {
            public void focusLost(final FocusEvent fe) {
                unEditCurrent();
            }
            public void focusGained(final FocusEvent fe) {
                
            }
        });
        this.ideaMap.requestFocusInWindow();
        this.mapMover = new MapMover(this.ideaMap);
        DropTargetListener dtl = new DragAndDropController(this.ideaMap);
        DropTarget dt = new DropTarget(ideaMap, dtl);
        dt.setActive(true);
    }
    
    public void propertyChange(final PropertyChangeEvent evt) {
        if ("selected".equals(evt.getPropertyName())) {
            Idea selectedIdea = (Idea) evt.getNewValue();
            ideaMap.selectIdea(selectedIdea);
            
        }
        if (!ideaMap.getTextField().isEnabled()) {
            IdeaDocument doc = ideaMap.getDocument();
            if (doc != null) {
                Idea selected = doc.getSelected();
                if (selected != null) {
                    ideaMap.getTextField().setText(selected.getText());
                }
            }
        }
    }
    
    /**
     * Stop the automatic adjustment process.
     */
    public void stopAdjust() {
        mapMover.stopAdjust();
    }
    
    /**
     * Start the automatic adjustment process.
     */
    public void startAdjust() {
        mapMover.startAdjust();
    }
    
    /**
     * Switch the current idea view out of editing mode.
     */
    private void unEditCurrent() {
        this.ideaMap.unEdit();
    }
    
    /**
     * Called when a mouse is pressed and released on
     * the idea map.
     * @param evt event describing the mouse click.
     */
    public void mouseClicked(final MouseEvent evt) {
        Point2D p = this.ideaMap.getViewport().getMapPoint(
                this.ideaMap.getSize(), evt.getPoint());
        IdeaView hit = this.ideaMap.getViewAt(p);
        if (hit == null) {
            ideaMap.getDocument().setSelected(null);
        } else {
            String url = hit.getIdea().getUrl();
            if ((url.length() > 0) && (!url.startsWith("#"))
            && (evt.getClickCount() == 2)) {
                try {
                    BrowserLauncher.openURL(url);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    /**
     * Called when a mouse is pressed on the idea map.
     * @param evt event describing the mouse press.
     */
    public void mousePressed(final MouseEvent evt) {
        this.ideaMap.unEdit();
        downPoint = evt.getPoint();
        Point2D p = this.ideaMap.getViewport().getMapPoint(
                this.ideaMap.getSize(), evt.getPoint());
        if (this.ideaMap != null) {
            boolean shouldEdit = (evt.getClickCount() == 2);
            selectIdeaViewAt(p, shouldEdit);
        }
    }
    
    /**
     * Select the ideaView (if any) that is at the given point.
     * @param p point to consider.
     * @param shouldEdit true if the view should go into edit mode,
     * false otherwise.
     */
    private void selectIdeaViewAt(final Point2D p, final boolean shouldEdit) {
        IdeaView hit = this.ideaMap.getViewAt(p);
        if (hit != null) {
            this.ideaMap.getDocument().setSelected(hit.getIdea());
            if (hit instanceof BranchView) {
                draggedBranch = (BranchView) hit;
                mapMover.setFixedBranch(draggedBranch);
            }
            ideaMap.getTextField().setText(hit.getIdea().getText());
            if ((shouldEdit) && ((hit.getIdea().getUrl() == null)
            || (hit.getIdea().getUrl().length() == 0))) {
                this.ideaMap.edit();
            }
        }
    }
    
    /**
     * Called when a mouse is released over the idea map.
     * @param evt event describing the mouse release.
     */
    public void mouseReleased(final MouseEvent evt) {
        if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0) {
            Point2D p = this.ideaMap.getViewport().getMapPoint(
                    this.ideaMap.getSize(), evt.getPoint());
            createLinkTo(p);
        }
        draggedBranch = null;
        mapMover.setFixedBranch(null);
        this.ideaMap.clearRubberBand();
    }
    
    /**
     * Called when a mouse exits the idea map.
     * @param evt event describing the mouse exit.
     */
    public void mouseExited(final MouseEvent evt) {
        
    }
    
    /**
     * Called when a mouse enters the idea map.
     * @param evt event describing the mouse entry.
     */
    public void mouseEntered(final MouseEvent evt) {
        
    }
    
    /**
     * Called when a mouse moves over the idea map.
     * @param evt event describing the mouse movement.
     */
    public void mouseMoved(final MouseEvent evt) {
        
    }
    
    /**
     * Called when a mouse is dragged over the idea map.
     * @param evt event describing the mouse drag.
     */
    public synchronized void mouseDragged(final MouseEvent evt) {
        if ((evt.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == 0) {
            if (draggedBranch != null) {
                this.ideaMap.dragBranchTo(draggedBranch, evt.getPoint());
            } else {
                dragMapTo(evt.getPoint());
                return;
            }
        } else {
            this.ideaMap.drawLinkRubberBand(evt.getPoint());
        }
    }
    
    /**
     * Drag the map so that the point that the mouse was
     * first pressed at is moved to the given point.
     * @param p point that the down point should be moved
     * to.
     */
    private void dragMapTo(final Point p) {
        if ((p == null) || (downPoint == null)) {
            return;
        }
        int xDiff = p.x - downPoint.x;
        int yDiff = p.y - downPoint.y;
        Point offset = ideaMap.getViewport().getOffset();
        if (offset == null) {
            offset = new Point(0, 0);
        }
        ideaMap.getViewport().setOffset(new Point(offset.x + xDiff,
                offset.y + yDiff));
        downPoint = p;
    }
    
    /**
     * Called the idea map gains the focus.
     * @param evt event describing the capture of the focus.
     */
    public void focusGained(final FocusEvent evt) {
    }
    
    /**
     * Calledwhen the idea map loses the focus.
     * @param evt event describing the loss of focus.
     */
    public void focusLost(final FocusEvent evt) {
    }
    
    /**
     * Called the a key is released when the idea map
     * has the focus.
     * @param evt event describing the release of the key.
     */
    public void keyReleased(final KeyEvent evt) {
    }
    
    /**
     * Called whe a key is typed when the idea map
     * has the focus.
     * @param evt event describing the typing of the key.
     */
    public void keyTyped(final KeyEvent evt) {
    }
    
    /**
     * Called whe a key is pressed down when the idea map
     * has the focus.
     * @param evt event describing the pressing of the key.
     */
    public void keyPressed(final KeyEvent evt) {
        switch(evt.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (mapMover.isRunning()) {
                    stopAdjust();
                } else {
                    startAdjust();
                }
                break;
            case KeyEvent.VK_UP:
                selectUp();
                break;
            case KeyEvent.VK_DOWN:
                selectDown();
                break;
            case KeyEvent.VK_LEFT:
                selectLeft();
                break;
            case KeyEvent.VK_RIGHT:
                selectRight();
                break;
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_DELETE:
                this.ideaMap.getDocument().deleteSelected();
                break;
            case KeyEvent.VK_ESCAPE:
                this.ideaMap.unEdit();
                break;
            case KeyEvent.VK_ENTER:
                if (evt.getModifiers() != 0) {
                    this.ideaMap.edit();
                } else {
                    this.ideaMap.insertSibling();
                }
                break;
            case KeyEvent.VK_TAB:
                this.ideaMap.insertChild();
                break;
            default:
                // Do nothing
        }
    }
    
    /**
     * Select the idea that appears below the current
     * one in the idea map.
     */
    private void selectDown() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Westerly
        double y = Double.NEGATIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getY() > y) {
                y = endPoint.getY();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.getDocument().setSelected(nextView.getIdea());
        }
    }
    
    /**
     * Select the idea that appears above the current
     * one in the idea map.
     */
    private void selectUp() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Westerly
        double y = Double.POSITIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getY() < y) {
                y = endPoint.getY();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.getDocument().setSelected(nextView.getIdea());
        }
    }
    
    /**
     * Select the sibling given by the different (clockwise
     * is positive). Calling this method with +1 will select
     * the sibling idea immediately clockwise of the current
     * one.
     * @param diff offset of the sibling idea, in a clockwise
     * direction.
     */
    private void selectSibling(final int diff) {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        IdeaView previous = selected.getSibling(diff);
        if (previous == null) {
            return;
        }
        this.ideaMap.getDocument().setSelected(previous.getIdea());
    }
    
    /**
     * Select the idea immediately to the right of the current
     * one in the idea map.
     */
    private void selectRight() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Easterly
        double x = Double.NEGATIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getX() > x) {
                x = endPoint.getX();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.getDocument().setSelected(nextView.getIdea());
        }
    }
    
    /**
     * Select the idea immediately to the left of the
     * current one in the idea map.
     */
    private void selectLeft() {
        final IdeaView selected = this.ideaMap.getSelectedView();
        if (selected == null) {
            return;
        }
        Map<Point2D, IdeaView> viewMap = endPoints(selected);
        if (viewMap.size() == 0) {
            return;
        }
        // Find the most Westerly
        double x = Double.POSITIVE_INFINITY;
        IdeaView nextView = null;
        for (Point2D endPoint : viewMap.keySet()) {
            if (endPoint.getX() < x) {
                x = endPoint.getX();
                nextView = viewMap.get(endPoint);
            }
        }
        if (nextView != null) {
            this.ideaMap.getDocument().setSelected(nextView.getIdea());
        }
    }
    
    /**
     * Get a map of endpoints of the ideas that are
     * immediately connected to the given one. This is
     * useful when deciding which ideas lie to the
     * left or right of the current one.
     * @param ideaView central view that the others will
     * be connected to.
     * @return map of point->views.
     */
    private Map<Point2D, IdeaView> endPoints(final IdeaView ideaView) {
        Map<Point2D, IdeaView> results = new HashMap<Point2D, IdeaView>();
        List<BranchView> subViews = ideaView.getSubBranches();
        // Add all the sub-views
        results.put(ideaView.getEndPoint(), ideaView);
        for (IdeaView subView : subViews) {
            results.put(subView.getEndPoint(), subView);
        }
        IdeaView previousSibling = ideaView.getPreviousSibling();
        if (previousSibling != null) {
            results.put(previousSibling.getEndPoint(), previousSibling);
        }
        IdeaView nextSibling = ideaView.getNextSibling();
        if (nextSibling != null) {
            results.put(nextSibling.getEndPoint(), nextSibling);
        }
        MapComponent parent = ideaView.getParent();
        if (parent instanceof IdeaView) {
            IdeaView parentView = (IdeaView) parent;
            MapComponent grandParent = parentView.getParent();
            if (grandParent instanceof IdeaView) {
                IdeaView grandParentView = (IdeaView) grandParent;
                results.put(grandParentView.getEndPoint(), parentView);
            } else {
                results.put(parentView.getEndPoint(), parentView);
            }
        }
        return results;
    }
    
    /**
     * Create a link from the currently selected idea
     * to the view at the given point.
     * @param p point where a view may lie.
     */
    private void createLinkTo(final Point2D p) {
        if (this.ideaMap != null) {
            IdeaView hit = this.ideaMap.getViewAt(p);
            if ((hit != null) && (hit instanceof BranchView)) {
                Idea selectedIdea = this.ideaMap.getDocument().getSelected();
                if ((selectedIdea != null)
                && (this.ideaMap.getSelectedView() instanceof BranchView)) {
                    Idea hitIdea = hit.getIdea();
                    selectedIdea.addLink(new IdeaLink(selectedIdea, hitIdea));
                }
            }
        }
    }
}

/**
 * Meta-data about a given file.
 */
class FileMetaData {
    /**
     * Map used to store meta-data attributes and values.
     */
    private Map<String, String> data;
    
    /**
     * Constructor for meta-data of a given file.
     * @param f file we are interested in.
     */
    FileMetaData(final File f) {
        data = new HashMap<String, String>();
        data.put("kMDItemTitle", f.getName());
        if (Main.isMac()) {
            Process process = null;
            DataInputStream in = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{
                    "mdls", f.toString()
                }, null, f.getParentFile());
                
                in = new DataInputStream(process.getInputStream());
                String line = null;
                while ((line = in.readLine()) != null) {
                    int pos0 = line.indexOf('=');
                    if (pos0 != -1) {
                        String mdAttr = line.substring(0, pos0).trim();
                        int pos1 = line.indexOf('"');
                        if (pos1 != -1) {
                            int pos2 = line.indexOf('"', pos1 + 2);
                            if (pos2 != 0) {
                                data.put(mdAttr,
                                        line.substring(pos1 + 1, pos2));
                            }
                        }
                    }
                }
                
            } catch (Exception e) {
                // Oh well...
            } finally {
                try {
                    in.close();
                } catch (Exception e2) {
                    // Oh well... part 2
                }
            }
        }
    }
    
    /**
     * Return the value of the requested parameters, separated by
     * newlines.
     *@param names - list of attribute names.
     *@return multi-line string.
     */
    public String get(final String... names) {
        String result = "";
        for (String name : names) {
            String s = get(name);
            if (s.length() > 0) {
                result += s + "\n";
            }
        }
        return result;
    }
    
    /**
     * Get a single meta-data attribute, with names matching
     * those on the Mac platform.
     *@param name - attribute name.
     *@return string value of the attribute.
     */
    public String get(final String name) {
        String result = data.get(name);
        if (result == null) {
            result = "";
        }
        return result;
    }
}

/**
 * Drag and drop controller for the idea map.
 */
class DragAndDropController implements DropTargetListener {
    /**
     * Internationalization strings.
     */
    private static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    /**
     * Idea map that stuff is being dropped onto.
     */
    private IdeaMap ideaMap;
    
    /**
     * Constructor of a controller for a given
     * idea map.
     * @param anIdeaMap idea map that we are watching for
     * DnD events on.
     */
    public DragAndDropController(final IdeaMap anIdeaMap) {
        this.ideaMap = anIdeaMap;
    }
    /**
     * Drag peration enters the idea map.
     * @param dtde event describing the drag.
     */
    public void dragEnter(final DropTargetDragEvent dtde) {
    }
    /**
     * Drag operation has left the idea map.
     * @param dtde event describing the exit.
     */
    public void dragExit(final DropTargetEvent dtde) {
    }
    /**
     * Called each time the drag operation
     * continues over the idea map.
     * @param event event describing the lates drag operation.
     */
    public void dragOver(final DropTargetDragEvent event) {
    }
    /**
     * Called if the user changes the nature of
     * the drag operation, for example by pressing
     * a modifier key.
     * @param dtde event describing the change in the nature
     * of the drag operation.
     */
    public void dropActionChanged(final DropTargetDragEvent dtde) {
    }
    /**
     * Something has been dropped on the idea map.
     * @param event event describing the drop operation.
     */
    public void drop(final DropTargetDropEvent event) {
        try {
            Transferable transferable = event.getTransferable();
            IdeaView ideaView = ideaMap.getViewAt(
                    ideaMap.getViewport().getMapPoint(
                    ideaMap.getSize(), event.getLocation()));
            if (transferable.isDataFlavorSupported(
                    DataFlavor.javaFileListFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                final Object os = transferable.getTransferData(
                        DataFlavor.javaFileListFlavor);
                if (!(os instanceof java.util.Collection)) {
                    event.rejectDrop();
                    return;
                }
                boolean err = false;
                File f = (File) ((Collection) os).iterator().next();
                boolean isOpml = f.getName().toUpperCase().endsWith(".OPML");
                if (isOpml) {
                    ReaderFactory factory = ReaderFactory.getInstance();
                    IdeaDocument document = factory.read(f);
                    if (ideaView != null) {
                        int answer = JOptionPane.showConfirmDialog(ideaMap,
                                resBundle.getString("insert_drag_question"),
                                resBundle.getString("app.name"),
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        if (answer == JOptionPane.YES_OPTION) {
                            ideaView.getIdea().add(document.getIdea());
                        } else if (answer == JOptionPane.NO_OPTION) {
                            Main.getMainframe().setDocument(document);
                        }
                    } else {
                        Main.getMainframe().setDocument(document);
                    }
                } else if (ideaView != null) {
                    FileMetaData metadata = new FileMetaData(f);
                    Idea idea = new Idea(metadata.get("kMDItemTitle"));
                    idea.setNotes(metadata.get("kMDItemComment",
                            "kMDItemCopyright"));
                    idea.setDescription(
                            metadata.get("kMDItemDisplayName")
                            );
                    String url = Utilities.toStringUrl(f);
                    idea.setUrl(url);
                    ideaView.getIdea().add(idea);
                } else {
                    event.rejectDrop();
                }
                event.getDropTargetContext().dropComplete(true);
            } else if (transferable.isDataFlavorSupported(
                    DataFlavor.stringFlavor)) {
                event.acceptDrop(DnDConstants.ACTION_COPY);
                String s = transferable.getTransferData(
                        DataFlavor.stringFlavor).toString();
                boolean isURLFormatted = (s.indexOf(':') >= 2);
                if (isURLFormatted) {
                    int linebreakIndex = s.lastIndexOf('\n');
                    String insertText = null;
                    Idea idea = new Idea(s);
                    if (linebreakIndex != -1) {
                        insertText = s.substring(0, linebreakIndex);
                        idea.setText(s.substring(linebreakIndex + 1));
                    } else {
                        insertText = s;
                    }
                    idea.setUrl(insertText);
                    ideaView.getIdea().add(idea);
                } else {
                    event.rejectDrop();
                }
            } else {
                event.rejectDrop();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            event.rejectDrop();
        }
    }
}