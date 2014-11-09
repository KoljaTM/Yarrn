package de.vanmar.android.yarrn.sentry;

import org.acra.ACRA;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

abstract public class BaseObject {

    protected JSONObject jsonObject = new JSONObject();

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    protected void put(String key, String value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            ACRA.log.w(getTag(), "Failed to set value on JSON Object", e);
        }
    }

    protected void put(String key, Object object) {
        try {
            jsonObject.put(key, object);
        } catch (JSONException e) {
            ACRA.log.w(getTag(), "Failed to set value on JSON Object", e);
        }
    }

    protected void put(String key, Integer integer) {
        try {
            jsonObject.put(key, integer);
        } catch (JSONException e) {
            ACRA.log.w(getTag(), "Failed to set value on JSON Object", e);
        }
    }

    protected void put(String key, Boolean bool) {
        try {
            jsonObject.put(key, bool);
        } catch (JSONException e) {
            ACRA.log.w(getTag(), "Failed to set value on JSON Object", e);
        }
    }

    protected String getString(String key) {
        return getString(key, null);
    }

    protected String getString(String key, String defaultValue) {
        try {
            return jsonObject.getString(key);
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    protected JSONObject getMappedObject(Map<String, ?> map) {
        JSONObject mappedObject = new JSONObject();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                mappedObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                ACRA.log.w(getTag(), String.format("Failed to set value %s for tag %s", entry.getValue(), entry.getKey()), e);
            } catch (NoSuchMethodError e) {
                ACRA.log.e(getTag(), String.format("There is no method to add value for type %s", entry.getValue().getClass().getSimpleName()));
            }
        }

        return mappedObject;
    }

    abstract public String getTag();
}