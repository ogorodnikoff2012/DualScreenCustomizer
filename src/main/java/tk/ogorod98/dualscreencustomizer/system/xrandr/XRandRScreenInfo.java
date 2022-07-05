package tk.ogorod98.dualscreencustomizer.system.xrandr;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class XRandRScreenInfo {

	private final Map<String, List<String>> options = new HashMap<>();
	private boolean connected;
	private boolean primary;
	private Rectangle geometry;
	private String port;
	private byte[] edid;
	private final List<String[]> modes = new ArrayList<>();


	public Map<String, List<String>> getOptions() {
		return options;
	}

	public void addOption(String key, List<String> value) {
		options.put(key, value);
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public Rectangle getGeometry() {
		return geometry;
	}

	public void setGeometry(Rectangle geometry) {
		this.geometry = geometry;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public List<String[]> getModes() {
		return modes;
	}

	public void addMode(String[] mode) {
		modes.add(mode);
	}

	public byte[] getEDID() {
		return edid;
	}

	public void setEdid(byte[] edid) {
		this.edid = edid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		XRandRScreenInfo that = (XRandRScreenInfo) o;
		return connected == that.connected && primary == that.primary && options.equals(that.options)
				&& Objects.equals(geometry, that.geometry) && Objects.equals(port, that.port)
				&& Arrays.equals(edid, that.edid) && modes.equals(that.modes);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(options, connected, primary, geometry, port, modes);
		result = 31 * result + Arrays.hashCode(edid);
		return result;
	}

	@Override
	public String toString() {
		return "XRandRScreenInfo{" +
				"options=" + options +
				", connected=" + connected +
				", primary=" + primary +
				", geometry=" + geometry +
				", port='" + port + '\'' +
				", edid=" + Arrays.toString(edid) +
				", modes=" + modes.stream().map(Arrays::toString).collect(Collectors.toList()) +
				'}';
	}
}
