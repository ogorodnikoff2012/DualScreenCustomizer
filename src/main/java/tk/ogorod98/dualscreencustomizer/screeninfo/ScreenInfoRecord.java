/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.screeninfo;

import java.awt.Rectangle;
import java.util.Objects;

public class ScreenInfoRecord {
  public final ScreenDescriptor descriptor;
  public final Rectangle geometry;
  public final int priority;

  public ScreenInfoRecord(ScreenDescriptor descriptor, Rectangle geometry, int priority) {
    this.descriptor = descriptor;
    this.geometry = geometry;
    this.priority = priority;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScreenInfoRecord that = (ScreenInfoRecord) o;
    return Objects.equals(descriptor, that.descriptor) && Objects.equals(geometry, that.geometry);
  }

  @Override
  public int hashCode() {
    return Objects.hash(descriptor, geometry);
  }

  @Override
  public String toString() {
    return "ScreenInfoRecord{"
        + "descriptor="
        + descriptor
        + ", geometry="
        + geometry
        + ", priority="
        + priority
        + '}';
  }
}
