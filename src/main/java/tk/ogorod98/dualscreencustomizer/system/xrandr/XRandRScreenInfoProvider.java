package tk.ogorod98.dualscreencustomizer.system.xrandr;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.swing.Timer;
import tk.ogorod98.dualscreencustomizer.screeninfo.IScreenInfoProvider;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRecord;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRegistry;
import tk.ogorod98.dualscreencustomizer.system.ExternalCall;

public class XRandRScreenInfoProvider implements IScreenInfoProvider, Disposable, ActionListener {

	private static final int DEFAULT_PRIORITY = 0;
	private final AtomicReference<Consumer<ScreenInfoRegistry>> registryConsumer = new AtomicReference<>();
	private final XRandRMonitorListProvider monitorListProvider = new XRandRMonitorListProvider();
	private final Timer externalCallTimer;

	private final AtomicBoolean panicFlag = new AtomicBoolean(false);

	public XRandRScreenInfoProvider() {
		externalCallTimer = new Timer(1000, this);
		externalCallTimer.start();
	}

	@Override
	public void onUpdate(Consumer<ScreenInfoRegistry> registryConsumer) {
		this.registryConsumer.set(registryConsumer);
	}

	@Override
	public void dispose() {
		externalCallTimer.stop();
		registryConsumer.set(null);
	}

	private static String getStackTrace(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		if (e != null) {
			e.printStackTrace(pw);
		} else {
			pw.println("No exception provided.");
		}
		return sw.toString();
	}

	private void panic(Throwable e) {
		if (panicFlag.compareAndSet(false, true)) {
			dispose();
			//   public static int showDialog(Project project,
			//                               @DialogMessage String message,
			//                               @NotNull @DialogTitle String title,
			//                               @Nullable String moreInfo,
			//                               String @NotNull @NlsContexts.Button [] options,
			//                               int defaultOptionIndex,
			//                               int focusedOptionIndex,
			//                               Icon icon);
			ApplicationManager.getApplication()
					.invokeLater(() -> Messages.showDialog(
							null,
							"Cannot retrieve XRandR info. External retriever will be disabled. "
									+ "You can use virtual screens instead (see Settings > Editor > DualScreenCustomizer > Virtual Screens). "
									+ "If this error continues occurring, you can disable XRandR discovery in 'Virtual Screens' settings section.",
							"DualScreenCustomizer - XRandR Error",
							getStackTrace(e),
							new String[] {Messages.getOkButton()},
							0,
							0,
							Messages.getWarningIcon()
					));
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (panicFlag.get()) {
			// Do nothing
			return;
		}

		try {
			ExternalCall
					.runCmd("xrandr --verbose")
					.thenApply(XRandRParser::parse)
					.thenCombine(monitorListProvider.listActiveMonitors(),
							(list, activeMonitors) -> {
						ScreenInfoRegistry registry = new ScreenInfoRegistry();

						for (XRandRScreenInfo info : list) {
							try {
								if (activeMonitors.containsKey(info.getPort())) {
									info.setConnected(true);
									info.setGeometry(activeMonitors.get(info.getPort()));
								}

								if (!info.isConnected()) {
									continue;
								}
								ScreenDescriptor descriptor = EDID.parseEDID(info.getEDID()).getScreenDescriptor();
								Rectangle geometry = info.getGeometry();
								ScreenInfoRecord record = new ScreenInfoRecord(descriptor, geometry,
										XRandRScreenInfoProvider.DEFAULT_PRIORITY);
								registry.put(descriptor, record);
							} catch (Exception ex) {
								panic(ex);
							}
						}

						return registry;
					})
					.thenAccept(registry -> {
						var consumer = registryConsumer.get();
						if (consumer != null) {
							consumer.accept(registry);
						}
					})
					.exceptionally(ex -> {
						panic(ex);
						return null;
					});
		} catch (Exception ex) {
			panic(ex);
		}
	}
}
