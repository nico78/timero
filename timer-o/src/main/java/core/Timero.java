package core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import localdb.ActivityRecord;
import localdb.DataManager;
import localdb.Job;
import localdb.Task;
import lockedstatus.LockRecord;
import notification.NotifierDialog;
import notification.cache.ImageCache;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import application.Application;
import application.DisplayProvider;

public class Timero extends Thread{

	private static final Job NULL_ACTIVE_JOB = new Job("none","none","NUL");
	private Display display;
	private TimerShell timerShell;
	private UnlockPrompter unlockPrompter;
	private TaskSwitcher taskSwitcher ;
	private DataManager dataManager;
	
	private Job activeJob;
	private DisplayProvider displayProvider;
	private boolean ready=false;
		private Task activeTask;
	private Application application;
	private ActivityRecord activeActivity;
	
	

	
	public Timero(DisplayProvider displayProvider, DataManager dataManager, Application application){
		this.displayProvider = displayProvider;
		this.dataManager = dataManager;
		this.application = application;
	}
  
	public synchronized boolean isReady() {
		return ready;
	}

	public synchronized void setReady(boolean ready) {
		this.ready = ready;
	}

	public void run(){
		display = displayProvider.getDisplay();
        unlockPrompter = new UnlockPrompter(displayProvider);
        timerShell = new TimerShell(display, "Starting...",this);
        taskSwitcher = new TaskSwitcher(timerShell.getShell(), dataManager);
             setActiveJob(NULL_ACTIVE_JOB);
   updateTimerText();
       timerShell.show();
	}

	private void updateTimerText() {
		display.timerExec(500, new Runnable(){
			@Override
			public void run() {
				if(isReady()){
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					Date now = new Date();
					String timeNow = df.format(now);
					Job activeJob = getActiveJob();
					String taskDesc;
					String timeDisplay=timeNow;
					if(activeJob==NULL_ACTIVE_JOB){
						taskDesc = "none";
						
					}
					else {
						ActivityRecord activity = getActiveActivity();
						taskDesc = activity.getTask().getTaskDescription();
						String activityStartTime = df.format(activity.getStartTime());
						timeDisplay = activityStartTime + " - " + timeNow;
						activity.setEndTime(now);
						dataManager.save(activity);
					}
					timerShell.setHeaderText(activeJob.toString());
					timerShell.setSubText(taskDesc + " \n" + timeDisplay);
					
				}
				display.timerExec(500, this);
			}
        });
	}
	
	public void promptJob(){
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
		if(this.activeJob==activeJob)
			return;
		this.activeJob = activeJob;
		if(activeJob==NULL_ACTIVE_JOB)
			return;
		
		this.activeTask = new Task(activeJob, "default");
		dataManager.save(activeTask);
		ActivityRecord activity = new ActivityRecord(activeTask);
		activity.setStartTime(new Date());
		setActiveActivity(activity);
	}

	public ActivityRecord getActiveActivity() {
		return activeActivity;
	}

	public void setActiveActivity(ActivityRecord activeActivity) {
		this.activeActivity = activeActivity;
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
