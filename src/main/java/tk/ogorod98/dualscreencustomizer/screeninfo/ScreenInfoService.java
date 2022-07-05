package tk.ogorod98.dualscreencustomizer.screeninfo;

import com.intellij.openapi.application.ApplicationManager;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState;
import tk.ogorod98.dualscreencustomizer.screeninfo.virtualscreen.VirtualScreenInfoProvider;
import tk.ogorod98.dualscreencustomizer.system.xrandr.XRandRScreenInfoProvider;

public class ScreenInfoService {

	private final Map<IScreenInfoProvider, ScreenInfoRegistry> registries = new HashMap<>();

	private volatile ScreenInfoRegistry mergedRegistry = new ScreenInfoRegistry();

	private final Set<ActionListener> updateListeners = ConcurrentHashMap.newKeySet();

	private final Set<IScreenInfoProvider> providers = new HashSet<>();

	private volatile boolean useXRandR = false;

	public static ScreenInfoService getInstance() {
		return ApplicationManager.getApplication().getService(ScreenInfoService.class);
	}

	public ScreenInfoService() {
		if (VirtualScreenState.getInstance().useXRandR) {
			startXRandRProvider();
		}
		registerProvider(new VirtualScreenInfoProvider());

		VirtualScreenState.getInstance().addUpdateListener(e -> {
			VirtualScreenState state = (VirtualScreenState) e.getSource();
			if (state.useXRandR != useXRandR) {
				if (state.useXRandR) {
					startXRandRProvider();
				} else {
					stopXRandRProvider();
				}
			}
		});
	}

	public ScreenInfoRegistry getMergedRegistry() {
		return mergedRegistry;
	}

	public void registerProvider(final IScreenInfoProvider provider) {
		providers.add(provider);
		provider.onUpdate((registry) -> updateScreenInfo(provider, registry));
	}

	public synchronized void stopXRandRProvider() {
		Iterator<IScreenInfoProvider> it = providers.iterator();
		while (it.hasNext()) {
			IScreenInfoProvider provider = it.next();
			if (!(provider instanceof XRandRScreenInfoProvider)) {
				continue;
			}

			XRandRScreenInfoProvider realProvider = (XRandRScreenInfoProvider) provider;
			realProvider.dispose();
			it.remove();
			registries.remove(realProvider);
		}
		useXRandR = false;
	}

	public synchronized void startXRandRProvider() {
		if (useXRandR) {
			return;
		}
		useXRandR = true;
		registerProvider(new XRandRScreenInfoProvider());
	}

	public void updateScreenInfo(IScreenInfoProvider provider, ScreenInfoRegistry registry) {
		boolean updated = false;
		synchronized (this) {
			registries.put(provider, registry);
			ScreenInfoRegistry newRegistry = mergeRegistries(registries);
			if (!Objects.equals(newRegistry, mergedRegistry)) {
				updated = true;
				mergedRegistry = newRegistry;
			}
		}

		if (updated) {
			sendUpdateEvent();
		}
	}

	public void addUpdateListener(ActionListener listener) {
		updateListeners.add(listener);
	}

	public void removeUpdateListener(ActionListener listener) {
		updateListeners.remove(listener);
	}

	private void sendUpdateEvent() {
		for (ActionListener listener : updateListeners) {
			ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
			EventQueue.invokeLater(() -> listener.actionPerformed(event));
		}
	}

	private ScreenInfoRegistry mergeRegistries(Map<IScreenInfoProvider, ScreenInfoRegistry> registries) {
		ScreenInfoRegistry result = new ScreenInfoRegistry();
		for (ScreenInfoRegistry registry : registries.values()) {
			result.putAll(registry);
		}
		return result;
	}
}
