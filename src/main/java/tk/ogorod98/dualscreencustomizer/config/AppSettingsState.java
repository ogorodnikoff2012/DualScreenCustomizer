package tk.ogorod98.dualscreencustomizer.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;

@State(
		name = "tk.ogorod98.dualscreencustomizer.config.AppSettingsState",
		storages = @Storage("DualScreenCustomizer.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

	public Map<ScreenDescriptor, ScreenConfig> screenToConfig = new HashMap<>();

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
	}

	@Override
	public String toString() {
		return "AppSettingsState{" +
				"screenToConfig=" + screenToConfig +
				'}';
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
