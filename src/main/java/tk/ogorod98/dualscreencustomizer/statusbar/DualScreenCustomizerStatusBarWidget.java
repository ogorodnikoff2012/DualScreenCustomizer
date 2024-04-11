package tk.ogorod98.dualscreencustomizer.statusbar;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidget.IconPresentation;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Consumer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.SwingConstants;
import org.jetbrains.annotations.NotNull;
import tk.ogorod98.dualscreencustomizer.meta.Icons;
import tk.ogorod98.dualscreencustomizer.meta.Version;

public class DualScreenCustomizerStatusBarWidget implements StatusBarWidget, IconPresentation {
  public DualScreenCustomizerStatusBarWidget(final @NotNull Project project) {}

  @NotNull
  @Override
  public String ID() {
    return StatusBarFactory.STATUS_BAR_ID;
  }


  @NotNull
  @Override
  public Icon getIcon() {
    return Icons.DUAL_SCREEN_CUSTOMIZER;
  }

  @NotNull
  @Override
  public String getTooltipText() {
    return StatusBarFactory.DISPLAY_NAME;
  }

  @NotNull
  @Override
  public WidgetPresentation getPresentation() {
    return this;
  }

  @NotNull
  @Override
  public Consumer<MouseEvent> getClickConsumer() {
    return event -> {
      final Component component = event.getComponent();
      final ListPopup popup = getPopup(DataManager.getInstance().getDataContext(component));
      final Dimension dimension = popup.getContent().getPreferredSize();

      final Point at = new Point(0, -dimension.height);
      popup.show(new RelativePoint(component, at));
    };
  }

  @NotNull
  private static ListPopup getPopup(final DataContext dataContext) {
    final ActionGroup actions = getActions();
    final ListPopup popup = JBPopupFactory.getInstance().createActionGroupPopup(
        StatusBarFactory.DISPLAY_NAME,
        actions,
        dataContext,
        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
        true,
            ActionPlaces.POPUP
    );
    popup.setAdText(Version.getVersion(), SwingConstants.CENTER);
    return popup;
  }

  @NotNull
  private static ActionGroup getActions() {
    final DefaultActionGroup group = new DefaultActionGroup();
    group.setPopup(true);

    group.addSeparator("XRandR Discovery");
    group.add(ActionManager.getInstance().getAction("ToggleXRandRDiscoveryAction"));

    return group;
  }
}
