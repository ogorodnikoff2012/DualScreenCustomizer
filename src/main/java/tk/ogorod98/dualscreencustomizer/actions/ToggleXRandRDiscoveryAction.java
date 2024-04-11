package tk.ogorod98.dualscreencustomizer.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareToggleAction;
import org.jetbrains.annotations.NotNull;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState;

public class ToggleXRandRDiscoveryAction extends DumbAwareToggleAction {

  @Override
  public boolean isSelected(@NotNull final AnActionEvent anActionEvent) {
    return VirtualScreenState.getInstance().useXRandR;
  }

  @Override
  public void setSelected(@NotNull final AnActionEvent anActionEvent, final boolean b) {
    VirtualScreenState.getInstance().setUseXRandR(b);
  }

  @Override
  public void update(@NotNull final AnActionEvent e) {
    super.update(e);

    e.getPresentation().setText(isSelected(e) ? "Enabled" : "Enable");
  }

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }
}
