package core;

import localdb.ActivityRecord;
import localdb.Job;
import localdb.Task;
import lockedstatus.LockRecord;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import application.DisplayProvider;

public class UnlockPrompter {

	private DisplayProvider displayProvider;
	private Image currentBackgroundImage;
	private Timero timero;
	private boolean currentlyPromptingJob=false;
	private JobSelector awayJobSelector;
	private TaskSelector awayTaskSelector;
	
	public UnlockPrompter(DisplayProvider displayProvider) {
		this.displayProvider = displayProvider;
	}
	
	
	public void setTimero(Timero timero) {
		this.timero = timero;
	}

	public void handleLock(LockRecord lockRecord){
		System.out.println("Lock " + lockRecord);
		if(currentlyPromptingJob){
			System.out.println("Already prompting job- cancel active one");
			awayJobSelector.cancelActiveDialog();
		}
		if(awayTaskSelector!=null){
			System.out.println("Already prompting task - cancel active one");
			awayTaskSelector.cancelActiveDialog();
			//TODO synchronize on something rather than doing this
			while(awayTaskSelector!=null){
				try {
					Thread.sleep(100l);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			
		timero.setAway();
	}
	
	public void handleUnlock(LockRecord lockRecord){
		System.out.println("Unlock " + lockRecord);
		whatHaveYouBeenDoing(lockRecord);
	}
	
	
	public void whatHaveYouBeenDoing(final LockRecord lockRecord) {
		final Display display = displayProvider.getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				display.timerExec(300, new Runnable() {
					@Override
					public void run() {
						showUnlockPrompt(lockRecord);
					}
				});
			}
		});
//		System.out.println("Requested unlock prompt");
//		display.asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				display.timerExec(3000, new Runnable() {
//					@Override
//					public void run() {
//						taskSwitcher.cancelActiveDialog();
//					}
//				});
//			}
//		});
//		System.out.println("Requested cancel prompt");
	}

	private void showUnlockPrompt(final LockRecord lockRecord) {
		
		
		String prompt = "Your computer was locked from \n"
		+ lockRecord.getLockTime() + " to \n"
		+ lockRecord.getUnlockTime() + "\n" + "What were you doing?";
		
		Task awayTask=null;
		currentlyPromptingJob = true;
		Job awayJob = getAwayJobSelector().promptNewJob(prompt);
		System.out.println("Got away job from prompt " + awayJob);
		currentlyPromptingJob=false;

		if(awayJob!=null){
			awayTask =getAwayTaskSelector(awayJob).showSelector(null);
			System.out.println("Got away task from prompt " + awayTask);
			awayTaskSelector=null;
			
			if(awayTask==null)
				awayTask = new Task(awayJob, "awayFrom desk");
		}
		System.out.println("Setting back again - away task was "+ awayTask);
		timero.setBackAgain(awayTask);
		awayTaskSelector = null;
		
	}
	
	private TaskSelector getAwayTaskSelector(Job awayJob){
		awayTaskSelector = new TaskSelector(displayProvider.getDisplay().getActiveShell(), awayJob, timero.getDataManager());
		
		return awayTaskSelector;
	}


	private JobSelector getAwayJobSelector() {
		if(awayJobSelector==null)
		awayJobSelector = new JobSelector(displayProvider.getDisplay().getActiveShell(), timero.getDataManager());
		return awayJobSelector;
	}

	private void drawBgImage(Shell shell, Display display) {
		try {
			// get the size of the drawing area
			Rectangle rect = shell.getClientArea();
			// create a new image with that size
			Image newImage1 = new Image(display, Math.max(1, rect.width),
					rect.height);
			// create a GC object we can use to draw with
			GC gc = new GC(newImage1);
			// title foreground color
			// fill background
			gc.setForeground(TimerShell.INIT_BG_FG_GRADIENT);
			gc.setBackground(TimerShell.INIT_BG_BG_GRADIENT);
			gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height,
					true);

			// draw shell edge
			gc.setLineWidth(2);
			gc.setForeground(TimerShell.INIT_BORDER_COLOR);
			gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2,
					rect.height - 2);
			// remember to dipose the GC object!
			gc.dispose();

			// now set the background image on the shell
			shell.setBackgroundImage(newImage1);
			Image newImage = newImage1;

			// remember/dispose old used iamge
			if (currentBackgroundImage != null) {
				currentBackgroundImage.dispose();
			}
			currentBackgroundImage = newImage;
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

}