package animation;

import org.eclipse.nebula.cwt.animation.movement.IMovement;

public abstract class AbstractMovement implements IMovement {

	protected double min;
	protected double max;
	protected double duration;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.cwt.animation.movement.IMovement#getValue(double)
	 */
	public abstract double getValue(double step);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.nebula.cwt.animation.movement.IMovement#init(double, double,
	 * int)
	 */
	public void init(double minValue, double maxValue, int steps) {
		this.min = minValue;
		this.max = maxValue;
		this.duration = steps;
	}

}