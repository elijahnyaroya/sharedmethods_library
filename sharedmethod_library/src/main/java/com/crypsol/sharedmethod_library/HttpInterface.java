package com.crypsol.sharedmethod_library;

import org.json.JSONArray;
import org.json.JSONException;

public interface HttpInterface {
    void onMyResponse (JSONArray response);
    void onMyError(JSONArray response);
    void onJSONException (JSONException e) ;
}