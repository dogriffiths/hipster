/*
 * MenuManager.java
 *
 * Created on September 15, 2006, 9:33 PM
 *
 * Copyright (c) 2006, David Griffiths
 * All rights reserved.
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
 * * Neither the name of the David Griffiths nor the names of his contributors
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

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 *
 * @author davidg
 */

class MenuManager {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    private Map<String, JMenu> menus = new HashMap<String, JMenu>();
    private Map<String, JMenuItem> menuItems = new HashMap<String, JMenuItem>();
    
    MenuManager() {
        
    }
    
    void createMenus(final JMenuBar menu, Object[][] params) {
        for(Object[] p: params) {
            String menuName = (String)p[0];
            createMenu(menuName, menu, (Object[][])p[1]);
        }
    }
    
    void createMenu(final String name, final JMenuBar menu, Object[][] params) {
        String text = resBundle.getString("menu." + name);
        JMenu aMenu = new JMenu(text);
        menus.put(name, aMenu);
        menu.add(aMenu);
        for (Object[] p: params) {
            if (p.length == 2) {
                createItem((String)p[0], aMenu, (Integer)p[1]);
            } else if ("-".equals(p[0])) {
                aMenu.addSeparator();
            } else {
                createItem((String)p[0], aMenu);
            }
        }
    }
    
    void createItem(String name, JMenu menu) {
        createItem(name, menu, 0);
    }
    
    void createItem(String name, JMenu menu, Integer keyStroke) {
        String text = resBundle.getString("menu." + name);
        JMenuItem item = new JMenuItem(text);
        menu.add(item);
        if (keyStroke != 0) {
            item.setAccelerator(KeyStroke.getKeyStroke(keyStroke,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        menuItems.put(name, item);
    }
    
    public JMenu getMenu(String name) {
        return menus.get(name);
    }
    
    public JMenuItem getItem(String name) {
        return menuItems.get(name);
    }
}