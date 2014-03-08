package listSelectionDialog;
/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * A class to select elements out of a list of elements.
 * 
 * @since 2.0
 */
public class ElementListSelectionDialog extends
        AbstractElementListSelectionDialog {

    private Object[] fElements;

    /**
     * Creates a list selection dialog.
     * @param parent   the parent widget.
     * @param renderer the label renderer.
     */
    public ElementListSelectionDialog(Shell parent, ILabelProvider renderer) {
        super(parent, renderer);
    }

    /**
     * Sets the elements of the list.
     * @param elements the elements of the list.
     */
    public void setElements(Object[] elements) {
        fElements = elements;
    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    protected void computeResult() {
        List<Object> selection = Arrays.asList(getSelectedElements());
        if(selection.isEmpty())
        	setResult(Arrays.asList(new Object[]{new String[]{enteredText()}}));
        else
        	setResult(selection);
    }

    /*
     * @see Dialog#createDialogArea(Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite contents = (Composite) super.createDialogArea(parent);

        createMessageArea(contents);
        createFilterText(contents);
        createFilteredList(contents);

        setListElements(fElements);

        setSelection(getInitialElementSelections().toArray());

        return contents;
    }
}