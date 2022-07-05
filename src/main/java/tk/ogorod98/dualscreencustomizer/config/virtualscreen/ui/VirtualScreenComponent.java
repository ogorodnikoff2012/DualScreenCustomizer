package tk.ogorod98.dualscreencustomizer.config.virtualscreen.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.FormBuilder;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState;
import tk.ogorod98.dualscreencustomizer.config.virtualscreen.VirtualScreenState.VirtualScreenInfo;
import tk.ogorod98.dualscreencustomizer.util.UiUtils;

public class VirtualScreenComponent {

	private final JPanel mainPanel;
	private final VerticalBox entriesBox = new VerticalBox();
	private final JButton addEntryButton;
	private final JButton clearEntriesButton;

	private final JBCheckBox useXRandR;

	private boolean modifiedFlag = false;
	private final List<EntryComponent> screens = new ArrayList<>();

	public VirtualScreenComponent() {
		addEntryButton = new JButton(new AddEntryAction());
		clearEntriesButton = new JButton(new ClearEntriesAction());
		useXRandR = new JBCheckBox("Use XRandR screen discovery");
		useXRandR.addChangeListener(e -> onModify());

		mainPanel = FormBuilder.createFormBuilder()
				.addComponent(new JBLabel(getDescriptionHtml()))
				.addComponent(entriesBox)
				.addComponent(UiUtils.buildHorizontalBox(addEntryButton, clearEntriesButton))
				.addComponent(useXRandR)
				.addComponentFillVertically(new JPanel(), 0)
				.getPanel();
	}

	@Contract(pure = true)
	private @NotNull String getDescriptionHtml() {
		return "<html>"
				+ "If XRandR screen discovery doesn't work properly, <br>"
				+ "you can define your screen layout here."
				+ "</html>";
	}

	public JPanel getPanel() {
		return mainPanel;
	}

	public boolean isModified() {
		return modifiedFlag;
	}

	public VirtualScreenState dumpModel() {
		VirtualScreenState result = new VirtualScreenState();
		Map<String, VirtualScreenState.VirtualScreenInfo> screens = result.virtualScreens;

		for (var component : this.screens) {
			String identifier = component.getIdentifier();

			VirtualScreenInfo screenInfo = new VirtualScreenInfo();
			screenInfo.x = Integer.parseInt(component.getX());
			screenInfo.y = Integer.parseInt(component.getY());
			screenInfo.width = Integer.parseInt(component.getWidth());
			screenInfo.height = Integer.parseInt(component.getHeight());

			screens.put(identifier, screenInfo);
		}

		result.useXRandR = useXRandR.isSelected();

		return result;
	}

	public void applyModel(VirtualScreenState instance) {
		clearEntries();
		for (var entry : instance.virtualScreens.entrySet()) {
			String identifier = entry.getKey();
			var screenInfo = entry.getValue();

			EntryComponent component = new EntryComponent(this);
			component.setIdentifier(identifier);
			component.setX(String.valueOf(screenInfo.x));
			component.setY(String.valueOf(screenInfo.y));
			component.setWidth(String.valueOf(screenInfo.width));
			component.setHeight(String.valueOf(screenInfo.height));

			addEntry(component);
		}
		useXRandR.setSelected(instance.useXRandR);

		resetModify();
	}

	public void onModify() {
		modifiedFlag = true;
	}

	public void resetModify() {
		modifiedFlag = false;
	}

	public void addEntry() {
		addEntry(new EntryComponent(this));
	}

	public void addEntry(EntryComponent component) {
		screens.add(component);
		component.addRemoveAction(new RemoveEntryAction(component));
		entriesBox.add(component.getPanel());
		mainPanel.validate();
	}

	public void clearEntries() {
		screens.clear();
		entriesBox.removeAll();
		mainPanel.validate();
	}

	public void removeEntry(EntryComponent component) {
		entriesBox.remove(component.getPanel());
		mainPanel.validate();
		screens.remove(component);
	}

	private class AddEntryAction extends AbstractAction {

		public AddEntryAction() {
			super("Add Entry");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			onModify();
			addEntry();
		}
	}

	private class ClearEntriesAction extends AbstractAction {

		public ClearEntriesAction() {
			super("Clear Entries");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			onModify();
			clearEntries();
		}
	}

	private class RemoveEntryAction extends AbstractAction {

		private final EntryComponent component;

		public RemoveEntryAction(EntryComponent component) {
			super("Remove");
			this.component = component;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			onModify();
			removeEntry(component);
		}
	}
}
