package core;
import lockedstatus.LockRecord;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UnlockPrompter {

	private Display display;
	

	public UnlockPrompter(Display display) {
		this.display = display;
	}


	public void whatHaveYouBeenDoing(final LockRecord lockRecord) {
		display.asyncExec(new Runnable(){
			@Override
			public void run() {
				display.timerExec(1000,new Runnable(){
					@Override
					public void run() {
						Shell unlockDialog = new Shell(display, SWT.SYSTEM_MODAL | SWT.ON_TOP );
						unlockDialog.setSize(200, 200);
						unlockDialog.setLayout(new FillLayout());
						unlockDialog.setText("What have you been doing ?");
						Text prompt = new Text(unlockDialog,SWT.MULTI);
						prompt.setText("Your computer was locked from " + lockRecord.getLockTime() + " to " + lockRecord.getUnlockTime() + 
								"\n" + "What were you doing?");
						String[] proposals = new String[]{"dump","tea","meeting"};
						ComboViewer comboViewer = new ComboViewer(unlockDialog, SWT.NONE);
						comboViewer.setContentProvider(new ArrayContentProvider());
						comboViewer.setLabelProvider(new LabelProvider());
						comboViewer.setInput(proposals);
						unlockDialog.open();
						comboViewer.getCombo().setFocus();
					}
		    	});
			}
		});
		;
	}

}