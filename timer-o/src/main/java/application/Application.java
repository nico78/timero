package application;

import java.util.Date;

import org.eclipse.swt.widgets.Display;

import tray.WindowsTrayRunner;
import hotkey.HotKeyRegister;
import localdb.ActivityRecord;
import localdb.DataManager;
import localdb.Job;
import localdb.Task;
import lockedstatus.LockedStatusMonitor;
import core.Timero;
import core.TimeroActionAssigned;


public class Application  {

	private final HotKeyRegister hotKeyRegister ;
	private final WindowsTrayRunner trayRunner;
	private final LockedStatusMonitor lockedStatusMonitor;
	private final DisplayProvider displayProvider;
	private DataManager dataManager;
	private Timero timero;
	
	public Application(HotKeyRegister hotKeyRegister,
			WindowsTrayRunner trayRunner,
			LockedStatusMonitor lockedStatusMonitor, DisplayProvider displayProvider, DataManager dataManager) {
		this.hotKeyRegister = hotKeyRegister;
		this.trayRunner = trayRunner;
		this.lockedStatusMonitor = lockedStatusMonitor;
		this.displayProvider = displayProvider;
		this.dataManager = dataManager;
		
	}

	public static void main(String[] args) throws InterruptedException {
		Application application = new RealApplicationBuilder().buildApp();
		application.start();
	}

	private  void start() {
		timero = new Timero(displayProvider, dataManager, this);
		timero.start();
		dataManager.init();
	//	initializeData();
		hotKeyRegister.registerHotKey("alt J", new TimeroActionAssigned(timero){
			@Override
			public void doAction() {
				timero.promptJob();
			}
		});
		trayRunner.setApp(this);
		trayRunner.runInTray();
		lockedStatusMonitor.getUnlockPrompter().setTimero(timero);
		lockedStatusMonitor.start();
		
		timero.setReady(true);
	}

	private void initializeData() {
		Job job1 = new Job("221736", "SippCentre ViewModel issue", "FIG");

		Job job2 = new Job("221654", "I11 AJBELL Web trading down issue", "FIG");
		
		Job job3 = new Job("ADM","Admin","LOC");
		
		Task dev = new Task(job1, "Development");
		Task releaseNotes = new Task(job1, "Release notes");
		
		ActivityRecord actRecord = new ActivityRecord(releaseNotes, now(), now());
		actRecord.setStartTime(now());
		actRecord.setEndTime(now());

		dataManager.save(job1, job2, job3, dev, releaseNotes,actRecord);
	}

	private Date now() {
		return new Date();
	}
	
	
	
	public void quit(){
		timero.quit();
	}

}
