package tk.ogorod98.dualscreencustomizer.util;

import java.util.Set;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class DictionaryInputVerifier extends InputVerifier {

	private final Set<String> dictionary;

	public DictionaryInputVerifier(Set<String> dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public boolean verify(JComponent input) {
		if (!(input instanceof JTextComponent)) {
			return false;
		}

		String text = ((JTextComponent) input).getText();
		return dictionary.contains(text);
	}
}
