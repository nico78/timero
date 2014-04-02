package animation;

import org.eclipse.nebula.cwt.animation.effects.AbstractEffect;
import org.eclipse.nebula.cwt.animation.movement.IMovement;
import org.eclipse.swt.widgets.Control;

public class Stretch extends AbstractEffect {

	private int startX;
	private int endX;
	private int stepX;
	private int startY;
	private int endY;
	private int stepY;
	private int startLocX;
	private int startLocY;
	private Control control;
	private boolean move;




	public Stretch(Control control, int startX, int endX, int startY,
			int endY, boolean move,long lengthMilli, IMovement movement, Runnable onStop,
			Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);

		this.startX = startX;
		this.endX = endX;
		this.move = move;
		this.startLocX=control.getLocation().x;
		this.startLocY=control.getLocation().y;
		
		stepX = endX - startX;

		this.startY = startY;
		this.endY = endY;
		stepY = endY - startY;

		easingFunction.init(0, 1, (int) lengthMilli);

		this.control = control;
	}

	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			double value = easingFunction.getValue((int) currentTime);
			if(move)
				control.setLocation(((int) (startLocX - stepX
						* value)),
						((int) (startLocY - stepY
								* value)));
			control.setSize(((int) (startX + stepX
					* value)),
					Math.max(1,(int) (startY + stepY
							* value)));
		}
	}

	public int getStartX() {
		return startX;
	}

	public int getEnd() {
		return endY;
	}

	
	

}
