/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.system.x11event;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;

public interface XRandr extends Library {

  XRandr INSTANCE = Native.load("Xrandr", XRandr.class);

  void XRRSelectInput(X11.Display display, X11.Window window, int mask);

  void XRRQueryExtension(Display display, int[] eventBasePtr, int[] errorBasePtr);
}
