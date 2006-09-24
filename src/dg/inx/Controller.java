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

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.lang.reflect.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.JTextComponent;


public class Controller {
    private AbstractModel model;
    
    public Controller(AbstractModel model) {
        this.model = model;
    }
    
    private static String getSetter(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase()
        + propertyName.substring(1);
    }
    
    private static String getGetter(String propertyName) {
        return "get" + propertyName.substring(0, 1).toUpperCase()
        + propertyName.substring(1);
    }
    
    private static String getIsser(String propertyName) {
        return "is" + propertyName.substring(0, 1).toUpperCase()
        + propertyName.substring(1);
    }
    
    private static Method getMethod(Object object, String methodName) {
        try {
            Method[] methods = object.getClass().getMethods();
            Method method = null;
            for (int i = 0; i < methods.length; i++) {
                if (methodName.equals(methods[i].getName())) {
                    method = methods[i];
                    break;
                }
            }
            if (method == null) {
                throw new RuntimeException("Can't find method " + methodName 
                        + " on " + object);
            }
            return method;
        } catch(Exception e) {
            throw new RuntimeException("Binding failed: " + e.getMessage());
        }
    }
    
    public void bind(ItemSelectable view, String modelProperty) {
        try {
            String viewProperty = "selected";
            Method viewSetter = getMethod(view, getSetter(viewProperty));
            Method modelGetter = null;
            try {
                modelGetter = getMethod(model, getGetter(modelProperty));
            } catch(Exception e) {
                modelGetter = getMethod(model, getIsser(modelProperty));
            }
            Object value = modelGetter.invoke(model, null);
            Object[] values = {value};
            viewSetter.invoke(view, values);
            bindImpl(view, viewProperty, model, modelProperty);
            bindImpl(model, modelProperty, view, viewProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    public void bind(JComboBox view, String modelProperty) {
        try {
            String viewProperty = "selectedItem";
            Method viewSetter = getMethod(view, getSetter(viewProperty));
            Method modelGetter = null;
            try {
                modelGetter = getMethod(model, getGetter(modelProperty));
            } catch(Exception e) {
                modelGetter = getMethod(model, getIsser(modelProperty));
            }
            Object value = modelGetter.invoke(model, null);
            Object[] values = {value};
            viewSetter.invoke(view, values);
            bindImpl(view, viewProperty, model, modelProperty);
            bindImpl(model, modelProperty, (ItemSelectable)view, viewProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    public void bind(JCheckBox view, String modelProperty) {
        try {
            String viewProperty = "selected";
            Method viewSetter = getMethod(view, getSetter(viewProperty));
            Method modelGetter = null;
            try {
                modelGetter = getMethod(model, getGetter(modelProperty));
            } catch(Exception e) {
                modelGetter = getMethod(model, getIsser(modelProperty));
            }
            Object value = modelGetter.invoke(model, null);
            Object[] values = {value};
            viewSetter.invoke(view, values);
            bindImpl(view, viewProperty, model, modelProperty);
            bindImpl(model, modelProperty, (ItemSelectable)view, viewProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    public void bind(JTextArea view, String modelProperty) {
        try {
            String viewProperty = "text";
            Method viewSetter = getMethod(view, getSetter(viewProperty));
            Method modelGetter = null;
            try {
                modelGetter = getMethod(model, getGetter(modelProperty));
            } catch(Exception e) {
                modelGetter = getMethod(model, getIsser(modelProperty));
            }
            Object value = modelGetter.invoke(model, null);
            Object[] values = {value};
            viewSetter.invoke(view, values);
            bindImpl(view, viewProperty, model, modelProperty);
            bindImpl(model, modelProperty, view, viewProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    public void bind(JTextField view, String modelProperty) {
        try {
            String viewProperty = "text";
            Method viewSetter = getMethod(view, getSetter(viewProperty));
            Method modelGetter = null;
            try {
                modelGetter = getMethod(model, getGetter(modelProperty));
            } catch(Exception e) {
                modelGetter = getMethod(model, getIsser(modelProperty));
            }
            Object value = modelGetter.invoke(model, null);
            Object[] values = {value};
            viewSetter.invoke(view, values);
            bindImpl(view, viewProperty, model, modelProperty);
            bindImpl(model, modelProperty, view, viewProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    public void bind(TextComponent view, String modelProperty) {
        try {
            String viewProperty = "text";
            Method viewSetter = getMethod(view, getSetter(viewProperty));
            Method modelGetter = null;
            try {
                modelGetter = getMethod(model, getGetter(modelProperty));
            } catch(Exception e) {
                modelGetter = getMethod(model, getIsser(modelProperty));
            }
            Object value = modelGetter.invoke(model, null);
            Object[] values = {value};
            viewSetter.invoke(view, values);
            bindImpl(view, viewProperty, model, modelProperty);
            bindImpl(model, modelProperty, view, viewProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    public void bind(Object target, String targetProperty, 
            String itemProperty) {
        try {
            bindImpl(target, targetProperty, model, itemProperty);
        } catch(Exception e) {
            throw new RuntimeException("Oops - " + e.getMessage());
        }
    }
    
    private void bindImpl(Object target, String targetProperty, 
            AbstractModel source, String itemProperty) {
        PropertyChangeListener pcl = (PropertyChangeListener)EventHandler.create(
                PropertyChangeListener.class, target, targetProperty, 
                "source." + itemProperty, "propertyChange");
        source.addPropertyChangeListener(pcl);
    }
    
    private void bindImpl(Object target, String targetProperty, Component source,
            String itemProperty) {
        PropertyChangeListener pcl = (PropertyChangeListener)EventHandler.create(
                PropertyChangeListener.class, target, targetProperty, 
                "source." + itemProperty, "propertyChange");
        source.addPropertyChangeListener(pcl);
    }
    
    private void bindImpl(Object target, String targetProperty, 
            ItemSelectable is, String itemProperty) {
        is.addItemListener(
                (ItemListener)EventHandler.create(
                ItemListener.class,
                target,
                targetProperty,
                "source." + itemProperty
                )
                );
    }
    
    private void bindImpl(Object target, String targetProperty, 
            ComboBoxModel model, String itemProperty) {
        model.addListDataListener(
                (ListDataListener)EventHandler.create(
                ListDataListener.class,
                target,
                targetProperty,
                "source." + itemProperty
                )
                );
    }
    private void bindImpl(Object target, String targetProperty, JTextField view,
            String itemProperty) {
        view.addFocusListener(
                (FocusListener)EventHandler.create(
                FocusListener.class,
                target,
                targetProperty,
                "source." + itemProperty
                )
                );
    }
    private void bindImpl(Object target, String targetProperty, JTextArea view,
            String itemProperty) {
        view.addFocusListener(
                (FocusListener)EventHandler.create(
                FocusListener.class,
                target,
                targetProperty,
                "source." + itemProperty
                )
                );
    }
}
