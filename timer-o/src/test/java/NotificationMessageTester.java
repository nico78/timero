import notification.*;
import java.util.Random;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class NotificationMessageTester {
	private static void doNotify(Display display) {
		int max = NotificationType.values().length;
		Random r = new Random();
		int toUse = r.nextInt(max);

		NotifierDialog
				.notify(display, "Hi There! I'm a notification widget!",
						"Today we are creating a widget that allows us to show notifications that fade in and out!",
						NotificationType.values()[toUse]);
	}

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		Display display = new Display();
		
		Shell shell = new Shell(display);
		shell.setText("Parent shell");
		shell.setSize(200, 200);
		shell.setLayout(new FillLayout());

		shell.open();

		doNotify(display);
		while (!shell.isDisposed()) {
			// Thread.sleep(1000l);
			if (!display.readAndDispatch())
				display.sleep();
			System.out.println("notify");
			// Thread.sleep(1000l);
			// doNotify();
		}
		display.dispose();

	}

}
