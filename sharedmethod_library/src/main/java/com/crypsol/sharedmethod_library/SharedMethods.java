package com.crypsol.sharedmethod_library;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

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
import com.crypsol.sessionmanager_library.SharedMethods_libs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class SharedMethods {
    public interface Create3 {
        public void onCreate3();
        public void onCreateAfterLanguages();
    }


    private static Context context;
    private static int editTextstyling;
    private static int btnSubmit;
    private static int btnCancel;
    private static int messageDialog;
    private static int alertDialogMessageToDeveloper;
    private  static  Class dashboardClass;
    public SharedMethods(Context context, int editTextstyling, int btnSubmit, int btnCancel, int messageDialog, int alertDialogMessageToDeveloper, Class dashboardClass){
        this.context = context;
        this.editTextstyling = editTextstyling;
        this.btnSubmit = btnSubmit;
        this.btnCancel = btnCancel;
        this.messageDialog = messageDialog;
        this.alertDialogMessageToDeveloper = alertDialogMessageToDeveloper;
        this.dashboardClass = dashboardClass;
    }

    static class QueueAppStringData{
        String result,description;
        public   QueueAppStringData(String rest,String desc){
            result = rest;
            description = desc;
        }
    }
    // AlertDialog.Builder mBuilder;
    // David05nov2020 Branch

    static String prefix = "";
    static String nsnString = "";
    static HashMap hashMap = new HashMap();

    public static boolean TOAST = true; // Default is to print. We will set this from INTRO point (ShowWheelExpand)
    public static boolean SOP = true; // Default is to toast. We will set this from INTRO point (ShowWheelExpand)
    // TODO 282 Experiment, check if we can use this generic ErrorHandler...
    static Response.ErrorListener genericErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

            String text = "";
            if (error instanceof TimeoutError) {
                //text = "RecoverAccount: The Internet has timeout errors";
                text = SharedMethods.gaStr("Esha017 internetTimeouterror" );
            }else if (error instanceof NoConnectionError){
                //text = "RecoverAccount: No network Connection";
                text= SharedMethods.gaStr("Esha018 NoInternet" );
            }else if (error instanceof AuthFailureError){
                //text = "RecoverAccount: Error Authenticating";
                text= SharedMethods.gaStr( "Esha019 errorAuthenticating" );
            }else if (error instanceof ServerError){
                //text = "RecoverAccount: There is an error on the Server side, please try again later. The system reports this error automatically";
                text= SharedMethods.gaStr("Esha020 serverError");
            }else if (error instanceof NetworkError){
                //text = "RecoverAccount: Please check your internet connection";
                text= SharedMethods.gaStr("Esha021 noInternetconnection" );
            }else {
                //text = "RecoverAccount: Unexpected error occurred";
                text= SharedMethods.gaStr("Esha022 unexpectedError");
            }

        }
    };


    public static String rinse(String toRinse, String allowed) {
        StringBuffer sb = new StringBuffer(20);

        for (int i = 0; i < toRinse.length(); i++) {
            if (allowed.indexOf(toRinse.charAt(i)) > -1)
                sb.append(toRinse.charAt(i));
        }
        return sb.toString();
    }

    public static String cleanPhoneNumber(String phone, Context context) {
        int nsn = 0;
        String toInternationalize;
        // Speeding this up by storing in static memory, thereby avoiding slower flash (SessionManager) memory
        if ((prefix == null || prefix.length() < 1)) {
            prefix = "+" + SessionManager.getMyE_164_PREFIX();
        }
        if ((nsnString == null || nsnString.length() < 1)) {
            nsnString = SessionManager.getMyNSN();
        }

        try {
            nsn = Integer.parseInt(rinse(nsnString, "01234567890"));
        } catch (NumberFormatException nfe) {
            nsn = -1;
            reportError("", "",  "", "","RegistrationActivity.java", context,
                    "The National Significant Number Length contains non digits (" + nsnString + ")\r\n" + nfe.getMessage());
        }

        phone = rinse(phone, "+0123456789");

        // Assuming that if the number starts with +, it is indeed already internationalized...
        // Furthermore if nsn is not a number, we cannot convert and will return the phone number.
        if (phone.charAt(0) == '+' || nsn == -1) {
            return phone; // Strip anything but + and digits.
        } else if (phone.length() != nsn) {
            // ie. nsn=9: 0712345678 or 12345678
            if (phone.charAt(0) != '0') {
                // ie. nsn=9: 12345678 (ie. DK no.) or 1234567890 (US number without +)
                if (phone.length() < nsn)
                    // ie. nsn=9: 12345678 (ie. DK number)
                    return phone;
                else // phone length is now > nsn and first character is not 0, so return as is.
                    // ie. nsn=9: 1234567890 (ie. US number)
                    return phone;
            } else
                // ie. nsn=9: 0712345678 or 08881234567
                if (phone.length() == nsn + 1) { //  ie. nsn=9: 0712345678 - we leave this for
                    // This could be a Kenyan number with a ZERO in front. Internationalize it.
                } else {
                    return phone; // this is then not a Kenyan phone number... 0xxxxxxxx but not KE.
                }
        }

        // Else... converting based on local prefix
//        if(SOP)System.out.println("SharedMethods.java : cleanPhoneNumber("+phone+", prefix:"+prefix+", "+nsnString+" digits)");
        toInternationalize = phone;

        if (toInternationalize.length() <= nsn) {
            toInternationalize = prefix + toInternationalize;
        } else {
            toInternationalize = prefix + toInternationalize.substring(toInternationalize.length() - nsn);
        }

//        if(SOP)System.out.println("SharedMethods.java : cleanPhoneNumber return ("+toInternationalize+")");

        return toInternationalize; // Convert the compressed phone number
    }

    public static String toBase41 (long toConvert) {
        // TODO 400 The Base64crc32 is for unique identification while paying for the tile. This oes all the way through the system, for
        //  payment. So when paying, we identify this tile uniquely this way, in a readable fashion. We may consider changing the
        //  code from Base64 (leaving 6 characters for 32 bits), to a lower base, where we have more characters, but, they are not
        //  confusing to read aloud, ie. leaving 0, O, I, 1, l, out. This would create a base which could have the following characters:
        //  23456789ABCDEFGHJKLMNPQRSTUVWXYZ                        (which would be base 32,or 5 bits, becoming 7 characters)
        //  23456789ABCDEFGHJKLMNPQRSTUVWXYZ,.$:!-+=?               (which would be base 41,or 6 bits, becoming 6 characters)
        //  (2^32)^(1/6) = 40.31747359, thus we need 41 characters in order for us to have a 41
        //  23456789ABCDEFGHJKLMNPQRSTUVWXYZ,.-*/%&+#?!:()£$€=<>    (which would be base 52, or 6 bits, becoming 6 characters)
        //  1...+...10....+...20....+...30....+...40....+...50..
        if (toConvert > ((1L << 32)-1)) throw new NumberFormatException();
        byte [] base41 = new byte[] {'2','3','4','5','6','7','8','9','A','B','C','D','E','F','G','H','J','K','L','M','N',
                'P','Q','R','S','T','U','V','W','X','Y','Z',',','.','$',':','!','-','+','=','?'};
        String output = "";
        for (int i = 0; i < 6; i++) {
            output += (char) base41[(int)(toConvert % base41.length)];
            toConvert/=base41.length;
        }

        return output;
    }

    // This is a function that is used when one has commented out any piece of code that can cause problems, it will show where the change has been made;
    // with a toast,system.out.print(), log.d()

    public static void AlertChanges(String message,String changeId){
        AlertDialog.Builder showChange=  new AlertDialog.Builder(context);
        showChange.setTitle(SharedMethods.gaStr("Mshm020 show changes"))
                .setMessage(SharedMethods.gaStr("Mshm022 message")+" :: \n\t"+changeId+"\n "+message)
                .setCancelable(false)
                .setPositiveButton(SharedMethods.gaStr("Mshm021 ok"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
    }

    public static void showUserOptionToSelectFromAfetrShaking( final String dashBoardID,  final String showID,  final String imagpathpass,  final String imagepass,final Context context, final String ActivityFrom,final int messagetodeveloper_alert) {
        class MyInteger {
            int myInt;

            public void set(int i) {
                myInt = i;
            }

            public int get() {
                return myInt;
            }
        }
        String[] actionsToTake = {SharedMethods.gaStr("Mshm017 Change Language"),
                SharedMethods.gaStr("Mshm018 Error reporting"),
                SharedMethods.gaStr("Mshm019 Send message")};
        final MyInteger mymy = new MyInteger();
        int checkedItem = 0;
        final AlertDialog.Builder mBuilder;
        mBuilder = new AlertDialog.Builder(context);
        //mBuilder.setTitle(context.getResources().getString(R.string.whatyouwanttodo));
        mBuilder.setTitle(SharedMethods.gaStr("Ishm006 settings Alertdialog"));
        mBuilder.setSingleChoiceItems(actionsToTake, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mymy.set(which);
            }
        }).setNegativeButton(SharedMethods.gaStr("Ishm007 cancel"), null)
                .setPositiveButton(SharedMethods.gaStr("Ishm008 Ok"), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(SOP)System.out.println("SharedMethods.java : showUserOptionToSelectFromAfetrShaking() - which = " + mymy.get());
                                switch (mymy.get()) {
                                    case 0:
                                        triggerCountryLanguageSelection(context);

                                        if(SOP)System.out.println("sharedMethod.java showUserOptionToSelectFromAfetrShaking() (A1) "+ SessionManager.getLanguageCountry());
                                        break;
                                    case 1:
                                        reportError(dashBoardID, showID,  imagpathpass,  imagepass,ActivityFrom, context, "General error reporting");
                                        break;
                                    case 2:
                                        reportSendDeveloperMessage(ActivityFrom, context, "Message To developer",messagetodeveloper_alert);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).setCancelable(false);
        mBuilder.create();
        mBuilder.show();

    }

    /**
     * reportError () - this is reports to the developers, from ourselves to ourselves that something is outrageously wrong in
     * our code, and that we need to make corrections. This is behind the scene. Messages more deliberately reported by the
     * users, upon their clik etc. will be dealt with by reportSendDeveloperMessage (see below)
     *
     * @param activityfrom
     * @param context
     * @param typeOfReporting
     */
    public static void reportError(final String dashBoardID,  final String showID,  final String imagpathpass,  final String imagepass,final String activityfrom, final Context context,final String typeOfReporting) {
        if (TOAST)
            Toast.makeText(context, activityfrom + ":\r\n" + typeOfReporting, Toast.LENGTH_LONG).show();
        // TODO 212 Implementing error reporting, here we will also store back to the database, such that we have a chance, from a central position
        //  to log and find errors occurring on all devices, for us to capture problems early.
        // TODO 235 We will create a central repository of these errors, where we will number them, and feed back
        //  to the user on ticket numbers etc., such that the usability can be improved while we update people.
        // TODO 236 We need to create a subscription system such that people who report an error, automatically subscribe to
        //  all reports on that error. The same subscription system must make it possible for the user to pick all outstanding
        //  issues and mark which of them he want to follow. We can maybe also create a chat function around that, such that
        //  it is possible to have a dialogue between developers and users, based on tickets.

       /* //************************
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.errorreporting_layout, null);

        final EditText error = (EditText) dialogView.findViewById(R.id.error);
        final EditText errorDetails = (EditText) dialogView.findViewById(R.id.errorDetails);
        final EditText reasonWhyitsAnRerror = (EditText) dialogView.findViewById(R.id.reasonWhyitsAnRerror);
        final EditText recommendationToError = (EditText) dialogView.findViewById(R.id.recommendationToError);

        Button btnReporterror = (Button) dialogView.findViewById(R.id.btnReporterror);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        btnReporterror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error2 = error.getText().toString().trim();
                String errorDetails2 = errorDetails.getText().toString().trim();
                String reasonWhyitsAnRerror2 = reasonWhyitsAnRerror.getText().toString().trim();
                String recommendationToError2 = recommendationToError.getText().toString().trim();
                if (messagetosent.equals("") || messagetosent.length() < 5) {
                    Toast.makeText(context, "message " + messagetosent, Toast.LENGTH_SHORT).show();
                }else{
                    sendReportToTheDatabase(messagetosent, context, activityfrom, typeOfReporting);
                }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

        //**************************/
        //final Context context, final String title, String textfieldHint,String textView


        String whatIstheError = modalTextFieldDialog(context, SharedMethods.gaStr("Mshm003 error title1"), SharedMethods.gaStr("Mshm004 error reporting edittext hint"), SharedMethods.gaStr("Mshm005 error reporting textview Text"), true);
        String describeError = modalTextFieldDialog(context, SharedMethods.gaStr("Mshm006 error title2"), SharedMethods.gaStr("Mshm007 edittext hint"), SharedMethods.gaStr("Mshm008 TextView text"), true);
        String  giveaReasonWhyitsAnerror = modalTextFieldDialog(context, SharedMethods.gaStr("Mshm009 error title 3"), SharedMethods.gaStr("Mshm010 reporting error EditText hint"), SharedMethods.gaStr("Mshm011 TextView text"),true);
        String howshoulditBedone = modalTextFieldDialog(context, SharedMethods.gaStr("Mshm012 error title 4"), SharedMethods.gaStr("Mshm013 error reporting hind"), SharedMethods.gaStr("Mshm014 error reportint text"), true);

        JSONObject jsonAnswer = new JSONObject();
        try {
            jsonAnswer.put("whatIstheError",whatIstheError);
            jsonAnswer.put("giveaReasonWhyitsAnerror",giveaReasonWhyitsAnerror);
            jsonAnswer.put("howshoulditBedone",howshoulditBedone);
            jsonAnswer.put("describeError",describeError);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("SharedMethod reportError() ANSWER ERROR REPORTING "+jsonAnswer);

        if (jsonAnswer.length()<1){

        }else{
            ErrorReportingByTheUsers(dashBoardID, showID, imagpathpass, imagepass, activityfrom, context, typeOfReporting, jsonAnswer);

        }

    }

    /**
     * reportSendDeveloperMessage () - Messages more deliberately reported by the users, upon their clik etc. will be dealt with here.
     * if you want to report an error in the code to the developers (like from you to your colleagues) then use reportError() ...
     *
     * @param activityfrom
     * @param context
     * @param typeOfReporting
     */
/*    public static void reportSendDeveloperMessage(final String activityfrom, final Context context, final String typeOfReporting,final int messagetodeveloper_alert) {
        if(SOP)System.out.println("SharedMethod.java reportSendDeveloperMessage() (W) "+typeOfReporting);
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(messagetodeveloper_alert, null);

        final EditText message = (EditText) dialogView.findViewById(R.id.message);
        final TextView textViewMessagetodeveloper = (TextView) dialogView.findViewById(R.id.textViewMessagetodeveloper);
        textViewMessagetodeveloper.setText(SharedMethods.strLu("Ishm022 message to developer","Send Message To Developer","This is the title that will be displayed on the alertDialog box. This happens when the users are typing message to the developer"));
        message.setHint(SharedMethods.strLu("Ishm023 hint message to developer","Please enter your message","This is the hint text to Edittext (Messaging developer)"));
        Button btnSubmit = (Button) dialogView.findViewById(R.id.btnSubmit);
        btnSubmit.setText(SharedMethods.strLu("Ishm024 button submit","Submit","Text to be displayed on submit button to developer alert dialog"));
;
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnCancel.setText(SharedMethods.strLu("Ishm025 button cancel","Cancel","Text to be displayed on cancel button to developer alert dialog"));
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messagetosent = message.getText().toString().trim();
                if (messagetosent.equals("") || messagetosent.length() < 5) {
                    if(TOAST)Toast.makeText(context, SharedMethods.strLu("Mshm015 message","message ","This the message toast to the user if the message they are sending to developers is empty.") + messagetosent, Toast.LENGTH_SHORT).show();
                }else{
                    //dialogView.setVisibility(View.GONE);
                    dialogBuilder.cancel();
                    sendReportToTheDatabase(messagetosent, context, activityfrom, typeOfReporting,messagetodeveloper_alert);
                    if(SOP)System.out.println("SharedMethod.java reportSendDeveloperMessage() (W1) "+typeOfReporting+" Message "+messagetosent);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }*/

    public static void reportSendDeveloperMessage(final String activityfrom, final Context context, final String typeOfReporting,final int messagetodeveloper_alert) {
        if(SOP)System.out.println("SharedMethod.java reportSendDeveloperMessage() (W) "+typeOfReporting);
        final AlertDialog dialogBuilder = new AlertDialog.Builder(context).create();
        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(messagetodeveloper_alert, null);

        final EditText message = (EditText) dialogView.findViewById(messageDialog);
        final TextView textViewMessagetodeveloper = (TextView) dialogView.findViewById(alertDialogMessageToDeveloper);

        // textViewMessagetodeveloper.setText(SharedMethods.strLu("Ishm022 message to developer","Send Message To Developer","This is the title that will be displayed on the alertDialog box. This happens when the users are typing message to the developer"));
        //message.setHint(SharedMethods.strLu("Ishm023 hint message to developer","Please enter your message","This is the hint text to Edittext (Messaging developer)"));
        Button btnSubmit2 = (Button) dialogView.findViewById(btnSubmit);
        // btnSubmit2.setText(SharedMethods.strLu("Ishm024 button submit","Submit","Text to be displayed on submit button to developer alert dialog"));
        ;
        final Button btnCancel2 = (Button) dialogView.findViewById(btnCancel);
        try {
            btnCancel2.setText(SessionManager.getAppString().getString("Ishm025 button cancel"));
            btnSubmit2.setText(SessionManager.getAppString().getString("Ishm024 button submit"));
            textViewMessagetodeveloper.setText(SessionManager.getAppString().getString("Ishm022 message to developer"));
        } catch (JSONException e) {
            //System.out.println("Error displaying APPSTRING "+SessionManager.getAppString().getString("Ishm025 button cancel"));
            System.out.println("Error displaying or setting text message");
            e.printStackTrace();
        }
        //btnCancel2.setText(SharedMethods.strLu("Ishm025 button cancel","Cancel","Text to be displayed on cancel button to developer alert dialog"));
        btnSubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messagetosent = message.getText().toString().trim();
                if (messagetosent.equals("") || messagetosent.length() < 5) {
                    if(TOAST)Toast.makeText(context, SharedMethods.gaStr("Mshm015 message") + messagetosent, Toast.LENGTH_SHORT).show();
                }else{
                    //dialogView.setVisibility(View.GONE);
                    dialogBuilder.cancel();
                    sendReportToTheDatabase(messagetosent, context, activityfrom, typeOfReporting,messagetodeveloper_alert);
                    if(SOP)System.out.println("SharedMethod.java reportSendDeveloperMessage() (W1) "+typeOfReporting+" Message "+messagetosent);
                }
            }
        });
        btnCancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    public static void ChangeSettings(String activityfrom, Context context) {
        if(TOAST)Toast.makeText(context, SharedMethods.gaStr("Mshm016 change settings")+ activityfrom, Toast.LENGTH_LONG).show();
        //TODO Implementing the setting value  to reset user setting
    }


    public static void sendReportToTheDatabase(final String messagetosent, final Context context, final String activityfrom, final String typeOfReporting,final int messagetodeveloper_alert) {
        //Toast.makeText(context, "RRRRRRRR ", Toast.LENGTH_SHORT).show();
        RequestQueue mRequestQueue;
        mRequestQueue = Volley.newRequestQueue(context);
        String url = "reportingissues.php";

        StringRequest requestingServer = new StringRequest(
                Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(SOP)System.out.println("*** ISSUE REPORTING BACK " + response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int data = 0; data < jsonArray.length(); data++) {
                                JSONObject object = jsonArray.getJSONObject(data);
                                String result = object.getString("code");

                                if (result.equals("success")) {
                                    final AlertDialog.Builder mBuilder;
                                    mBuilder = new AlertDialog.Builder(context);
                                    mBuilder.setMessage(object.getString("message"))
                                            .setPositiveButton(SharedMethods.gaStr("Ishm009 Ok"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //context.startActivity(new Intent(context, context.getClass()));
                                                    mBuilder.setCancelable(true);
                                                }
                                            })
                                            .create()
                                            .show();
                                } else {
                                    AlertDialog.Builder mBuilder;
                                    mBuilder = new AlertDialog.Builder(context);
                                    mBuilder.setMessage(object.getString("message"))
                                            .setPositiveButton(SharedMethods.gaStr("Ishm001 positive button"), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //
                                                }
                                            })
                                            .create()
                                            .show();
                                }

                            }

                        } catch (JSONException e) {
                            if(TOAST)Toast.makeText(context, "Error (A): __ " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                genericErrorHandler
        ) {
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "choop:choop";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");headers.put("Authorization", auth);
                return headers;
            }*/@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("messageToReport", messagetosent);
                params.put("typeOfReporting", typeOfReporting);
                params.put("activityFrom", activityfrom);
                SharedMethods.printParams("SharedMethods.java ==> reportingissues.php", params);

                return params;
            }
        };
        mRequestQueue.add(requestingServer);
    }

    public static void showMyDefaultLanguageandCountry(final String lang, final String state, final List<String> countryandlanguage,
                                                       final RequestQueue mRequestQueue, final Context context, final List<String> countryandCurrency) {
        class MyInteger {
            int myInt;

            public void set(int i) {
                myInt = i;
            }

            public int get() {
                return myInt;
            }
        }
        String combinationofLangandCountry = state + "; " + lang;
        final MyInteger mymy = new MyInteger();



        // TODO test E Here we need to move the inner dialogue with Choose From Others (text hereunder R.string.selectcountrylanguage) such that we have one menu with languages,
        //  hereof the phone language and country as default at the top.
        //final String[] totalList = {combinationofLangandCountry, context.getResources().getString(R.string.selectcountrylanguage)};
        final String[] totalList = {combinationofLangandCountry, SharedMethods.gaStr("Ishm013 choice other languages")};
        final boolean[] checkedItems = new boolean[totalList.length];
        AlertDialog.Builder mBuilder;
        mBuilder = new AlertDialog.Builder(context);
        //mBuilder.setTitle(context.getResources().getString(R.string.confirmCountryLangSelection));
        mBuilder.setTitle(SharedMethods.gaStr("Ishm003 confirm language selection"));

        mBuilder.setSingleChoiceItems(totalList, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mymy.set(which);
            }
        }).setPositiveButton(SharedMethods.gaStr("Ishm010 Ok"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                which = mymy.get();
                if(SOP)System.out.println("SharedMethods.java : showMyDefaultLanguageandCountry() : mbuilder which = " + which);
                switch (which) {
                    case 0:
                        String[] iso_lan = lang.split("-", 2);
                        Locale curencyLocale = new Locale(iso_lan[0], iso_lan[1]);
                        if(SOP)System.out.println("SharedMethod.java showMyDefaultLanguageandCountry() -- "+ Currency.getInstance(curencyLocale));
                        registerUserLanguageandCountry(iso_lan[0], iso_lan[1], state, mRequestQueue, context,""+Currency.getInstance(curencyLocale));
                        break;
                    case 1:
                        showCountryLanguageAlertDialog(countryandlanguage, mRequestQueue, context,countryandCurrency);
                        break;
                    default:
                        break;
                }
            }
        }).setCancelable(false);
        mBuilder.create();
        mBuilder.show();
    }

    public static void showCountryLanguageAlertDialog(List<String> countryandlanguage, final RequestQueue mRequestQueue, final Context context, final List<String> countryandCurrency) {

        if(SOP)System.out.println("SharedMethod.java showCountryLanguageAlertDialog() list of country currency "+countryandCurrency);

        final String[] country_lang_toselectFrom = countryandlanguage.toArray(new String[0]);
        final boolean[] checkedItems = new boolean[country_lang_toselectFrom.length];
        AlertDialog.Builder mBuilder;
        mBuilder = new AlertDialog.Builder(context);
        // TODO make this one single choice...
        // mBuilder.setTitle(context.getResources().getString(R.string.Select_country));
        mBuilder.setTitle(SharedMethods.gaStr("Ishm004 select country"));
        mBuilder.setMultiChoiceItems(country_lang_toselectFrom, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //
            }
            //}).setPositiveButton(context.getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
        }).setPositiveButton(SharedMethods.gaStr("Ishm014 confirm"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    boolean checked = checkedItems[i];
                    if (checked) {
                        String[] split_country_and_language = country_lang_toselectFrom[i].split(" ", 2);
                        String[] split_state_iso_lang = split_country_and_language[0].split("-", 2);
                        getCountryOfResidence(split_state_iso_lang[1], split_state_iso_lang[0], split_country_and_language[1], mRequestQueue, context,countryandCurrency);
                    }
                }
            }
        }).setOnDismissListener(null);
        AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    // TODO create a function to be called before registerUserLanguageandCountry that will show currency and country of origin
    //TODO this currency will be used in transaction where currency is needed
    // elijahsunwa
    public static void getCountryOfResidence(final String iso_lang, final String isoCountry, final String lang, final RequestQueue mRequestQueue, final Context context,List<String>countryandCurrency){
        if(SOP)System.out.println("SharedMethod.java getCountryOfResidence() (R) list of countrys and currency "+countryandCurrency);

        final String[] currency_country = countryandCurrency.toArray(new String[0]);
        if(SOP)System.out.println("SharedMethod.java getCountryOfResidence() (R) list of countrys and currency  string "+currency_country);
        final boolean[] checkedItems = new boolean[currency_country.length];
        AlertDialog.Builder mBuilder;
        mBuilder = new AlertDialog.Builder(context);
        //mBuilder.setTitle(context.getResources().getString(R.string.selectCurrency));
        mBuilder.setTitle(SharedMethods.gaStr("Ishm005 select currency"));
        mBuilder.setMultiChoiceItems(currency_country, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                //
            }
        }).setPositiveButton(SharedMethods.gaStr("Ishm015 confirm"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    boolean checked = checkedItems[i];
                    if (checked) {
                        String[] split_currency_country = currency_country[i].split(" ", 2);
                        //String[] currency = split_currency_country[1].split(" ", 2);
                        registerUserLanguageandCountry(iso_lang, isoCountry, lang, mRequestQueue, context, split_currency_country[1]);
                    }
                }
            }
        }).setOnDismissListener(null);
        AlertDialog dialog = mBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void registerUserLanguageandCountry(final String iso_lang, final String isoCountry, final String lang, RequestQueue mRequestQueue, final Context context, final String currency) {
        String url = "setLanguageAndCountry.php";

        if(SOP)System.out.println("SharedMethod registerUserLanguageandCountry()  currency -- "+currency);
        SessionManager.setMyISOCOUNTRY(isoCountry);
        SessionManager.setMyISOLANGUAGE(iso_lang);
        StringRequest request = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AlertDialog.Builder mBuilder;
                        try {
                            JSONArray responses = new JSONArray(response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String result = jsonObject.getString("code");

                            if (result.equals("success")) {
                                String myE_164_prefix = jsonObject.getString("numberPrefix");
                                String nationalSignificantNumber = jsonObject.getString("nationalSignificantNumberLength");

                                // TODO 209 The country code will code for a Phone number prefix.
                                //  When the user registers, this prefix MAY change to the one received from the text message.
                                // TODO 210 We must find a way to ask the client which country his phone normally resides in, or
                                //  in other words, which country the phone numbers in general are assumed to belong to. The problem
                                //  is to find a way to ask which is clear, simple to understand etc.
                                //  THIS question is BEYOND the question about where the person currently is...
                                // TODO 211 We also need to find out how the person changes country, ie. by detecting SIM-change
                                //  and other means or methods, for instance GPS location, or other ways, such that we can
                                //  adapt to where the person is, and the default of the numbers in the phone so as to know which
                                //  country prefix to put in front when...
                                // SessionManager.checkAndGenerateImei (); - commented as already called at params.put() below.
                                SessionManager.setMyE_164_PREFIX(myE_164_prefix);
                                SessionManager.setMyNSN(nationalSignificantNumber);
                                if(SOP)System.out.println("SharedMethods.java : registerUserLanguageandCountry() - prefix " + result + "," + myE_164_prefix + "," + nationalSignificantNumber);
                                if(SOP)System.out.println("SharedMethods.java : registerUserLanguageandCountry() - ISO " + SessionManager.getMyISOLANGUAGE() + "," + SessionManager.getMyISOCOUNTRY());

                                mBuilder = new AlertDialog.Builder(context);
                                mBuilder.setMessage(jsonObject.getString("message"))
                                        .setPositiveButton(SharedMethods.gaStr("Ishm011 Ok"), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                                dialog.dismiss();
                                                ((Create3) context).onCreate3(); // This ought being the call back now to complete after language...
                                            }
                                        })
                                        .setCancelable(true)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(SOP)System.out.println("SharedMethods.java : registerUserLanguageandCountry - " + response);
                    }
                },
                genericErrorHandler) {
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("IMEI_NUMBER", SessionManager.checkAndGenerateImei ());
                params.put("appID", SessionManager.getAPPNAME());
                params.put("country", isoCountry);
                params.put("language", lang);
                params.put("iso_lang", iso_lang);
                params.put("currency", currency);
                SharedMethods.printParams("SharedMethods.java ==> setLanguageAndCountry.php", params);
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    public static void triggerCountryLanguageSelection(final Context context) {

        if(SOP)System.out.println("sharedMethod.java showUserOptionToSelectFromAfetrShaking() (B1) ");
        final RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        final List<String> countryandlanguage = new ArrayList<String>();

        String url = "getcountryandlanguage.php";
        StringRequest request = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {
                        if(SOP)System.out.println("ShowWheelExpand : getCountrieToSelectFrom()  123/10/2020" + response);

                        try {

                            JSONArray getCountryArray = new JSONArray(response);
                            JSONArray responses = getCountryArray.getJSONArray(0);
                            JSONArray imei = getCountryArray.getJSONArray(1);
                            JSONObject hit;
                            if(SOP)System.out.println("   >>> responses = " + responses);
                            if(SOP)System.out.println("   >>> imei = " + imei);
                            for (int i = 0; i < responses.length(); i++) {
                                hit = responses.getJSONObject(i);
                                String lang = hit.getString("lang");
                                String state = hit.getString("state");
                                countryandlanguage.add(state + " " + lang);
                            }

                            countryandlanguage.add(SessionManager.getPhoneDefaultLnaguage());

                            String languagename = Locale.getDefault().toLanguageTag();
                            String country = Locale.getDefault().getCountry();

                            ArrayList mynewArray = new ArrayList();
                            String[] stringArray = SessionManager.getCurrenyList().split(",");
                            for (String part : stringArray) {
                                String removeFirstBracket = part.replaceAll("]","");
                                String removeFirstBracket2 = removeFirstBracket.replaceAll("\\[","");
                                //mynewArray.add('"'+removeFirstBracket2.trim()+'"');
                                mynewArray.add(removeFirstBracket2.trim());

                                if(SessionManager.getSOP()!="")System.out.println("FISH - tilapia - a big one "+" part "+part+" "+mynewArray);
                            }

                            if(SessionManager.getSOP()!="")System.out.println("FISH - tilapia - a big one" + SessionManager.getCurrenyList() +" Stringa Array "+ mynewArray);

                            showMyDefaultLanguageandCountry(languagename, country, countryandlanguage, mRequestQueue, context, mynewArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                genericErrorHandler
        ) {
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "choop:choop";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");headers.put("Authorization", auth);
                return headers;
            }*/@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("IMEI", SessionManager.checkAndGenerateImei());
                SharedMethods.printParams("SharedMethods.java ==> getcountryandlanguage.php", params);
                return params;
            }
        };
        ;
        mRequestQueue.add(request);
    }

    //import java.security.MessageDigest;
    //import java.security.NoSuchAlgorithmException;


    public static String generateMyImeiNo() {
        String myImeiCandidate = md5("" + new Random().nextInt(1000000000) + 1000000000 + ";" + System.currentTimeMillis());
        return myImeiCandidate;
    }


    public static int modalRadioDialog(final Context context, final String title, final int checkedItem, final String [] actionsToTake) throws JSONException {
        // TODO 325 MyInteger ought to be generalized
        class MyInteger {
            int myInt;
            public void set(int i) {
                myInt = i;
            }
            public int get() {
                return myInt;
            }
        }
        final MyInteger mymy = new MyInteger();

        Thread t = new Thread() {
            public void run() {
                AlertDialog.Builder mBuilder;

                mBuilder = new AlertDialog.Builder(context);
                // mBuilder.setTitle(context.getResources().getString(R.string.whatyouwanttodo));
                mBuilder.setTitle(title);
                mBuilder.setSingleChoiceItems(actionsToTake, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mymy.set(which);
//                        synchronized (mymy) { // Not notifying here, as we are just choosing the individual language so far...
//                            mymy.notify(); // The notify here makes the dialog disappear !! That's a bit peculiar.
//                        }
                        // TODO 232 Here we will one day insert display with a toast of the Country and Language
                    }
                }).setNegativeButton(
                        //context.getResources().getString(R.string.label_cancel),
                        SharedMethods.gaStr("Ishm017 cancel")
                        ,
                        new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        which = mymy.get();
                                        if(SessionManager.getSOP()!="")System.out.println("SharedMethods.java : modalDialog() - CANCEL (" + which + ") picked = " + actionsToTake[which]);
                                        synchronized (mymy) {
                                            mymy.notify();
                                        }
                                    }
                                }).setPositiveButton(
                        SharedMethods.gaStr("Ishm018 Ok"), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        which = mymy.get();
                                        if(SessionManager.getSOP()!="")System.out.println("dashboard.java : modalDialog() - OK  (" + which + ") picked = " + actionsToTake[which]);
                                        synchronized (mymy) {
                                            mymy.notify();
                                        }
                                    }
                                }).setCancelable(false);
                Looper.prepare(); // TODO 800 check up on the looper problem occurring today 08 Nov 2020
                mBuilder.create();
                mBuilder.show();
                Looper.loop();
            }
        };
        t.start();
        synchronized (mymy) {
            try {
                mymy.wait();
            } catch (
                    InterruptedException e) {
                e.printStackTrace();
            }
            return mymy.get();
        }
    }

    public static HashMap<String,String> modalCheckDialog(final Context context, final String title, final CharSequence [] actionsToTake) throws JSONException {
        final boolean[] checkedItems = new boolean[actionsToTake.length];
        HashMap<String,String> returnedItems = new HashMap<String,String>();
        // TODO 325 MyInteger ought to be generalized
        class MyInteger {
            HashMap<String,String> share = new HashMap<String,String>();

            public void set(int i) throws JSONException {
                share.put(actionsToTake[i].toString(), "1");
            }
            public void reset(int i) throws JSONException {
                share.remove(actionsToTake[i].toString());
            }
            public void clear() {
                share = new HashMap<String,String>();
            }
            public HashMap<String,String> get() {
                return share;
            }
        }
        final MyInteger mymy = new MyInteger();


        Thread t = new Thread() {
            public void run() {
                AlertDialog.Builder mBuilder;

                mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle(title);
                // mBuilder.setMessage(message); // To be used when there is a message to display... ??
                mBuilder.setMultiChoiceItems(actionsToTake, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        try {
                            if (checkedItems[which] = isChecked) {
                                mymy.set(which);
                            } else
                                mymy.reset(which);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton(SharedMethods.gaStr("Ishm019 cancel"), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                mymy.clear();
                                synchronized (mymy) {
                                    mymy.notify();
                                }
                            }
                        }).setPositiveButton(SharedMethods.gaStr("Ishm020 Ok"), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                synchronized (mymy) {
                                    mymy.notify();
                                }
                            }
                        }).setCancelable(false);
                Looper.prepare(); // TODO 800 check up on the looper problem occurring today 08 Nov 2020
                mBuilder.create();
                mBuilder.show();
                Looper.loop();
            }
        };
        t.start();
        synchronized (mymy) {
            try {
                mymy.wait();
            } catch (
                    InterruptedException e) {
                e.printStackTrace();
            }
        }
        return mymy.get();
    }

    public static long modalCheck64Dialog (final Context context, final String title, final CharSequence [] actionsToTake)  {
        final boolean[] checkedItems = new boolean[actionsToTake.length];
        // TODO 325 MyInteger ought to be generalized
        class MyInteger {
            long share = 0;

            public void set(int i) { share |= (1 << i); }
            public void reset(int i) { share &= ~(1 << i); }
            public void clear() { share = 0; }
            public long get() {
                return share;
            }
        }
        final MyInteger mymy = new MyInteger();

        Thread t = new Thread() {
            public void run() {
                AlertDialog.Builder mBuilder;

                mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle(title);
                // mBuilder.setMessage(message); // To be used when there is a message to display... ??
                mBuilder.setMultiChoiceItems(actionsToTake, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (checkedItems[which] = isChecked) {
                            mymy.set(which);
                        } else
                            mymy.reset(which);
                    }
                }).setNegativeButton(
                        SharedMethods.gaStr("Ishm021 cancel"), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mymy.clear();
                                        synchronized (mymy) {
                                            mymy.notify();
                                        }
                                    }
                                }).setPositiveButton(
                        SharedMethods.gaStr("Ishm022 Ok"), new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        synchronized (mymy) {
                                            mymy.notify();
                                        }
                                    }
                                }).setCancelable(false);
                Looper.prepare(); // TODO 800 check up on the looper problem occurring today 08 Nov 2020
                mBuilder.create();
                mBuilder.show();
                Looper.loop();
            }
        };
        t.start();
        synchronized (mymy) {
            try {
                mymy.wait();
            } catch (
                    InterruptedException e) {
                e.printStackTrace();
            }
            return mymy.get();
        }
    }

    private static String md5(String s) {
        try {

// Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

// Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void cancellableText(Context context, String title, String message) {
        AlertDialog.Builder answer = new AlertDialog.Builder(context);
        answer.setTitle(title);
        answer.setMessage(message)
                .setCancelable(true)
                .create()
                .show();
    }

    public static void printParams(String javaFileName, Map<String, String> params) {
        Iterator iParms = params.keySet().iterator();
        String parm;
        while (iParms.hasNext()) {
            parm = (String)iParms.next();
            if(SessionManager.getSOP()!="")System.out.println("   "+javaFileName+": "+(parm+":"+params.get(parm)));
        }
    }

    /// Load user data from the server and store it
    public static void getUserData(final Context context,RequestQueue requestQueue){
        final RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        String url = "getUserdata.php";
        StringRequest request = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {

                        if(SessionManager.getSOP()!="")System.out.println("SharedMethods.java getUserData() "+response);
                        try {
                            JSONArray responses = new JSONArray(response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String currency = jsonObject.getString("currency");
                            String accumulatedAmount = jsonObject.getString("amount");
                          /*  String payableAmount = jsonObject.getString("payableAmount");
                            String payOutFee = jsonObject.getString("payOutFee");
                            String chargeRate = jsonObject.getString("chargeRate");*/

                            SessionManager.setCurrencyAmount(currency+accumulatedAmount);
                            SessionManager.setWithdrawableAmount(accumulatedAmount); // Total cumulative commission
                            SessionManager.setCurrency(currency);

                         /*   SessionManager.setPayableAmount(payableAmount);
                            SessionManager.setPayOutFee(payOutFee); // Rational intuitive withdrawal fee
                            SessionManager.setChargeRate(chargeRate); // Safaricom withdrawal fee*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                SharedMethods.genericErrorHandler
        ) {
            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String credentials = "choop:choop";
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Content-Type", "application/json");headers.put("Authorization", auth);
                return headers;
            }*/@Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("IMEI", SessionManager.checkAndGenerateImei());
                params.put("phoneNumber", SessionManager.getPhone());
                return params;
            }
        };

        mRequestQueue.add(request);
    }


    public static void alertDialog(final Context context, final String title, String message)  {
        alertDialogWithIntent(context, title, message, false, null);
    }

    public static void alertDialogWithCancel(final Context context, final String title, String message, boolean cancelable)  {
        alertDialogWithIntent(context, title, message, cancelable, null);
    }

    public static void alertDialogWithIntent(final Context context, final String title, String message, boolean cancelable, final Intent intent)  {
        AlertDialog.Builder mbuilder = new AlertDialog.Builder(context);
        mbuilder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(SharedMethods.gaStr("Ishm002 OK"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (intent != null) {
                            context.startActivity(intent);
                        }
                    }
                })
                .setCancelable(cancelable)
                .create()
                .show();
    }
//        final Integer lock = 0;
//        final int CLICKED_INIT = -1, CLICKED_LONG = 1, CLICKED_SHORT = 2;
//        class MyClicker {
//            boolean executed = false;
//
//            public boolean execute() {
//                synchronized (lock) {
//                    if(!executed) {
//                        executed = true;
//                        return true;
//                    } else {
//                        return false;
//                    }
//                }
//            }
//        };
//
//        final MyClicker clickDetect = new MyClicker();

    public static void makeWithdrawals(Context context) {

        if (SessionManager.getPhone() != null) {

            //int withdrawableAmount = Integer.parseInt(SessionManager.getWithdrawableAmount());
            int payoutamount = Integer.parseInt(SessionManager.getWithdrawableAmount());
            // TODO 801 - we need all hard coded figures to be database stored and properly documented.
            if (payoutamount < 10) {
                widthdrawalAlert(context, SharedMethods.gaStr("Ishm023 error making withdrawal") + SessionManager.getCurrency() + " " + 10);
            } else {
                //System.out.println("SharedMethod.java, makeWithdrawals() 1085 payoutFee "+SessionManager.getPayOutFee()+" ChargedRate"+SessionManager.getChargeRate()+" PayableAmount "+SessionManager.getPayableAmount()+" Phone "+SessionManager.getPhone()+" Currency "+SessionManager.getCurrency());
                RequestForComissionPayOut(context, SessionManager.getPhone(), SessionManager.getCurrency(), SessionManager.getWithdrawableAmount());
            }
        } else {
            widthdrawalAlert(context, SharedMethods.gaStr("Ishm024 error requesting payment" ));
        }

    }

    public static void widthdrawalAlert(final Context context, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message)
                .create()
                .show();
    }

    public static JSONArray RequestForComissionPayOut(final Context context,String Phone,String currency,String withdrawalAmount){

        final GenericHttpJsonRequest genHttpJSONrequest = new GenericHttpJsonRequest();

        //Removing + from the phone number
        String phoneTrim= Phone.replace("+", "");
        JSONArray response = null; // initialized to null in case of nothing returned or JSONException...

        ///
        HttpInterface hpi = new HttpInterface() {
            @Override
            public void onMyResponse(JSONArray response) {
                System.out.println ("SharedMethod.java : RequestForComissionPayOut() = JSONArray = " + response.toString());
                try {
                    JSONObject phpMessage = response.getJSONObject(0);
                    JSONObject requestpayment = response.getJSONObject(1);
                    // TODO test G We need to check on the dashboard.class new intent, versus the finish(). We are renewing the intent without considering updating it's status instead.
                    Intent intent = new Intent(context, dashboardClass);
                    alertDialogWithIntent(context, SharedMethods.gaStr("Ishm025 Payment Request"), requestpayment.getString("message"), true, intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(JSONArray response) {

            }

            @Override
            public void onJSONException(JSONException e) {

            }
        };

        try {
            JSONObject requestpayment = new JSONObject()
                    .put("AppID", SessionManager.getAPPNAME())
                    .put("phone", phoneTrim)
                    .put("currency",currency )
                    .put("amunt",withdrawalAmount);

            genHttpJSONrequest.genericHttpcallBack(context, "B2Cpayments/init_payment.php", requestpayment, hpi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }



    public static String modalTextFieldDialog(final Context context, final String title, final String textfieldHint, final String textView, final boolean acceptEmptyTextFormField) {

        class MyInteger {
            String myInt;


            public void set(String i) {
                myInt = i;
            }

            public String get() {
                return myInt;
            }


        }
        final MyInteger mymy = new MyInteger();


        Thread t = new Thread() {
            public void run() {

                AlertDialog.Builder mBuilder;

                mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle(title);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView txttitle = new TextView(context);
                txttitle.setText(textView);
                txttitle.setPadding(0, 0, 0, 5);
                final EditText errorTextField = new EditText(context);
                linearLayout.setPadding(16, 2, 16, 2);
                linearLayout.addView(txttitle);
                linearLayout.addView(errorTextField);
                errorTextField.setMaxLines(40);
                errorTextField.setMinLines(20);
                errorTextField.setHeight(200);
                errorTextField.setWidth(300);
                errorTextField.setPadding(10, 10, 10, 3);
                errorTextField.setLeft(100);
                errorTextField.setRight(100);
                errorTextField.setBackgroundResource(editTextstyling);
                errorTextField.setHint(textfieldHint);
                errorTextField.setGravity(Gravity.LEFT);
                errorTextField.setGravity(Gravity.TOP);
                mBuilder.setView(linearLayout);

                mBuilder.setNegativeButton(SharedMethods.gaStr("Ishm016 cancel"), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO when cancelled we clear stored data in shared preference

                                dialog.dismiss();
                                synchronized (mymy) {
                                    mymy.notify();
                                }
                            }
                        }).setPositiveButton(SharedMethods.gaStr("Ishm012 Ok"), new
                        DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final String errordata = errorTextField.getText().toString().trim();
                                // mymy.set(which);

                                if (acceptEmptyTextFormField == true && !errordata.isEmpty()) {

                                    mymy.set(errordata);
                                } else if (acceptEmptyTextFormField == false && errordata.isEmpty()) {
                                    mymy.set(errordata);
                                    modalTextFieldDialog(context, title, textfieldHint, textView, acceptEmptyTextFormField);
                                }
                                synchronized (mymy) {
                                    mymy.notify();
                                }
                            }
                        }).setCancelable(false);
                Looper.prepare(); // TODO 800 check up on the looper problem occurring today 08 Nov 2020
                mBuilder.create();
                mBuilder.show();

                Looper.loop();
            }
        };

        t.start();
        synchronized (mymy) {
            try {
                mymy.wait();
            } catch (
                    InterruptedException e) {
                e.printStackTrace();
            }
            return mymy.get();
        }
    }


    public static JSONArray ErrorReportingByTheUsers(final String dashBoardID, final String showID, final String imagpathpass, final String imagepass, final String activityfrom, final Context context, final String typeOfReporting, JSONObject jsonAnswer){
        String whatIstheError1 = null;
        String describeError1 = null;
        String giveaReasonWhyitsAnerror1 = null;
        String howshoulditBedone1 = null;
        try {
            whatIstheError1 = jsonAnswer.getString("whatIstheError");
            describeError1 = jsonAnswer.getString("describeError");
            giveaReasonWhyitsAnerror1 = jsonAnswer.getString("giveaReasonWhyitsAnerror");
            howshoulditBedone1 = jsonAnswer.getString("howshoulditBedone");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (SharedMethods.SOP) System.out.println("SharedMethod reportError() Strings from jsonObject describeError1 "+describeError1+ " whatIstheError1 "+ whatIstheError1);

        final GenericHttpJsonRequest genHttpJSONrequest = new GenericHttpJsonRequest();
        JSONObject requestpayment = new JSONObject();
        JSONObject phpMessage;

        JSONArray response = null; // initialized to null in case of nothing returned or JSONException...

        HttpInterface httpInterface = new HttpInterface() {
            @Override
            public void onMyResponse(JSONArray response) {
                if (SharedMethods.SOP) System.out.println ("SharedMethod.java : ErrorReportingByTheUsers = JSONArray = " + response.toString());
                try {
//                    phpMessage = response.getJSONObject(0);
//                    requestpayment = response.getJSONObject(1);
                    widthdrawalAlert(context,response.getJSONObject(1).getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMyError(JSONArray response) {

            }

            @Override
            public void onJSONException(JSONException e) {

            }
        };

        try {
            requestpayment
                    .put("appName", SessionManager.getAPPNAME())
                    .put("phone", SessionManager.getPhone())
                    .put("currency", SessionManager.getCurrency() )
                    .put("dashBoardID", dashBoardID)
                    .put("showID", showID)
                    .put("imagepathpass", imagpathpass)
                    .put("imagepass", imagepass)
                    .put("typeOfReporting", typeOfReporting)
                    .put("activityFrom", activityfrom)
                    .put("error",whatIstheError1)
                    .put("errorDescription",describeError1)
                    .put("reasonForError",giveaReasonWhyitsAnerror1)
                    .put("errorCorrection",howshoulditBedone1);

            if (SharedMethods.SOP) System.out.println("ErrorReportingByTheUsers = JSONArray"+requestpayment);

            genHttpJSONrequest.genericHttpcallBack(context, "reporterror.php", requestpayment, httpInterface);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static JSONArray  getMyAppSettings(final Context context){

        final GenericHttpJsonRequest genHttpJSONrequest = new GenericHttpJsonRequest();
        JSONObject requestpayment = new JSONObject();
        JSONObject phpMessage;

        JSONArray response = null; // initialized to null in case of nothing returned or JSONException...

        HttpInterface hpi = new HttpInterface() {
            @Override
            public void onMyResponse(JSONArray response) {
                if (response != null) {
                    try {
                        if(SOP) System.out.println ("SharedMethod.java : getMyAppSettings() = JSONArray = " + response.toString());
                        JSONObject phpMessage = response.getJSONObject(0);
                        JSONObject respString = response.getJSONObject(1);
                        String subjesctHead = respString.getString("subject");
                        String body = respString.getString("sharedbodyContent");
                        String appLinks = respString.getString("appLink");
                        String sytemoutprint = respString.getString("sytemoutprint");
                        String toast = respString.getString("toast");
                        String sharedAppData = respString.getString("sharedAppData");
                        SessionManager.setTOAST(toast);
                        SessionManager.setSOP(sytemoutprint);

                        SessionManager.setGENERALAPPSETTINGS(respString);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // TODO 800 what do we do when nothing has been returned from the HTTP calls? In general, all over?
                    //  We can apply many strategies but we need to do something.
                }
            }

            @Override
            public void onMyError(JSONArray response) {

            }

            @Override
            public void onJSONException(JSONException e) {

            }
        };

        try {
            requestpayment
                    .put("AppID", SessionManager.getAPPNAME())
            ;

            genHttpJSONrequest.genericHttpcallBack (context, "appSettings.php", requestpayment, hpi);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void deleteImagesForTileDeleted(Context context,String ownerPhone,String DashboardID,String AppID,String ShowID){
        // TODO 804 Create the method which will be deleting images when the tile is moved from active (tileAvailability 0) to deleted (tileAvailbility 2)
    }

    public static int getPhoneOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    };

    public static boolean SOP(){
        return SessionManager.getSOP().equals("1");
    }

    public static boolean TOAST(){
        return SessionManager.getTOAST().equals("1");
    }

    public static boolean checkInternetConnection() {
        ConnectivityManager checkconnection = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return checkconnection.getActiveNetworkInfo() != null && checkconnection.getActiveNetworkInfo().isConnected();
    }



    /*public static String strLu(final String stringID, final String result, final String description){

        hashMap.put(stringID, new SharedMethod.QueueAppStringData(result,description));
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        String url = "appString/sendAppStringData.php";
        StringRequest request = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response strLu() sharedMethods.java "+response);

                        try {
                            JSONArray responses = new JSONArray(response);
                            JSONObject jsonObject = responses.getJSONObject(0);
                            String result = jsonObject.getString("code");
                            if (result.equals("success")){
                                Iterator iterator = hashMap.entrySet().iterator();
                                while (iterator.hasNext()) {
                                    Map.Entry hashit = (Map.Entry) iterator.next();
                                    if (hashit.getKey().equals(stringID)){
                                        iterator.remove();
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                genericErrorHandler
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("language", SessionManager.getMyISOLANGUAGE());
                params.put("stringID",stringID);
                params.put("result",result);
                params.put("description",description);

                System.out.println(context+" values "+params);
                return params;
            }
        };

        mRequestQueue.add(request);

        return getAppStrings(context,stringID,result);
    }*/


    public static void sendQueuedStringData(){
        Iterator iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry hashit = (Map.Entry) iterator.next();
            System.out.println("Key: "+hashit.getKey() + " & Value: " + ((SharedMethods.QueueAppStringData)hashit.getValue()).description);
            //strLu(hashit.getKey().toString(), ((SharedMethod.QueueAppStringData)hashit.getValue()).result, ((SharedMethod.QueueAppStringData)hashit.getValue()).description);
            gaStr(hashit.getKey().toString());

        }


    }

/*
    public  static String getAppStrings(final Context xx,String stringID,String result)  {
        RequestQueue mRequestQueue = Volley.newRequestQueue(xx);
        String url = "appString/getAppString.php";
        StringRequest request2 = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response getAppStrings() sharedMethods.java " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String serverCRC32 = jsonObject.getString("CRC32");
                            String sharepreCRC = SessionManager.getAppString().getString("CRC32");

                            if (serverCRC32.equals(sharepreCRC)){
                                // we do not load app string from the server
                            }else{
                                //updating shared preference
                                SessionManager.setAppString(jsonObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                genericErrorHandler

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("language", SessionManager.getMyISOLANGUAGE());

                System.out.println(" parameters "+params);
                return params;
            }
        };

        mRequestQueue.add(request2);
        try {
            return  SessionManager.getAppString().getString(stringID);
        } catch (JSONException e) {
            e.printStackTrace();
            return result;
        }

    }
*/

    public  static String gaStr(String stringID)  {
        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        String url = "appString/getAppString.php";
        StringRequest request2 = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response getAppStrings() sharedMethods.java " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String serverCRC32 = jsonObject.getString("CRC32");
                            if (SessionManager.getAppString()!=null) {
                                String sharepreCRC = SessionManager.getAppString().getString("CRC32");

                                if (serverCRC32.equals(sharepreCRC)) {
                                    // we do not load app string from the server
                                } else {
                                    //updating shared preference
                                    SessionManager.setAppString(jsonObject);
                                }
                            }else{
                                SessionManager.setAppString(jsonObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                genericErrorHandler

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("language", SessionManager.getMyISOLANGUAGE());

                System.out.println(" parameters "+params);
                return params;
            }
        };

        if (checkInternetConnection()) {
            mRequestQueue.add(request2);
        }
        try {

            return  SessionManager.getAppString()!=null?SessionManager.getAppString().getString(stringID):stringID;
        } catch (JSONException e) {
            e.printStackTrace();
            return stringID;
        }

    }



    public  static void loadAppstringData(final Context xx)  {
        RequestQueue mRequestQueue = Volley.newRequestQueue(xx);
        String url = "appString/getAppString.php";
        StringRequest request2 = new StringRequest(Request.Method.POST, SessionManager.getURL() + url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response getAppStrings() sharedMethods.java 44 " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String serverCRC32 = jsonObject.getString("CRC32");
                            //SessionManager.setAppString(jsonObject);
                            if (SessionManager.getAppString()!=null){

                                if (SessionManager.getAppString().has("CRC32")) {
                                    String sharepreCRC = SessionManager.getAppString().getString("CRC32");
                                    System.out.println("CRC EXISTS HERE");
                                    if (serverCRC32.equals(sharepreCRC) || serverCRC32.equals("") ||serverCRC32==null){
                                        // we do not load app string from the server
                                    }else{
                                        //updating shared preference
                                        SessionManager.setAppString(jsonObject);
                                    }
                                }else{
                                    SessionManager.setAppString(jsonObject);
                                }


                            }else{
                                SessionManager.setAppString(jsonObject);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                genericErrorHandler

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("appName", SessionManager.getAPPNAME());
                params.put("language", SessionManager.getMyISOLANGUAGE());

                System.out.println(" parameters "+params);
                return params;
            }
        };

        mRequestQueue.add(request2);

    }

}
