package core;
import java.util.ArrayList;
import java.util.List;

import listSelectionDialog.ElementListSelectionDialog;
import listSelectionDialog.NewItemCreator;
import localdb.DataManager;
import localdb.Job;

import notification.NotificationType;
import notification.cache.ColorCache;
import notification.cache.FontCache;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class TaskSwitcher {
	private Shell parent;
	private DataManager dataManager;
	private NewItemCreator<Job> newJobCreator;

	public TaskSwitcher(Shell parent, DataManager dataManager) {
		this.parent = parent;
		this.dataManager = dataManager;
		this.newJobCreator = new NewJobCreator(dataManager);
	}

	public  Job showSelector() {
		System.out.println(" show selector..");
		ILabelProvider lp = new ArrayLabelProvider();
		ElementListSelectionDialog<Job> dialog = new ElementListSelectionDialog<Job>(
				parent, lp,newJobCreator){
			Image currentBackgroundImage;

					@Override
					protected void configureShell(Shell shell) {
						super.configureShell(shell);
						decorate(shell);
						try {
							
						    		// get the size of the drawing area
									Rectangle rect = shell.getClientArea();
									
									// create a new image with that size
									Image newImage1 = new Image(shell.getDisplay(), Math.max(1, rect.width), rect.height);
									// create a GC object we can use to draw with
									GC gc = new GC(newImage1);
									
									// fill background
									gc.setForeground(TimerShell.INIT_BG_FG_GRADIENT);
									gc.setBackground(TimerShell.INIT_BG_BG_GRADIENT);
									gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
									
									// draw shell edge
									gc.setLineWidth(2);
									gc.setForeground(TimerShell.INIT_FG_COLOR);
									gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
									// remember to dipose the GC object!
									gc.dispose();
									
									// now set the background image on the shell
									shell.setBackgroundImage(newImage1);
									Image newImage = newImage1;
						    		
						    		// remember/dispose old used iamge
						    		if (currentBackgroundImage != null) {
						    			currentBackgroundImage.dispose();
						    		}
						    		currentBackgroundImage = newImage;
						    	} catch (Exception err) {
						    		err.printStackTrace();
						    	}
					}
			
		};

		dialog.setTitle("Title");

		dialog.setMessage("choose a job");
		
		dialog.setEmptyListMessage("EMPTY LIST!");
		dialog.setEmptySelectionMessage("New Item");
		List<Job> allJobs = dataManager.getAllJobs();
		dialog.setElements(allJobs.toArray(new Job[0]));
		
		dialog.setMultipleSelection(false);
		if(dialog.open() == Window.OK)
			return (Job)dialog.getResult()[0];
		else
			return null;

	}

private void decorate(Shell shell){
	
    shell.setForeground(ColorCache.getColor(45, 64, 93));
    shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
    

    final Composite inner = new Composite(shell, SWT.NONE);

    GridLayout gl = new GridLayout(2, false);
    gl.marginLeft = 5;
    gl.marginTop = 0;
    gl.marginRight = 5;
    gl.marginBottom = 5;

    inner.setLayout(gl);
  
}
	
	
	static class ArrayLabelProvider extends LabelProvider {

		public String getText(Object element) {
			Job job = (Job) element;
			return job.getReference() + "--" + job.getDescription().toString();
		}
	}
}