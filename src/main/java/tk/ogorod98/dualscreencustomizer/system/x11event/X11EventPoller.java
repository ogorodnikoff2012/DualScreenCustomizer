/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.system.x11event;

import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.XEvent;

public class X11EventPoller implements AutoCloseable {
  private final Thread pollerThread;
  private final Runnable onEventCallback;

  public X11EventPoller(final Runnable onEventCallback) {
    this.pollerThread = new Thread(this::run, X11EventPoller.class.getSimpleName());
    this.onEventCallback = onEventCallback;
    this.pollerThread.start();
  }

  @Override
  public void close() throws Exception {
    this.pollerThread.interrupt();
    this.pollerThread.join();
  }

  private void run() {
    Display display = X11.INSTANCE.XOpenDisplay(null);
    XEvent event = new XEvent();
    X11.Window root = X11.INSTANCE.XDefaultRootWindow(display);
    XRandr.INSTANCE.XRRSelectInput(display, root, 1);

    final int rrEventBase;
    {
      int[] rrEventBaseBuffer = new int[1];
      int[] rrErrorBaseBuffer = new int[1];
      XRandr.INSTANCE.XRRQueryExtension(display, rrEventBaseBuffer, rrErrorBaseBuffer);
      rrEventBase = rrEventBaseBuffer[0];
    }

    try {
      while (true) {
        while (X11.INSTANCE.XPending(display) == 0) {
          Thread.sleep(100);
        }
        X11.INSTANCE.XNextEvent(display, event);
        if (event.type != rrEventBase) {
          continue;
        }
        notifyX11Event();
      }
    } catch (InterruptedException e) {
      // Do nothing, quietly shutdown
    } finally {
      event.clear();
      XRandr.INSTANCE.XRRSelectInput(display, root, 0);
      X11.INSTANCE.XCloseDisplay(display);
    }
  }

  private void notifyX11Event() {
    this.onEventCallback.run();
  }
}
