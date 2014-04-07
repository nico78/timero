package animation;

import org.eclipse.nebula.cwt.animation.movement.BounceOut;
import org.eclipse.nebula.cwt.animation.movement.ExpoOut;


/**
 * This is not an easing equation. This movement goes from f(0)=0 to f(t)=0 with
 * intermediate values between -amplitude and amplitude.
 * 
 * @author Nicolas Richeton
 * 
 */
public class ThrowAndBounce extends AbstractMovement {

	private BounceOut bounce = new BounceOut();
	double halfway;
	
	@Override
	public void init(double minValue, double maxValue, int steps) {
		super.init(minValue, maxValue, steps);
		halfway=duration/2.0d;
		bounce.init(maxValue, minValue, steps/2);
	}

	public double getValue(double step) {
		double t = step/(duration);
		double gravVal = -7.5625*Math.pow(t,2) +5.78125*t;
		double bounceVal = bounce.getValue(step-halfway);
		double val= t<0.5?gravVal:bounceVal;
		return val;
	}

}
