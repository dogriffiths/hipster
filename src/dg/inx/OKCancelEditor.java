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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 *
 * @author  davidg
 */
public class OKCancelEditor extends JDialog {
    private boolean isCancelled = true;
    private Controller controller;
    
    public OKCancelEditor(Frame parent, String title, AbstractModel model, String viewXML) {
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
        JPanel panButtons = this.createOKCancel();
        c.insets = new Insets(0, 20, 20, 20);
        c.weighty =0.0;
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = GridBagConstraints.REMAINDER;
        gbl.setConstraints(panButtons, c);
        pan2.add(panButtons);
        this.getContentPane().add(pan2, BorderLayout.CENTER);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        centreOnScreen();
        //this.setAlwaysOnTop(true);
    }
    
    public void centreOnScreen() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        int initialWidth = 350;
        int initialHeight = 400;
        
        setBounds((screenSize.width - initialWidth) / 2,
                (screenSize.height - initialHeight) / 2,
                initialWidth, initialHeight);
    }
    
    private JPanel createOKCancel() {
        JPanel panel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        GridBagConstraints con = new GridBagConstraints();
        con.gridwidth = GridBagConstraints.RELATIVE;
        con.anchor = GridBagConstraints.EAST;
        con.insets = new Insets(0, 0, 0, 0);
        con.weightx = 10.0;
        
        JButton btnCancel = new JButton("Cancel");
        gbl.setConstraints(btnCancel, con);
        panel.add(btnCancel);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cancel();
            }
        });
        
        con.gridwidth = GridBagConstraints.REMAINDER;
        con.weightx = 0.0;
        
        JButton btnOK = new JButton("OK");
        this.getRootPane().setDefaultButton(btnOK);
        gbl.setConstraints(btnOK, con);
        panel.add(btnOK);
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                OK();
            }
        });
        return panel;
    }
    
    private void OK() {
        this.isCancelled = false;
        this.setVisible(false);
    }
    
    private void cancel() {
        this.isCancelled = true;
        this.setVisible(false);
    }
    
    public boolean isCancelled() {
        return this.isCancelled;
    }
}
