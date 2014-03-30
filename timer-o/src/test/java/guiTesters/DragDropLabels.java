/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package guiTesters;

/*
 * Drag and Drop example snippet: drag text between two labels
 *
 * For a list of all SWT example snippets see
 * http://www.eclipse.org/swt/snippets/
 */
import org.eclipse.swt.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class DragDropLabels {

public static void main (String [] args) {
	
	final Display display = new Display ();
	final Shell shell = new Shell (display);
	 ImageData image = new ImageData("C:\\Users\\Nic\\Downloads\\1x-icon.png");
     
     Cursor dragCursor = new Cursor(display, image, 0, 0);
    
	shell.setLayout(new FillLayout());
	final Label label1 = new Label (shell, SWT.BORDER);
	label1.setText ("TEXT");
	final Label label2 = new Label (shell, SWT.BORDER);
	setDragDrop (shell, label1, dragCursor);
	setDragDrop (shell,label2, dragCursor);
	shell.setSize (200, 200);
	shell.open ();
	while (!shell.isDisposed ()) {
		if (!display.readAndDispatch ()) display.sleep ();
	}
	display.dispose ();
}
public static void setDragDrop (final Shell shell, final Label label, final Cursor cursor) {
	
	Transfer[] types = new Transfer[] {TextTransfer.getInstance()};
	int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
	
	final DragSource source = new DragSource (label, operations);
	source.setTransfer(types);
	source.addDragListener (new DragSourceListener () {
		private Cursor origCursor;
		@Override
		public void dragStart(DragSourceEvent event) {
			origCursor = shell.getCursor();
			System.out.println("orig cursor" + origCursor);
			shell.setCursor(cursor);
			System.out.println("new cursor" + shell.getCursor());
			event.doit = (label.getText ().length () != 0);
		}
		@Override
		public void dragSetData (DragSourceEvent event) {
			event.data = label.getText ();
		}
		@Override
		public void dragFinished(DragSourceEvent event) {
			if (event.detail == DND.DROP_MOVE)
				label.setText ("");
			shell.setCursor(origCursor);
		}
	});

	DropTarget target = new DropTarget(label, operations);
	target.setTransfer(types);
	target.addDropListener (new DropTargetAdapter() {
		@Override
		public void drop(DropTargetEvent event) {
			if (event.data == null) {
				event.detail = DND.DROP_NONE;
				return;
			}
			label.setText ((String) event.data);
		}
	});
}
}