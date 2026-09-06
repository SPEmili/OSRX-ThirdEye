package thirdeye;

/*
 * 		From S.Emili (github.com/spemili)
 * 		---------------------------------
 * 		The GNU GPLv3 licence that OS/RX ThirdEye along with other software of mine unfortunately
 * 		doesn't allow further restrictions, including preventing you (the user) or others to feed
 * 		my questionable code into AI. As such all I can do is politely ask any users of my code to
 * 		kindly not shove my work into an LLM.
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ThirdEyeAIO extends JFrame {
	//NOTE - It's recommended to use WindowBuilder for Eclipse users.
	private static ThirdEyeMagnifier magpanel;
	private static boolean visibleUI=true;
	private static About aboot = new About();
    public ThirdEyeAIO() {
    	
    	ToolTipManager.sharedInstance().setInitialDelay(0); //fixes an ugly bug with tooltips
    	
    	magpanel = new ThirdEyeMagnifier();
        getContentPane().add(magpanel);
        magpanel.setLayout(null);
    	
        JLabel lblHowToUse = new JLabel("Scroll up and down on this window to zoom in and out.");
        lblHowToUse.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblHowToUse.setForeground(Color.white);
        
        JPanel howToUse = new JPanel();
        howToUse.setBackground(new Color(0,0,0,100));
        howToUse.add(lblHowToUse);
        magpanel.add(howToUse);
        
        JPanel btnSettings = new JPanel();
        btnSettings.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		//TODO: Settings Menu
        	}
        });
        btnSettings.setToolTipText("Configure ThirdEye's settings");
        btnSettings.setBackground(UIManager.getColor("DesktopIcon.labelBackground"));
        btnSettings.setBounds(434, 47, 30, 30);
        btnSettings.setVisible(true); //TODO: turn on when a settings window is complete
        //zoomDisplay.add(btnSettings);
        
        JPanel btnAbout = new JPanel();
        btnAbout.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		if(!aboot.isRunning) //prevent multiple instances of About
        			aboot.main(null);
        	}
        });
        btnAbout.setToolTipText("About ThirdEye and its License");
        btnAbout.setBackground(UIManager.getColor("DesktopIcon.labelBackground"));
        btnAbout.setBounds(434, 47, 30, 30);
        
        btnAbout.setVisible(true); //TODO: turn on when a settings window is complete
        magpanel.add(btnAbout);
        
    	JPanel btnToggleItems = new JPanel();
    	btnToggleItems.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			visibleUI = !visibleUI;
    			magpanel.setVisibleUI(visibleUI);
    			//btnSettings.setVisible(visibleUI);
    			btnAbout.setVisible(visibleUI);
    			howToUse.setVisible(visibleUI);
    			if(visibleUI) {
    				btnToggleItems.setBackground(new Color(0,0,0,100));
    				btnToggleItems.setForeground(new Color(255,255,255));
    			}
    			else {
    				btnToggleItems.setBackground(new Color(0,0,0,30));
    				btnToggleItems.setForeground(new Color(255,255,255,100));
    			}
    		}
    	});
    	btnToggleItems.setToolTipText("Turn off/on buttons and text");
    	btnToggleItems.setBackground(new Color(0,0,0,100));
        btnToggleItems.setBounds(434, 5, 30, 30);
        magpanel.add(btnToggleItems);
        
        JLabel lblEyeIcon = new JLabel("⏻");
        lblEyeIcon.setFont(new Font("SansSerif", Font.PLAIN, 20));
        lblEyeIcon.setForeground(Color.WHITE);
        btnToggleItems.add(lblEyeIcon);
        
        JLabel lblConfig = new JLabel("⛭");
        lblConfig.setForeground(Color.WHITE);
        lblConfig.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btnSettings.add(lblConfig);
        
        JLabel lblAbout = new JLabel("?");
        lblAbout.setForeground(Color.WHITE);
        lblAbout.setFont(new Font("SansSerif", Font.PLAIN, 20));
        btnAbout.add(lblAbout);

    	addComponentListener(new ComponentAdapter() {
    		@Override
    		public void componentResized(ComponentEvent e) {
    			btnToggleItems.setLocation(magpanel.getWidth()-btnToggleItems.getWidth()-6,6);
    			btnSettings.setLocation(magpanel.getWidth()-btnSettings.getWidth()-6,btnSettings.getHeight()*2+18);
    			btnAbout.setLocation(magpanel.getWidth()-btnSettings.getWidth()-6,btnSettings.getHeight()+12);
    			howToUse.setSize(magpanel.getWidth()-12, 22);
    			howToUse.setLocation(6, magpanel.getHeight()-28);
    		}
    	});
        setTitle("OS/RX ThirdEye 1.0");
        setSize(470, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        //fixes the event dispatch thread (DO NOT TOUCH)
        SwingUtilities.invokeLater(() -> new ThirdEyeAIO().setVisible(true));
    }
}

