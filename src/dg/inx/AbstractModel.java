/*
 *  $Id: $
 *
 *  Part of INX: INterfaces in Xml.
 *  Copyright (C) 2004 David Griffiths
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dg.inx;

import java.beans.*;

public abstract class AbstractModel {
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
}
