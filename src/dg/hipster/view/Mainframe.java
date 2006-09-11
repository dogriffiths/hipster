/*
 * Mainframe.java
 *
 * Created on July 19, 2006, 7:41 AM
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

import dg.hipster.model.Idea;
import dg.hipster.model.Settings;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Main window of the application.
 *
 * @author davidg
 */
public class Mainframe extends JFrame {
    /**
     * Internationalization strings.
     */
    protected static ResourceBundle resBundle = ResourceBundle.getBundle(
            "dg/hipster/resource/strings");
    
    /**
     * Main idea processor component.
     */
    private IdeaMap ideaMap;
    
    /** Creates a new instance of Mainframe */
    public Mainframe() {
        setTitle(resBundle.getString("app.name"));
        
        Settings s = Settings.getInstance();
        setBounds(s.getWindowLeft(), s.getWindowTop(),
                s.getWindowWidth(), s.getWindowHeight());
        buildView();
        buildModel();
        text.setText(ideaMap.getRootView().getIdea().getText());
        text.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ideaMap.requestFocusInWindow();
                IdeaView current = ideaMap.getSelectedView();
                if (current != null) {
                    current.getIdea().setText(Mainframe.text.getText());
                    current.setEditing(false);
                    Mainframe.text.setEnabled(false);
                }
            }
        });
        this.ideaMap.requestFocusInWindow();
        ideaMap.getRootView().setEditing(true);
    }
    
    /**
     * Lay the window out.
     */
    private void buildView() {
        text = new JTextField("text");
        this.getContentPane().add(text, BorderLayout.NORTH);
        ideaMap = new IdeaMap();
        this.getContentPane().add(ideaMap, BorderLayout.CENTER);
//        JPanel rot = new Rotater();
////        add(rot, BorderLayout.CENTER);
//        setLayout(null);
//        add(rot);
//
//        Dimension dim = rot.getMinimumSize();
//        rot.setBounds(100, 100, 200, 100);
    }
    public static JTextField text;
    
    
    /**
     * Set up the data.
     */
    private void buildModel() {
//        ReaderFactory factory = ReaderFactory.getInstance();
//        try {
//            IdeaReader reader = factory.read(new File("etc/test.opml"));
//            ideaMap.setIdea(reader.getIdea());
//        } catch(ReaderException re) {
//            re.printStackTrace();
//        }
        
        Idea idea = new Idea("Persistence");
        ideaMap.setIdea(idea);
        Idea mistakes = new Idea("Mistakes");
        Idea platforms = new Idea("Platforms");
        mistakes.add(platforms);
        Idea attempts = new Idea("Attempts");
        platforms.add(attempts);
        Idea continual = new Idea("Continual");
        attempts.add(continual);
        Idea further = new Idea("Further");
        attempts.add(further);
        Idea enjoyed = new Idea("Enjoyed");
        attempts.add(enjoyed);
        Idea thousands = new Idea("Thousands");
        mistakes.add(thousands);
        Idea making = new Idea("Making");
        mistakes.add(making);
        Idea progress = new Idea("Progress");
        mistakes.add(progress);
        Idea learning = new Idea("Learning");
        idea.add(learning);
        Idea love = new Idea("Love");
        learning.add(love);
        love.add(mistakes);
        idea.add(mistakes);
        
        
        
        
//        final int lines = 35;
//        final Idea idea = new Idea("Test pattern");
//        ideaMap.setIdea(idea);
//        (new Thread(){public void run() {
//            for (int i = 0; i < lines; i++) {
//                Idea fred2 = new Idea("      i = " + i);
//                synchronized(idea) {
//                    idea.add(fred2);
//                }
//                //try { Thread.sleep(100);} catch(Exception e){}
//            }
//
//            Idea sub = idea.getSubIdeas().get(0);
//
//            Idea subIdea0 = null;
//            for (int i = 0; i < 4; i++) {
//                subIdea0 = new Idea("i = " + i);
//                sub.add(subIdea0);
//                try { Thread.sleep(1000);} catch(Exception e){}
//            }
//            try { Thread.sleep(10000);} catch(Exception e){}
//
//            Idea s2 = subIdea0;
//            for (int i = 0; i < 6; i++) {
//                Idea subIdea2 = new Idea("i = " + i);
//                s2.add(subIdea2);
//                try { Thread.sleep(1000);} catch(Exception e){}
//            }
//        }}).start();
    }
}
class Rotater extends JPanel implements ActionListener {
    private Timer ticker = new Timer(50, this);
    private double orientation = -Math.PI / 6;
    private Point p = new Point(0, 0);
    private JTextField text = new JTextField("this is a text field");
    private JPanel privPanel;
    public Rotater() {
        text.setHighlighter(null);
        privPanel = new JPanel();
        privPanel.add(text);
        ticker.start();
    }
    public void actionPerformed(ActionEvent evt) {
        repaint();
    }
    public void setOrientation(double orient) {
        this.orientation = orient;
    }
    public double getOrientation() {
        return this.orientation;
    }
    public Dimension getMinimumSize() {
        Dimension dim = text.getSize();
        double orient = orientation;
        double w = dim.width * Math.cos(orient) + dim.height * Math.sin(orient);
        double h = dim.width * Math.sin(orient) + dim.height * Math.cos(orient);
        return new Dimension((int)w, (int)h);
    }
    public void update(Graphics g) {
        paint(g);
    }
    public void paint(Graphics g) {
        Dimension size = getSize();
        int w = size.width;
        int h = size.height;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(w, h);
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        double orient = orientation % Math.PI;
        
        if (orient > (Math.PI / 2.0)) {
            orient -= Math.PI;
        }
        if (orient < (-Math.PI / 2.0)) {
            orient += Math.PI;
        }
        
        int xc = p.x;
        int yc = p.y;
        
        AffineTransform transform = g2.getTransform();
        
        transform(g2, orient, xc, yc);
        
        
        
        
        privPanel.paintComponents(g2);
        
        g2.setTransform(transform);
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(image, 0, 0, this);
    }
    
    private void transform(Graphics2D graphics2d, double orientation, int x,
            int y) {
        graphics2d.transform(
                AffineTransform.getRotateInstance(
                -orientation, x, y
                )
                );
    }
    
    public void paintComponent(Graphics g) {
    }
    
    public void paintComponents(Graphics g) {
    }
}
