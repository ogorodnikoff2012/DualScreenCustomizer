package tk.ogorod98.dualscreencustomizer.config.ui;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.FormBuilder;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import tk.ogorod98.dualscreencustomizer.config.ScreenConfig;
import tk.ogorod98.dualscreencustomizer.util.UiUtils;

public class ScreenSettingsComponent {

	private final AppSettingsComponent parent;
	private final JPanel mainPanel;

	private final JBTextField identifier = new JBTextField();
	private final JBTextField vendorName = new JBTextField();
	private final JBTextField modelName = new JBTextField();

	private final JButton removeButton = new JButton();
	private final JBOptionButton addLineButton;

	private final List<LineComponent> lines = new ArrayList<>();

	private final Map<String, LineComponent> fieldNameToLine = new HashMap<>();
	private final VerticalBox linesBox = new VerticalBox();

	public ScreenSettingsComponent(AppSettingsComponent parent) {
		this.parent = parent;

		addLineButton = new JBOptionButton(
				new AbstractAction("Add Line") {
					@Override
					public void actionPerformed(ActionEvent e) {
						addLineButton.showPopup(null, true);
					}
				}, null
		);

		updateLineOptions();
		addModifyListener(identifier);
		addModifyListener(vendorName);
		addModifyListener(modelName);

		mainPanel = FormBuilder.createFormBuilder()
				.addComponent(UiUtils.buildHorizontalBox(
						new JBLabel("Identifier"), identifier,
						new JBLabel("Vendor name"), vendorName,
						new JBLabel("Model name"), modelName,
						addLineButton, removeButton
				))
				.addComponent(linesBox)
				.addSeparator()
				.getPanel();
	}

	private void addModifyListener(JBTextField textField) {
		textField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				onModify();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				onModify();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				onModify();
			}
		});
	}

	protected void onModify() {
		parent.onModify();
	}

	private Action[] buildLineOptions() {
		List<Action> actions = new ArrayList<>();
		Collection<Field> availableOptions = ScreenConfig.getFields().values();

		for (Field option : availableOptions) {
			if (fieldNameToLine.containsKey(option.getName())) {
				continue;
			}

			actions.add(new NewLineAction(option));
		}

		return actions.toArray(new Action[0]);
	}

	public String getIdentifier() {
		return identifier.getText();
	}

	public void setIdentifier(String identifier) {
		this.identifier.setText(identifier);
	}

	public String getVendorName() {
		return vendorName.getText();
	}

	public void setVendorName(String vendorName) {
		this.vendorName.setText(vendorName);
	}

	public String getModelName() {
		return modelName.getText();
	}

	public void setModelName(String modelName) {
		this.modelName.setText(modelName);
	}

	public void setRemoveAction(Action action) {
		removeButton.setAction(action);
	}
	public JPanel getPanel() {
		return mainPanel;
	}

	public void applyModel(ScreenConfig model) {
		for (Field f : ScreenConfig.getFields().values()) {
			try {
				Object value = f.get(model);
				if (value != null) {
					addLine(f, Objects.toString(value));
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public ScreenConfig dumpModel() {
		ScreenConfig config = new ScreenConfig();

		for (LineComponent line : lines) {
			String fieldName = line.getFieldName();
			String fieldValue = line.getFieldValue();
			if (fieldValue == null || fieldValue.length() == 0) {
				continue;
			}

			Field field = ScreenConfig.getFields().get(fieldName);
			try {
				field.set(config, ScreenConfig.getParser(field).apply(fieldValue));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return config;
	}

	private class NewLineAction extends AbstractAction {

		private final Field field;
		public NewLineAction(Field field) {
			super(field.getName());
			this.field = field;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			onModify();
			addLine(field, null);
		}
	}

	public void addLine(Field field, String value) {
		LineComponent component = new LineComponent(this, field, value);
		lines.add(component);
		fieldNameToLine.put(field.getName(), component);
		component.setRemoveAction(new RemoveLineAction(component));
		linesBox.add(component.getPanel());
		mainPanel.validate();
		updateLineOptions();
	}

	public void removeLine(LineComponent component) {
		linesBox.remove(component.getPanel());
		lines.remove(component);
		fieldNameToLine.remove(component.getFieldName());
		mainPanel.validate();
		updateLineOptions();
	}

	private void updateLineOptions() {
		Action[] lineOptions = buildLineOptions();
		addLineButton.setOptions(lineOptions);
		addLineButton.setEnabled(lineOptions.length > 0);
	}

	private class RemoveLineAction extends AbstractAction {

		private final LineComponent component;
		public RemoveLineAction(LineComponent component) {
			super("Remove line");
			this.component = component;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			onModify();
			removeLine(component);
		}
	}
}
