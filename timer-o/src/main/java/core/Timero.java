package core;

import static core.DateUtil.today;
import static core.DateUtil.tomorrow;

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

import editor.ActivityTableTransferViewer;

import application.Application;
import application.DisplayProvider;

public class Timero extends Thread{

	private static final int REMINDER_INTERVAL_MILLIS = 300_000;
	public static final Job NULL_ACTIVE_JOB = new Job("none","none","NUL");
	public static final Task NULL_ACTIVE_TASK = new Task(NULL_ACTIVE_JOB, "no task"); 
	private static final Task UNSPECIFIED_AWAY_TASK = new Task(new Job("away","away","IDL"),"away from desk(unspecified)");
	private Display display;
	private TimerShell timerShell;
	private UnlockPrompter unlockPrompter;
	private JobSelector jobSwitcher ;
	private DataManager dataManager;
	
	
	private DisplayProvider displayProvider;
	private boolean ready=false;
	private Application application;
	private ActivityRecord activeActivity;
	private Task prevAtDeskTask;
	private Runnable animator;
	
	
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
        animator = new Runnable(){
			@Override
			public void run() {
				timerShell.upAndDrop();
				display.timerExec(REMINDER_INTERVAL_MILLIS, this);
			}
		};
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
						System.out.println("TICK: "+ activity);
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

	private synchronized void reloadActiveActivity(){
		System.out.println("reloading active - was " + activeActivity);
		dataManager.refresh(activeActivity);
		System.out.println("reloaded active - now " + activeActivity);
	}

	public synchronized void setActiveTask(Task task) {
		System.out.println("Set active task: " + task);
		if(getActiveTask()==task)
			return;
		if(task != NULL_ACTIVE_TASK) {
			cancelReminder();
			dataManager.save(task);
			ActivityRecord activity = new ActivityRecord(task, now());
			setActiveActivity(activity);
			setPeriodicReminder();
		} 
	}
	
	private void cancelReminder(){
		display.timerExec(-1, animator);
	}
	private void setPeriodicReminder() {
		display.timerExec(REMINDER_INTERVAL_MILLIS, animator);
	}

	public synchronized Task getActiveTask(){
		return activeActivity!=null?activeActivity.getTask():NULL_ACTIVE_TASK;
	}
	
	public void setAway(){
		savePreviousAtDeskTask();
		dataManager.save(UNSPECIFIED_AWAY_TASK);
		System.out.println("setaway - active activity was " + getActiveActivity());
		setActiveActivity(new ActivityRecord(UNSPECIFIED_AWAY_TASK, now(),true));
		System.out.println("setaway - active activity now " + getActiveActivity());
	}

	public void savePreviousAtDeskTask() {
		ActivityRecord currentActivity = getActiveActivity();
		if(currentActivity!=null && !currentActivity.isAwayFromDesk())
			prevAtDeskTask = currentActivity.getTask();
	}
	
	public void setBackAgain(Task task){
		if(task!=null){
			getDataManager().save(task);
			ActivityRecord activeActivity = getActiveActivity();
			activeActivity.setTask(task);
			System.out.println("backagain - saving : " + activeActivity);
			getDataManager().save(activeActivity);
		}
		setActiveTask(prevAtDeskTask);
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
	

	public void quit() {
		setReady(false);
		timerShell.quit();
		display.dispose();
	}

	public DataManager getDataManager() {
		return dataManager;
	}
	
	public void showTable(){
		display.asyncExec(new Runnable(){

			@Override
			public void run() {
				new ActivityTableTransferViewer(Timero.this, display,dataManager, today(), tomorrow()).runIt();
				setReady(false);
				System.out.println("finished showing table");
			}});
	}

	public void reEnable() {
		reloadActiveActivity();
		setReady(true);
	}
}
