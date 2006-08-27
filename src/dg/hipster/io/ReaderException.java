/*
 * ReaderException.java
 *
 * Created on August 27, 2006, 11:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster.io;

/**
 *
 * @author davidg
 */
public class ReaderException extends Exception {
    private String message;
    private Throwable cause;
    
    /** Creates a new instance of ReaderException */
    public ReaderException(String aMessage, Throwable aCause) {
        this.message = aMessage;
        this.cause = aCause;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }
    
}
