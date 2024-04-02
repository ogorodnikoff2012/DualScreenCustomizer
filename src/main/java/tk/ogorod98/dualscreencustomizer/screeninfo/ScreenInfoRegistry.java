/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.screeninfo;

import java.awt.Point;
import java.util.HashMap;

public class ScreenInfoRegistry extends HashMap<ScreenDescriptor, ScreenInfoRecord> {

  public ScreenDescriptor findScreen(Point point) {
    ScreenDescriptor result = null;
    int priority = 0;

    for (var screen : values()) {
      if (!screen.geometry.contains(point)) {
        continue;
      }

      if (result == null || priority < screen.priority) {
        result = screen.descriptor;
        priority = screen.priority;
      }
    }

    return result;
  }
}
