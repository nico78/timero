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
	
	
	public UnlockPrompter(DisplayProvider displayProvider) {
		this.displayProvider = displayProvider;
	}
	
	
	public void setTimero(Timero timero) {
		this.timero = timero;
	}

	public void handleLock(LockRecord lockRecord){
		timero.setAway();
	}
	
	public void handleUnlock(LockRecord lockRecord){
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
						showUnlockPrompt(lockRecord, display);
						
						
					}
				});
			}
		});
		;
	}

	private void showUnlockPrompt(final LockRecord lockRecord,
			final Display display) {

//		final Shell shell = new Shell(display, SWT.NO_FOCUS | SWT.NO_TRIM
//				| SWT.ON_TOP);
//		shell.setLayout(new FillLayout());
//		final Color _fgColor = ColorCache.getColor(45, 64, 93);
//		shell.setForeground(_fgColor);
//		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
//
//		final Composite inner = new Composite(shell, SWT.NONE);
//
//		GridLayout gl = new GridLayout(2, false);
//		gl.marginLeft = 5;
//		gl.marginTop = 0;
//		gl.marginRight = 5;
//		gl.marginBottom = 5;
//
//		inner.setLayout(gl);
//		shell.addListener(SWT.Resize, new Listener() {
//			@Override
//			public void handleEvent(Event e) {
//				drawBgImage(shell, display);
//			}
//		});
//
//		CLabel imgLabel = new CLabel(inner, SWT.NONE);
//		imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING
//				| GridData.HORIZONTAL_ALIGN_BEGINNING));
//		imgLabel.setImage(NotificationType.TIMERO.getImage());
//
//		CLabel title = new CLabel(inner, SWT.NONE);
//		title.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
//				| GridData.VERTICAL_ALIGN_CENTER));
//		title.setText("What have you been doing ?");
//		title.setForeground(TimerShell.INIT_FG_COLOR);
//		Font f = title.getFont();
//		FontData fd = f.getFontData()[0];
//		fd.setStyle(SWT.BOLD);
//		fd.setName("calibri");
//		fd.height = 11;
//		title.setFont(FontCache.getFont(fd));
//
//		Label subText = new Label(inner, SWT.WRAP);
//		Font tf = subText.getFont();
//		FontData tfd = tf.getFontData()[0];
//		tfd.setName("calibri");
//		tfd.setStyle(SWT.BOLD);
//		tfd.height = 8;
//		subText.setFont(FontCache.getFont(tfd));
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.horizontalSpan = 2;
//		subText.setLayoutData(gd);
//		subText.setForeground(TimerShell.INIT_FG_COLOR);
//		subText.setText("Your computer was locked from "
//				+ lockRecord.getLockTime() + " to "
//				+ lockRecord.getUnlockTime() + "\n" + "What were you doing?");
//
//		String[] proposals = new String[] { "dump", "tea", "meeting" };
//		ComboViewer comboViewer = new ComboViewer(inner, SWT.NONE);
//
//		comboViewer.setContentProvider(new ArrayContentProvider());
//		comboViewer.setLabelProvider(new LabelProvider());
//		comboViewer.setInput(proposals);
//		comboViewer.getCombo().setLayoutData(gd);
//		comboViewer.getCombo().setFocus();
//
//		shell.setSize(350, 200);
//		
//		 Rectangle monitorArea = shell.getMonitor().getClientArea();
//		 int startX = ( monitorArea.x + monitorArea.width- shell.getSize().x)/2;
//		 int startY = ( monitorArea.y + monitorArea.height- shell.getSize().y)/2;
//		 shell.setLocation(startX, startY);
//		shell.open();
		
		JobSelector taskSwitcher = new JobSelector(display.getActiveShell(), timero.getDataManager());
		
		String prompt = "Your computer was locked from \n"
		+ lockRecord.getLockTime() + " to \n"
		+ lockRecord.getUnlockTime() + "\n" + "What were you doing?";
		
		Job job = taskSwitcher.promptNewJob(prompt);
		
		
		//todo move to timero
		Task task = new Task(job, "awayFrom desk");
		timero.getDataManager().save(task);
		ActivityRecord activeActivity = timero.getActiveActivity();
		activeActivity.setTask(task);
		timero.getDataManager().save(activeActivity);
		
		timero.setBackAgain();
		
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