import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import lockedstatus.LockRecord;


import notification.NotificationType;
import notification.NotifierDialog;
import notification.cache.FontCache;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class Timero {

	private Display display;
	private Shell timerShell;
	private String activeTaskName;
	private UnlockPrompter unlockPrompter;

	/**
     * @param args
     */
    public static void main(String[] args) {
    	new Timero().run();
    }
  
	public void run(){

        display = new Display();
        unlockPrompter = new UnlockPrompter(display);
        timerShell = new Shell(display, SWT.NO_FOCUS | SWT.ON_TOP);
        timerShell.setText("Timero");
        timerShell.setSize(200, 200);
        timerShell.setLayout(new FillLayout());
//        GridLayout gl = new GridLayout(2, false);
//        gl.marginLeft = 5;
//        gl.marginTop = 0;
//        gl.marginRight = 5;
//        gl.marginBottom = 5;
//        timerShell.setLayout(gl);
        
        Rectangle clientArea = timerShell.getMonitor().getClientArea();

        int startX = clientArea.x + clientArea.width - 200;
        int startY = clientArea.y + clientArea.height - 200;
        timerShell.setLocation(startX, startY);
        
        final Text timerText = new Text(timerShell, SWT.MULTI);
        timerText.setText("starting...");
        setActiveTaskName("none");
        display.timerExec(500, new Runnable(){

			@Override
			public void run() {
				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
				String timeNow = df.format(new Date());
				
				timerText.setText(getActiveTaskName() + " -- " + timeNow);
				display.timerExec(500, this);
			}
        	
        });
        timerShell.open();
       
        
        while (!timerShell.isDisposed()) {
            if (!display.readAndDispatch()) 
            	display.sleep();
        }
        System.out.println("dispose...");
        display.dispose();
        System.exit(0);
	}
	
	public void setFocus(){
    	System.out.println("set focus..");
    	display.syncExec(new Runnable(){
    		public void run(){
    			System.out.println("setfocus run");
    			String newTaskName = promptForNewTask();
    			if(newTaskName!=null)
    				Timero.this.notify("Now selected: " + newTaskName);
    			setActiveTaskName(newTaskName);
    			timerFocus();
    		}
    	});
    }
	
	public void timerFocus(){
		System.out.println("timer focus..");
    	display.syncExec(new Runnable(){
    		public void run(){
    			System.out.println("timer setfocus run");
    			timerShell.setActive();
    		}
    	});
	}
	
	
	public  void notify(final String message) {
		display.asyncExec(new Runnable(){

			@Override
			public void run() {
				System.out.println("Notify: " + message);
				NotifierDialog.notify(display,message, message, NotificationType.INFO);
				System.out.println("Done notifying " + message);
			}
		});
	}


	private synchronized String getActiveTaskName() {
		return activeTaskName;
	}

	private synchronized void setActiveTaskName(String activeTaskName) {
		this.activeTaskName = activeTaskName;
	}

	private String promptForNewTask() {
		Object[] result = ElementListSelectionDialogTester.showSelector(timerShell);
		if(result==null)
			return null;
		return (String[]) result[0]==null?null:((String[]) result[0])[0];
	}
	
	public void whatHaveYouBeenDoing(LockRecord lockRecord){
		unlockPrompter.whatHaveYouBeenDoing(lockRecord);
	}
}
