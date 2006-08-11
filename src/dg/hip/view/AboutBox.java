/*
 * AboutBox.java
 *
 * Created on July 19, 2006, 7:46 AM
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

package dg.hip.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * About box describing this application.
 *
 * @author davidg
 */
public class AboutBox extends JFrame implements ActionListener {
    protected JLabel titleLabel, aboutLabel[];
    protected static int labelCount = 8;
    protected static int aboutWidth = 280;
    protected static int aboutHeight = 230;
    protected static int aboutTop = 200;
    protected static int aboutLeft = 350;
    protected Font titleFont, bodyFont;
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hip/resource/strings");
    
    public AboutBox() {
        super("");
        this.setResizable(false);
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);
        
        // Initialize useful fonts
        titleFont = new Font("Lucida Grande", Font.BOLD, 14);
        if (titleFont == null) {
            titleFont = new Font("SansSerif", Font.BOLD, 14);
        }
        bodyFont  = new Font("Lucida Grande", Font.PLAIN, 10);
        if (bodyFont == null) {
            bodyFont = new Font("SansSerif", Font.PLAIN, 10);
        }
        
        this.getContentPane().setLayout(new BorderLayout(15, 15));
        
        aboutLabel = new JLabel[labelCount];
        aboutLabel[0] = new JLabel("");
        aboutLabel[1] = new JLabel(resBundle.getString("app.name"));
        aboutLabel[1].setFont(titleFont);
        aboutLabel[2] = new JLabel(resBundle.getString("version"));
        aboutLabel[2].setFont(bodyFont);
        aboutLabel[3] = new JLabel("");
        aboutLabel[4] = new JLabel("");
        aboutLabel[5] = new JLabel("JDK " + System.getProperty("java.version"));
        aboutLabel[5].setFont(bodyFont);
        aboutLabel[6] = new JLabel(resBundle.getString("copyright"));
        aboutLabel[6].setFont(bodyFont);
        aboutLabel[7] = new JLabel("");
        
        Panel textPanel2 = new Panel(new GridLayout(labelCount, 1));
        for (int i = 0; i<labelCount; i++) {
            aboutLabel[i].setHorizontalAlignment(JLabel.CENTER);
            textPanel2.add(aboutLabel[i]);
        }
        this.getContentPane().add(textPanel2, BorderLayout.CENTER);
        this.pack();
        this.setLocation(aboutLeft, aboutTop);
        this.setSize(aboutWidth, aboutHeight);
    }
    
    class SymWindow extends java.awt.event.WindowAdapter {
        public void windowClosing(java.awt.event.WindowEvent event) {
            setVisible(false);
        }
    }
    
    public void actionPerformed(ActionEvent newEvent) {
        setVisible(false);
    }
}
