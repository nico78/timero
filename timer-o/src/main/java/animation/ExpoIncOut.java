package animation;
public class ExpoIncOut extends AbstractMovement {

	private float increment;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sharemedia.gui.viewers.impl.gl.IMovement#getValue(int)
	 */
	public double getValue(double step) {
		float currentCos = 1.0f - (float) Math.exp(((float) step) * increment);
		if (step != duration)
			return min + max * currentCos;
		else
			return max;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sharemedia.gui.viewers.impl.gl.IMovement#init(float, float, int)
	 */
	public void init(double min, double max, int steps) {
		increment = -10.0f / steps;
		super.init(min, max, steps);
	}

}
