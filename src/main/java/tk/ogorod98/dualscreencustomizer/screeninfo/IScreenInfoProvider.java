package tk.ogorod98.dualscreencustomizer.screeninfo;

import java.util.function.Consumer;

public interface IScreenInfoProvider {

	void onUpdate(Consumer<ScreenInfoRegistry> registryConsumer);
}
