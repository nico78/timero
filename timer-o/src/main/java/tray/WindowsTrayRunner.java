package tray;
import img.Icons;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import application.Application;

public class WindowsTrayRunner {


	public static void main(String[] args) {
		new WindowsTrayRunner().runInTray();
	}
	
	private Application app;
	private TrayIcon trayIcon;
	private SystemTray tray;
	
	
	public void setApp(Application app) {
		this.app = app;
	}

	public void runInTray() {
		if (supportsSystemTray()) {
	
		    tray = getSystemTray();
		    Image image = Icons.getAWTImage("timero.png");
	
		    MouseListener mouseListener = new MouseAdapter(){};
			        
			PopupMenu popup = new PopupMenu();
			trayIcon = new TrayIcon(image, "Tray Demo", popup);
			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					quit();
					System.out.println("Exiting...");
					if(app!=null)
						app.quit();
				}
			};
			MenuItem exitItem = new MenuItem("Exit");
			exitItem.addActionListener(exitListener);
			popup.add(exitItem);
	
		   
	
	
		    ActionListener actionListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            trayIcon.displayMessage("Action Event", 
		                "An Action Event Has Been Performed!",
		                TrayIcon.MessageType.INFO);
		        }
		    };
		            
		    trayIcon.setImageAutoSize(true);
		    trayIcon.addActionListener(actionListener);
		    trayIcon.addMouseListener(mouseListener);
		  
		    try {
		        tray.add(trayIcon);
		  
		    } catch (AWTException e) {
		        System.err.println("TrayIcon could not be added.");
		    }
	
		} else {
	
		   throw new RuntimeException("System tray not supported");
	
		}
	}

	private SystemTray getSystemTray() {
		return SystemTray.getSystemTray();
	}

	private boolean supportsSystemTray() {
		return SystemTray.isSupported();
	}

	public void quit() {
		tray.remove(trayIcon);
	}

}