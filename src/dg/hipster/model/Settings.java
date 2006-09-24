/*
 * Settings.java
 *
 * Created on July 19, 2006, 8:04 AM
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

package dg.hipster.model;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;

/**
 * Bean interface for preferences. This class is a singleton accessed
 * by the getInstance() method.
 * @author davidg
 */
public final class Settings {
    private static Preferences prefs = Preferences.userNodeForPackage(
            Settings.class);
    private final static String WINDOW_TOP = "windowTop";
    private final static String WINDOW_LEFT = "windowLeft";
    private final static String WINDOW_WIDTH = "windowWidth";
    private final static String WINDOW_HEIGHT = "windowHeight";

    private static Settings settings = new Settings();

    private static Dimension screenSize = Toolkit.getDefaultToolkit(
            ).getScreenSize();
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(
            PropertyChangeListener listener
            ) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(
            PropertyChangeListener listener
            ) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener
            ) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener
            ) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void firePropertyChange(String propertyName,
            Object oldValue, Object newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    public void firePropertyChange(String propertyName,
            int oldValue, int newValue) {
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /** Creates a new instance of Settings */
    private Settings() {
    }

    /**
     * Get an instance of the settings bean.
     * @return instance of the settings bean
     */
    public static Settings getInstance() {
        return settings;
    }

    /**
     * y-coordinate of the top-left corner of the main window.
     * @return y-coordinate of the top-left corner of the main window.
     */
    public int getWindowTop() {
        int dflt = (screenSize.height - getWindowHeight()) / 8;
        return prefs.getInt(WINDOW_TOP, dflt);
    }

    /**
     * Set y-coordinate of the top-left corner of the main window.
     * @param value y-coordinate of the top-left corner of the main window.
     */
    public void setWindowTop(int value) {
        int oldValue = getWindowTop();
        prefs.putInt(WINDOW_TOP, value);
        firePropertyChange("windowTop", oldValue, value);
    }

    /**
     * x-coordinate of the top-left corner of the main window.
     * @return x-coordinate of the top-left corner of the main window.
     */
    public int getWindowLeft() {
        int dflt = (screenSize.width - getWindowWidth()) / 5;
        return prefs.getInt(WINDOW_LEFT, dflt);
    }

    /**
     * Set the x-coordinate of the top-left corner of the main window.
     * @param value x-coordinate of the top-left corner of the main window.
     */
    public void setWindowLeft(int value) {
        int oldValue = getWindowLeft();
        prefs.putInt(WINDOW_LEFT, value);
        firePropertyChange("windowLeft", oldValue, value);
    }

    /**
     * Width of the main window.
     * @return width of the main window.
     */
    public int getWindowWidth() {
        int dflt = 3 * screenSize.width / 5;
        return prefs.getInt(WINDOW_WIDTH, dflt);
    }

    /**
     * Set the width of the main window.
     * @param value width of the main window.
     */
    public void setWindowWidth(int value) {
        int oldValue = getWindowWidth();
        prefs.putInt(WINDOW_WIDTH, value);
        firePropertyChange("windowWidth", oldValue, value);
    }

    /**
     * Height of the main window.
     * @return height of the main window.
     */
    public int getWindowHeight() {
        int dflt = 3 * screenSize.height / 4;
        return prefs.getInt(WINDOW_HEIGHT, dflt);
    }

    /**
     * Set the height of the main window.
     * @param value height of the main window.
     */
    public void setWindowHeight(int value) {
        int oldValue = getWindowHeight();
        prefs.putInt(WINDOW_HEIGHT, value);
        firePropertyChange("windowHeight", oldValue, value);
    }
}
