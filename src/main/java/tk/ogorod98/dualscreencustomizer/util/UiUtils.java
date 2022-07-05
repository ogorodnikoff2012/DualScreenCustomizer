package tk.ogorod98.dualscreencustomizer.util;

import java.awt.BorderLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class UiUtils {
	public static JPanel buildHorizontalBox(JComponent... components) {
		Box box = new Box(BoxLayout.X_AXIS);
		for (JComponent component : components) {
			component.setAlignmentY(0.5f);
			box.add(component);
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(box, BorderLayout.CENTER);
		return panel;
	}
}
