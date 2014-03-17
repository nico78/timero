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

	public static final Job NULL_ACTIVE_JOB = new Job("none","none","NUL");
	public static final Task NULL_ACTIVE_TASK = new Task(NULL_ACTIVE_JOB, "no task"); 
	private static final Task UNSPECIFIED_AWAY_TASK = new Task(new Job("away","away","IDL"),"away from desk(unspecified)");
	private Display display;
	private TimerShell timerShell;
	private UnlockPrompter unlockPrompter;
	private JobSelector jobSwitcher ;
	private DataManager dataManager;
	
	private Task activeTask;
	
	
	private DisplayProvider displayProvider;
	private boolean ready=false;
	private Application application;
	private ActivityRecord activeActivity;
	private Task prevTask;
	
	
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
        timerShell = new TimerShell(display, "Starting...",this);
        jobSwitcher = new JobSelector(timerShell.getShell(), dataManager);
        setActiveTask(NULL_ACTIVE_TASK);
        updateTimerText();
       timerShell.show();
	}

	private void updateTimerText() {
		display.timerExec(500, new Runnable(){
			@Override
			public void run() {
				if(isReady()){
					SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
					Date now = now();
					String timeNow = df.format(now);
					
					Task activeTask = getActiveTask();
					
					String taskDesc = activeTask.getTaskDescription();
					String timeDisplay=timeNow;
					if(activeTask != NULL_ACTIVE_TASK) {
						ActivityRecord activity = getActiveActivity();
						String activityStartTime = df.format(activity.getStartTime());
						timeDisplay = activityStartTime + " - " + timeNow;
						activity.setEndTime(now);
						dataManager.save(activity);
					} 
					timerShell.setHeaderText(activeTask.getJob().toString());
					timerShell.setSubText(taskDesc + " \n" + timeDisplay);
					
				}
				if(!display.isDisposed())
					display.timerExec(500, this);
			}
        });
	}
	
	public void promptNewJob(){
    	display.syncExec(new Runnable(){
    		public void run(){
    			Job newJob = jobSwitcher.promptNewJob(null);
    			if(newJob!=null){
    				promptNewTask(newJob);
    			}
    			timerShell.focus();
    		}

    	});
    }

	public void promptNewTaskActiveJob(){
		display.syncExec(new Runnable(){
			public void run(){
				promptNewTask(getActiveJob());
				timerShell.focus();
			}
		});
	}
	
	private void promptNewTask(Job newJob) {
		Task newTask = new TaskSelector(timerShell.getShell(), newJob, dataManager).showSelector(null);
		timerShell.highlight();
		if(newTask!=null)
			setActiveTask(newTask);
	}


	private synchronized Job getActiveJob() {
		return getActiveTask()==null?NULL_ACTIVE_JOB:getActiveTask().getJob();
	}

//	public synchronized void setActiveJob(Job activeJob) {
//		if(getActiveJob()==activeJob)
//			return;
//		if(activeJob==NULL_ACTIVE_JOB)
//			return;
//		if(getActiveTask()==null|| getActive){
//			Task task = new Task(activeJob, "default");
//			setActiveTask(task);
//		}
//	}

	public synchronized void setActiveTask(Task task) {
		if(getActiveTask()==task)
			return;
		this.activeTask = task;
		if(task != NULL_ACTIVE_TASK) {
			dataManager.save(activeTask);
			ActivityRecord activity = new ActivityRecord(activeTask, now());
			setActiveActivity(activity);
		} 
	}
	
	public synchronized Task getActiveTask(){
		return activeTask;
	}
	
	public void setAway(){
		ActivityRecord currentActivity = getActiveActivity();
		prevTask = currentActivity==null?null:currentActivity.getTask();
		
		dataManager.save(UNSPECIFIED_AWAY_TASK);
		setActiveActivity(new ActivityRecord(UNSPECIFIED_AWAY_TASK, now()));
	}
	
	public void setBackAgain(){
		setActiveTask(prevTask);
	}

	private Date now() {
		return new Date();
	}

	public ActivityRecord getActiveActivity() {
		return activeActivity;
	}

	public void setActiveActivity(ActivityRecord activeActivity) {
		this.activeActivity = activeActivity;
	}
	
	public void whatHaveYouBeenDoing(LockRecord lockRecord){
		unlockPrompter.whatHaveYouBeenDoing(lockRecord);
	}

	public void quit() {
		setReady(false);
		timerShell.quit();
		display.dispose();
	}

	public DataManager getDataManager() {
		return dataManager;
	}
	
}
