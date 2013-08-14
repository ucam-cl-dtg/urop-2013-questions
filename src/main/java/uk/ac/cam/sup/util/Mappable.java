package uk.ac.cam.sup.util;

import java.util.Map;

public interface Mappable {
	public abstract Map<String,?> toMap(boolean shadow);
	public abstract Map<String,?> toMap();
}
