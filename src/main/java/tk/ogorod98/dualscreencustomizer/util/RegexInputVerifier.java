package tk.ogorod98.dualscreencustomizer.util;

import java.util.regex.Pattern;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

public class RegexInputVerifier extends InputVerifier {

	private final Pattern pattern;

	public RegexInputVerifier(Pattern pattern) {
		this.pattern = pattern;
	}

	public RegexInputVerifier(String regex) {
		this(Pattern.compile(regex));
	}

	@Override
	public boolean verify(JComponent input) {
		if (!(input instanceof JTextComponent)) {
			return false;
		}

		String text = ((JTextComponent) input).getText();
		return pattern.asMatchPredicate().test(text);
	}
}
