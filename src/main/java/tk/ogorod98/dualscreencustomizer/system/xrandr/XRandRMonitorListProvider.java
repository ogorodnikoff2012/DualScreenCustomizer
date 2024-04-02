/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.system.xrandr;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import tk.ogorod98.dualscreencustomizer.system.ExternalCall;

public class XRandRMonitorListProvider {
  public CompletableFuture<Map<String, Rectangle>> listActiveMonitors() throws IOException {
    return ExternalCall.runCmd("xrandr --listactivemonitors")
        .thenApply(XRandRParser::parseListMonitors);
  }
}
