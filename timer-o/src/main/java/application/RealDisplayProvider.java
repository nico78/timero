package application;

import org.eclipse.swt.widgets.Display;

public class RealDisplayProvider  implements DisplayProvider {
	
	private Display display;
	@Override
	public Display getDisplay() {
		if(display==null|| display.isDisposed())
			display=new Display();
		return display;
	}

}
