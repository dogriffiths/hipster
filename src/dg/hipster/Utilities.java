/*
 * Utilities.java
 *
 * Created on August 24, 2006, 11:54 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dg.hipster;


/**
 *
 * @author dgriffiths
 */
public final class Utilities {
    /** Creates a new instance of Utilities */
    private Utilities() {
    }

    /**
     * Whether two strings are equal, without throwing
     * a null pointer exception.
     * @param s0 first string we are comparing
     * @param s1 second thing we are comparing
     * @return true if they are the same, false otherwise
     */
    public static boolean stringsEqual(final String s0, final String s1) {
        if ((s0 == null) && (s1 == null)) {
            return true;
        }
        if ((s0 == null) || (s1 == null)) {
            return false;
        }
        return s0.equals(s1);
    }
}
