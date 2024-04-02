/* (C) Vladimir Ogorodnikov <https://github.com/ogorodnikoff2012>, 2024 */
package tk.ogorod98.dualscreencustomizer.config.virtualscreen.ui;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import javax.swing.Action;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JPanel;
import tk.ogorod98.dualscreencustomizer.util.RegexInputVerifier;
import tk.ogorod98.dualscreencustomizer.util.UiUtils;

public class EntryComponent {

  private final VirtualScreenComponent parent;
  private final JPanel mainPanel;

  private final JBTextField identifier = new JBTextField();
  private final JBTextField x = new JBTextField();
  private final JBTextField y = new JBTextField();
  private final JBTextField width = new JBTextField();
  private final JBTextField height = new JBTextField();

  private final JButton removeButton = new JButton();

  public EntryComponent(VirtualScreenComponent parent) {
    this.parent = parent;

    identifier.setText(buildRandomString(8));
    identifier.setInputVerifier(new RegexInputVerifier(".+"));

    InputVerifier uintVerifier = new RegexInputVerifier("\\d*");
    x.setText("0");
    x.setInputVerifier(uintVerifier);
    y.setText("0");
    y.setInputVerifier(uintVerifier);
    width.setText("1024");
    width.setInputVerifier(uintVerifier);
    height.setText("768");
    height.setInputVerifier(uintVerifier);
    addModifyListeners();

    mainPanel =
        FormBuilder.createFormBuilder()
            .addComponent(
                UiUtils.buildHorizontalBox(new JBLabel("Identifier"), identifier, removeButton))
            .addComponent(
                UiUtils.buildHorizontalBox(
                    new JBLabel("X"), x,
                    new JBLabel("Y"), y,
                    new JBLabel("Width"), width,
                    new JBLabel("Height"), height))
            .addSeparator()
            .getPanel();
  }

  private void addModifyListeners() {
    addModifyListener(identifier);
    addModifyListener(x);
    addModifyListener(y);
    addModifyListener(width);
    addModifyListener(height);
  }

  private void addModifyListener(JBTextField textField) {
    textField.addKeyListener(
        new KeyListener() {
          @Override
          public void keyTyped(KeyEvent e) {
            onModify(textField);
          }

          @Override
          public void keyPressed(KeyEvent e) {
            onModify(textField);
          }

          @Override
          public void keyReleased(KeyEvent e) {
            onModify(textField);
          }
        });
  }

  private void onModify() {
    parent.onModify();
  }

  private void onModify(JBTextField textField) {
    onModify();
    boolean isValid = textField.getInputVerifier().verify(textField);
    textField.setForeground(isValid ? JBColor.foreground() : JBColor.RED);
  }

  private static String buildRandomString(int length) {
    char[] chars = new char[length];
    Random random = new Random();

    for (int i = 0; i < length; ++i) {
      chars[i] = (char) ('A' + random.nextInt('Z' - 'A'));
    }

    return new String(chars);
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public void addRemoveAction(Action action) {
    removeButton.setAction(action);
  }

  public void setIdentifier(String value) {
    identifier.setText(value);
  }

  public String getIdentifier() {
    return identifier.getText();
  }

  public void setX(String value) {
    x.setText(value);
  }

  public String getX() {
    return x.getText();
  }

  public void setY(String value) {
    y.setText(value);
  }

  public String getY() {
    return y.getText();
  }

  public void setWidth(String value) {
    width.setText(value);
  }

  public String getWidth() {
    return width.getText();
  }

  public void setHeight(String value) {
    height.setText(value);
  }

  public String getHeight() {
    return height.getText();
  }
}
