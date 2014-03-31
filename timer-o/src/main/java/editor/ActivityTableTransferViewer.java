package editor;

import static core.DateUtil.today;
import static core.DateUtil.tomorrow;
import static java.util.Arrays.asList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import localdb.ActivityRecord;
import localdb.DataManager;
import localdb.HibernateDataManager;
import localdb.Job;
import localdb.Task;
import notification.NotificationType;
import notification.cache.ColorCache;
import notification.cache.FontCache;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import time.TimeFormatter;
import core.TimerShell;
import core.TimerShell.Layouts;
import core.Timero;
import editor.ActivityTimeTransfer.ActivityTimeTransferType;

public class ActivityTableTransferViewer {
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat(
			"EEE dd MMM yyyy");
	private static final TimeFormatter TIME_FORMATTER = new TimeFormatter();
	
	private DataManager dataManager;
	private Table table;
	private Display display;
	private Date fromTime;
	private Date toTime;
	private Shell shell;
	private Image currentBackgroundImage;
	private Timero timero;
	private Color bgBgGradient = TimerShell.INIT_BG_BG_GRADIENT;
	private Color fgColor = TimerShell.INIT_FG_COLOR;
	private Color bgFgGradient = TimerShell.INIT_BG_FG_GRADIENT;
	private Color borderColor = TimerShell.INIT_BORDER_COLOR;
	private boolean mouseDown = false;
	protected int xPos;
	protected int yPos;
	
	public ActivityTableTransferViewer(Timero timero, Display display,
			DataManager dataManager, Date fromTime, Date toTime) {
		this.timero = timero;
		this.display = display;
		this.dataManager = dataManager;
		this.fromTime = fromTime;
		this.toTime = toTime;
	}

	public static void main(String[] args) throws ParseException {

		DataManager dataManager = HibernateDataManager.create();
		ActivityTableTransferViewer activityTableTransferViewer = new ActivityTableTransferViewer(null,
				new Display(), dataManager, today(), tomorrow());
		activityTableTransferViewer.runIt();
		activityTableTransferViewer.keepOpen();
		dataManager.close();
	}
	
	private void drawBgImage() {
		try {
			// get the size of the drawing area
			Rectangle rect = shell.getClientArea();
			// create a new image with that size
			Image newImage1 = new Image(display, Math.max(1, rect.width),
					rect.height);
			// create a GC object we can use to draw with
			GC gc = new GC(newImage1);

			// fill background
			gc.setForeground(getBgFgGradient());
			gc.setBackground(getBgBgGradient());
			gc.fillGradientRectangle(rect.x, rect.y, rect.width,
					rect.height, true);

			// draw shell edge
			gc.setLineWidth(2);
			gc.setForeground(getBorderColor());
			gc.drawRectangle(rect.x + 1, rect.y + 1, rect.width - 2,
					rect.height - 2);
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
	public void runIt() {
		shell = new Shell(display, SWT.NO_TRIM|SWT.RESIZE|SWT.ON_TOP);
		shell.setLayout(new FillLayout());
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
		shell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event e) {
				drawBgImage();
			}
		});
		GridLayout paddedGridLaout = Layouts.PADDING;
		final Composite inner = new Composite(shell, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		gl.marginLeft = 5;
		gl.marginTop = 5;
		gl.marginRight = 5;
		gl.marginBottom = 5;
		
		inner.setLayout(gl);
		inner.setBackgroundMode(SWT.INHERIT_DEFAULT);
		inner.addMouseTrackListener(new MouseTrackListener() {

			@Override
			public void mouseHover(MouseEvent e) {
				shell.setCursor(new Cursor(display, SWT.CURSOR_SIZEALL));
			}

			@Override
			public void mouseExit(MouseEvent e) {
				bgBgGradient = TimerShell.INIT_BG_BG_GRADIENT;
			
				drawBgImage();
				shell.setCursor(null);
			}

			@Override
			public void mouseEnter(MouseEvent e) {
				bgBgGradient = TimerShell.HOVER_BG_COLOR;
				drawBgImage();
			}
		});
		inner.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				mouseDown = false;
			}

			@Override
			public void mouseDown(MouseEvent e) {
				mouseDown = true;
				xPos = e.x;
				yPos = e.y;
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				timero.promptNewJob();
				mouseDown = false;
			}
		});
		inner.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent e) {
				if (mouseDown) {
					shell.setLocation(shell.getLocation().x
							+ (e.x - xPos), shell.getLocation().y
							+ (e.y - yPos));
				}
			}
		});
		
		
		
		
		
		Label day = new Label(inner, SWT.NONE);
		day.setText(DAY_FORMAT.format(fromTime));
		GridData gd1 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		day.setLayoutData(gd1);
		
		Label close = new Label(inner,SWT.NONE);
		close.setText("X");
		
		GridData gd = new GridData(GridData.FILL_BOTH| GridData.HORIZONTAL_ALIGN_END);
		gd.horizontalSpan = 1;
		close.setLayoutData(gd);
		close.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				
			}
			
			@Override
			public void mouseDown(MouseEvent e) {
				shell.dispose();
				if(timero!=null)
					timero.reEnable();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
			}
		});
		List<ActivityRecord> activities = dataManager.activity(fromTime, toTime);
		table = new Table(inner, SWT.BORDER );
		table.setFont(FontCache.getFont(TimerShell.TITLE_FONT));
		table.setLinesVisible(true);
		//table.setSize(table.getSize().x, 67 * activities.size() + 500);
		// resize the row height using a MeasureItem listener
		table.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				// height cannot be per row so simply set
				event.height = 67;
			}
		});
		List<Integer> columnWidths = asList(500, 100, 150, 150, 150, 40, 40, 40, 40);
		for (int width : columnWidths) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(width);
		}
		printSizes();
		repopulateTable(activities);
		printSizes();
		
		

		
		
		shell.open();
		
		shell.pack();
		
		Rectangle monitorArea = shell.getMonitor().getClientArea();
		
		int startX = monitorArea.x + monitorArea.width
				- shell.getSize().x - 2;
		int startY = monitorArea.y + monitorArea.height
				- shell.getSize().y - 2;
		shell.setLocation(startX, startY);
		printSizes();
		printSizes();
	}

	public void printSizes() {
		System.out.println("Table:"+table.getSize());
		System.out.println("shell:"+shell.getSize());
	}

	public void keepOpen() {
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

	}

	public void repopulateTable(List<ActivityRecord> activities) {
		for (int i = 0; i < activities.size(); i++) {
			ActivityRecord activity = activities.get(i);
			TableItem item = new TableItem(table, SWT.NONE);
			item.setBackground(getBgColor());
			item.setForeground(getFgColor());
			
			item.setData(activity);
			setTableItemForActivity(i, item, activity);
		}
	}

	
	public void setTableItemForActivity(int index, TableItem item,
			ActivityRecord activity) {

		Task task = activity.getTask();
		Job job = task.getJob();
		disposeEditors(item);

		List<TableEditor> editors = new ArrayList<TableEditor>();

		int col = 0;

		item.setText(col++, job.getReference() + " - " + job.getDescription());
		item.setText(col++, task.getTaskDescription() + (activity.isAwayFromDesk()?"\n(away)":""));

		addLabelColumn(item, editors, DATE_FORMAT.format(activity.getStartTime()), col++);
		addLabelColumn(item, editors, DATE_FORMAT.format(activity.getEndTime()), col++);
		addLabelColumn(item, editors, TIME_FORMATTER.formatSecsAsTime(activity.getDurationSecs()), col++);
		addTransfer(item, editors, col++, 1, NotificationType.TRANSFER_1);
		addTransfer(item, editors, col++, 5, NotificationType.TRANSFER_5);
		addTransfer(item, editors, col++, 15, NotificationType.TRANSFER_15);
		addTransfer(item, editors, col++, 60, NotificationType.TRANSFER_60);

		item.setData("editors", editors);
	}

	
	private void applyControlStyling(Control controlT, FontData titleFont,
			GridData titleLayout) {
		controlT.setLayoutData(titleLayout);
		controlT.setForeground(getFgColor());
		controlT.setBackground(getBgColor());
		applyFont(controlT, titleFont);
	}
	
	private Color getBgColor() {
		return getBgBgGradient();
	}

	private Color getFgColor() {
		return fgColor;
	}
	private Color getBgFgGradient() {
		return bgFgGradient;
	}

	private Color getBgBgGradient() {
		return bgBgGradient;
	}

	private Color getBorderColor() {
		return borderColor;
	}
	
	public void applyFont(Control text, FontData fd) {
		text.setFont(FontCache.getFont(fd));
	}
	public void addLabelColumn(TableItem item, List<TableEditor> editors,
			String text, int column) {
		TableEditor editor = new TableEditor(table);
		editor.grabHorizontal = true;
		CLabel label = new CLabel(table, SWT.NONE);
		label.setText(text);
		applyControlStyling(label, TimerShell.TITLE_FONT, new GridData(
				 GridData.VERTICAL_ALIGN_CENTER|GridData.HORIZONTAL_ALIGN_CENTER) );
		editor.setEditor(label, item, column);
		editors.add(editor);
	}

	private void removeItem(int index) {
		TableItem item = table.getItem(index);
		dataManager.deleteActivity((ActivityRecord) item.getData());
		disposeEditors(item);
		table.remove(index);
		shell.pack();
		System.out.println("pack..");
		printSizes();
	}
	
	private void colorItem(TableItem item, Color color){
		item.setBackground(color);
		for(TableEditor editor:getEditors(item))
			editor.getEditor().setBackground(color);
		
	}

	private void disposeEditors(TableItem item) {
		List<TableEditor> editors = getEditors(item);
		if (editors != null) {
			for (TableEditor editor : editors) {
				editor.getEditor().dispose();
				editor.dispose();
			}
		}
	}

	public List<TableEditor> getEditors(TableItem item) {
		return (List<TableEditor>) item.getData("editors");
	}

	private CLabel addTransfer( TableItem item,
			List<TableEditor> editors, int column, int mins,
			NotificationType icon) {
		TableEditor transferEditor = new TableEditor(table);
		item.setData("transferEditor", transferEditor);
		CLabel transferAmountLabel = new CLabel(table, SWT.NONE);
		transferAmountLabel.setLayoutData(Layouts.GRID_TOP_LEFT);
		transferAmountLabel.setImage(icon.getImage());
		transferAmountLabel.setBackground(TimerShell.INIT_BG_BG_GRADIENT);
		transferEditor.grabHorizontal = true;
		editors.add(transferEditor);
		transferEditor.setEditor(transferAmountLabel, item, column);
		transferAmountLabel.setData("owningItem", item);
		;
		setDragDrop(transferAmountLabel,  (int)TimeUnit.MINUTES.toSeconds(mins));
		return transferAmountLabel;
	}

	private void resetTableItem(TableItem item) {
		setTableItemForActivity(table.indexOf(item), item,
				(ActivityRecord) item.getData());
	}

	public void setDragDrop(final CLabel label,
			final int timeSecs) {

		Transfer[] types = new Transfer[] { ActivityTimeTransferType
				.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
		final TableItem item = (TableItem) label.getData("owningItem");
		final DragSource source = new DragSource(label, operations);
		source.setTransfer(types);
		source.addDragListener(new DragSourceListener() {

			@Override
			public void dragStart(DragSourceEvent event) {
				//event.doit = true;//can set to false if neccesary
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				event.data = new ActivityTimeTransfer(timeSecs, table.indexOf(item));
				System.out.println("Set event data " + event.data);
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
					TableItem owningItem = (TableItem) item;
					if(owningItem.getData("ToDelete")!=null)
						removeItem(table.indexOf(owningItem));
					else
						resetTableItem(owningItem);
				
				// if (event.detail == DND.DROP_MOVE)
				// label.setText ("");
				// shell.setCursor(origCursor);
			}
		});

		DropTarget target = new DropTarget(label, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {

			@Override
			public void dropAccept(DropTargetEvent event) {
				System.out.println("drop accept..");
				int itemIndex = table.indexOf(item);
				ActivityTimeTransfer transfer = getData(event);

				if (transfer == null) {
					System.out.println("drop accept.. null event data!");
					event.detail = DND.DROP_NONE;
					return;
				} else {
					System.out.println("about to drop " + transfer);
					if (!canDrop(itemIndex, transfer)) {
						event.detail = DND.DROP_NONE;
						System.out.println("Cannot drop here");
					}

				}
			}

			private boolean canDrop(final int itemIndex,
					ActivityTimeTransfer transfer) {
				return Math.abs(transfer.getFromIndex() - itemIndex) == 1;
			}

			private ActivityTimeTransfer getData(DropTargetEvent event) {
				ActivityTimeTransferType transferType = ActivityTimeTransferType
						.getInstance();
				return (ActivityTimeTransfer) transferType
						.nativeToJava(event.currentDataType);
			}

			@Override
			public void dragLeave(DropTargetEvent event) {
				System.out.println("drag leave..");
				colorItem(item,TimerShell.INIT_BG_BG_GRADIENT);
			}

			@Override
			public void dragEnter(DropTargetEvent event) {
				int itemIndex = table.indexOf(item);
				System.out.println("drag enter..");
				ActivityTimeTransfer transfer = getData(event);
				if (canDrop(itemIndex, transfer)) {
					event.detail = DND.DROP_MOVE;
					colorItem(item,ColorCache.getColor(0, 255, 0) );
				} else {
					event.detail = DND.DROP_NONE;
					colorItem(item,ColorCache.getColor(244, 0, 0));
				}

			}

			@Override
			public void drop(DropTargetEvent event) {
				System.out.println("got drop");
				if (event.data == null) {
					System.out.println("null event data!");
					event.detail = DND.DROP_NONE;
					return;
				}
				ActivityTimeTransfer timeTransfer = (ActivityTimeTransfer) event.data;
				int fromIndex = timeTransfer.getFromIndex();
				TableItem fromItem = table.getItem(fromIndex);
				TableItem toItem = item;
				int toIndex = table.indexOf(toItem);
				ActivityRecord fromActivity =(ActivityRecord) fromItem.getData();
				ActivityRecord toActivity = (ActivityRecord) item.getData();
			
				System.out.println("dragged " + timeTransfer.getTimeSecs() + " from "
						+ fromActivity + " to " + toActivity);

				int changeSecs = Math.min(timeTransfer.getTimeSecs(),
						fromActivity.getDurationSecs());

				System.out.println("using min- " + changeSecs);
				if (fromIndex < toIndex) {
					fromActivity.setEndTime(shiftBack(
							fromActivity.getEndTime(), changeSecs));
					toActivity.setStartTime(shiftBack(
							toActivity.getStartTime(), changeSecs));
				} else if (fromIndex > toIndex) {
					fromActivity.setStartTime(shiftForward(
							fromActivity.getStartTime(), changeSecs));
					toActivity.setEndTime(shiftForward(toActivity.getEndTime(),
							changeSecs));
				}
				System.out.println("From activity now " + fromActivity);
				if (fromActivity.getDurationSecs() < 1) {
					System.out.println("Nothing left for fromActivity:"
							+ fromActivity);
					markForDeletion(fromItem);
					//removeItem(fromIndex);
				}
				else
					dataManager.save(fromActivity);
				
				System.out.println("To activity now " + toActivity);
				dataManager.save( toActivity);
				
				setTableItemForActivity(toIndex, toItem, toActivity);

			}

			private void markForDeletion(TableItem item) {
				item.setData("ToDelete",true);
				
			}

		});
	}
	
	


	private static Date shiftForward(Date origStartTime, int changeSecs) {
		Calendar toStartTimeCal = Calendar.getInstance();
		toStartTimeCal.setTime(origStartTime);
		toStartTimeCal.add(Calendar.SECOND, changeSecs);
		return toStartTimeCal.getTime();
	}

	private static Date shiftBack(Date origStartTime, int changeSecs) {
		return shiftForward(origStartTime, -changeSecs);
	}
}