/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.config.virtualscreen;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState",
    storages = @Storage("DualScreenCustomizer.VirtualScreen.xml"))
public class VirtualScreenState implements PersistentStateComponent<VirtualScreenState> {

  public static class VirtualScreenInfo {
    public int x;
    public int y;
    public int width;
    public int height;

    public static Builder builder() {
      return new Builder();
    }

    public static class Builder {
      private int x;
      private int y;
      private int width;
      private int height;

      private Builder() {}

      public Builder withX(int x) {
        this.x = x;
        return this;
      }

      public Builder withY(int y) {
        this.y = y;
        return this;
      }

      public Builder withWidth(int width) {
        this.width = width;
        return this;
      }

      public Builder withHeight(int height) {
        this.height = height;
        return this;
      }

      public VirtualScreenInfo build() {
        VirtualScreenInfo result = new VirtualScreenInfo();
        result.x = x;
        result.y = y;
        result.width = width;
        result.height = height;
        return result;
      }
    }
  }

  public Map<String, VirtualScreenInfo> virtualScreens = new HashMap<>();
  public boolean useXRandR = true;

  @Transient private final Set<ActionListener> updateListeners = ConcurrentHashMap.newKeySet();

  public static VirtualScreenState getInstance() {
    return ApplicationManager.getApplication().getService(VirtualScreenState.class);
  }

  @Override
  public @Nullable VirtualScreenState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull VirtualScreenState state) {
    XmlSerializerUtil.copyBean(state, this);
    EventQueue.invokeLater(this::sendUpdateEvent);
  }

  private void sendUpdateEvent() {
    for (ActionListener listener : updateListeners) {
      ActionEvent evt = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
      listener.actionPerformed(evt);
    }
  }

  public void addUpdateListener(ActionListener listener) {
    updateListeners.add(listener);
  }

  public void removeUpdateListener(ActionListener listener) {
    updateListeners.remove(listener);
  }

  @Override
  public String toString() {
    return "VirtualScreenState{" + "virtualScreens=" + virtualScreens + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VirtualScreenState that = (VirtualScreenState) o;
    return virtualScreens.equals(that.virtualScreens);
  }

  @Override
  public int hashCode() {
    return Objects.hash(virtualScreens);
  }
}
