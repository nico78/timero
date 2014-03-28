package application;

import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.effects.Grow;
import org.eclipse.nebula.cwt.animation.effects.MoveControl;
import org.eclipse.nebula.cwt.animation.effects.Shake;
import org.eclipse.nebula.cwt.animation.movement.BounceOut;
import org.eclipse.nebula.cwt.animation.movement.ElasticOut;
import org.eclipse.nebula.cwt.animation.movement.ExpoOut;
import org.eclipse.nebula.cwt.animation.movement.SinusVariation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import animation.ExpoIncOut;


public class AnimatedShellTest {

	public static void main(String[] args) {

		RealDisplayProvider displayProvider = new RealDisplayProvider();

		Display display = displayProvider.getDisplay();
		final Shell shell = new Shell(display, SWT.NO_FOCUS | SWT.NO_TRIM
				| SWT.ON_TOP | SWT.RESIZE);
		shell.setText("Parent shell");
		shell.setSize(200, 200);
		shell.setLayout(new FillLayout());

		Button button = new Button(shell, SWT.PUSH);
		button.setText("Click me");
		button.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				final AnimationRunner sr = new AnimationRunner();

				// Grow growDirect = new Grow(shell, shell.getBounds(), new
				// Rectangle(shell.getBounds().x , shell.getBounds().y,
				// shell.getBounds().width+10,
				// shell.getBounds().height+10),500l, new ExpoOut(), new
				// Runnable(){
				//
				// @Override
				// public void run() {
				// final Grow shrinkElastic = new Grow(shell, shell.getBounds(),
				// new Rectangle(shell.getBounds().x , shell.getBounds().y,
				// shell.getBounds().width-10,
				// shell.getBounds().height-10),1000l, new ElasticOut(), null,
				// null);
				// sr.runEffect(shrinkElastic);
				//
				// }},null);
				// sr.runEffect(growDirect);

				MoveControl right = new MoveControl(shell, shell.getLocation().x,
						shell.getLocation().x+100, shell.getLocation().y, shell
								.getLocation().y , 1000l, new ExpoIncOut(),
						new Runnable() {

							@Override
							public void run() {
								MoveControl down = new MoveControl(shell, shell
										.getLocation().x,
										shell.getLocation().x+100, shell
												.getLocation().y, shell
												.getLocation().y , 1000l,
										new BounceOut(), null, null);
								sr.runEffect(down);
							}

						}, null);
				
				sr.runEffect(right);
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
