
package guiTesters;


import org.eclipse.nebula.cwt.animation.ScrollingSmoother;
import org.eclipse.nebula.cwt.animation.movement.ElasticOut;
import org.eclipse.nebula.cwt.animation.movement.ExpoOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ScrollTest  {

    public static void main(String[] args) {

    	final Display display = new Display ();
    	final Shell shell = new Shell (display);
    	shell.setLayout(new FillLayout());
    	Composite inner = new Composite(shell, SWT.V_SCROLL);
    	
    	for (int n = 1;n<100;n++){
    		Label label = new Label(inner, SWT.NONE);
    		label.setText(n +" nnn");
    		label.setSize(new Point(50,50));
    		System.out.println(n);
    	}
    		
    	inner.setSize(new Point(800,800));
    	ScrollingSmoother s = new ScrollingSmoother(inner,new ElasticOut());
    	s.smoothControl(true);
    	
    	shell.open();
    	while (!shell.isDisposed ()) {
    		if (!display.readAndDispatch ()) display.sleep ();
    	}
    	display.dispose ();
    
    }
}