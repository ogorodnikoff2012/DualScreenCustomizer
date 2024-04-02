/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.config;

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
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;

@State(
    name = "tk.ogorod98.dualscreencustomizer.config.AppSettingsState",
    storages = @Storage("DualScreenCustomizer.xml"))
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

  public Map<ScreenDescriptor, ScreenConfig> screenToConfig = new HashMap<>();

  @Transient private final Set<ActionListener> updateListeners = ConcurrentHashMap.newKeySet();

  public static AppSettingsState getInstance() {
    return ApplicationManager.getApplication().getService(AppSettingsState.class);
  }

  @Override
  public @Nullable AppSettingsState getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull AppSettingsState state) {
    XmlSerializerUtil.copyBean(state, this);
    EventQueue.invokeLater(this::sendUpdateEvent);
  }

  private void sendUpdateEvent() {
    for (ActionListener listener : updateListeners) {
      ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
      listener.actionPerformed(event);
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
    return "AppSettingsState{" + "screenToConfig=" + screenToConfig + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AppSettingsState state = (AppSettingsState) o;
    return Objects.equals(screenToConfig, state.screenToConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(screenToConfig);
  }
}
