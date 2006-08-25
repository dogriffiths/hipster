/*
 * IdeaView.java
 *
 * Created on August 24, 2006, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.view;

import dg.hipster.model.Idea;
import dg.hipster.model.IdeaEvent;
import dg.hipster.model.IdeaListener;
import java.util.Vector;

/**
 *
 * @author davidg
 */
public class IdeaView implements IdeaListener {
    private double length;
    private double angle;
    private double v;
    private Vector<IdeaView> subViews = new Vector<IdeaView>();
    private Idea idea;
    
    public IdeaView(Idea anIdea) {
        this.idea = anIdea;
        anIdea.addIdeaListener(this);
    }
    
    public double getLength() {
        return length;
    }
    
    public void setLength(double length) {
        this.length = length;
    }
    
    public double getAngle() {
        return angle;
    }
    
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public void ideaChanged(IdeaEvent fe) {
        String cmd = fe.getCommand();
        if ("ADDED".equals(cmd)) {
            Idea subIdea = (Idea)fe.getParas()[0];
            addViewFor(subIdea);
//            IdeaView subView = new IdeaView(subIdea);
//            subView.setLength(100);
//            subView.setAngle(Math.PI / 4);
//            add(subView);
        } else if ("REMOVED".equals(cmd)) {
            Idea subIdea = (Idea)fe.getParas()[0];
            for (int i = 0; i < subViews.size(); i++) {
                Idea idea = (Idea)subViews.get(i).getIdea();
                if (idea.equals(subIdea)) {
                    subViews.remove(i);
                    break;
                }
            }
        }
    }
    
    public synchronized void add(IdeaView subView) {
        subViews.add(subView);
    }
    
    private synchronized void addViewFor(Idea anIdea) {
        IdeaView subView = new IdeaView(anIdea);
        subView.setLength(100);
        subView.setAngle(Math.PI / 4);
        add(subView);
        for (Idea subIdea: anIdea.getSubIdeas()) {
            subView.addViewFor(subIdea);
        }
        
    }
    
    public synchronized void remove(IdeaView subView) {
        subViews.remove(subView);
    }
    
    public synchronized Vector<IdeaView> getSubViews() {
        return (Vector<IdeaView>)subViews.clone();
    }
    
    public double getV() {
        return v;
    }
    
    public void setV(double v) {
        this.v = v;
    }
    
    public Idea getIdea() {
        return idea;
    }
}
