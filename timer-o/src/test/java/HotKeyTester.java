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

import javax.swing.KeyStroke;

import lockedstatus.ActualSleeper;
import lockedstatus.LockRecord;
import lockedstatus.LockedStatusMonitor;
import lockedstatus.LockedStatusUpdater;
import lockedstatus.Win32LockedStatusChecker;
import time.ActualClock;

import com.tulskiy.keymaster.common.HotKey;
import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;

import event.Event;
import event.EventPublisher;


public class HotKeyTester {

	
	public static void main(String[] args) throws InterruptedException {
		final Timero delayedNotificationTester = new Timero();
		Provider provider = Provider.getCurrentProvider(false);

		KeyStroke switchTaskKeyStroke = KeyStroke.getKeyStroke("alt J");
		HotKeyListener switchTaskListener = new HotKeyListener() {
			
			@Override
			public void onHotKey(HotKey hotKey) {
				delayedNotificationTester.setFocus();
			}
		};
		provider.register(switchTaskKeyStroke, switchTaskListener);
//		
		
		KeyStroke timerKeyStrok = KeyStroke.getKeyStroke("alt K");
		HotKeyListener timerFocusListener = new HotKeyListener() {
			
			@Override
			public void onHotKey(HotKey hotKey) {
				delayedNotificationTester.timerFocus();
			}
		};
		provider.register(timerKeyStrok, timerFocusListener);
		
		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {

		    SystemTray tray = SystemTray.getSystemTray();
		    Image image = Toolkit.getDefaultToolkit().getImage("connecting.png");

		    MouseListener mouseListener = new MouseAdapter(){};

		    ActionListener exitListener = new ActionListener() {
		        public void actionPerformed(ActionEvent e) {
		            System.out.println("Exiting...");
		            System.exit(0);
		        }
		    };
		            
		    PopupMenu popup = new PopupMenu();
		    MenuItem defaultItem = new MenuItem("Exit");
		    defaultItem.addActionListener(exitListener);
		    popup.add(defaultItem);

		    trayIcon = new TrayIcon(image, "Tray Demo", popup);

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
		LockedStatusMonitor monitor = 
				new LockedStatusMonitor(
						new LockedStatusUpdater(
								new Win32LockedStatusChecker(), 
								new ActualClock(), //todo share with timer thread 
								new EventPublisher(){

									@Override
									public void publishEvent(Event<?> event) {
									
										LockRecord lockRecord = (LockRecord) event.getObject();
										System.out.println("unlock:" + lockRecord);
										delayedNotificationTester.whatHaveYouBeenDoing(lockRecord);
										
									}
									
								}),
						new ActualSleeper(500l));
		monitor.start();
		delayedNotificationTester.run();//note - inline not separate thread..
		
	}
	

}
