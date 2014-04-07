package application;

import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.effects.Grow;
import org.eclipse.nebula.cwt.animation.effects.MoveControl;
import org.eclipse.nebula.cwt.animation.effects.Resize;
import org.eclipse.nebula.cwt.animation.effects.Shake;
import org.eclipse.nebula.cwt.animation.movement.BounceOut;
import org.eclipse.nebula.cwt.animation.movement.ElasticOut;
import org.eclipse.nebula.cwt.animation.movement.ExpoOut;
import org.eclipse.nebula.cwt.animation.movement.SinusVariation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import animation.ExpoIncOut;
import animation.Jump;
import animation.ThrowAndBounce;
import animation.Stretch;


public class AnimatedShellTest {

	public static void main(String[] args) {

		RealDisplayProvider displayProvider = new RealDisplayProvider();

		Display display = displayProvider.getDisplay();
		final Shell shell = new Shell(display, SWT.NO_FOCUS | SWT.NO_TRIM
				| SWT.ON_TOP | SWT.RESIZE);
		shell.setText("Parent shell");
		shell.setSize(200, 200);
		shell.setLayout(new FillLayout());
		Rectangle monitorArea = shell.getMonitor().getClientArea();
		int startX = monitorArea.x + monitorArea.width
				- shell.getSize().x - 200;
		int startY = monitorArea.y + monitorArea.height
				- shell.getSize().y - 200;
		shell.setLocation(startX, startY);

		
		
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Click me");
		button.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				
				final Region origRegion = shell.getRegion();
				final int initialXLoc=shell.getLocation().x;
				final int initialYLoc=shell.getLocation().y;
				final AnimationRunner sr = new AnimationRunner();
				MoveControl jump = new MoveControl(shell, initialXLoc, initialXLoc, initialYLoc, initialYLoc - 100, 3000l, new ThrowAndBounce(), null, null);
				
				sr.runEffect(jump);
				
				
			}
		});

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}
	
}
