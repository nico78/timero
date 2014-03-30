package guiTesters;

/*
 * Cursor example snippet: create a color cursor from an image file
 *
 * For a list of all SWT example snippets see
 * http://dev.eclipse.org/viewcvs/index.cgi/%7Echeckout%7E/platform-swt-home/dev.html#snippets
 */
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Snippet83 {

  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setSize(150, 150);
    final Cursor[] cursor = new Cursor[1];
    Button button = new Button(shell, SWT.PUSH);
    button.setText("Change cursor");
    Point size = button.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    button.setSize(size);
    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event e) {
        FileDialog dialog = new FileDialog(shell);
        dialog.setFilterExtensions(new String[] { "*.ico", "*.gif",
            "*.png" });
        String name = dialog.open();
        if (name == null)
          return;
        ImageData image = new ImageData(name);
        Cursor oldCursor = cursor[0];
        cursor[0] = new Cursor(display, image, 0, 0);
        //shell.setCursor(cursor[0]);
        if (oldCursor != null)
          oldCursor.dispose();
      }
    });
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    if (cursor[0] != null)
      cursor[0].dispose();
    display.dispose();
  }
}