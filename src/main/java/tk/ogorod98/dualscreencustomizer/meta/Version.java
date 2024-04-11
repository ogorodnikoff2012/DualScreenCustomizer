package tk.ogorod98.dualscreencustomizer.meta;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

public class Version {
  private Version() {}

  public static @NotNull String getVersion() {
    final IdeaPluginDescriptor plugin = PluginManagerCore.getPlugin(getPluginId());
    return plugin != null ? plugin.getVersion() : "SNAPSHOT";
  }

    public static @NotNull PluginId getPluginId() {
        return PluginId.getId("tk.ogorod98.dualscreencustomizer");
    }
}
