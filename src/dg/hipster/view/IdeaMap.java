/*
 * IdeaMap.java
 *
 * Created on August 31, 2006, 6:03 PM
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

import dg.hipster.controller.IdeaMapController;
import dg.hipster.model.Idea;
import dg.hipster.model.IdeaDocument;
import dg.hipster.view.BranchView;
import dg.hipster.view.IdeaView;
import dg.hipster.view.LinkView;
import dg.hipster.view.MapComponent;
import dg.inx.XMLPanel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Main component for displaying the mind maps.
 * @author davidg
 */
public final class IdeaMap extends JComponent implements MapComponent {
    /**
     * Controller object for this idea map.
     */
    private IdeaMapController controller;
    /**
     * Idea that will appear at the centre of the map.
     */
    private CentreView rootView;
    /**
     * Document containing the idea-map.
     */
    private IdeaDocument document;
    /**
     * Text field that appears at the top of the component.
     */
    private JTextField text;
    /**
     * Index to be applied to next autogenerated idea name
     * (eg 3 for &quot;New 3&quot;).
     */
    private int newCount = 0;
    /**
     * Default background colour.
     */
//    private static final Color DEFAULT_BACKGROUND = new Color(95, 95, 95);
    private static final Color DEFAULT_BACKGROUND = new Color(255, 255, 255);
    /**
     * Floating properties panel.
     */
    private FloatingPanel propertiesPanel;
    /**
     * Pause between animation updates. 40 = 20 fps.
     */
    private static final int REFRESH_PAUSE = 50; // milliseconds
    /**
     * Timer that updates the edited text.
     */
    private Timer ticker = new Timer(REFRESH_PAUSE, new ActionListener() {
        public void actionPerformed(final ActionEvent evt) {
            repaint();
        }
    });
    /**
     * &quot;From&quot; point of the link rubber-band.
     */
    private Point rubberBandFrom;
    /**
     * &quot;To&quot; point of the link rubber-band.
     */
    private Point rubberBandTo;
    /**
     * Whether the properties panel is visible.
     */
    private boolean propertiesVisible;
    /**
     * Viewport to see the map area.
     */
    private Viewport viewport = new Viewport();


    /** Creates a new instance of Fred */
    public IdeaMap() {
        text = new JTextField("");
        setLayout(new BorderLayout());
        add(text, BorderLayout.NORTH);
        controller = new IdeaMapController(this);
        setBackground(DEFAULT_BACKGROUND);
        JLayeredPane mapArea = new JLayeredPane();
        mapArea.setBackground(new Color(0, 0, 0, 0));
        add(mapArea, BorderLayout.CENTER);
        setPropertiesPanel(new FloatingPanel());
        getPropertiesPanel().setCaption("Properties");
        getPropertiesPanel().setBounds(50, 50, 200, 200);
        JLabel labTest = new JLabel("Test");
        labTest.setForeground(null);
        getPropertiesPanel().getContentPane().add(labTest);
        mapArea.add(getPropertiesPanel());
        this.setPropertiesVisible(false);
        viewport.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                repaint();
            }
        });
    }

    /**
     * Text field that appears at the top of the component.
     * @return Text field that appears at the top of the component.
     */
    public JTextField getTextField() {
        return this.text;
    }

    /**
     * Set document.
     */
    public void setDocument(IdeaDocument newDocument) {
        if (this.document != null) {
            this.document.removePropertyChangeListener(this.controller);
        }
        Idea oldIdea = null;
        if (this.document != null) {
            oldIdea = this.document.getIdea();
        }
        this.document = newDocument;
        if (this.document != null) {
            this.document.addPropertyChangeListener(this.controller);
        }
        Idea newIdea = this.document.getIdea();
        if ((newIdea != null) && (!newIdea.equals(oldIdea))) {
            this.rootView = new CentreView(newIdea);
            this.rootView.setParent(this);
            text.setText(newIdea.getText());
            text.setEnabled(false);
            selectIdea(newIdea);
        }
        this.getViewport().resetView();
    }

    public IdeaDocument getDocument() {
        return document;
    }

    public void selectIdea(final Idea selectedIdea) {
        if (selectedIdea != null) {
            FloatingPanel propertiesPanel = this.getPropertiesPanel();
            propertiesPanel.getContentPane().removeAll();
            propertiesPanel.getContentPane().add(new XMLPanel(
                    selectedIdea,
                    "/dg/hipster/view/ideaProperties.xml"));
            propertiesPanel.auto();
            this.setPropertiesVisible(this.getPropertiesVisible());
            if (this.getPropertiesVisible()) {
                propertiesPanel.setVisible(false);
                propertiesPanel.setVisible(true);
            }
            this.getTextField().setText(selectedIdea.getText());
        } else {
            this.setPropertiesVisible(false);
            this.getTextField().setText("");
        }
    }

    /**
     * Currently selected idea branch (if any).
     * @return Currently selected idea branch (if any).
     */
    public IdeaView getSelectedView() {
        return findIdeaViewFor(rootView, getDocument().getSelected());
    }

    /**
     * The idea-view at the centre of this map.
     * @return idea-view at the centre of this map.
     */
    public IdeaView getRootView() {
        return this.rootView;
    }

    /**
     * Viewport for this idea map.
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Paint the map part of the component (the text-field
     * will paint itself).
     * @param gOrig Graphics object to draw on.
     */
    public void paintComponent(Graphics gOrig) {
        Dimension size = getSize();
        gOrig.setColor(this.getBackground());
        gOrig.fillRect(0, 0, size.width, size.height);
        paintMainThing(gOrig);
        if ((rubberBandFrom != null) && (rubberBandTo != null)) {
            this.drawRubberBand((Graphics2D) gOrig);
        }
    }

    public void paintMainThing(Graphics gOrig) {
        Dimension size = getSize();
        Graphics g = gOrig.create();
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.translate(size.width / 2, size.height / 2);
        viewport.transform(g);
        if (rootView != null) {
            rootView.paint(g, this);
        }
        g.dispose();
    }

    /**
     * Start adjusting the map.
     */
    public void startAdjust() {
        controller.startAdjust();
    }

    /**
     * Stop any automatic adjusting of the map.
     */
    public void stopAdjust() {
        controller.stopAdjust();
    }

    /**
     * Insert a child branch to the currently selected idea (do nothing
     * if none selected).
     */
    public void insertChild() {
        final IdeaView selected = getSelectedView();
        if (selected == null) {
            return;
        }
        Idea newIdea = new Idea("New " + (newCount++));
        selected.getIdea().add(0, newIdea);
        this.document.setSelected(newIdea);
        edit();
    }

    /**
     * Put the selected idea into edit mode.
     */
    public void edit() {
        IdeaView selected = this.getSelectedView();
        if (selected != null) {
            this.text.setText(selected.getIdea().getText());
            selected.setEditing(true);
            text.setEnabled(true);
            text.requestFocusInWindow();
            text.selectAll();
            ticker.start();
            repaint();
        }
    }

    /**
     * Switch the given idea view out of edit mode.
     */
    public void unEdit() {
        requestFocusInWindow();
        IdeaView ideaView = getSelectedView();
        if ((ideaView != null) && (ideaView.isEditing())) {
            ideaView.getIdea().setText(text.getText());
            ideaView.setEditing(false);
        }
        text.select(0, 0);
        text.setEnabled(false);
        ticker.stop();
        repaint();
    }

    /**
     * Insert a child idea to the currently selected idea.
     */
    public void insertSibling() {
        final IdeaView selected = getSelectedView();
        if (selected == null) {
            return;
        }
        MapComponent parent = selected.getParent();
        if (!(parent instanceof IdeaView)) {
            return;
        }
        IdeaView parentView = (IdeaView) parent;
        int pos = parentView.getSubBranches().indexOf(selected);
        Idea newIdea = new Idea("New " + (newCount++));
        parentView.getIdea().add(pos + 1, newIdea);
        this.document.setSelected(newIdea);
        edit();
    }

    /**
     * Turn the given branch to point at the given point.
     * @param branch idea to to drag.
     * @param screenPoint Point it will face.
     */
    public void dragBranchTo(final BranchView branch,
            final Point screenPoint) {
        Point2D p = viewport.getMapPoint(getSize(), screenPoint);
        Point2D fromPoint = branch.getFromPoint();
        MapComponent parent = branch.getParent();
        if (parent instanceof CentreView) {
            CentreView centre = (CentreView) parent;
            double x = p.getX();
            x = x * centre.ROOT_RADIUS_Y / centre.ROOT_RADIUS_X;
            p.setLocation(x, p.getY());
            fromPoint = new Point2D.Double(0, 0);
        }
        double angle = getAngleBetween(fromPoint, p);
        if (parent instanceof IdeaView) {
            IdeaView parentView = (IdeaView) parent;
            angle = angle - parentView.getRealAngle();
        }
        angle = normalizeRange(angle);
        double oldAngle = branch.getIdea().getAngle();
        if (Math.abs(oldAngle - angle) < Math.PI) {
            branch.getIdea().setAngle(angle);
        }
        startAdjust();
    }

    /**
     * The clockwise angle in radians of the line between
     * the given points. If toP is directly above fromP, the
     * angle is 0.0.
     * @param fromP start point of the line.
     * @param toP end point of the line.
     * @return clockwise angle in radians of the line.
     */
    static double getAngleBetween(final Point2D fromP, final Point2D toP) {
        double diffX = toP.getX() - fromP.getX();
        double diffY = toP.getY() - fromP.getY();
        double angle = 0.0;
        double tan = Math.abs(diffX) / Math.abs(diffY);
        angle = Math.atan(tan);
        if (diffY > 0) {
            angle = (Math.PI - angle);
        }
        if (diffX < 0) {
            angle *= -1;
        }
        return angle;
    }

    /**
     * Request that a rubber band be drawn from the mid-point
     * of the selected idea, to the given point. This will
     * not actually be drawn until some time later, when
     * this idea map is repainted.
     * @param toPoint point to draw the rubber band line to.
     */
    public void drawLinkRubberBand(final Point toPoint) {
        IdeaView selectedView = getSelectedView();
        if ((selectedView == null) || (!(selectedView instanceof BranchView))) {
            return;
        }
        BranchView branch = (BranchView) selectedView;
        Point2D fromPoint = branch.getMidPoint();
        Point fp = viewport.getScreenPoint(getSize(), fromPoint);
        Graphics2D g = (Graphics2D) getGraphics();
        rubberBandFrom = fp;
        rubberBandTo = toPoint;
        repaint();
    }

    /**
     * Request that the rubber-band line is removed. It will
     * not actually be cleared until this idea-map is repainted.
     */
    public void clearRubberBand() {
        this.rubberBandFrom = null;
        this.rubberBandTo = null;
        repaint();
    }

    public void deSelect() {
        IdeaView selected = getSelectedView();
        if (selected != null) {
            selected.getIdea().setText(getTextField().getText());
            getTextField().setEnabled(false);
            selected.setEditing(false);
        }
    }

    public IdeaView getViewAt(Point2D p) {
        if (this.rootView == null) {
            return null;
        }
        return rootView.getViewAt(p);
    }

    /**
     * Find the view (if any) that represents the given idea.
     * Start the search at the given view, and search all of
     * it's sub-views.
     * @param parentView View to start the search at.
     * @param idea idea we are looking for.
     * @return idea-view representing the idea, or null
     * if none are found.
     */
    public IdeaView findIdeaViewFor(IdeaView parentView, Idea idea) {
        if (parentView == null) {
            return null;
        }
        if (idea == null) {
            return null;
        }
        if (parentView.getIdea().equals(idea)) {
            return parentView;
        }
        for (IdeaView subView: parentView.getSubBranches()) {
            IdeaView ideaView = findIdeaViewFor(subView, idea);
            if (ideaView != null) {
                return ideaView;
            }
        }
        for (LinkView linkView : parentView.linkViews) {
            if (linkView.getIdea().equals(idea)) {
                return linkView;
            }
        }
        return null;
    }

    /**
     * Put the given angle into the range -Pi to Pi.
     * @param angle angle to transform.
     * @return equivalent in the -Pi to Pi range.
     */
    private double normalizeRange(double angle) {
        while (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Draw a rubber band as specified by drawLinkRubberBand.
     * May do nothing if no band specified.
     * @param g graphics to draw on.
     */
    private void drawRubberBand(final Graphics2D g) {
        g.setColor(Color.GRAY);
        Dimension size = getSize();
        Stroke oldStroke = g.getStroke();
        float strokeWidth = BranchView.DEFAULT_STROKE_WIDTH / 2;
        Stroke stroke = new BasicStroke(strokeWidth,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        g.setStroke(stroke);
        g.drawLine(rubberBandFrom.x, rubberBandFrom.y,
                rubberBandTo.x, rubberBandTo.y);
        g.setStroke(oldStroke);
    }

    /**
     * Whether the properties panel is visible.
     * @param show true if visible, false otherwise.
     */
    public void setPropertiesVisible(boolean show) {
        if (!show || (this.getSelectedView() != null)) {
            getPropertiesPanel().setVisible(show);
        }
        propertiesVisible = show;
    }

    /**
     * Whether the properties panel is visible.
     * @return true if visible, false otherwise.
     */
    public boolean getPropertiesVisible() {
        return this.propertiesVisible;
    }

    public FloatingPanel getPropertiesPanel() {
        return propertiesPanel;
    }

    public void setPropertiesPanel(FloatingPanel propertiesPanel) {
        this.propertiesPanel = propertiesPanel;
    }
}
