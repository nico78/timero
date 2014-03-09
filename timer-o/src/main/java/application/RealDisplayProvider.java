package application;

import org.eclipse.swt.widgets.Display;

public class RealDisplayProvider  implements DisplayProvider {

	@Override
	public Display createDisplay() {
		return new Display();
	}

}
