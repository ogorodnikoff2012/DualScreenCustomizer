package tk.ogorod98.dualscreencustomizer.system.xrandr;

import java.util.Arrays;
import java.util.Objects;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenDescriptor;

public class EDID {

	public static final int EDID_LENGTH = 128;

	private final String manufacturerId;
	private final String displaySerial;
	private final String displayName;

	private EDID(String manufacturerId, String displaySerial, String displayName) {
		this.manufacturerId = manufacturerId;
		this.displaySerial = Objects.requireNonNullElse(displaySerial, "");
		this.displayName = Objects.requireNonNullElse(displayName, "");
	}

	public ScreenDescriptor getScreenDescriptor() {
		return new ScreenDescriptor(displaySerial, manufacturerId, displayName);
	}

	public static class EDIDParserException extends Exception {
		public EDIDParserException(String message) {
			super(message);
		}
	}

	public static EDID parseEDID(byte[] edid) throws EDIDParserException {
		if (edid.length < EDID_LENGTH) {
			throw new EDIDParserException(String.format("Bad EDID length, expected 128, got %d", edid.length));
		}

		byte checksum = countChecksum(edid);
		if (checksum != 0) {
			throw new EDIDParserException(String.format("Bad EDID checksum, expected 0, got %d", checksum));
		}

		if (!verifyMagicHeader(edid)) {
			throw new EDIDParserException("Bad EDID magic header");
		}

		String manufacturerId = extractManufacturerId(edid);

		String displayName = null;
		String displaySerial = null;

		for (int i = 0; i < 4; ++i) {
			int descriptorLength = 18;
			int descriptorBegin = 54 + i * descriptorLength;

			if (edid[descriptorBegin] != 0) {
				continue;
			}

			byte descriptorType = edid[descriptorBegin + 3];
			if (descriptorType == (byte) 0xFF) {
				displaySerial = extractDescriptorText(edid, descriptorBegin + 5, descriptorBegin + descriptorLength);
			} else if (descriptorType == (byte) 0xFC) {
				displayName = extractDescriptorText(edid, descriptorBegin + 5, descriptorBegin + descriptorLength);
			}
		}

		return new EDID(manufacturerId, displaySerial, displayName);
	}

	private static String extractDescriptorText(byte[] edid, int begin, int end) {
		int strEnd = begin;
		while (strEnd != end && edid[strEnd] != '\n') {
			++strEnd;
		}
		return new String(Arrays.copyOfRange(edid, begin, strEnd));
	}

	private static String extractManufacturerId(byte[] edid) {
		int id = (0xFF & (int) edid[8]) << 8 | (0xFF & (int) edid[9]);

		char[] c = new char[3];
		c[0] = (char) ('A' + ((id >> 10) & 0x1F) - 1);
		c[1] = (char) ('A' + ((id >> 5 ) & 0x1F) - 1);
		c[2] = (char) ('A' + (id 				 & 0x1F) - 1);

		return new String(c);
	}

	private static boolean verifyMagicHeader(byte[] edid) {
		if (edid[0] != 0) {
			return false;
		}

		for (int i = 1; i < 7; ++i) {
			if (edid[i] != (byte) 0xFF) {
				return false;
			}
		}

		return edid[7] == 0;
	}

	private static byte countChecksum(byte[] edid) {
		byte sum = 0;
		for (int i = 0; i < EDID_LENGTH; ++i) {
			sum += edid[i];
		}
		return sum;
	}
}
