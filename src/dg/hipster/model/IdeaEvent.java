/*
 * IdeaEvent.java
 *
 * Created on August 24, 2006, 11:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.model;

/**
 *
 * @author davidg
 */
public
        class IdeaEvent {
    private Object source;
    private String command;
    private Object[] paras;
    
    public IdeaEvent(Object aSource, String aCommand,
            Object[] theParas) {
        this.source = aSource;
        this.command = aCommand;
        this.paras = theParas;
    }
    
    public Object getSource() {
        return source;
    }
    
    public void setSource(Object source) {
        this.source = source;
    }
    
    public String getCommand() {
        return command;
    }
    
    public void setCommand(String command) {
        this.command = command;
    }
    
    public Object[] getParas() {
        return paras;
    }
    
    public void setParas(Object[] paras) {
        this.paras = paras;
    }
}