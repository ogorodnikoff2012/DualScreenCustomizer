/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.meta;

import com.intellij.ui.IconManager;
import javax.swing.Icon;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class Icons {
  public static final @NotNull Icon DUAL_SCREEN_CUSTOMIZER =
      load("/icons/dualscreencustomizer.svg");

  private static @NotNull Icon load(@NotNull @NonNls String path) {
    return IconManager.getInstance().getIcon(path, Icons.class.getClassLoader());
  }
}
