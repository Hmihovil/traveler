package com.kennethatria.traveller.traveller;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static java.lang.Thread.sleep;


public class StartActivity extends AppCompatActivity  {

    EditText origin_edit_view;
    EditText destination_edit_view;
    TextView txtDate;
    String user_origin, user_destination, travel_date;
    Long token_valid_time;
    String token_time_last_called;
    String access_token;
    String token_type;
    Double location_latitude;
    Double location_longtitude;
    Intent maps_intent;
    public ProgressDialog progress;
    AlertDialog.Builder alertDialog;
    double origin_latitude,origin_longtitude,destination_latitude,destination_longtitude;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Button button_date;
    final int totalProgressTime = 100;
    ProgressDialog progressDialog;


    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        progress = new ProgressDialog(this); //  initializing progress bar

        maps_intent = new Intent(this, MapsActivity.class);//maps intent

        /** start of date picker **/
        txtDate = (TextView)findViewById(R.id.in_date);

        button_date = (Button)findViewById(R.id.button_date);
        button_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        /** end: date picker **/

        origin_edit_view = (EditText)findViewById(R.id.origins_edit_view);
        origin_edit_view.setFocusable(false);
        origin_edit_view.setClickable(true);

        origin_edit_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    travelOptions("origin");
                }
                return true; // return is important...
            }
        });

        destination_edit_view = (EditText)findViewById(R.id.destinations_edit_view);
        destination_edit_view.setFocusable(false);
        destination_edit_view.setClickable(true);


        destination_edit_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    travelOptions("destination");
                }
                return true; // return is important...
            }
        });


        Button start_map_button = findViewById(R.id.start_maps_button); // start button
        start_map_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                /** validators for user input **/
                if(TextUtils.isEmpty(travel_date)){
                    showAlertDialogBox("Please select a valid date !!!");
                }else if(TextUtils.isEmpty(user_origin)) {
                    showAlertDialogBox("Please select a origin !!!");

                    return;
                }else if(TextUtils.isEmpty(user_destination)){
                    showAlertDialogBox("Please select a destination !!!");

                    return;
                }else if(user_origin.equals(user_destination)){
                    showAlertDialogBox("please select another destination ");
                }

                 /** end of validation **/

                /** setting origin and destination latitude and longtitude **/

                // origin
                getLocationLatLong(user_origin);
                maps_intent.putExtra("origin_latitude",location_latitude);
                maps_intent.putExtra("origin_longtitude",location_longtitude);

                // destination
                getLocationLatLong(user_destination);
                maps_intent.putExtra("destination_latitude",location_latitude);
                maps_intent.putExtra("destination_longtitude",location_longtitude);

                /** end **/

                //showProgressCounter();

                progress.show(); // show progress bar

                getAccessToken(); // get api access token
            }

            //
        });

    } // end of onCreate

    public void datePicker(){
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        //txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                        String month = "";
                        String date_today = "";
                        if((monthOfYear + 1) < 10){
                            month = "0"+(monthOfYear + 1);
                        }else{
                            month = getString(monthOfYear + 1);
                        }
                        if(dayOfMonth < 10){
                            date_today = "0"+dayOfMonth;
                        }else{
                            date_today = Integer.toString(dayOfMonth);
                        }

                        travel_date = year + "-" + month + "-" + date_today;
                        txtDate.setText(travel_date);
                        showToast(travel_date);// setting travel date

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    public void travelOptions(final String location){ // returns airport options and inputs in user input : origin && destination

        final CharSequence[] countries={"Uganda","Kenya","Tanzania","Rwanda","Congo"}; //"Burundi"

        AlertDialog.Builder builder=new AlertDialog.Builder(StartActivity.this);
        builder.setTitle("Pick your choice").setItems(countries, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(location=="origin"){
                    origin_edit_view.setText(countries[which]);
                    user_origin = getCountryCodes(countries[which].toString());
                    //Toast.makeText(StartActivity.this,getCountryCodes(countries[which].toString()),Toast.LENGTH_LONG).show();
                }else if(location=="destination"){
                    destination_edit_view.setText(countries[which]);
                    user_destination = getCountryCodes(countries[which].toString());
                   // Toast.makeText(StartActivity.this,getCountryCodes(countries[which].toString()),Toast.LENGTH_LONG).show();
                }
            }
        });

        builder.show();
    }// end of travel options



    public String getCountryCodes(String country){  // returns country airport codes: IATA
        switch(country) {
            case "Uganda":
                return "EBB";
            case "Kenya":
                return "NBO";
            case "Tanzania":
                return "DAR";
            case "Burundi":
                return "BJM";
            case "Rwanda":
                return "KGL";
            case "Congo":
                return "FIH";
            default:
                return "Null";
        }
    }// end of getCountryCodes

    public void getAccessToken(){ // method retrivies access token for api usage

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.lufthansa.com/v1/oauth/token";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        JSONParser parser = new JSONParser();

                        try {

                            Object obj = parser.parse(response);
                            JSONObject jsonObject = (JSONObject)obj;

                            token_valid_time = (Long) jsonObject.get("expires_in");
                            access_token = (String) jsonObject.get("access_token");
                            token_type = (String) jsonObject.get("token_type");

                            fetchLocationFlights(); // call to fetch Location Flights method

                        }catch(Exception e){
                            if(progress != null && progress.isShowing()){
                                progress.hide();
                            }
                            Toast.makeText(StartActivity.this,"Error: receiving token !!! : "+e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //showProgressCounter(10);
                        progress.dismiss();
                        showAlertDialogBox("Token : Contact Administrator \n ______________________________ \n"+error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() // sets  paramater types
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("client_id", getString(R.string.client_id));
                params.put("client_secret",getString(R.string.client_secret));
                params.put("grant_type",getString(R.string.grant_type));
                return params;
            }
        };
        queue.add(postRequest);
        }// end of getAccessToken

    public void fetchLocationFlights(){ // method returns flight logs depending on user location

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://api.lufthansa.com/v1/operations/schedules/"+user_origin+"/"+user_destination+"/"+travel_date+"?limit=3";

        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        JSONParser parser = new JSONParser();
                        try{

                            if(progress != null && progress.isShowing()){
                                progress.hide();
                            }

                            Object obj = parser.parse(response);
                            JSONObject jsonObject = (JSONObject)obj;

                            JSONObject ScheduleResource = (JSONObject)jsonObject.get("ScheduleResource");

                            JSONArray Schedule = (JSONArray) ScheduleResource.get("Schedule");

                            JSONObject obj_two = (JSONObject)Schedule.get(0);

                            Object flight_obj = obj_two.get("Flight");

                            if(flight_obj.equals(null)){
                                Log.e("answer","null");
                            }else{
                                if(flight_obj instanceof  JSONObject){

                                    JSONObject jsonObject_flight_details = (JSONObject)flight_obj;

                                    /**flight depature details**/
                                    JSONObject Departure_obj = (JSONObject) jsonObject_flight_details.get("Departure");
                                    JSONObject Departure_obj_schedulted_time = (JSONObject) Departure_obj.get("ScheduledTimeLocal");
                                    String departure_datetime = (String) Departure_obj_schedulted_time.get("DateTime");
                                    String departure_airport = Departure_obj.get("AirportCode").toString();

                                    /**flight arrival details json object**/
                                    JSONObject Arrival_obj = (JSONObject) jsonObject_flight_details.get("Arrival");
                                    JSONObject Arrival_obj_schedulted_time = (JSONObject) Arrival_obj.get("ScheduledTimeLocal");
                                    String Arrival_obj_datetime = (String) Arrival_obj_schedulted_time.get("DateTime");
                                    String Arrival_obj_airport = Departure_obj.get("AirportCode").toString();

                                    Log.e("test", Arrival_obj_datetime + " "+  Arrival_obj_airport );

                                    /** end of arrival details json object**/

                                    String flight_options = "Airport: "+getAirportName(departure_airport) + "\nDeparture: "+departure_datetime // saves string
                                    + "\nArrival:" + Arrival_obj_datetime + "\n____________________";
                                    String[] options = new String[1];
                                    options[0]=flight_options;

                                    showListDialogBox(options);

                                }else if(flight_obj instanceof  JSONArray){

                                    Log.e("test - a",flight_obj.getClass().toString());

                                    JSONArray jsonObject_flight_details = (JSONArray) flight_obj;

                                    String[] options = new String[3];
                                    for(int i = 0; i <= 2; i ++){

                                        JSONObject flightObj = (JSONObject) jsonObject_flight_details.get(i);

                                        /** flight departure details **/
                                        JSONObject Departure_obj_ = (JSONObject) flightObj.get("Departure");
                                        JSONObject Departure_obj_ScheduledTime = (JSONObject)Departure_obj_.get("ScheduledTimeLocal");
                                        String Departure_obj_DateTime = (String)Departure_obj_ScheduledTime.get("DateTime");
                                        String Departure_obj_airport = (String) Departure_obj_.get("AirportCode");


                                        /** flight arrival details **/

                                        JSONObject Arrival_obj_ = (JSONObject) flightObj.get("Arrival");
                                        JSONObject Arrival_obj_ScheduledTime = (JSONObject)Arrival_obj_.get("ScheduledTimeLocal");
                                        String Arrival_obj_DateTime = (String)Arrival_obj_ScheduledTime.get("DateTime");
                                        String Arrival_obj_airport = (String) Arrival_obj_.get("AirportCode");

                                        /** end of flight arrival details **/

                                        String flight_options = "Airport: "+getAirportName(Departure_obj_airport) + "\nDeparture: "+Departure_obj_DateTime // saves string
                                        + "\nArrival:" + Arrival_obj_DateTime + "\n____________________";

                                        options[i]=flight_options;

                                    }
                                    showListDialogBox(options);

                                } else{
                                    showAlertDialogBox("Notify user \n Message: API Object");
                                    Log.e("message","unknown object");
                                }
                            }
                            progress.dismiss(); // hide progress bar

                        }catch(Exception e){
                            Log.d("error",e.toString());
                            showAlertDialogBox(e.toString());
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // display error code
                        //showProgressCounter(3);
                        progress.dismiss();
                        //Toast.makeText(StartActivity.this,"Flight details error : " +error.toString(),Toast.LENGTH_LONG).show();
                        showAlertDialogBox("API response 401 : Contact Administrator \n "+error.toString());
                    }
                }
        ) {

            // Passing some request headers
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer "+ access_token);
                return headers;
            }
        };
        queue.add(postRequest);
    }

    public void getLocationLatLong(String location){ // returns latitude and longititude of location

        switch(location) {
            case "EBB":
                location_latitude = 0.03999984;
                location_longtitude = 32.43883157;
                break;
            case "NBO":
                location_latitude = -1.318165394;
                location_longtitude = 36.923162974;
                break;
            case "DAR":
                location_latitude = -6.871996512;
                location_longtitude = 39.2011658;
                break;
            case "BJM":
                location_latitude = -3.32107704902;
                location_longtitude = 29.3177770622;
                break;
            case "KGL":
                location_latitude = -1.963042;
                location_longtitude = 30.135014;
                break;
            case "FIH":
                location_latitude = -4.3847817942;
                location_longtitude = 15.4400732397;
                break;
            default:
                break;
        }
    }

    public String getAirportName(String location){ // returns Airport Name

        switch(location) {
            case "EBB":
                return "Entebbe";
            case "NBO":
                return "Nairobi";
            case "DAR":
                return "Dar es Salam";
            case "BJM":
                return "Burundi";
            case "KGL":
                return "Kigali";
            case "FIH":
                return "Congo";
            default:
                return "Null";
        }
    }

    public void showListDialogBox(final String[] listItems){ // displays dialog box with items

        final AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
        builder.setTitle("Flight List ").setItems(listItems, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                showAlertDialogBox(listItems[which].toString());
            }
        });

        builder.show();


    }

    public void showAlertDialogBox(String message){ // displays messages to users

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                                }

                        });

        /**alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });**/

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void showProgressCounter() { // increments counter while dialog progress is running

        final Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progress.incrementProgressBy(1);
            }
        };

        progress.setMax(100);
        progress.setTitle("Loading");
        progress.setMessage("Fetching Flight details ... ");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (progress.getProgress() <= progress
                            .getMax()) {
                        Thread.sleep(50);
                        handle.sendMessage(handle.obtainMessage());
                        if (progress.getProgress() == progress
                                .getMax()) {
                            progress.dismiss();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    public void showToast(String msg){ //used for debugging only
        Toast.makeText(StartActivity.this,msg,Toast.LENGTH_LONG).show();
    }






}