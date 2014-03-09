package core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import localdb.DataManager;
import localdb.Job;
import localdb.Task;
import lockedstatus.LockRecord;
import notification.NotifierDialog;
import notification.cache.ImageCache;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import application.DisplayProvider;

public class Timero extends Thread{

	private Display display;
	private TimerShell timerShell;
	private UnlockPrompter unlockPrompter;
	private TaskSwitcher taskSwitcher ;
	private DataManager dataManager;
	
	private Job activeJob;
	private DisplayProvider displayProvider;
	private boolean ready=false;
	
	
	public Timero(DisplayProvider displayProvider, DataManager dataManager){
		this.displayProvider = displayProvider;
		this.dataManager = dataManager;
	}
  
	public synchronized boolean isReady() {
		return ready;
	}

	public synchronized void setReady(boolean ready) {
		this.ready = ready;
	}

	public void run(){
		display = displayProvider.createDisplay();
        unlockPrompter = new UnlockPrompter(display);
        timerShell = new TimerShell(display, "Starting...");
        taskSwitcher = new TaskSwitcher(timerShell.getShell(), dataManager);
        setActiveJob(new Job("none","none","NUL"));
        updateTimerText();
       timerShell.show();
	}

	private void updateTimerText() {
		display.timerExec(500, new Runnable(){
			@Override
			public void run() {
				if(isReady()){
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					String timeNow = df.format(new Date());
					Job activeJob = getActiveJob();
					timerShell.setHeaderText(activeJob.toString());
					timerShell.setSubText(timeNow);
				}
				display.timerExec(500, this);
			}
        });
	}
	
	public void setFocus(){
    	display.syncExec(new Runnable(){
    		public void run(){
    			Job newJob = promptForNewJob();
    			if(newJob!=null){
    				timerShell.highlight();
    				setActiveJob(newJob);
    			}
    			timerShell.focus();
    		}
    	});
    }
	

	private synchronized Job getActiveJob() {
		return activeJob;
	}

	private synchronized void setActiveJob(Job activeJob) {
		this.activeJob = activeJob;
	}

	private Job promptForNewJob() {
		return taskSwitcher.showSelector();
	}
	
	public void whatHaveYouBeenDoing(LockRecord lockRecord){
		unlockPrompter.whatHaveYouBeenDoing(lockRecord);
	}

	public void quit() {
		timerShell.quit();
	}
}
