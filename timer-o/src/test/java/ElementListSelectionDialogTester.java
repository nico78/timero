import java.util.ArrayList;
import java.util.List;

import listSelectionDialog.ElementListSelectionDialog;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * This class is used to ptovide an example of showing completion feature in a
 * text field.
 * 
 * @author Debadatta Mishra(PIKU)
 * 
 */
public class ElementListSelectionDialogTester {
	public static void main(String[] args) {

		Display display = new Display();
		Shell parent = new Shell(display);
		showSelector(parent);

		while (!parent.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static Object[] showSelector(Shell shell) {
		System.out.println(" show selector..");
		ILabelProvider lp = new ArrayLabelProvider();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				shell, lp);

		dialog.setTitle("Title");

		dialog.setMessage("message");
		
		dialog.setEmptyListMessage("EMPTY LIST!");
		dialog.setEmptySelectionMessage("New Item");
		List<String[]> input = new ArrayList<String[]>();

		input.add(new String[] { "Admin", "123" });

		input.add(new String[] { "Break", "break" });

		input.add(new String[] { "Meeting", "mtg" });

		dialog.setElements(input.toArray());

		dialog.setMultipleSelection(false);
		if(dialog.open() == Window.OK)
			return dialog.getResult();
		else
			return null;

	}

	static class ArrayLabelProvider extends LabelProvider {

		public String getText(Object element) {

			return ((String[]) element)[0].toString();

		}

	}

}