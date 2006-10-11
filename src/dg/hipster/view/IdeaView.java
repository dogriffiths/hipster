/*
 * IdeaView.java
 *
 * Created on August 24, 2006, 11:39 AM
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

import dg.hipster.model.Idea;
import dg.hipster.model.IdeaEvent;
import dg.hipster.model.IdeaLink;
import dg.hipster.model.IdeaListener;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author davidg
 */
public abstract class IdeaView implements IdeaListener, MapComponent {
    Vector<BranchView> subViews = new Vector<BranchView>();
    Vector<LinkView> linkViews = new Vector<LinkView>();
    private Idea idea;
    private boolean selected;
    private boolean editing;
    MapComponent parent;
    private double realAngle;
    
    public IdeaView() {
        this(null);
    }
    
    public IdeaView(Idea anIdea) {
        setIdea(anIdea);
    }
    
    public double getMinSubAngle() {
        double minAngle = Math.PI;
        for (IdeaView subView: subViews) {
            if (subView.getIdea().getAngle() < minAngle) {
                minAngle = subView.getIdea().getAngle();
            }
        }
        return minAngle;
    }
    
    public double getMaxSubAngle() {
        double maxAngle = -Math.PI;
        for (IdeaView subView: subViews) {
            if (subView.getIdea().getAngle() > maxAngle) {
                maxAngle = subView.getIdea().getAngle();
            }
        }
        return maxAngle;
    }
    
    public void ideaChanged(IdeaEvent fe) {
        int id = fe.getID();
        if (id == IdeaEvent.ADDED) {
            Idea ideaParent = (Idea)fe.getSource();
            if (this.idea.equals(ideaParent)) {
                Idea subIdea = fe.getIdea();
                int pos = (Integer)fe.getParam();
                BranchView subIdeaView = new BranchView(subIdea);
                double maxAngle = getMaxSubAngle();
                double angle = (maxAngle + Math.PI) / 2.0;
                subIdeaView.getIdea().setAngle(angle);
                add(pos, subIdeaView);
            }
        } else if (id == IdeaEvent.REMOVED) {
            Idea ideaParent = (Idea)fe.getSource();
            if (this.idea.equals(ideaParent)) {
                Idea subIdea = fe.getIdea();
                for (int i = 0; i < subViews.size(); i++) {
                    Idea idea = (Idea) subViews.get(i).getIdea();
                    if (idea.equals(subIdea)) {
                        subViews.remove(i);
                        break;
                    }
                }
            }
        } else if (id == IdeaEvent.ADDED_LINK) {
            Idea ideaParent = (Idea)fe.getSource();
            LinkView linkView = new LinkView((IdeaLink)fe.getIdea());
            linkView.parent = this;
            linkViews.add(linkView);
        } else if (id == IdeaEvent.REMOVED_LINK) {
            Idea ideaParent = (Idea)fe.getSource();
            if (this.idea.equals(ideaParent)) {
                IdeaLink link = (IdeaLink)fe.getIdea();
                for (int i = 0; i < linkViews.size(); i++) {
                    IdeaLink idea = (IdeaLink) linkViews.get(i).getIdea();
                    if (link.equals(idea)) {
                        linkViews.remove(i);
                        break;
                    }
                }
            }
        } else {
            this.getIdea().setLength(10 * idea.getText().length() + 20);
        }
        startAdjust();
    }
    
    public IdeaView getPreviousSibling() {
        return getSibling(-1);
    }
    
    public IdeaView getNextSibling() {
        return getSibling(+1);
    }
    
    public IdeaView getSibling(int difference) {
        MapComponent parent = getParent();
        if (!(parent instanceof IdeaView)) {
            return null;
        }
        IdeaView parentView = (IdeaView) parent;
        int pos = parentView.getSubViews().indexOf(this);
        int subCount = parentView.getSubViews().size();
        int diff = difference % subCount;
        int siblingPos = pos + diff;
        if ((diff == 0) || (siblingPos < 0) || (siblingPos > (subCount - 1))) {
            return null;
        }
        siblingPos = (siblingPos + subCount) % subCount;
        return parentView.getSubViews().get(siblingPos);
    }
    
    public synchronized void add(BranchView subView) {
        subView.parent = this;
        subViews.add(subView);
    }
    
    public synchronized void add(int pos, BranchView subView) {
        subView.parent = this;
        subViews.add(pos, subView);
    }
    
    public synchronized void remove(BranchView subView) {
        subView.parent = null;
        subViews.remove(subView);
    }
    
    public synchronized Vector<BranchView> getSubViews() {
        return (Vector<BranchView>)subViews.clone();
    }
    
    public Idea getIdea() {
        return idea;
    }
    
    public void setIdea(Idea newIdea) {
        this.idea = newIdea;
        subViews.clear();
        if (newIdea != null) {
            this.getIdea().setLength(10 * newIdea.getText().length() + 20);
            int subNum = newIdea.getSubIdeas().size();
            int i = 0;
            for (Idea subIdea: newIdea.getSubIdeas()) {
                BranchView subView = new BranchView(subIdea);
                double divAngle = 2 * Math.PI / subNum;
                double mult = i * divAngle;
                double subAngle = mult - (divAngle * (subNum - 1) / 2.0);
                if (!(this instanceof CentreView)) {
                    subAngle /= 2.0;
                }
                //subView.getIdea().setAngle(subAngle);
                add(subView);
                for (IdeaLink link : subIdea.getLinks()) {
                    LinkView linkView = new LinkView(link);
                    subView.linkViews.add(linkView);
                    linkView.parent = subView;
                }
                i++;
            }
            newIdea.addIdeaListener(this);
        } else {
            if (this.idea != null) {
                this.idea.setLength(0);
            }
        }
    }
    
    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
        if (!isSelected) {
            setEditing(false);
        }
        repaint();
    }
    
    public void repaint() {
        if (parent != null) {
            parent.repaint();
        }
    }
    
    public boolean isSelected() {
        return this.selected;
    }
    
    public void setEditing(boolean isEditing) {
        this.editing = isEditing;
        repaint();
    }
    
    public boolean isEditing() {
        return this.editing;
    }
    
    public IdeaView getViewAt(Point2D p) {
        IdeaView hit = null;
        if (hits(p)) {
            hit = this;
        }
        
        for (BranchView subView: subViews) {
            IdeaView hit2 = subView.getViewAt(p);
            if (hit2 != null) {
                hit = hit2;
            }
        }
        if (hit == null) {
            for (LinkView linkView : linkViews) {
                if (linkView.hits(p)) {
                    return linkView;
                }
            }
        }
        return hit;
    }
    
    abstract boolean hits(Point2D p);
    
    void paintBranches(final Graphics g, final Point c2,
            final IdeaView aView, final double initAngle,
            final int depth, final IdeaMap map) {
        List<BranchView> views = aView.getSubViews();
        synchronized(views) {
            for (BranchView view: views) {
                view.paint(g, depth, map);
            }
        }
    }
    
    static Color invert(Color colour) {
        int red = colour.getRed();
        int green = colour.getGreen();
        int blue = colour.getBlue();
        return new Color(255 - red, 255 - green, 255 - blue);
    }
    
    Point getView(final Point c, final Point2D p) {
        int sx = c.x + (int) p.getX();
        int sy = c.y - (int) p.getY();
        return new Point(sx, sy);
    }
    
    /**
     * Search this view and it's child-views and return any that have
     * an idea matching the one given.
     *@param anIdea idea we are looking for.
     *@return view matching the idea, or null if none.
     */
    public IdeaView getViewFor(Idea anIdea) {
        if ((this.idea != null) && (this.idea.equals(anIdea))) {
            return this;
        }
        for (IdeaView subView: this.subViews) {
            IdeaView view = subView.getViewFor(anIdea);
            if (view != null) {
                return view;
            }
        }
        return null;
    }
    
    /**
     * Search this view and it's child-views and return any that have
     * an aLink matching the one given.
     *
     * @param anIdea aLink we are looking for.
     * @return view matching the aLink, or null if none.
     */
    public LinkView getLinkViewFor(IdeaLink aLink) {
        for (LinkView linkView: this.linkViews) {
            if ((linkView.getLink() != null) && (linkView.getLink().equals(aLink))) {
                return linkView;
            }
        }
        return null;
    }
    
    void paintLinks(final Graphics g) {
        for (IdeaLink link: this.getIdea().getLinks()) {
            LinkView linkView = this.getLinkViewFor(link);
            if (linkView != null) {
                linkView.paintLink(g);
            }
        }
        for (BranchView branch : this.getSubViews()) {
            branch.paintLinks(g);
        }
    }
    
    /**
     * Get the root view - that is the ancestor without an ancestor.
     */
    public IdeaView getRootView() {
        if (this instanceof CentreView) {
            return this;
        }
        IdeaView parentView = (IdeaView) this.getParent();
        return parentView.getRootView();
    }
    
    void drawString(Graphics2D graphics2d, String string, Point p,
            int alignment, double orientation, boolean editing, IdeaMap map) {
        double orient = orientation % Math.PI;
        
        if (orient > (Math.PI / 2.0)) {
            orient -= Math.PI;
        }
        if (orient < (-Math.PI / 2.0)) {
            orient += Math.PI;
        }
        
        int xc = p.x;
        int yc = p.y;
        
        AffineTransform transform = graphics2d.getTransform();
        
        transform(graphics2d, orient, xc, yc);
        
        FontMetrics fm = graphics2d.getFontMetrics();
        double realTextWidth = fm.stringWidth(string);
        double realTextHeight = fm.getHeight() - fm.getDescent();
        
        int offsetX = (int) (realTextWidth * (double) (alignment % 3) / 2.0);
        int offsetY = (int) (-realTextHeight * (double) (alignment / 3) / 2.0);
        
        if (!editing) {
            
            if ((graphics2d != null) && (string != null)) {
                graphics2d.drawString(string, xc - offsetX, yc - offsetY);
            }
        } else {
            Graphics2D g2d = (Graphics2D)graphics2d.create(xc - offsetX - 5, yc + offsetY - 5,
                    (int) realTextWidth + 10, (int) realTextHeight + 10);
            map.getTextField().paint(g2d);
            g2d.dispose();
        }
        
        graphics2d.setTransform(transform);
    }
    
    private void transform(Graphics2D graphics2d, double orientation, int x,
            int y) {
        graphics2d.transform(
                AffineTransform.getRotateInstance(
                -orientation, x, y
                )
                );
    }
    
    public void startAdjust() {
        if (parent != null) {
            parent.startAdjust();
        }
    }
    
    public MapComponent getParent() {
        return parent;
    }
    
    public void setParent(MapComponent parent) {
        this.parent = parent;
    }
    
    public Point2D getEndPoint() {
        double x = 0.0;
        double y = 0.0;
        double angle = 0.0;
        IdeaView aView = this;
        ArrayList<IdeaView> views = new ArrayList<IdeaView>();
        while(!(aView instanceof CentreView)) {
            MapComponent parent = aView.getParent();
            if (!(parent instanceof IdeaView)) {
                break;
            }
            views.add(aView);
            aView = (IdeaView) parent;
        }
        for (int i = views.size() - 1; i >= 0; i--) {
            aView = views.get(i);
            double length = aView.getIdea().getLength();
            angle += aView.getIdea().getAngle();
            if (i == (views.size() - 1)) {
                x += (Math.sin(angle) * CentreView.ROOT_RADIUS_X);
                y -= (Math.cos(angle) * CentreView.ROOT_RADIUS_Y);
            }
            x += Math.sin(angle) * length;
            y -= Math.cos(angle) * length;
        }
        return new Point2D.Double(x, y);
    }
    
    void setRealAngle(double ra) {
        this.realAngle = ra;
    }
    
    public double getRealAngle() {
        return this.realAngle;
    }
}
