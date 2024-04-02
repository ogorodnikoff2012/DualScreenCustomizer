/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.config.ui;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import tk.ogorod98.dualscreencustomizer.config.ScreenConfig;
import tk.ogorod98.dualscreencustomizer.util.UiUtils;

public class LineComponent {

  private final JPanel mainPanel;
  private final JBTextField fieldName = new JBTextField();
  private final JBTextField fieldValue = new JBTextField();

  private final JButton removeButton = new JButton();
  private final ScreenSettingsComponent parent;

  public LineComponent(ScreenSettingsComponent parent, Field field, String value) {
    this.parent = parent;
    fieldName.setText(field.getName());
    fieldName.setEditable(false);

    if (value != null) {
      fieldValue.setText(value);
    }

    fieldValue.setInputVerifier(ScreenConfig.getVerifier(field));
    fieldValue.addKeyListener(
        new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
            verifyAndChangeStyle();
          }

          @Override
          public void keyPressed(KeyEvent e) {
            verifyAndChangeStyle();
          }

          @Override
          public void keyReleased(KeyEvent e) {
            verifyAndChangeStyle();
          }

          private void verifyAndChangeStyle() {
            onModify();
            boolean isValid = fieldValue.getInputVerifier().verify(fieldValue);
            fieldValue.setForeground(isValid ? JBColor.foreground() : JBColor.RED);
          }
        });

    mainPanel =
        FormBuilder.createFormBuilder()
            .addComponent(
                UiUtils.buildHorizontalBox(
                    new JBLabel("Field name"),
                    fieldName,
                    new JBLabel("Field value"),
                    fieldValue,
                    removeButton))
            .getPanel();
  }

  private void onModify() {
    parent.onModify();
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public String getFieldName() {
    return fieldName.getText();
  }

  public String getFieldValue() {
    return fieldValue.getText();
  }

  public void setFieldValue(String value) {
    fieldValue.setText(value);
  }

  public void setRemoveAction(Action action) {
    removeButton.setAction(action);
  }
}
