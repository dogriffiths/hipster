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

package dg.hipster.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * About box describing this application.
 *
 * @author davidg
 */
public final class AboutBox extends JFrame implements ActionListener {
    protected JLabel titleLabel, aboutLabel[];
    protected static int labelCount = 8;
    protected static int aboutWidth = 500;
    protected static int aboutHeight = 314;
    protected Font titleFont, bodyFont;
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");

    public AboutBox() {
        super(resBundle.getString("aboutbox.title"));
        this.setResizable(false);
        SymWindow aSymWindow = new SymWindow();
        this.addWindowListener(aSymWindow);

        ImagePanel backPanel = new ImagePanel(
                "/dg/hipster/resource/hipster_about.png");

        setContentPane(backPanel);

        // Initialize useful fonts
        titleFont = new Font("Lucida Grande", Font.BOLD, 14);
        if (titleFont == null) {
            titleFont = new Font("SansSerif", Font.BOLD, 14);
        }
        bodyFont  = new Font("Lucida Grande", Font.PLAIN, 10);
        if (bodyFont == null) {
            bodyFont = new Font("SansSerif", Font.PLAIN, 10);
        }

        int labCount = 0;
        aboutLabel = new JLabel[14];
        aboutLabel[labCount++] = new JLabel("");
        aboutLabel[labCount++] = new JLabel("");
        String padding = "          ";
        aboutLabel[labCount] = new JLabel(resBundle.getString("copyright")
        + padding);
        aboutLabel[labCount++].setFont(bodyFont);
        aboutLabel[labCount] = new JLabel("JDK " + System.getProperty("java.version")
        + padding);
        aboutLabel[labCount++].setFont(bodyFont);
        aboutLabel[labCount] = new JLabel(resBundle.getString("version")
        + padding);
        aboutLabel[labCount++].setFont(bodyFont);

        GridLayout layout = new GridLayout(labCount, 1);
        backPanel.setLayout(layout);
        for (int i = 0; i<labCount; i++) {
            aboutLabel[i].setHorizontalAlignment(JLabel.RIGHT);
            backPanel.add(aboutLabel[i]);
        }
        this.pack();

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice screen = ge.getDefaultScreenDevice();
        Rectangle screenRect = screen.getDefaultConfiguration().getBounds();
        int aboutLeft = (int) (screenRect.getWidth() - aboutWidth) / 2;
        int aboutTop = (int) (screenRect.getHeight() - aboutHeight) / 2;
        this.setLocation(aboutLeft, aboutTop);
        this.setSize(aboutWidth, aboutHeight);

        this.setIconImage(createIcon());
    }

    private Image createIcon() {
        String imageName = "/dg/hipster/resource/hipster_icon.png";
        java.net.URL url = getClass().getResource(imageName);
        if (url == null) {
            throw new RuntimeException("Unable to find picture " + imageName);
        }
        return Toolkit.getDefaultToolkit().getImage(url);
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

class ImagePanel extends JPanel {
    private Image background;

    ImagePanel(String imageName) {
        super();
        java.net.URL url = getClass().getResource(imageName);
        if (url == null) {
            throw new RuntimeException("Unable to find picture " + imageName);
        }
        background = Toolkit.getDefaultToolkit().getImage(url);
        loadImage(background);
    }

    private void loadImage(Image image) {
        MediaTracker mediaTracker = new MediaTracker(this);
        mediaTracker.addImage(image, 0);
        try {
            mediaTracker.waitForID(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this);
    }

    public Dimension getMinimumSize() {
        int w = background.getWidth(this);
        int h = background.getHeight(this);
        return new Dimension(w, h);
    }
}
