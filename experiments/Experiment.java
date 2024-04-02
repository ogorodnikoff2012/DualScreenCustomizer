/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.system.x11event;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import java.io.IOException;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRRCrtcChangeNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRREvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRRNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRROutputChangeNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRROutputPropertyNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRRProviderChangeNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRRProviderPropertyNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRRResourceChangeNotifyEvent;
import tk.ogorod98.dualscreencustomizer.system.x11event.XRandr.XRRScreenChangeNotifyEvent;

public class Experiment {

  private static final X11 x11 = X11.INSTANCE;
  private static final XRandr X_RANDR = XRandr.INSTANCE;

  public static void main(String[] args) {
    X11.Display display = x11.XOpenDisplay(null);
    if (display == null) {
      System.err.println("Cannot open display");
      System.exit(1);
    }

    final int rrEventBase, rrErrorBase;
    {
      int[] rrEventBaseBuffer = new int[1];
      int[] rrErrorBaseBuffer = new int[1];
      X_RANDR.XRRQueryExtension(display, rrEventBaseBuffer, rrErrorBaseBuffer);
      rrEventBase = rrEventBaseBuffer[0];
      rrErrorBase = rrErrorBaseBuffer[0];
    }

    X11.Window root = x11.XDefaultRootWindow(display);
    X_RANDR.XRRSelectInput(display, root, 1);

    while (true) {
      XRREvent event = new XRREvent();
      x11.XNextEvent(display, event);

      int eventCode = event.type - rrEventBase;
      switch (eventCode) {
        case 0:
          processEvent(event.getTypedValue(XRRScreenChangeNotifyEvent.class));
          break;
        case 1:
          {
            int subcode = ((XRRNotifyEvent) event.getTypedValue(XRRNotifyEvent.class)).subtype;
            switch (subcode) {
              case 0:
                processEvent(event.getTypedValue(XRRCrtcChangeNotifyEvent.class));
                break;
              case 1:
                processEvent(event.getTypedValue(XRROutputChangeNotifyEvent.class));
                break;
              case 2:
                processEvent(event.getTypedValue(XRROutputPropertyNotifyEvent.class));
                break;
              case 3:
                processEvent(event.getTypedValue(XRRProviderChangeNotifyEvent.class));
                break;
              case 4:
                processEvent(event.getTypedValue(XRRProviderPropertyNotifyEvent.class));
                break;
              case 5:
                processEvent(event.getTypedValue(XRRResourceChangeNotifyEvent.class));
                break;
              default:
                throw new RuntimeException("Unknown event code: " + eventCode + "." + subcode);
            }
            break;
          }
        default:
          throw new RuntimeException("Unknown event code: " + eventCode);
      }
    }
  }

  private static void processEvent(Object event) {
    try {
      System.out.println(
          event.getClass().getSimpleName()
              + "\n"
              + new ObjectMapper()
                  .enable(SerializationFeature.INDENT_OUTPUT)
                  .registerModule(
                      new SimpleModule("JNA")
                          .addSerializer(
                              Pointer.class,
                              new StdSerializer<>(Pointer.class) {
                                @Override
                                public void serialize(
                                    final Pointer pointer,
                                    final JsonGenerator jsonGenerator,
                                    final SerializerProvider serializerProvider)
                                    throws IOException {
                                  jsonGenerator.writeString(pointer.toString());
                                }
                              }))
                  .writeValueAsString(event));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
