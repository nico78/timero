package core;

import notification.NotificationType;
import notification.cache.ColorCache;
import notification.cache.FontCache;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.nebula.cwt.animation.AnimationRunner;
import org.eclipse.nebula.cwt.animation.effects.Grow;
import org.eclipse.nebula.cwt.animation.effects.MoveControl;
import org.eclipse.nebula.cwt.animation.movement.BounceOut;
import org.eclipse.nebula.cwt.animation.movement.ElasticOut;
import org.eclipse.nebula.cwt.animation.movement.ExpoOut;
import org.eclipse.nebula.cwt.animation.movement.SinusVariation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

public class TimerShell {
	public static final Color INIT_FG_COLOR = ColorCache.getColor(40, 73, 97);

	public static final Color INIT_TITLE_COLOR = ColorCache
			.getColor(45, 64, 93);
	public static final Color INIT_BORDER_COLOR = ColorCache.getColor(40, 73,
			97);
	public static final Color INIT_BG_BG_GRADIENT = ColorCache.getColor(171,
			211, 248);
	public static final Color INIT_BG_FG_GRADIENT = ColorCache.getColor(226,
			239, 249);

	public static final Color HOVER_BG_COLOR = ColorCache
			.getColor(0, 211, 243);

	public static final FontData TITLE_FONT = new FontData("calibri", 11,
			SWT.BOLD);
	public static final FontData SUBTEXT_FONT = new FontData("calibri", 8,
			SWT.BOLD);

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
	private Timero timero;
	private boolean mouseDown = false;
	protected int xPos;
	protected int yPos;
	private Stylist stylist;

	public TimerShell(Display display, String initialText, Timero timero) {
		this.timero = timero;
		shell = new Shell(display, SWT.NO_FOCUS | SWT.NO_TRIM | SWT.ON_TOP
				| SWT.RESIZE);
		this.display = display;
		this.initialText = initialText;
		stylist = new Stylist();
		stylist.style(shell);
	}

	public void setHeaderText(String text) {
		timerText.setText(text);
	}

	public void setSubText(String text) {
		subText.setText(text);
	}

	public static class Layouts {
		public static final GridLayout PADDING = createPadding();

		public static GridLayout createPadding() {
			GridLayout gl = new GridLayout(2, false);
			gl.marginLeft = 5;
			gl.marginTop = 0;
			gl.marginRight = 5;
			gl.marginBottom = 5;
			return gl;
		}

		public static GridData getFillHorizontal2Span() {
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.horizontalSpan = 2;
			return gd;
		}

		public static final GridData GRID_TOP_LEFT = new GridData(
				GridData.VERTICAL_ALIGN_BEGINNING
						| GridData.HORIZONTAL_ALIGN_BEGINNING);
		public static final GridData GD_FILL_CENTERED = new GridData(
				GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);

	}

	private class Stylist {

		private void style(final Shell locShell) {
			int width = 350;
			int initHeight = 100;

			locShell.setLayout(new FillLayout());
			locShell.setForeground(getFgColor());
			locShell.setBackgroundMode(SWT.INHERIT_DEFAULT);

			final Composite inner = new Composite(locShell, SWT.NONE);
			GridLayout paddedGridLaout = Layouts.PADDING;
			inner.setLayout(paddedGridLaout);
			locShell.addListener(SWT.Resize, new Listener() {
				@Override
				public void handleEvent(Event e) {
					drawBgImage();
				}
			});

			Image image = NotificationType.TIMERO.getImage();
			GridData imgPosition = Layouts.GRID_TOP_LEFT;
			addImage(inner, image, imgPosition);// TODO remove inner

			String initialTitle = "TIMERO";

			timerText = new CLabel(inner, SWT.NONE);
			timerText.setText(initialTitle);
			applyControlStyling(timerText, TITLE_FONT, Layouts.GD_FILL_CENTERED);

			subText = new Label(inner, SWT.WRAP);
			subText.setText(initialText);
			applyControlStyling(subText, SUBTEXT_FONT,
					Layouts.getFillHorizontal2Span());

			locShell.setSize(width, Math
					.max(initHeight,
							requiredHeightForString(initialText,
									heightOfFont(subText))));

			Rectangle monitorArea = locShell.getMonitor().getClientArea();

			int startX = monitorArea.x + monitorArea.width
					- locShell.getSize().x - 2;
			int startY = monitorArea.y + monitorArea.height
					- locShell.getSize().y - 2;
			locShell.setLocation(startX, startY);

			inner.addMouseTrackListener(new MouseTrackListener() {

				@Override
				public void mouseHover(MouseEvent e) {
					locShell.setCursor(new Cursor(display, SWT.CURSOR_SIZEALL));
				}

				@Override
				public void mouseExit(MouseEvent e) {
					bgBgGradient = INIT_BG_BG_GRADIENT;
					fgColor = INIT_FG_COLOR;
					drawBgImage();
					locShell.setCursor(null);
				}

				@Override
				public void mouseEnter(MouseEvent e) {
					bgBgGradient = HOVER_BG_COLOR;
					drawBgImage();
				}
			});

			MenuManager popManager = new MenuManager();
			IAction hideAction = new HideAction();
			IAction configureAction = new ConfigureAction();
			popManager.add(hideAction);
			popManager.add(configureAction);

			Menu menu = popManager.createContextMenu(inner);
			inner.setMenu(menu);

			// moving..
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
						locShell.setLocation(locShell.getLocation().x
								+ (e.x - xPos), locShell.getLocation().y
								+ (e.y - yPos));
					}
				}
			});

			locShell.setVisible(true);
		}

		public void applyControlStyling(Control controlT, FontData titleFont,
				GridData titleLayout) {
			controlT.setLayoutData(titleLayout);
			controlT.setForeground(getFgColor());
			applyFont(controlT, titleFont);
		}

		public void applyFont(Control text, FontData fd) {
			text.setFont(FontCache.getFont(fd));
		}

		private void addImage(final Composite inner, Image image,
				GridData imgPosition) {
			CLabel imgLabel = new CLabel(inner, SWT.NONE);
			imgLabel.setLayoutData(imgPosition);
			imgLabel.setImage(image);
		}

		private int requiredHeightForString(String text, int typicalHeight) {
			return typicalHeight * text.split("\n").length;
		}

		private int heightOfFont(final Drawable item) {
			GC gc = new GC(item);
			int typicalHeight = gc.stringExtent("X").y;
			gc.dispose();
			return typicalHeight;
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

		private void fadeIn(final int numberOfTimes) {
			final int FADE_IN_STEP = 25;
			final int FINAL_ALPHA = 255;
			Runnable run = new Runnable() {

				@Override
				public void run() {
					try {

						int cur = shell.getAlpha();
						cur += FADE_IN_STEP;

						if (cur > FINAL_ALPHA) {
							shell.setAlpha(FINAL_ALPHA);
							if (numberOfTimes > 0) {
								fadeOut(numberOfTimes);
							}
							// else
							// slideDown();
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

		private void fadeOut(final int numberOfTimes) {
			final int FADE_OUT_STEP = 25;
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
							fadeIn(numberOfTimes - 1);
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

		private void pulseIn(final int numberOfTimes) {
			final int FADE_IN_STEP = 10;
			final int FINAL_X = 400;
			Runnable run = new Runnable() {

				@Override
				public void run() {
					try {

						int curX = shell.getSize().x;
						int curY = shell.getSize().y;
						curX += FADE_IN_STEP;
						curY += FADE_IN_STEP;

						if (curX > FINAL_X) {
							shell.setSize(FINAL_X, (int) (FINAL_X * 3.5));
							Point location = shell.getLocation();
							shell.setLocation(location.x - FADE_IN_STEP,
									location.y - FADE_IN_STEP);
							if (numberOfTimes > 0) {
								pulseOut(numberOfTimes);
							} else
								slideDown();
							return;
						}

						shell.setSize(curX, curY);
						Point location = shell.getLocation();
						shell.setLocation(location.x - FADE_IN_STEP, location.y
								- FADE_IN_STEP);
						display.timerExec(25, this);
					} catch (Exception err) {
						err.printStackTrace();
					}
				}

			};
			display.timerExec(25, run);
		}

		private void pulseOut(final int numberOfTimes) {
			final int FADE_OUT_STEP = 10;
			final Runnable run = new Runnable() {
				Image _oldImage;

				@Override
				public void run() {
					try {

						int curX = shell.getSize().x;
						int curY = shell.getSize().y;
						curX -= FADE_OUT_STEP;
						curY -= FADE_OUT_STEP;
						if (curX <= 350) {
							shell.setSize(350, 100);
							Point location = shell.getLocation();
							shell.setLocation(location.x + FADE_OUT_STEP,
									location.y + FADE_OUT_STEP);
							pulseIn(numberOfTimes - 1);
							return;
						}

						shell.setSize(curX, curY);
						Point location = shell.getLocation();
						shell.setLocation(location.x + FADE_OUT_STEP,
								location.y + FADE_OUT_STEP);
						// drawBgImage();

						display.timerExec(25, this);

					} catch (Exception err) {
						err.printStackTrace();
					}
				}

			};
			display.timerExec(25, run);
		}
	}

	public void highlight() {
		flash();

		// upAndDrop();
		// sideTwang();
		// elasticGrow();

	}

	private void elasticGrow() {
		// meant to keep things centred but doesn't
		final AnimationRunner sr = new AnimationRunner();

		Grow growDirect = new Grow(shell, shell.getBounds(), new Rectangle(
				shell.getBounds().x, shell.getBounds().y,
				shell.getBounds().width + 10, shell.getBounds().height + 10),
				500l, new ExpoOut(), new Runnable() {

					@Override
					public void run() {
						final Grow shrinkElastic = new Grow(shell,
								shell.getBounds(), new Rectangle(
										shell.getBounds().x,
										shell.getBounds().y,
										shell.getBounds().width - 10,
										shell.getBounds().height - 10), 1000l,
								new ElasticOut(), null, null);
						sr.runEffect(shrinkElastic);

					}
				}, null);
		sr.runEffect(growDirect);
	}

	private void sideTwang() {
		final AnimationRunner sr = new AnimationRunner();
		MoveControl left = new MoveControl(shell, shell.getLocation().x,
				shell.getLocation().x - 100, shell.getLocation().y,
				shell.getLocation().y, 1000l, new ExpoOut(), new Runnable() {

					@Override
					public void run() {
						MoveControl backAgain = new MoveControl(shell,
								shell.getLocation().x,
								shell.getLocation().x + 100,
								shell.getLocation().y, shell.getLocation().y,
								1000l, new ElasticOut(), null, null);
						sr.runEffect(backAgain);
					}

				}, null);

		sr.runEffect(left);
	}

	
	
	
	public void upAndDrop() {
		final AnimationRunner sr = new AnimationRunner();
		final int initialY = shell.getLocation().y;
		
		MoveControl up = new MoveControl(shell, shell.getLocation().x,
				shell.getLocation().x, shell.getLocation().y,
				shell.getLocation().y - 50, 1000l, new ExpoOut(),
				new Runnable() {

					@Override
					public void run() {
						MoveControl down = new MoveControl(shell, shell
								.getLocation().x, shell.getLocation().x, shell
								.getLocation().y, initialY,
								1000l, new BounceOut(), null, null);
						sr.runEffect(down);
					}

				}, null);

		sr.runEffect(up);
	}

	private void flash() {

		stylist.fadeOut(1);

	}

	private void slideDown() {
		final int SINK_RATE = 3;
		Runnable run = new Runnable() {

			@Override
			public void run() {
				int monitoryHeight = shell.getMonitor().getClientArea().height + 50;
				Point location = shell.getLocation();
				int cur = location.y;
				cur += SINK_RATE;
				if (cur >= monitoryHeight) {
					shell.setLocation(location.x, monitoryHeight);
					slideUp();
					return;
				}
				shell.setLocation(location.x, cur);
				display.timerExec(25, this);
			}

		};

		display.timerExec(25, run);
	}

	private void slideUp() {
		final int RISE_RATE = 5;
		final int INIT_Y = 50;// TODO set from startY when drawn

		Runnable run = new Runnable() {

			@Override
			public void run() {
				int monitoryHeight = shell.getMonitor().getClientArea().height + 50;
				Point location = shell.getLocation();
				int cur = location.y;
				cur -= RISE_RATE;
				if (cur >= INIT_Y) {
					shell.setLocation(location.x, INIT_Y);
					return;
				}
				shell.setLocation(location.x, cur);
				display.timerExec(25, this);
			}

		};

		display.timerExec(3000, run);
	}

	private final class HideAction extends Action {

		public HideAction() {
			super("Hide");
		}

		@Override
		public void run() {
			flash();
		}
	}

	public class ConfigureAction extends Action {

		public ConfigureAction() {
			super("Configure...");
		}

		@Override
		public void run() {
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					System.out.println("CONFIGURE");
					// TODO popup configurer
				}
			});
		}

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
		display.dispose();
		System.exit(0);
	}

	public void focus() {
		display.syncExec(new Runnable() {
			public void run() {
				shell.setActive();
			}
		});
	}

	public void quit() {
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				shell.dispose();
			}
		});
	}
}