/*
 *  $Id: $
 *
 *  Part of INX: INterfaces in Xml.
 *  Copyright (C) 2004 David Griffiths
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
