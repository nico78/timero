package core;

import notification.NotificationType;
import notification.cache.ColorCache;
import notification.cache.FontCache;

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

public class TimerShell {
	public static final Color INIT_FG_COLOR = ColorCache.getColor(40,73,97);
	public static final Color INIT_BORDER_COLOR =  ColorCache.getColor(226, 239, 249);
	public static final Color INIT_BG_BG_GRADIENT = ColorCache.getColor(171, 211, 248);
	public static final Color INIT_BG_FG_GRADIENT = ColorCache.getColor(226, 239, 249);

	private static final Color HOVER_BG_COLOR = ColorCache.getColor(0, 211, 243);
	private Display display;
	private CLabel timerText;
	private Label subText;
	private Shell shell;
	
	private Image currentBackgroundImage;
	private String initialText;
	private Color bgFgGradient = INIT_BG_FG_GRADIENT;
	private Color bgBgGradient = INIT_BG_BG_GRADIENT;
	private Color borderColor = INIT_BORDER_COLOR;
	private Color fgColor = INIT_FG_COLOR;

	public TimerShell(Display display, String initialText){
		shell = new Shell(display, SWT.NO_FOCUS | SWT.NO_TRIM | SWT.ON_TOP);
		this.display = display;
		this.initialText = initialText;
		decorate();
	}
	
	public void setHeaderText(String text){
		timerText.setText(text);
	}

	public void setSubText(String text){
		subText.setText(text);
	}
	
	private void decorate(){
			shell.setLayout(new FillLayout());
	        final Color _fgColor = ColorCache.getColor(45, 64, 93);
	       
			final Color       _bgFgGradient = getBgFgGradient();
			
			final Color       _bgBgGradient = getBgBgGradient();
	         // shell border color
	        final Color _borderColor  = getBorderColor();
	        // title foreground color
	        final Color       _titleFgColor = getFgColor();
	        shell.setForeground(_fgColor);
	        shell.setBackgroundMode(SWT.INHERIT_DEFAULT);
	        
	
	        final Composite inner = new Composite(shell, SWT.NONE);
	
	        GridLayout gl = new GridLayout(2, false);
	        gl.marginLeft = 5;
	        gl.marginTop = 0;
	        gl.marginRight = 5;
	        gl.marginBottom = 5;
	
	        inner.setLayout(gl);
	        shell.addListener(SWT.Resize, new Listener() {
	            @Override
	            public void handleEvent(Event e) {
	                drawBgImage();
	            }
	        });
	
	        GC gc = new GC(shell);
	        String lines[] = initialText.split("\n");
	        Point longest = null;
	        int typicalHeight = gc.stringExtent("X").y;
	
	        for (String line : lines) {
	            Point extent = gc.stringExtent(line);
	            if (longest == null) {
	                longest = extent;
	                continue;
	            }
	
	            if (extent.x > longest.x) {
	                longest = extent;
	            }
	        }
	        gc.dispose();
	
	        int minHeight = typicalHeight * lines.length;
	
	        CLabel imgLabel = new CLabel(inner, SWT.NONE);
	        imgLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
	        imgLabel.setImage(NotificationType.TIMERO.getImage());
	
	        timerText = new CLabel(inner, SWT.NONE);
	        timerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
	        timerText.setText("TIMERO");
	        timerText.setForeground(_titleFgColor);
	        Font f = timerText.getFont();
	        FontData fd = f.getFontData()[0];
	        fd.setStyle(SWT.BOLD);
	        fd.setName("calibri");
	        fd.height = 11;
	        timerText.setFont(FontCache.getFont(fd));
	
	        subText = new Label(inner, SWT.WRAP);
	        Font tf = subText.getFont();
	        FontData tfd = tf.getFontData()[0];
	        tfd.setName("calibri");
	        tfd.setStyle(SWT.BOLD);
	        tfd.height = 8;
	        subText.setFont(FontCache.getFont(tfd));
	        GridData gd = new GridData(GridData.FILL_BOTH);
	        gd.horizontalSpan = 2;
	        subText.setLayoutData(gd);
	        subText.setForeground(_fgColor);
	        subText.setText(initialText);
	
	        minHeight = 100;
	
	        shell.setSize(350, minHeight);
	
	        Rectangle monitorArea = shell.getMonitor().getClientArea();
	
	        int startX = monitorArea.x + monitorArea.width - shell.getSize().x -2;
	        int startY = monitorArea.y + monitorArea.height - shell.getSize().y - 2;
	        shell.setLocation(startX, startY);
	      
	        timerText.addMouseTrackListener(new MouseTrackListener() {
				
				@Override
				public void mouseHover(MouseEvent e) {
					fgColor = ColorCache.getBlack();
					drawBgImage();
				}
				
				@Override
				public void mouseExit(MouseEvent e) {
					bgBgGradient = INIT_BG_BG_GRADIENT;
					fgColor = INIT_FG_COLOR;
					drawBgImage();
				}
				
				@Override
				public void mouseEnter(MouseEvent e) {
					bgBgGradient = HOVER_BG_COLOR;
					drawBgImage();
				}
			});
	        shell.setVisible(true);
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

	private void drawBgImage() {
    	try {
    		// get the size of the drawing area
			Rectangle rect = shell.getClientArea();
			// create a new image with that size
			Image newImage1 = new Image(display, Math.max(1, rect.width), rect.height);
			// create a GC object we can use to draw with
			GC gc = new GC(newImage1);
			
			// fill background
			gc.setForeground(getBgFgGradient());
			gc.setBackground(getBgBgGradient());
			gc.fillGradientRectangle(rect.x, rect.y, rect.width, rect.height, true);
			
			// draw shell edge
			gc.setLineWidth(2);
			gc.setForeground(getBorderColor());
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

	public void highlight(){
		flash();
	}

	private void flash() {
		fadeOut(3);
	}
	
	private  void fadeIn(final int numberOfTimes) {
		 final int FADE_IN_STEP=25;
		final int FINAL_ALPHA=255;
        Runnable run = new Runnable() {

            @Override
            public void run() {
                try {

                    int cur = shell.getAlpha();
                    cur += FADE_IN_STEP;
                    

                    if (cur > FINAL_ALPHA) {
                    	shell.setAlpha(FINAL_ALPHA);
                        if(numberOfTimes > 0){
                        	fadeOut(numberOfTimes);
                        }
                        return;
                    }

                    shell.setAlpha(cur);
                    drawBgImage();
                    display.timerExec(25, this);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }


        };
       display.timerExec(25, run);
    }
	
	private  void fadeOut(final int numberOfTimes) {
		final int FADE_OUT_STEP=25;
		final int MIN_ALPHA = 30;
		 final Runnable run = new Runnable() {
			 Image _oldImage;

	            @Override
	            public void run() {
	                try {

	                    int cur = shell.getAlpha();
	                    cur -= FADE_OUT_STEP;
	                    if (cur <= MIN_ALPHA) {
	                    	shell.setAlpha(MIN_ALPHA);
	                        if (_oldImage != null) {
	                            _oldImage.dispose();
	                        }
	                        fadeIn(numberOfTimes -1);
	                        return;
	                    }
	                    
	                    shell.setAlpha(cur);
	                    drawBgImage();

	                    display.timerExec(25, this);

	                } catch (Exception err) {
	                    err.printStackTrace();
	                }
	            }


	        };
	        display.timerExec(25, run);
   }

	public Shell getShell() {
		return shell;
	}

	public void show() {
		 shell.open();
	        while (!shell.isDisposed()) {
	            if (!display.readAndDispatch()) 
	            	display.sleep();
	        }
	        System.out.println("dispose...");
	        display.dispose();
	        System.out.println("exit from timer");
	        System.exit(0);
	}
	
	public void focus(){
		System.out.println("timer focus..");
    	display.syncExec(new Runnable(){
    		public void run(){
    			System.out.println("timer setfocus run");
    			shell.setActive();
    		}
    	});
	}

	public void quit() {
		display.syncExec(new Runnable(){
			@Override
			public void run() {
				shell.dispose();
			}});
	}
}