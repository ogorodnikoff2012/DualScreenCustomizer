package tk.ogorod98.dualscreencustomizer;

import com.intellij.codeInsight.daemon.impl.EditorTracker;
import com.intellij.codeInsight.daemon.impl.EditorTrackerListener;
import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.jetbrains.annotations.NotNull;
import tk.ogorod98.dualscreencustomizer.config.AppSettingsState;
import tk.ogorod98.dualscreencustomizer.config.ScreenConfig;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRegistry;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoService;

public class MainListener implements EditorTrackerListener {

	private static final WeakHashMap<JComponent, ComponentListener> LISTENERS =
			new WeakHashMap<>();

	private static final ComponentListener DUMMY_COMPONENT_LISTENER = new ComponentListener() {

		@Override
		public void componentResized(final ComponentEvent e) {
			System.out.println(e);
		}

		@Override
		public void componentMoved(final ComponentEvent e) {
			System.out.println(e);
		}

		@Override
		public void componentShown(final ComponentEvent e) {
			System.out.println(e);
		}

		@Override
		public void componentHidden(final ComponentEvent e) {
			System.out.println(e);
		}

	};

	public void attachDummyListener(final JComponent component) {
		synchronized (LISTENERS) {
			LISTENERS.computeIfAbsent(component, (c) -> {
				c.addComponentListener(DUMMY_COMPONENT_LISTENER);
				return DUMMY_COMPONENT_LISTENER;
			});
		}
	}
	private final Project project;
	private final Window projectWindow;

	public MainListener(Project project) {
		this.project = project;

		projectWindow = SwingUtilities.getWindowAncestor(
				Objects.requireNonNull(WindowManager.getInstance().getIdeFrame(project)).getComponent());
		WindowMoveEventListener windowMoveEventListener = new WindowMoveEventListener();
		projectWindow.addComponentListener(windowMoveEventListener);
		AppSettingsState.getInstance().addUpdateListener(new ActionListener() {
			private boolean panicFlag = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (panicFlag) {
					return;
				}

				try {
					updateSettings(EditorTracker.getInstance(MainListener.this.project).getActiveEditors());
				} catch (Exception ex) {
					panicFlag = true;
					((AppSettingsState) e.getSource()).removeUpdateListener(this);
				}
			}
		});
	}

	@Override
	public void activeEditorsChanged(@NotNull List<? extends Editor> activeEditors) {
		updateSettings(activeEditors);
	}

	private class WindowMoveEventListener extends ComponentAdapter implements ActionListener {

		private static final long MS_TO_NS = 1_000_000;
		private static final int SILENCE_DELAY_MS = 100;
		private static final long SILENCE_DELAY_NS = SILENCE_DELAY_MS * MS_TO_NS;
		private long silenceTimeout = Long.MIN_VALUE;
		private ComponentEvent delayedEvent = null;

		private boolean panicFlag = false;

		@Override
		public void componentMoved(ComponentEvent e) {
			processEvent(e);
		}

		@Override
		public void componentResized(ComponentEvent e) {
			processEvent(e);
		}

		private void processEvent(ComponentEvent e) {
			long now = System.nanoTime();
			if (now > silenceTimeout) {
				silenceTimeout = now + SILENCE_DELAY_NS;
				doProcessEvent(e);
			} else {
				if (delayedEvent == null) {
					Timer t = new Timer(SILENCE_DELAY_MS, this);
					t.setRepeats(false);
					t.start();
				}
				delayedEvent = e;
			}
		}

		private void doProcessEvent(ComponentEvent ignored) {
			if (panicFlag) {
				return;
			}

			try {
				updateSettings(EditorTracker.getInstance(project).getActiveEditors());
			} catch (Exception ex) {
				panicFlag = true;
				projectWindow.removeComponentListener(this);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (delayedEvent != null) {
				doProcessEvent(delayedEvent);
				delayedEvent = null;
			}
		}
	}

	private void updateSettings(List<? extends Editor> editors) {
		if (UISettings.getInstance().getPresentationMode()) {
			return;
		}

		ScreenInfoRegistry registry = ScreenInfoService.getInstance().getMergedRegistry();

		for (Editor editor : editors) {
			if (mayHaveSideEffects(editor)) {
				continue;
			}
			updateSettings(registry, editor);
		}
	}

	private boolean mayHaveSideEffects(Editor editor) {
		return !EditorUtil.isRealFileEditor(editor);
	}

	private void updateSettings(ScreenInfoRegistry registry, Editor editor) {
		attachDummyListener(editor.getComponent());
		Rectangle editorGeometry = editor.getComponent().getVisibleRect();
		Point editorOnScreen = editor.getComponent().getLocationOnScreen();
		editorGeometry.setLocation(editorOnScreen);
		Point editorCenter = new Point((int) editorGeometry.getCenterX(), (int) editorGeometry.getCenterY());

		ScreenDescriptor screen = registry.findScreen(editorCenter);

		EditorColorsScheme currentScheme = editor.getColorsScheme();
		EditorColorsScheme defaultScheme = EditorColorsManager.getInstance().getSchemeForCurrentUITheme();

		ScreenConfig.updateScheme(defaultScheme, currentScheme,
				screen == null ? null : AppSettingsState.getInstance().screenToConfig.get(screen));
	}
}
