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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 *
 * @author  davidg
 */
public class Editor extends JDialog {
    private Controller controller;
    
    public Editor(Frame parent, String title, AbstractModel model, String viewXML) {
        super(parent, true);
        XMLPanel panel = new XMLPanel(model, viewXML);
        if (title.startsWith("#")) {
            panel.getController().bind(this, "title", title.substring(1));
        } else {
            setTitle(title);
        }
        JPanel pan2 = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        pan2.setLayout(gbl);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(15, 20, 20, 20);
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridheight = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        gbl.setConstraints(panel, c);
        pan2.add(panel);
        this.getContentPane().add(pan2, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        centreOnScreen();
    }
    
    public void centreOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        int initialWidth = 350;
        int initialHeight = 400;
        
        setBounds((screenSize.width - initialWidth) / 2,
                (screenSize.height - initialHeight) / 2,
                initialWidth, initialHeight);
    }
}
