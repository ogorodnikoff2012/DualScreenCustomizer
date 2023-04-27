package tk.ogorod98.dualscreencustomizer.system.xrandr;

import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class XRandRParser {

	private final List<String> lines;
	private final List<XRandRScreenInfo> screenInfo = new ArrayList<>();

	private static final Pattern GEOMETRY_PATTERN = Pattern.compile(
			"^(?<width>\\d*)(/\\d*)?x(?<height>\\d*)(/\\d*)?\\+(?<x>\\d*)\\+(?<y>\\d*)$"
	);

	public XRandRParser(List<String> lines) {
		this.lines = lines;
	}

	public static List<XRandRScreenInfo> parse(List<String> lines) {
		return new XRandRParser(lines).parse();
	}

	public List<XRandRScreenInfo> parse() {
		screenInfo.clear();
		List<List<String>> screens = splitInScreens();
		for (List<String> rawScreen : screens) {
			screenInfo.add(parseScreen(rawScreen));
		}
		return screenInfo;
	}

	private List<List<String>> splitInScreens() {
		List<List<String>> screens = new ArrayList<>();

		for (String line : lines) {
			if (line.startsWith("Screen")) {
				continue;
			}

			if (!Character.isWhitespace(line.charAt(0))) {
				screens.add(new ArrayList<>());
			}

			screens.get(screens.size() - 1).add(line);
		}

		return screens;
	}

	private XRandRScreenInfo parseScreen(List<String> rawScreen) {
		XRandRScreenInfo result = new XRandRScreenInfo();
		List<String[]> modes = result.getModes();

		Iterator<String> iter = rawScreen.iterator();
		String currentLine = iter.hasNext() ? iter.next() : null;

		while (currentLine != null) {
			if (currentLine.startsWith("\t")) {
				int colonPos = currentLine.indexOf(':');
				String key = currentLine.substring(1, colonPos);
				String valueFirstLine = currentLine.substring(colonPos + 1).strip();

				List<String> value = new ArrayList<>();
				value.add(valueFirstLine);

				currentLine = iter.hasNext() ? iter.next() : null;
				while (currentLine != null && currentLine.startsWith("\t") && Character.isWhitespace(
						currentLine.charAt(1))) {
					value.add(currentLine.substring(1));
					currentLine = iter.hasNext() ? iter.next() : null;
				}

				if ("EDID".equals(key)) {
					String edidHex = value.stream().map(String::strip).collect(Collectors.joining(""));
					result.setEdid(parseHexDump(edidHex));
				} else {
					result.addOption(key, value);
				}
			} else if (currentLine.startsWith("  ")) {
				List<String> mode = new ArrayList<>();
				mode.add(currentLine.strip());

				currentLine = iter.hasNext() ? iter.next() : null;
				while (currentLine != null && currentLine.startsWith("  ") && Character.isWhitespace(
						currentLine.charAt(2))) {
					mode.add(currentLine.substring(2));
					currentLine = iter.hasNext() ? iter.next() : null;
				}

				modes.add(mode.toArray(new String[0]));
			} else {
				String[] tokens = currentLine.split("\\s+");

				String port = tokens[0];
				String status = tokens[1];
				boolean connected = "connected".equals(status);
				boolean primary;
				String geometry;

				if (connected) {
					primary = "primary".equals(tokens[2]);
					geometry = primary ? tokens[3] : tokens[2];
				} else {
					primary = false;
					geometry = "";
				}

				result.setPort(port);
				result.setConnected(connected);
				result.setPrimary(primary);
				result.setGeometry(parseGeometry(geometry));

				currentLine = iter.hasNext() ? iter.next() : null;
			}
		}

		return result;
	}

	private byte[] parseHexDump(String hexDump) {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		hexDump.codePoints().forEach(new IntConsumer() {
			private byte currentByte = 0;
			private int charsLeft = 2;

			@Override
			public void accept(int ch) {
				byte digit = -1;
				if (ch >= '0' && ch <= '9') {
					digit = (byte) (ch - '0');
				} else if (ch >= 'A' && ch <= 'F') {
					digit = (byte) (ch - 'A' + 10);
				} else if (ch >= 'a' && ch <= 'f') {
					digit = (byte) (ch - 'a' + 10);
				}

				if (digit >= 0) {
					currentByte <<= 4;
					currentByte |= digit;
					--charsLeft;
					if (charsLeft == 0) {
						out.write(currentByte);
						currentByte = 0;
						charsLeft = 2;
					}
				}
			}
		});

		return out.toByteArray();
	}

	private static Rectangle parseGeometry(String geometry) {
		Matcher m = GEOMETRY_PATTERN.matcher(geometry);
		if (m.matches()) {
			int width = Integer.parseInt(m.group("width"));
			int height = Integer.parseInt(m.group("height"));
			int x = Integer.parseInt(m.group("x"));
			int y = Integer.parseInt(m.group("y"));
			return new Rectangle(x, y, width, height);
		} else {
			return new Rectangle(0, 0, -1, -1);
		}
	}

	public static Map<String, Rectangle> parseListMonitors(final List<String> lines) {
		final Map<String, Rectangle> result = new HashMap<>();
		for (final String line : lines) {
			if (!Character.isWhitespace(line.charAt(0))) {
				continue;
			}

			final String[] tokens = line.split("\\s+");
			if (tokens.length != 5) {
				continue;
			}

			final String port = tokens[4];
			final String geometry = tokens[3];

			result.put(port, parseGeometry(geometry));
		}
		return result;
	}
}
