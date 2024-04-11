/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.statusbar;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts.ConfigurableName;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class StatusBarFactory implements StatusBarWidgetFactory {

  public static final String STATUS_BAR_ID = "DualScreenCustomizerStatusBar";
  public static final String DISPLAY_NAME = "DualScreenCustomizer";

  @Override
  public @NotNull @NonNls String getId() {
    return STATUS_BAR_ID;
  }

  @Override
  public @NotNull @ConfigurableName String getDisplayName() {
    return DISPLAY_NAME;
  }

  @Override
  public @NotNull StatusBarWidget createWidget(@NotNull final Project project) {
    return new DualScreenCustomizerStatusBarWidget(project);
  }
}
