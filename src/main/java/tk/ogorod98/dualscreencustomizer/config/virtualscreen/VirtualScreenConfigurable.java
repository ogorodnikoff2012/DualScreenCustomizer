package tk.ogorod98.dualscreencustomizer.config.virtualscreen;

import com.intellij.openapi.options.Configurable;
import javax.swing.JComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.Nullable;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.ui.VirtualScreenComponent;

public class VirtualScreenConfigurable implements Configurable {

	private VirtualScreenComponent virtualScreenComponent;

	@Nls(capitalization = Capitalization.Title)
	@Override
	public String getDisplayName() {
		return "Virtual Screens";
	}

	@Override
	public @Nullable JComponent createComponent() {
		virtualScreenComponent = new VirtualScreenComponent();
		return virtualScreenComponent.getPanel();
	}

	@Override
	public boolean isModified() {
		return virtualScreenComponent.isModified();
	}

	@Override
	public void apply() {
		// component -> state
		VirtualScreenState.getInstance().loadState(virtualScreenComponent.dumpModel());
		virtualScreenComponent.resetModify();
	}

	@Override
	public void reset() {
		// state -> component
		virtualScreenComponent.applyModel(VirtualScreenState.getInstance());
	}

	@Override
	public void disposeUIResources() {
		virtualScreenComponent = null;
	}
}
