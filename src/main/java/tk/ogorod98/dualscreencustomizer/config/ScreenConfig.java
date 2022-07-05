package tk.ogorod98.dualscreencustomizer.config;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.impl.FontFamilyService;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.swing.InputVerifier;
import tk.ogorod98.dualscreencustomizer.util.DictionaryInputVerifier;
import tk.ogorod98.dualscreencustomizer.util.RegexInputVerifier;

public class ScreenConfig {

	public String fontName;
	public Integer fontSize;

	private static final HashMap<String, Field> SCREEN_CONFIG_FIELDS;
	private static final HashMap<String, InputVerifier> INPUT_VERIFIERS;
	private static final HashMap<String, Function<String, Object>> INPUT_PARSERS;
	private static final HashMap<String, Function<EditorColorsScheme, Object>> GETTERS;
	private static final HashMap<String, BiConsumer<EditorColorsScheme, Object>> SETTERS;

	static {
		SCREEN_CONFIG_FIELDS = new HashMap<>();
		for (Field f : ScreenConfig.class.getFields()) {
			SCREEN_CONFIG_FIELDS.put(f.getName(), f);
		}

		INPUT_VERIFIERS = new HashMap<>();

		Set<String> fontFamilies = new HashSet<>(FontFamilyService.getAvailableFamilies());
		fontFamilies.add("");
		INPUT_VERIFIERS.put("fontName", new DictionaryInputVerifier(fontFamilies));
		INPUT_VERIFIERS.put("fontSize", new RegexInputVerifier("([1-9]\\d*)?"));

		INPUT_PARSERS = new HashMap<>();
		INPUT_PARSERS.put("fontName", x -> x);
		INPUT_PARSERS.put("fontSize", Integer::parseInt);

		GETTERS = new HashMap<>();
		GETTERS.put("fontName", EditorColorsScheme::getEditorFontName);
		GETTERS.put("fontSize", EditorColorsScheme::getEditorFontSize);

		SETTERS = new HashMap<>();
		SETTERS.put("fontName", (scheme, name) -> scheme.setEditorFontName((String) name));
		SETTERS.put("fontSize", (scheme, size) -> scheme.setEditorFontSize((Integer) size));

		verifyTables();
	}

	private static void verifyTables() {
		verifyTables(SCREEN_CONFIG_FIELDS, INPUT_VERIFIERS);
		verifyTables(SCREEN_CONFIG_FIELDS, INPUT_PARSERS);
		verifyTables(SCREEN_CONFIG_FIELDS, GETTERS);
		verifyTables(SCREEN_CONFIG_FIELDS, SETTERS);
	}

	private static void verifyTables(Map<String, ?> reference, Map<String, ?> testing) {
		Set<String> referenceKeys = reference.keySet();
		Set<String> testingKeys = testing.keySet();
		if (!referenceKeys.equals(testingKeys)) {
			StringBuilder sb = new StringBuilder();
			sb.append("Reference table and testing table have different key sets.");

			String delimiter = "\nMissing keys: ";
			for (String key : referenceKeys) {
				if (testingKeys.contains(key)) {
					continue;
				}

				sb.append(delimiter).append(key);
				delimiter = ", ";
			}

			delimiter = "\nExtra keys: ";
			for (String key : testingKeys) {
				if (referenceKeys.contains(key)) {
					continue;
				}

				sb.append(delimiter).append(key);
				delimiter = ", ";
			}

			throw new IllegalStateException(sb.toString());
		}
	}

	public static Map<String, Field> getFields() {
		return SCREEN_CONFIG_FIELDS;
	}

	public ScreenConfig(String fontName, Integer fontSize) {
		this.fontName = fontName;
		this.fontSize = fontSize;
	}

	public ScreenConfig() {
		this(null, null);
	}

	public static InputVerifier getVerifier(Field field) {
		return INPUT_VERIFIERS.get(field.getName());
	}

	public static Function<String, Object> getParser(Field field) {
		return INPUT_PARSERS.get(field.getName());
	}

	public static void updateScheme(EditorColorsScheme defaultScheme, EditorColorsScheme currentScheme,
			ScreenConfig screenConfig) {
		for (Field f : getFields().values()) {
			try {
				Object value = screenConfig == null ? null : f.get(screenConfig);
				if (value == null) {
					value = GETTERS.get(f.getName()).apply(defaultScheme);
				}

				SETTERS.get(f.getName()).accept(currentScheme, value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String toString() {
		return "ScreenConfig{" + "fontName='" + fontName + '\'' + ", fontSize=" + fontSize + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ScreenConfig that = (ScreenConfig) o;
		return Objects.equals(fontName, that.fontName) && Objects.equals(fontSize, that.fontSize);
	}

	@Override
	public int hashCode() {
		return Objects.hash(fontName, fontSize);
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
	}

	public static class Builder {

		private String fontName = null;
		private Integer fontSize = null;

		public Builder withFontName(String fontName) {
			this.fontName = fontName;
			return this;
		}

		public Builder withFontSize(int fontSize) {
			this.fontSize = fontSize;
			return this;
		}

		public ScreenConfig build() {
			return new ScreenConfig(fontName, fontSize);
		}
	}

	public static Builder builder() {
		return new Builder();
	}
}
