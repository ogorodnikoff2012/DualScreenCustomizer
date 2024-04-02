/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.config.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBOptionButton;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.ui.FormBuilder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import tk.ogorod98.dualscreencustomizer.config.AppSettingsState;
import tk.ogorod98.dualscreencustomizer.config.ScreenConfig;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRegistry;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoService;
import tk.ogorod98.dualscreencustomizer.util.UiUtils;

public class AppSettingsComponent implements ActionListener {

  private final AddEntryOption ADD_EMPTY_ENTRY_OPTION = new AddEntryOption("Empty", null);

  private final JPanel mainPanel;
  private final VerticalBox entriesBox = new VerticalBox();
  private final JBOptionButton addEntryButton;
  private final JButton clearEntriesButton = new JButton();

  private boolean modifiedFlag = false;
  private final List<ScreenSettingsComponent> screenSettings = new ArrayList<>();

  public AppSettingsComponent() {
    ScreenInfoService screenInfoService =
        ApplicationManager.getApplication().getService(ScreenInfoService.class);

    addEntryButton =
        new JBOptionButton(
            new AbstractAction("Add Entry") {
              @Override
              public void actionPerformed(ActionEvent e) {
                addEntryButton.showPopup(null, true);
              }
            },
            buildAddEntryOptions(screenInfoService.getMergedRegistry()));

    clearEntriesButton.setAction(
        new AbstractAction("Remove All") {
          @Override
          public void actionPerformed(ActionEvent e) {
            onModify();
            clearScreenSettings();
          }
        });

    mainPanel =
        FormBuilder.createFormBuilder()
            .addLabeledComponent(new JBLabel("Settings per screen"), entriesBox, 1, true)
            .addComponent(UiUtils.buildHorizontalBox(addEntryButton, clearEntriesButton))
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();

    screenInfoService.addUpdateListener(this);
  }

  private void clearScreenSettings() {
    this.entriesBox.removeAll();
    this.mainPanel.validate();
    this.screenSettings.clear();
  }

  private Action[] buildAddEntryOptions(ScreenInfoRegistry registry) {
    List<Action> options = new ArrayList<>();
    if (registry != null) {
      for (ScreenDescriptor descriptor : registry.keySet()) {
        options.add(new AddEntryOption(descriptor.getDisplayName(), descriptor));
      }
    }
    options.add(ADD_EMPTY_ENTRY_OPTION);
    return options.toArray(new Action[0]);
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ScreenInfoService screenInfoService =
        ApplicationManager.getApplication().getService(ScreenInfoService.class);
    addEntryButton.setOptions(buildAddEntryOptions(screenInfoService.getMergedRegistry()));
  }

  public void applyModel(AppSettingsState model) {
    clearScreenSettings();
    for (var entry : model.screenToConfig.entrySet()) {
      ScreenSettingsComponent component = buildScreenSettingsComponent(entry.getKey());
      component.applyModel(entry.getValue());
      addScreenSettings(component);
    }
    resetModify();
  }

  public void resetModify() {
    modifiedFlag = false;
  }

  public AppSettingsState dumpModel() {
    AppSettingsState state = new AppSettingsState();
    for (ScreenSettingsComponent screen : screenSettings) {
      ScreenDescriptor descriptor = new ScreenDescriptor();

      String identifier = screen.getIdentifier();
      if (identifier != null && identifier.length() > 0) {
        descriptor.setIdentifier(identifier);
      }

      String vendorName = screen.getVendorName();
      if (vendorName != null && vendorName.length() > 0) {
        descriptor.setVendorName(vendorName);
      }

      String modelName = screen.getModelName();
      if (modelName != null && modelName.length() > 0) {
        descriptor.setModelName(modelName);
      }

      ScreenConfig config = screen.dumpModel();
      state.screenToConfig.put(descriptor, config);
    }

    return state;
  }

  public boolean isModified() {
    return modifiedFlag;
  }

  protected void onModify() {
    modifiedFlag = true;
  }

  private class AddEntryOption extends AbstractAction {

    private final ScreenDescriptor screenDescriptor;

    public AddEntryOption(String title, ScreenDescriptor screenDescriptor) {
      super(title);
      this.screenDescriptor = screenDescriptor;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      onModify();
      ScreenSettingsComponent screenSettings = buildScreenSettingsComponent(screenDescriptor);
      AppSettingsComponent.this.addScreenSettings(screenSettings);
    }
  }

  private ScreenSettingsComponent buildScreenSettingsComponent(ScreenDescriptor screenDescriptor) {
    ScreenSettingsComponent screenSettings = new ScreenSettingsComponent(this);
    if (screenDescriptor != null) {
      screenSettings.setIdentifier(screenDescriptor.getIdentifier());
      screenSettings.setVendorName(screenDescriptor.getVendorName());
      screenSettings.setModelName(screenDescriptor.getModelName());
    }
    return screenSettings;
  }

  private void addScreenSettings(ScreenSettingsComponent screenSettings) {
    this.screenSettings.add(screenSettings);
    screenSettings.setRemoveAction(new RemoveScreenAction(screenSettings));
    this.entriesBox.add(screenSettings.getPanel());
    this.mainPanel.validate();
  }

  private class RemoveScreenAction extends AbstractAction {

    private final ScreenSettingsComponent screenSettings;

    public RemoveScreenAction(ScreenSettingsComponent screenSettings) {
      super("Remove");
      this.screenSettings = screenSettings;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      onModify();
      removeScreenSettings(screenSettings);
    }
  }

  private void removeScreenSettings(ScreenSettingsComponent screenSettings) {
    this.entriesBox.remove(screenSettings.getPanel());
    this.mainPanel.validate();
    this.screenSettings.remove(screenSettings);
  }
}
