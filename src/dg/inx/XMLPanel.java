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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



/**
 *
 * @author  davidg
 */
public class XMLPanel extends JPanel {
    private Controller c;
    
    public XMLPanel(AbstractModel model, String viewXML) {
        c = new Controller(model);
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints con = new GridBagConstraints();
        
        setLayout(gbl);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        InputStream in = null;
        try {
            in = XMLPanel.class.getResourceAsStream(viewXML);
            Document document = factory.newDocumentBuilder().parse(in);
            
            // docElement should point at the <commPort> element
            Element docElement = (Element)document.getElementsByTagName(
                    "panel").item(0);
            
            // Loop through each <statstic> element within <statistics> element
            NodeList rowList = docElement.getElementsByTagName("row");
            for (int i = 0; i < rowList.getLength(); i++) {
                // aStat is a single <statistic>...</statistic> element
                Element rowElement = (Element)rowList.item(i);
                NodeList colList = rowElement.getChildNodes();
                int colCount = 0;
                for (int j = 0; j < colList.getLength(); j++) {
                    Node colNode = (Node)colList.item(j);
                    if (colNode instanceof Element) {
                        colCount++;
                    }
                }
                con.weightx = 1.0 / colCount;
                int col = 0;
                for (int j = 0; j < colList.getLength(); j++) {
                    Node colNode = (Node)colList.item(j);
                    if (colNode instanceof Element) {
                        col++;
                        if (col == colCount) {
                            con.gridwidth = GridBagConstraints.REMAINDER;
                        } else if (col == (colCount - 1)) {
                            con.gridwidth = GridBagConstraints.RELATIVE;
                        } else {
                            con.gridwidth = 1;
                        }
                        Element colElement = (Element)colNode;
                        String name = colElement.getTagName();
                        con.anchor = GridBagConstraints.CENTER;
                        con.fill = GridBagConstraints.BOTH;
                        String align = colElement.getAttribute("align");
                        if ("left".equals(align)) {
                            con.fill = GridBagConstraints.VERTICAL;
                            con.anchor = GridBagConstraints.WEST;
                        } else if ("right".equals(align)) {
                            con.fill = GridBagConstraints.VERTICAL;
                            con.anchor = GridBagConstraints.EAST;
                        }
                        String width = colElement.getAttribute("width");
                        con.weightx = 1.0 / colCount;
                        if ((width != null) && (width.length() != 0)) {
                            con.weightx = (new Integer(width)).intValue() / 100.0;
                        }
                        int mr = 0;
                        String marginRight = colElement.getAttribute("margin-right");
                        if ((marginRight != null) && (marginRight.length() != 0)) {
                            mr = (new Integer(marginRight)).intValue();
                        }
                        int ml = 0;
                        String marginLeft = colElement.getAttribute("margin-left");
                        if ((marginLeft != null) && (marginLeft.length() != 0)) {
                            ml = (new Integer(marginLeft)).intValue();
                        }
                        int mt = 0;
                        String marginTop = colElement.getAttribute("margin-top");
                        if ((marginTop != null) && (marginTop.length() != 0)) {
                            mt = (new Integer(marginTop)).intValue();
                        }
                        int mb = 0;
                        String marginBottom = colElement.getAttribute("margin-bottom");
                        if ((marginBottom != null) && (marginBottom.length() != 0)) {
                            mb = (new Integer(marginBottom)).intValue();
                        }
                        con.insets = new Insets(mt, ml, mb, mr);
                        if ("label".equals(name)) {
                            con.weighty = 0.0;
                            add(makeLabel(colElement.getAttribute("value"), gbl, con));
                        } else if ("text".equals(name)) {
                            con.weighty = 0.0;
                            add(makeText(c, colElement.getAttribute("value"), gbl, con));
                        } else if ("checkBox".equals(name)) {
                            con.weighty = 0.0;
                            add(makeCheckBox(c, colElement.getAttribute("value"), gbl, con,
                                    colElement.getAttribute("label")));
                        } else if ("textArea".equals(name)) {
                            int rows = new Integer(colElement.getAttribute("rows")).intValue();
                            int cols = new Integer(colElement.getAttribute("cols")).intValue();
                            con.weighty = 0.01 * rows * 2;
                            add(makeTextArea(c, colElement.getAttribute("value"), gbl, con, rows, cols));
                        }
                    }
                }
            }
            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch(SAXException se) {
            se.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch(IOException ioe) {
                }
            }
        }
    }
    
    public Controller getController() {
        return this.c;
    }

    private static JLabel makeLabel(String text, GridBagLayout gbl,
            GridBagConstraints con) {
        JLabel label = new JLabel(text);
        gbl.setConstraints(label, con);
        return label;
    }
    
    private static JTextField makeText(Controller c, String source,
            GridBagLayout gbl, GridBagConstraints con) {
        JTextField txt = new JTextField();
        gbl.setConstraints(txt, con);
        if (source.startsWith("#")) {
            c.bind(txt, source.substring(1));
        } else {
            txt.setText(source);
        }
        return txt;
    }
    
    private static JTextArea makeTextArea(Controller c, String source,
            GridBagLayout gbl, GridBagConstraints con, int rows, int cols) {
        JTextArea txt = new JTextArea();
        txt.setAutoscrolls(true);
        txt.setColumns(cols);
        txt.setRows(rows);
        gbl.setConstraints(txt, con);
        if (source.startsWith("#")) {
            c.bind(txt, source.substring(1));
        } else {
            txt.setText(source);
        }
        return txt;
    }
    
    private static JCheckBox makeCheckBox(Controller c, String source,
            GridBagLayout gbl, GridBagConstraints con, String label) {
        JCheckBox chk = new JCheckBox();
        chk.setLabel(label);
        gbl.setConstraints(chk, con);
        if (source.startsWith("#")) {
            c.bind(chk, source.substring(1));
        } else {
            chk.setSelected((new Boolean(source)).booleanValue());
        }
        return chk;
    }
}
