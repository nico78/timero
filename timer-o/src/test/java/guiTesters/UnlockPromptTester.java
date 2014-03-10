package guiTesters;

import java.util.Date;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import lockedstatus.LockRecord;
import application.RealDisplayProvider;
import core.UnlockPrompter;

public class UnlockPromptTester {

	
	public static void main(String[] args) {
		
		RealDisplayProvider displayProvider = new RealDisplayProvider();
		
		Display display = displayProvider.getDisplay();
		Shell parent = new Shell(display);
		parent.open();
		parent.setMinimized(true);
		new UnlockPrompter(displayProvider).whatHaveYouBeenDoing(new LockRecord(new Date(), new Date()));
		while (!parent.isDisposed()) {
		      if (!display.readAndDispatch()) {
		        display.sleep();
		      }
		    }
		display.dispose();
	}
}
