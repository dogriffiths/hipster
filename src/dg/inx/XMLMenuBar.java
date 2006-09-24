/*
 * XMLMenuBar.java
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

package dg.inx;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author davidg
 */

public final class XMLMenuBar extends JMenuBar {
    /**
     * Internationalization strings.
     */
    private ResourceBundle resBundle;
    
    private Map<String, JMenu> menus = new HashMap<String, JMenu>();
    private Map<String, JMenuItem> menuItems = new HashMap<String, JMenuItem>();
    private Controller controller;
    private Object backingBean;
//    private JMenuBar menuBar;
    
    public XMLMenuBar(Object aBean, String viewXML, ResourceBundle resBundle) {
        super();
        this.backingBean = aBean;
        controller = new Controller(null);
        this.resBundle = resBundle;
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        InputStream in = null;
        try {
            in = XMLMenuBar.class.getResourceAsStream(viewXML);
            Document document = factory.newDocumentBuilder().parse(in);
            
            Element docMenuBar = (Element)document.getElementsByTagName(
                    "menuBar").item(0);
            
            //menuBar = new JMenuBar();
            
            NodeList menuList = docMenuBar.getElementsByTagName("menu");
            for (int i = 0; i < menuList.getLength(); i++) {
                Element menuElement = (Element)menuList.item(i);
                String menuName = menuElement.getTagName();
                String menuTextName = menuElement.getAttribute("textName");
                String menuText = resBundle.getString("menu." + menuTextName);
                JMenu aMenu = new JMenu(menuText);
                menus.put(menuTextName, aMenu);
                add(aMenu);
                NodeList itemList = menuElement.getChildNodes();
                int col = 0;
                for (int j = 0; j < itemList.getLength(); j++) {
                    Node itemNode = (Node)itemList.item(j);
                    if (itemNode instanceof Element) {
                        Element itemElement = (Element)itemNode;
                        String itemName = itemElement.getTagName();
                        String itemTextName = itemElement.getAttribute("textName");
                        String action = itemElement.getAttribute("action");
                        int keyStroke = 0;
                        String key = itemElement.getAttribute("key");
                        if ((key != null) && (key.length() > 0)) {
                            Class keyEventClass = KeyEvent.class;
                            Field ks = keyEventClass.getDeclaredField("VK_" + key);
                            keyStroke = ks.getInt(new Integer(0));
                        }
                        createItem(itemTextName, aMenu, action, keyStroke);
                    }
                }
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch(IOException ioe) {
                }
            }
        }
    }
    
    public JMenuBar getMenuBar() {
        return this;
    }
    
    public void createMenus(final JMenuBar menu, Object[][] params) {
        for(Object[] p: params) {
            String menuName = (String) p[0];
            createMenu(menuName, menu, (Object[][])p[1]);
        }
    }
    
    public void createMenu(final String name, final JMenuBar menu, Object[][] params) {
        String text = resBundle.getString("menu." + name);
        JMenu aMenu = new JMenu(text);
        menus.put(name, aMenu);
        menu.add(aMenu);
        for (Object[] p: params) {
            if (p.length == 3) {
                createItem((String) p[0], aMenu, (String) p[1], (Integer) p[2]);
            } else if ("-".equals(p[0])) {
                aMenu.addSeparator();
            } else {
                createItem((String) p[0], aMenu, (String) p[1]);
            }
        }
    }
    
    public void createItem(String name, JMenu menu, String action) {
        createItem(name, menu, action, 0);
    }
    
    public void createItem(String name, JMenu menu, String action, Integer keyStroke) {
        KeyStroke ks = null;
        if (keyStroke != 0) {
            ks = KeyStroke.getKeyStroke(keyStroke,
                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        }
        createItem(name, menu, action, ks);
    }
    
    public void createItem(String name, JMenu menu, String action, KeyStroke keyStroke) {
        String text = "-";
        if (!"-".equals(name)) {
            text = resBundle.getString("menu." + name);
        } else {
            menu.addSeparator();
            return;
        }
        JMenuItem item = new JMenuItem(text);
        menu.add(item);
        if (keyStroke != null) {
            item.setAccelerator(keyStroke);
        }
        menuItems.put(name, item);
        controller.bindMethod(backingBean, action, item);
    }
    
    public JMenu getMenu(String name) {
        return menus.get(name);
    }
    
    public JMenuItem getItem(String name) {
        return menuItems.get(name);
    }
}