package thirdeye;

/*
 * 		From S.Emili (github.com/spemili)
 * 		---------------------------------
 * 		The GNU GPLv3 licence that OS/RX ThirdEye along with other software of mine unfortunately
 * 		doesn't allow further restrictions, including preventing you (the user) or others to feed
 * 		my questionable code into AI. As such all I can do is politely ask any users of my code to
 * 		kindly not shove my work into an LLM.
 */

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

// This used to be included in ThirdEyeAIO.java but it became so big and hard to follow for me that i just ended up putting it in its own file

class ThirdEyeMagnifier extends JPanel {
    private Robot rbt;
    private double zoomamnt = 3.0; //initial zoom level (3x)
    private BufferedImage capturedImage;
    private JPanel zoomAmountPanel;
    public static JLabel zoomLabel = new JLabel(); //part of a janky fix that i'm not particularly proud of
    public boolean canScrollToZoom = true;
    private boolean visibleUI = true;
    
    public void setVisibleUI(boolean b)
    {
    	visibleUI = b;
    	zoomAmountPanel.setVisible(visibleUI);
    	zoomLabel.setVisible(visibleUI);
    	if(visibleUI)
        {
        	countdown=40;
            zoomAmountPanel.setBackground(new Color(0,0,0,100));
            zoomAmountPanel.setBounds(6, 6, 80, 30);
            zoomLabel.setVisible(true);
            zoomAmountPanel.setVisible(true);
            fadeOut.start();
        }
    	else
    		fadeOut.stop();
    	countdown=40;
    }
    
    public ThirdEyeMagnifier() {
        try {
            rbt = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(getComponentPopupMenu(), e);
        }
        //scrolling code
        addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				//zoom bits
			    if(canScrollToZoom)
			    {
			    	if (e.getWheelRotation() < 0) {
			            zoomamnt = Math.min(12.0, zoomamnt + 0.5);
			        } else {
			            zoomamnt = Math.max(1.5, zoomamnt - 0.5);
			        }
			        zoomLabel.setText("Zoom: " + zoomamnt + "x");
			    }
			    
			    //opacity bits
			    if(visibleUI)
			    {
			    	zoomAmountPanel.setOpaque(true);
			        zoomAmountPanel.setBackground(new Color(0,0,0,100));
			        zoomAmountPanel.setBounds(6, 6, 80, 30);
			        zoomLabel.setVisible(true);
			        zoomLabel.setOpaque(false);
			        zoomLabel.setForeground(new Color(255,255,255));
			        zoomAmountPanel.setVisible(true);
			    }
			    
			    countdown=40;
			    fadeOut.start();
			    repaint();
			}
		});
        
        //zoom indicator
        zoomAmountPanel = new JPanel();
        zoomAmountPanel.setBackground(new Color(0,0,0,100));
        zoomAmountPanel.setBounds(6, 6, 80, 30);
        zoomLabel = new JLabel("Zoom: 3.0x");
        zoomLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        zoomLabel.setForeground(Color.WHITE);
        zoomAmountPanel.add(zoomLabel);
        add(zoomAmountPanel);
        
        //aim for 60fps aka 16ms - TODO: add toggle for 15-30-60-120-custom FPS
        Timer timer = new Timer(16, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateCapture();
			}
		});
        timer.start();
    }
    
    //stinky messy ugly code here, it works so i won't change it (yet)
    //(all of this just for the fade out effect everyone will ignore)
    Timer fadeOut = new Timer(50, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			fadeOutWhenInactive();
		}
	});
    private int countdown = 100; //1 is subtracted every 20ms - when 0 hits the fade out starts
    private void fadeOutWhenInactive()
    {
    	countdown--;
    	if(countdown <= 10 && countdown > 0 && visibleUI)
    	{
    		zoomAmountPanel.setBackground(new Color(0,0,0,(countdown*5)));
    		zoomLabel.setForeground(new Color(255,255,255,(countdown*20)));
    	} else if (countdown == 0 && visibleUI) {
    		zoomAmountPanel.setBackground(new Color(0,0,0,0));
    		zoomAmountPanel.setOpaque(false);
    		zoomAmountPanel.setVisible(false);
    		zoomLabel.setVisible(false);
    		fadeOut.stop();
    	}
    	
    	if(!visibleUI)
    	{
    		
    		zoomAmountPanel.setVisible(false);
    		zoomAmountPanel.setBackground(new Color(0,0,0,100));
    		zoomLabel.setForeground(new Color(255,255,255));
    		fadeOut.stop();
    	}
    }
    
    private void updateCapture() {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        if (pointerInfo == null || rbt == null) {return;}
        Point cursor = pointerInfo.getLocation();
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        if (panelWidth <= 0 || panelHeight <= 0) {return;}
        int captureW = (int) (panelWidth / zoomamnt), captureH = (int) (panelHeight / zoomamnt);
        int captureX = cursor.x - (captureW / 2), captureY = cursor.y - (captureH / 2);
        Rectangle captureArea = new Rectangle(captureX, captureY, captureW, captureH);
        try {capturedImage = rbt.createScreenCapture(captureArea);} catch (Exception e) {}
        repaint();
    }

    //@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (capturedImage == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR); //TODO: add toggle
        g2d.drawImage(capturedImage, 0, 0, getWidth(), getHeight(), null);
        
        //old code that might be useful in the future
        /*g2d.setColor(Color.RED);
        int cx = getWidth() / 2, cy = getHeight() / 2;
        g2d.drawLine(cx - 10, cy, cx + 10, cy);
        g2d.drawLine(cx, cy - 10, cx, cy + 10);*/ //uncomment to enable red crosshair
        
        /*g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRect(8, 8, 95, 24);
        g2d.setColor(Color.WHITE);
        g2d.drawString(String.format("Zoom: %.1fx", zoomFactor), 15, 24);*/ //uncomment for buggy zoom label (old)
    }
}