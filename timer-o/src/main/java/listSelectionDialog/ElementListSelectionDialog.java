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

import listSelectionDialog.FilteredList.FilterMatcher;
import localdb.Filterable;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import antlr.DefaultFileLineFormatter;

/**
 * A class to select elements out of a list of elements.
 * 
 * @since 2.0
 */
public class ElementListSelectionDialog<T> extends
        AbstractElementListSelectionDialog {

    private T[] fElements;
	private ILabelProvider renderer;
	private NewItemCreator<T> newItemCreator;

    /**
     * Creates a list selection dialog.
     * @param parent   the parent widget.
     * @param renderer the label renderer.
     */
    public ElementListSelectionDialog(Shell parent, ILabelProvider renderer, NewItemCreator<T> newItemCreator) {
        super(parent, renderer);
		this.renderer = renderer;
		this.newItemCreator = newItemCreator;
		setShellStyle(SWT.NO_TRIM | SWT.APPLICATION_MODAL | SWT.MAX | SWT.RESIZE);
        
    }

    /**
     * Sets the elements of the list.
     * @param elements the elements of the list.
     */
    public void setElements(T[] elements) {
        fElements = elements;
    }

    /*
     * @see SelectionStatusDialog#computeResult()
     */
    protected void computeResult() {
        List<T> selection = (List<T>) Arrays.asList(getSelectedElements());
        if(selection.isEmpty())
        	setResult(Arrays.asList(new Object[]{createNewElementFor(enteredText())}));
        else
        	setResult(selection);
    }


	private Object createNewElementFor(String text){
		return newItemCreator.createItemFor(text);
	}
    /*
     * @see Dialog#createDialogArea(Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite contents = (Composite) super.createDialogArea(parent);

        createMessageArea(contents);
        createFilterText(contents);
        createFilteredList(contents);
        fFilteredList.setFilterMatcher(createFilterMatcher());
        setListElements(fElements);

        setSelection(getInitialElementSelections().toArray());

        return contents;
    }

	private FilterMatcher createFilterMatcher() {
		return new FilterMatcher(){
			private StringMatcher fMatcher;

			public void setFilter(String pattern, boolean ignoreCase,
					boolean ignoreWildCards) {
				fMatcher = new StringMatcher(pattern + '*', ignoreCase,
						ignoreWildCards);
			}

			public boolean match(Object element) {
				if(element instanceof Filterable)
					return matchAny(((Filterable)element).matchStrings());
				else
					return fMatcher.match(renderer.getText(element));
			}

			private boolean matchAny(List<String> filterStrings) {
				for(String filterString: filterStrings)
					if(fMatcher.match(filterString))
						return true;
				return false;
			}
		};
	}
}