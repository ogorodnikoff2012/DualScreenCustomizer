package tk.ogorod98.dualscreencustomizer.config;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;
import tk.ogorod98.dualscreencustomizer.config.ui.AppSettingsComponent;

public class AppSettingsConfigurable implements Configurable {

	private AppSettingsComponent settingsComponent;

	@Nls(capitalization = Capitalization.Title)
	@Override
	public String getDisplayName() {
		return "DualScreenCustomizer";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		settingsComponent = new AppSettingsComponent();
		return settingsComponent.getPanel();
	}

	@Override
	public boolean isModified() {
		return settingsComponent.isModified();
	}

	@Override
	public void apply() {
		// component -> state
		AppSettingsState.getInstance().loadState(settingsComponent.dumpModel());
		settingsComponent.resetModify();
	}

	@Override
	public void reset() {
		// state -> component
		settingsComponent.applyModel(AppSettingsState.getInstance());
	}

	@Override
	public void disposeUIResources() {
		settingsComponent = null;
	}
}
