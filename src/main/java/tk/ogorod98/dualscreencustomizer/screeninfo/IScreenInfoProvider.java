/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.screeninfo;

import java.util.function.Consumer;

public interface IScreenInfoProvider {

  void onUpdate(Consumer<ScreenInfoRegistry> registryConsumer);
}
