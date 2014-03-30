
package guiTesters; 

import org.eclipse.swt.*; 
import org.eclipse.swt.custom.*; 
import org.eclipse.swt.events.*; 
import org.eclipse.swt.layout.*; 
import org.eclipse.swt.widgets.*; 

public class RemoveTableItems { 
  public static void main(String[] args) { 
    Display display = new Display(); 
    final Shell shell = new Shell(display); 
    shell.setLayout(new GridLayout()); 
    final Table table = new Table(shell, SWT.BORDER | SWT.MULTI | SWT.CHECK); 
    table.setLinesVisible(true); 
    for (int i = 0; i < 3; i++) { 
      TableColumn column = new TableColumn(table, SWT.NONE); 
      column.setWidth(100); 
    } 
    for (int i = 0; i < 12; i++) { 
      new TableItem(table, SWT.NONE); 
    } 
    TableItem[] items = table.getItems(); 
    for (int i = 0; i < items.length; i++) { 
      TableItem item = items[i]; 
      TableEditor editor = new TableEditor(table); 
      CCombo combo = new CCombo(table, SWT.NONE); 
      combo.setText("Combo " + i); 
      combo.add("item 1"); 
      combo.add("item 2"); 
      editor.grabHorizontal = true; 
      editor.setEditor(combo, item, 0); 
      item.setData("ComboEditor", editor); 
      editor = new TableEditor(table); 
      Text text = new Text(table, SWT.NONE); 
      text.setText("Text " + i); 
      editor.grabHorizontal = true; 
      editor.setEditor(text, item, 1); 
      item.setData("TextEditor", editor); 
      editor = new TableEditor(table); 
      Button button = new Button(table, SWT.CHECK); 
      button.setText("" + i); 
      button.pack(); 
      editor.minimumWidth = button.getSize().x; 
      editor.horizontalAlignment = SWT.LEFT; 
      editor.setEditor(button, item, 2); 
      item.setData("ButtonEditor", editor); 
    } 
    Button button = new Button(shell, SWT.PUSH); 
    button.setText("Remove checked items"); 
    button.addSelectionListener(new SelectionAdapter() { 
            public void widgetSelected(SelectionEvent e) { 
                    TableItem[] items = table.getItems(); 
                    for (TableItem item : items) { 
                            if(item.getChecked()){   
                                    TableEditor editor = (TableEditor) item.getData("ComboEditor"); 
                                    editor.getEditor().dispose(); 
                                    editor.dispose(); 
                                    editor = (TableEditor) item.getData("TextEditor"); 
                                    editor.getEditor().dispose(); 
                                    editor.dispose(); 
                                    editor = (TableEditor) item.getData("ButtonEditor"); 
                                    editor.getEditor().dispose(); 
                                    editor.dispose(); 
                                    table.remove(table.indexOf(item)); 
                                shell.pack(); 
                            } 
                    }         

            } 
        }); 
    shell.pack(); 
    shell.open(); 
    while (!shell.isDisposed()) { 
      if (!display.readAndDispatch()) 
        display.sleep(); 
    } 
    display.dispose(); 
  } 
} 

