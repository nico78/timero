package animation;

import org.eclipse.nebula.cwt.animation.effects.AbstractEffect;
import org.eclipse.nebula.cwt.animation.movement.IMovement;
import org.eclipse.swt.widgets.Control;

public class Jump extends AbstractEffect {

	private int startSizeY;
	private int endY;
	private int stepY;
	private int startLocY;
	private Control control;
	private boolean move;
	private int endSizeY;




	public Jump(Control control,  int stepY, int endSizeY,
			long lengthMilli, IMovement movement, Runnable onStop,
			Runnable onCancel) {
		super(lengthMilli, movement, onStop, onCancel);

		this.startLocY=control.getLocation().y;
		

		this.startSizeY = control.getSize().y;
		this.endSizeY = endSizeY;
		
		this.stepY = stepY;

		easingFunction.init(0, 1, (int) lengthMilli);

		this.control = control;
	}

	public void applyEffect(final long currentTime) {
		if (!control.isDisposed()) {
			double value = easingFunction.getValue((int) currentTime);
			int newY = (int) (startSizeY + stepY
					* value);
			if(control.getSize().y<endSizeY){
					
			control.setSize(control.getSize().x,
					newY);

			}
			
				control.setLocation(control.getLocation().x,
						((int) (startLocY - stepY
								* value)));
			
			
		}
	}

	
	

}
