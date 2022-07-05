package tk.ogorod98.dualscreencustomizer.screeninfo.virtualscreen;

import com.intellij.openapi.Disposable;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState.VirtualScreenInfo;
import tk.ogorod98.dualscreencustomizer.screeninfo.IScreenInfoProvider;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRecord;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRegistry;

public class VirtualScreenInfoProvider implements IScreenInfoProvider, Disposable, ActionListener {

	private static final int DEFAULT_PRIORITY = 1;
	private volatile Consumer<ScreenInfoRegistry> registryConsumer;

	public VirtualScreenInfoProvider() {
		VirtualScreenState.getInstance().addUpdateListener(this);
	}

	@Override
	public void onUpdate(Consumer<ScreenInfoRegistry> registryConsumer) {
		this.registryConsumer = registryConsumer;
	}

	@Override
	public void dispose() {
		VirtualScreenState.getInstance().removeUpdateListener(this);
		registryConsumer = null;
	}

	public void updateVirtualScreenInfo(VirtualScreenState state) {
		ScreenInfoRegistry registry = new ScreenInfoRegistry();
		for (var entry : state.virtualScreens.entrySet()) {
			String identifier = entry.getKey();
			var screenInfo = entry.getValue();

			ScreenDescriptor descriptor = ScreenDescriptor.builder()
					.withIdentifier(identifier)
					.withVendorName("VIRTUAL")
					.build();

			Rectangle geometry = asGeometry(screenInfo);

			ScreenInfoRecord record = new ScreenInfoRecord(descriptor, geometry, VirtualScreenInfoProvider.DEFAULT_PRIORITY);
			registry.put(descriptor, record);
		}

		var consumer = registryConsumer;
		if (consumer != null) {
			consumer.accept(registry);
		}
	}

	private Rectangle asGeometry(VirtualScreenInfo screenInfo) {
		return new Rectangle(screenInfo.x, screenInfo.y, screenInfo.width, screenInfo.height);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		updateVirtualScreenInfo((VirtualScreenState) e.getSource());
	}
}
