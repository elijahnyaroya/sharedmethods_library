package com.crypsol.sharedmethod_library;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crypsol.sessionmanager_library.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class GenericHttpJsonRequest {
    private RequestQueue genericHttpRequestQueue;
    static boolean  TOAST = SharedMethods.TOAST();
    static boolean  SOP = SharedMethods.SOP() ;
    // TODO 282 Experiment, check if we can use this generic ErrorHandler...
    Response.ErrorListener genericErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            String text = "";
            if (error instanceof TimeoutError) {
                //text = "RecoverAccount: The Internet has timeout errors";
                text = SharedMethods.gaStr( "Eghr001 internetTimeouterror" );
            }else if (error instanceof NoConnectionError){
                //text = "RecoverAccount: No network Connection";
                text= SharedMethods.gaStr( "Eghr002 NoInternet" );
            }else if (error instanceof AuthFailureError){
                //text = "RecoverAccount: Error Authenticating";
                text= SharedMethods.gaStr( "Eghr003 errorAuthenticating" );
            }else if (error instanceof ServerError){
                //text = "RecoverAccount: There is an error on the Server side, please try again later. The system reports this error automatically";
                text= SharedMethods.gaStr("Eghr004 serverError" );
            }else if (error instanceof NetworkError){
                //text = "RecoverAccount: Please check your internet connection";
                text= SharedMethods.gaStr( "Eghr005 noInternetconnection" );
            }else {
                //text = "RecoverAccount: Unexpected error occurred";
                text= SharedMethods.gaStr( "Eghr006 unexpectedError" );
            }
            System.out.println("GenericHttpJsonRequest.java: Volley.onErrorResponse: "+text);
            // if(TOAST)Toast.makeText(.this,text,Toast.LENGTH_LONG).show();
        }
    };

    public class MyException extends Exception {

    }

    public volatile String result;

    JSONArray response = null;
    String url = null;

    public Map<String, String> jsonObjectToMap(JSONObject request) throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        try {
            Iterator ir = request.keys();
            while (ir.hasNext()) {
                String key = (String) ir.next();
                params.put(key, request.getString(key));
                // if(SOP)System.out.println("GenericHttpJsonRequest.java : getParams() - " + key + "," + request.getString(key));
            }
            SharedMethods.printParams("GenericHttpJsonRequest.java ==> " + url, params);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public Map<String, String> jsonSqlObjectToMap(JSONArray request) throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        try {
            // Now put in the keys of the SQL request prepended with "Key:"
            Iterator ir = request.getJSONObject(0).keys();
            while (ir.hasNext()) {
                String key = (String) ir.next();
                params.put(key, "Key:"+request.getJSONObject(0).getString(key));
                // if(SOP)System.out.println("GenericHttpJsonRequest.java : getParams() - " + key + "," + request.getString(key));
            }

            // Now put in the attributes of the SQL request preprended with "Attrib:"
            ir = request.getJSONObject(1).keys();
            while (ir.hasNext()) {
                String key = (String) ir.next();
                params.put(key, "Attrib:"+request.getJSONObject(1).getString(key));
                // if(SOP)System.out.println("GenericHttpJsonRequest.java : getParams() - " + key + "," + request.getString(key));
            }
            SharedMethods.printParams("GenericHttpJsonRequest.java ==> " + url, params);

        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public void genericHttpcallBack(Context context, final String url, final JSONObject request, final HttpInterface httpInterface) {

        this.url = url;

        genericHttpRequestQueue = Volley.newRequestQueue(context);

        final StringRequest httpRequest = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // JSONArray respReturn = new JSONArray(response);
                            httpInterface.onMyResponse(new JSONArray(response));
                        } catch (JSONException e) {
                            httpInterface.onJSONException(e);
                        }
                        if(SOP)System.out.println("***** " + url + " onResponse() from Server: (" + response + ")");
                    }
                },
                genericErrorHandler) {

            // Here the mRequestQueue handler will get the parameters for this request here.
            // Ref: https://stackoverflow.com/questions/33573803/how-to-send-a-post-request-using-volley-with-string-body#33578202
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "choop:choop";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");headers.put("Authorization", auth);
                return headers;
            }*/@Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                return jsonObjectToMap(request);
            }
        };

        httpRequest.setShouldCache(false); // Elijah: Could work on dropping the cache !!!
        if(SOP)System.out.println("GenericHttpJsonRequest.java : before HTTP request!");

        genericHttpRequestQueue.add(httpRequest);
    }


    public JSONArray genericHttpModal(Context context, final String url, final JSONObject request) {

        this.url = url;

        genericHttpRequestQueue = Volley.newRequestQueue(context);

        class MyJsonReturn {
            volatile JSONArray returnJsonArray = null;

            public void set(JSONArray i) {
                returnJsonArray = i;
            }

            public void set(String i) {
                try {
                    returnJsonArray.put(0, i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public JSONArray get() {
                return returnJsonArray;
            }
        }

        final MyJsonReturn mymy = new MyJsonReturn();

        // Positive Response / HTTP OK.
        //        final Handler handler = new Handler() {
        //            @Override
        //            public void handleMessage(@NonNull Message msg) {
        //                throw new RuntimeException();
        //            }
        //        };

        final Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(SOP)System.out.println("***** " + url + " onResponse() from Server: (" + response + ")");
                try {
                    mymy.set(new JSONArray(response));
                } catch (JSONException e) {
                    mymy.set("[{\"JSONException:\"" + e.getMessage() + "\"}]");
                }
                // handler.sendMessage(handler.obtainMessage());
            }
        };

        // Negative Response / HTTP NOT OK
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(SOP)System.out.println("***** " + url + " onErrorResponse() from Server: (" + response + ")");
                result = "fail";
                String text = "";
                if (error instanceof TimeoutError) {
                    //text = "RecoverAccount: The Internet has timeout errors";
                    text = SharedMethods.gaStr( "Eghr007 internetTimeouterror" );
                }else if (error instanceof NoConnectionError){
                    //text = "RecoverAccount: No network Connection";
                    text= SharedMethods.gaStr("Eghr008 NoInternet" );
                }else if (error instanceof AuthFailureError){
                    //text = "RecoverAccount: Error Authenticating";
                    text= SharedMethods.gaStr( "Eghr009 errorAuthenticating" );
                }else if (error instanceof ServerError){
                    //text = "RecoverAccount: There is an error on the Server side, please try again later. The system reports this error automatically";
                    text= SharedMethods.gaStr("Eghr010 serverError" );
                }else if (error instanceof NetworkError){
                    //text = "RecoverAccount: Please check your internet connection";
                    text= SharedMethods.gaStr( "Eghr011 noInternetconnection" );
                }else {
                    //text = "RecoverAccount: Unexpected error occurred";
                    text= SharedMethods.gaStr( "Eghr012 unexpectedError" );
                }

                try {
                    mymy.set(new JSONArray("[{\"JSONException:\"" + result + "\"}]"));
                } catch (JSONException e) {
                    mymy.set("[{\"JSONException:\"" + e.getMessage() + "\"}]");
                }
                // handler.sendMessage(handler.obtainMessage());
            }
        };



        final StringRequest httpRequest = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                responseListener,
                errorListener) {

            // Here the mRequestQueue handler will get the parameters for this request here.
            // Ref: https://stackoverflow.com/questions/33573803/how-to-send-a-post-request-using-volley-with-string-body#33578202
            // Ref: Multi Threaded solution 14 Oct 2020 (David Svarrer) : https://stackoverflow.com/questions/2028697/dialogs-alertdialogs-how-to-block-execution-while-dialog-is-up-net-style
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "choop:choop";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");headers.put("Authorization", auth);
                return headers;
            }*/@Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                return jsonObjectToMap(request);
            }
        };
        httpRequest.setShouldCache(false); // Elijah: Could work on dropping the cache !!!
        if(SOP)System.out.println("GenericHttpJsonRequest.java : before HTTP request!");
        genericHttpRequestQueue.add(httpRequest);

        if(SOP)System.out.println("GenericHttpJsonRequest.java : After HTTP request!");

        try {
            if(SOP)System.out.println("GenericHttpJsonRequest.java : before Loop!");

            Looper.loop();

            if(SOP)System.out.println("GenericHttpJsonRequest.java : After Loop!");
        } catch (RuntimeException re) {
            if(SOP)System.out.println("GenericHttpJsonRequest.java : Message Handler caught - RuntimeException...");

        }
        return mymy.get();
    }

    /**
     * genericHttpJsonModal makes it possible to stack a JSON-array as request, and return another JSON-array as response:
     *
     * This algorithm creates a modal call to a HTTP function, and thereby waits until it terminates. We have not yet created a
     * time-out, that is coming - so that we will not wait forever for a response.
     *
     * @param context : The Android context (activity) which we are in...
     * @param url : The PHP to call
     * @param request JSONArray : request: [0] = Primary Keys, [1] = Attributes. Primary keys as JSON name-value pairs, ie: {"AppID":"Covid19"}
     * @return JSONArray: response: [0] = messages, [1] [2] [...] [N] = results, ie. select results.
     */
    public JSONArray genericHttpJsonModal (Context context, final String url, final JSONArray request) {

        this.url = url;

        genericHttpRequestQueue = Volley.newRequestQueue(context);


        class MyJsonReturn {
            JSONArray returnJsonArray;

            public void set(JSONArray i) {
                returnJsonArray = i;
            }

            public void set(String i) {
                try {
                    returnJsonArray.put(0, i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public JSONArray get() {
                return returnJsonArray;
            }
        }

        final MyJsonReturn mymy = new MyJsonReturn();

        // Positive Response / HTTP OK.
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                throw new RuntimeException();
            }
        };

        final Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(SOP)System.out.println("***** " + url + " onResponse() from Server: (" + response + ")");
                try {
                    mymy.set(new JSONArray(response));
                } catch (JSONException e) {
                    mymy.set("[{\"JSONException:\"" + e.getMessage() + "\"}]");
                }
                handler.sendMessage(handler.obtainMessage());
            }
        };

        // Negative Response / HTTP NOT OK
        final Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(SOP)System.out.println("***** " + url + " onErrorResponse() from Server: (" + response + ")");
                result = "fail";
                String text = "";
                if (error instanceof TimeoutError) {
                    //text = "RecoverAccount: The Internet has timeout errors";
                    text = SharedMethods.gaStr( "Eghr013 internetTimeouterror");
                }else if (error instanceof NoConnectionError){
                    //text = "RecoverAccount: No network Connection";
                    text= SharedMethods.gaStr( "Eghr014 NoInternet" );
                }else if (error instanceof AuthFailureError){
                    //text = "RecoverAccount: Error Authenticating";
                    text=  SharedMethods.gaStr( "Eghr015 errorAuthenticating" );
                }else if (error instanceof ServerError){
                    //text = "RecoverAccount: There is an error on the Server side, please try again later. The system reports this error automatically";
                    text= SharedMethods.gaStr( "Eghr016 serverError" );
                }else if (error instanceof NetworkError){
                    //text = "RecoverAccount: Please check your internet connection";
                    text= SharedMethods.gaStr( "Eghr017 noInternetconnection" );
                }else {
                    //text = "RecoverAccount: Unexpected error occurred";
                    text= SharedMethods.gaStr( "Eghr018 unexpectedError" );
                }

                try {
                    mymy.set(new JSONArray("[{\"JSONException:\"" + result + "\"}]"));
                } catch (JSONException e) {
                    mymy.set("[{\"JSONException:\"" + e.getMessage() + "\"}]");
                }
                handler.sendMessage(handler.obtainMessage());
            }
        };

        final StringRequest httpRequest = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                responseListener,
                errorListener) {

            // Here the mRequestQueue handler will get the parameters for this request here.
            // Ref: https://stackoverflow.com/questions/33573803/how-to-send-a-post-request-using-volley-with-string-body#33578202
            // Ref: Multi Threaded solution 14 Oct 2020 (David Svarrer) : https://stackoverflow.com/questions/2028697/dialogs-alertdialogs-how-to-block-execution-while-dialog-is-up-net-style
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "choop:choop";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");headers.put("Authorization", auth);
                return headers;
            }*/@Override
            protected java.util.Map<String, String> getParams() throws AuthFailureError {
                return jsonSqlObjectToMap(request); // Primary Key fields prepended with "Key$", attribute fields with "Attrib$"..
            }
        };
        httpRequest.setShouldCache(false); // Elijah: Could work on dropping the cache !!!
        if(SOP)System.out.println("GenericHttpJsonRequest.java : before HTTP request!");
        genericHttpRequestQueue.add(httpRequest);

        if(SOP)System.out.println("GenericHttpJsonRequest.java : After HTTP request!");

        try {
            if(SOP)System.out.println("GenericHttpJsonRequest.java : before Loop!");
            Looper.loop();
            if(SOP)System.out.println("GenericHttpJsonRequest.java : After Loop!");
        } catch (RuntimeException re) {
            if(SOP)System.out.println("GenericHttpJsonRequest.java : Message Handler caught - RuntimeException...");
        }
        return mymy.get();
    }
}
