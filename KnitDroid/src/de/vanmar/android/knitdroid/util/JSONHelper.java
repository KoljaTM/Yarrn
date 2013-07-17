package de.vanmar.android.knitdroid.util;

import org.json.JSONObject;

public final class JSONHelper {

	public static String optString(final JSONObject json, final String property) {
		if (json.isNull(property)) {
			return "";
		}
		return json.optString(property);
	}

	public static int optInt(final JSONObject json, final String property) {
		if (json.isNull(property)) {
			return 0;
		}
		return json.optInt(property);
	}
}
