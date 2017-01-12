package com.sanmol.clinicapplication.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanmol.clinicapplication.DatabaseAdapter;
import com.sanmol.clinicapplication.R;
import com.sanmol.clinicapplication.services.ApiClass;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.sanmol.clinicapplication.R.id.contact;

public class ExistingPatientActivity extends AppCompatActivity {
    /*EditText usernameET, passwordET;
    Button loginBTN;
    TextView error;*/
    EditText prefno, pcontact, pemail;
    Button submitExistingPatientBTN;
    DatabaseAdapter databaseAdapter;
    Toolbar toolbar;
    static TextView tvexistingdob;
    ImageView ivdob;
    MaterialBetterSpinner deptspnr, doctorspnr, reasonspnr;
    FrameLayout loader_layout;
    List<String> deptArray = new ArrayList<String>();
    List<Integer> dept_idArray = new ArrayList<Integer>();
    List<String> doctorArray = new ArrayList<String>();
    List<Integer> doctor_idArray = new ArrayList<Integer>();
    List<String> reasonArray = new ArrayList<String>();
    List<Integer> reason_idArray = new ArrayList<Integer>();
    int dept_id, reason_id, doctor_id;
    String department, doctor, reason;
    static final int DATE_DIALOG_ID = 99;
    public String title = "Success";
    JSONObject jsonobj;
    String jsonString;
    String email, dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_patient);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Checking In");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // flag = 0;
                Intent new_patient = new Intent(ExistingPatientActivity.this, MainActivity.class);
                startActivity(new_patient);
                finish();
            }
        });

        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter = databaseAdapter.open();

        initialize();
        new GetDepartmentReasonWS().execute();
       /* fillSpinnerDept();
        fillSpinnerDoctor();
        fillSpinnerReason();*/
        deptspnr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                department = deptArray.get(position);
                dept_id = dept_idArray.get(position);
                Log.e("Item is selected..", department);
                doctorspnr.setVisibility(View.VISIBLE);
                new GetDoctorWS(dept_id).execute();


            }
        });

        doctorspnr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doctor = doctorArray.get(position);
                doctor_id = doctor_idArray.get(position);
                Log.e("Item is selected..", doctor);


            }
        });

        reasonspnr.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reason = reasonArray.get(position);
                reason_id = reason_idArray.get(position);
                Log.e("Item is selected..", reason);


            }
        });

        submitExistingPatientBTN.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String refno = prefno.getText().toString();
                String contact = pcontact.getText().toString();
                email = pemail.getText().toString();
                Boolean emailValid = MainActivity.isValidEmail(email);
                dob = tvexistingdob.getText().toString();


               /* if (email == null && email.equals("") && email.equals(" ")) {
                    pemail.setError("Email cannot be empty.");
                } else if (!emailValid) {
                    pemail.setError("Email-ID is invalid.");
                } else {*/
                if (!refno.equals("") || !contact.equals("") || !email.equals("")) {
                       /* Boolean resultContact = databaseAdapter
                                .contactExist(contact);
                        Boolean resultRefNo = databaseAdapter
                                .refnoExist(refno);
                        Boolean resultEmail = databaseAdapter
                                .emailExist(email);

                        if (resultContact == true || resultEmail == true || resultRefNo == true) {
                            successPopup("Success", ExistingPatientActivity.this);
                        } else
                            registerPopup("Register", ExistingPatientActivity.this);*/
                   /* if (!email.equals("")) {
                        if (!emailValid)
                            pemail.setError("Email-ID is invalid.");
                    } else {*/
                    JSONArray jsonArray = new JSONArray();
                    jsonobj = new JSONObject();

                    try {

                        jsonobj.put("ref_id", refno);
                        jsonobj.put("phone_no", contact);
                        jsonobj.put("email_id", email);
                        jsonobj.put("dept_id", dept_id);
                        jsonobj.put("doctor_id", doctor_id);
                        jsonobj.put("reason_id", reason_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonobj);

                    jsonString = jsonArray.toString().replaceFirst("\\[", "").replaceAll("\\}\\]\\}\\]", "}]}");
                    jsonString = jsonArray.toString().substring(1, jsonArray.toString().length() - 1);
                    Log.e("jsonString", "" + jsonString);
                    new AsyncCallSubmitExistingPatientWS(jsonString).execute();
                    // }

                } else {
                    Toast.makeText(ExistingPatientActivity.this, "Enter Ref No./Contact/Email-ID", Toast.LENGTH_LONG).show();
                }

            }
        });

        ivdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showDialog(DATE_DIALOG_ID);
                // Initialize a new date picker dialog fragment
                DialogFragment dFragment = new DatePickerFragment();

                // Show the date picker dialog fragment
                dFragment.show(getSupportFragmentManager(), "Date Picker");
            }
        });

    }


    public void registerPopup(final String titleStr, final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.register_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);


        alertDialogBuilder.setView(dialogSignature);


        Button dialog_btn_negative = (Button) dialogSignature.findViewById(R.id.dialog_btn_negative);
        dialog_btn_negative.setText("NO");
        Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);
        dialog_btn_positive.setText("YES");
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();


        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //flag=1;
                Intent newPatient = new Intent(ExistingPatientActivity.this, NewPatientActivity.class);
                newPatient.putExtra("contact", "" + contact);
                newPatient.putExtra("email", "" + email);
                newPatient.putExtra("dob", "" + dob);
                newPatient.putExtra("department_id", dept_id);
                newPatient.putExtra("doctor_id", doctor_id);
                newPatient.putExtra("reason_id", reason_id);
                Log.e("ID's Existing ", "department_id " + dept_id + " doctor_id " + doctor_id + " reason_id " + reason_id);
                startActivity(newPatient);
            }
        });

        dialog_btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
                Intent main = new Intent(ExistingPatientActivity.this, MainActivity.class);
                startActivity(main);
            }
        });
    }

    public void successPopup(final String titleStr, final String appointment_id, final String doctor_name, final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.success_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);

        final TextView tvappointmentid, tvroomno, tvdoctorname;

        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        title.setText(titleStr);

        tvappointmentid = (TextView) dialogSignature.findViewById(R.id.tvappointmentid);
        tvroomno = (TextView) dialogSignature.findViewById(R.id.tvroomno);
        tvdoctorname = (TextView) dialogSignature.findViewById(R.id.tvdoctorname);

        tvappointmentid.setVisibility(View.VISIBLE);
        tvroomno.setVisibility(View.VISIBLE);
        tvdoctorname.setVisibility(View.VISIBLE);
        tvappointmentid.setText("Your Appointment ID : " + appointment_id);
        tvroomno.setText("Your Room No : " + appointment_id);
        tvdoctorname.setText("Doctor Name : " + doctor_name);


        Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);
        dialog_btn_positive.setText("OK");
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

       /* new CountDownTimer(3000000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent main = new Intent(ExistingPatientActivity.this, MainActivity.class);
                startActivity(main);
            }
        }.start();*/

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(ExistingPatientActivity.this, MainActivity.class);
                startActivity(main);

            }
        });
    }
/*    public void successPopup(final String titleStr, final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.success_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);


        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        final TextView tvappointmentid = (TextView) dialogSignature.findViewById(R.id.tvappointmentid);
        final TextView tvroomno = (TextView) dialogSignature.findViewById(R.id.tvroomno);
        final TextView tvdoctorname = (TextView) dialogSignature.findViewById(R.id.tvdoctorname);

        title.setText(titleStr);

        Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);
        dialog_btn_positive.setText("OK");
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        int result = databaseAdapter
                .getID();
        tvappointmentid.setText("Your Appointment ID : " + result);
        tvroomno.setText("Room No. : " + result);
        tvdoctorname.setText("Doctor Name : " + doctor);

        new CountDownTimer(150000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent main = new Intent(ExistingPatientActivity.this, MainActivity.class);
                startActivity(main);
            }
        }.start();

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                prefno.setText("");
                pcontact.setText("");
                pemail.setText("");
                tvexistingdob.setText("");
                Intent main = new Intent(ExistingPatientActivity.this, MainActivity.class);
                startActivity(main);
//
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(ExistingPatientActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_HOLO_LIGHT, this, year, month, day);
            Log.e("Month :", "" + month);
            // Create a TextView programmatically.
            TextView tv = new TextView(getActivity());

            // Create a TextView programmatically
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            tv.setLayoutParams(lp);
            tv.setPadding(10, 10, 10, 10);
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            tv.setText("Selected Date is...");
            tv.setTextColor(Color.parseColor("#ff0000"));
            tv.setBackgroundColor(Color.parseColor("#FFD2DAA7"));

            dpd.setTitle("Selected Date is..."); // Uncomment this line to activate it

            // Return the DatePickerDialog
            return dpd;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            int mYear = year;
            int mMonth = monthOfYear;
            int mDay = dayOfMonth;
            tvexistingdob.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("/").append(mDay).append("/")
                    .append(mYear).append(" "));
            Log.d("Date:", "" + tvexistingdob.getText().toString());


        }
    }

    public void initialize() {
        loader_layout = (FrameLayout) findViewById(R.id.loading);
        prefno = (EditText) findViewById(R.id.et_prefno);
        NewPatientActivity.showEditTextsAsMandatory("Reference No.", prefno);
        pcontact = (EditText) findViewById(R.id.et_pcontact);
        NewPatientActivity.showEditTextsAsMandatory("Phone No.", pcontact);
        pemail = (EditText) findViewById(R.id.et_pemail);
        NewPatientActivity.showEditTextsAsMandatory("Email-ID", pemail);
        tvexistingdob = (TextView) findViewById(R.id.tv_dob);

        ////showEditTextsAsMandatory ( tvexistingdob );
        NewPatientActivity.showTextViewsAsMandatory("Date of Birth", tvexistingdob);

        ivdob = (ImageView) findViewById(R.id.iv_dob);


        deptspnr = (MaterialBetterSpinner) findViewById(R.id.deptspnr);
        doctorspnr = (MaterialBetterSpinner) findViewById(R.id.doctorspnr);
        reasonspnr = (MaterialBetterSpinner) findViewById(R.id.reasonspnr);

        submitExistingPatientBTN = (Button) findViewById(R.id.submit_existing_patient);
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }*/

   /* @Override
    public void onUserLeaveHint() {
        if (npaFlag == 0) {
            finish();
            Intent i = getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            super.onUserLeaveHint();
        }
    }*/

    private class GetDepartmentReasonWS extends AsyncTask<String, Void, String> {
        ApiClass apiClass;
        String res;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader_layout.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            apiClass = new ApiClass();
        }

        protected String doInBackground(String... args) {
            res = apiClass.getDepartmentReason();
            return res;
        }

        protected void onPostExecute(String response) {

            Log.d("response ... ", "Hello  " + response);
            if (response == null || response.equals("0") || response.equals("[]") || response.equals("TimeOut")) {
                Toast.makeText(ExistingPatientActivity.this, "Error in invoking webservice.", Toast.LENGTH_LONG).show();
                deptspnr.setVisibility(View.GONE);
                reasonspnr.setVisibility(View.GONE);
                loader_layout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String details = jsonObject.getString("details");
                    JSONObject subJsonObject = new JSONObject(details);
                    String departments = subJsonObject.getString("departments");
                    JSONArray jarrdept = new JSONArray(departments);

                    for (int i = 0; i < jarrdept.length(); i++) {

                        JSONObject jobj = jarrdept.getJSONObject(i);

                        String type = jobj.getString("name");
                        int id = jobj.getInt("id");

                        deptArray.add(type);
                        dept_idArray.add(id);
                    }
                    // Creating adapter for spinner
                    ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(ExistingPatientActivity.this, android.R.layout.simple_spinner_dropdown_item, deptArray);
                    deptspnr.setAdapter(deptAdapter);

                    String reasons = subJsonObject.getString("reasons");
                    JSONArray jarrreasons = new JSONArray(reasons);

                    for (int i = 0; i < jarrreasons.length(); i++) {

                        JSONObject jobj = jarrreasons.getJSONObject(i);

                        String type = jobj.getString("description");
                        int id = jobj.getInt("id");

                        reasonArray.add(type);
                        reason_idArray.add(id);
                    }
                    // Creating adapter for spinner
                    ArrayAdapter<String> reasonsAdapter = new ArrayAdapter<String>(ExistingPatientActivity.this, android.R.layout.simple_spinner_dropdown_item, reasonArray);
                    reasonspnr.setAdapter(reasonsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            loader_layout.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            this.cancel(true);
        }

    }

    private class GetDoctorWS extends AsyncTask<String, Void, String> {
        ApiClass apiClass;
        String res;
        int dept_id;

        public GetDoctorWS(int dept_id) {
            this.dept_id = dept_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader_layout.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            apiClass = new ApiClass();
        }

        protected String doInBackground(String... args) {
            res = apiClass.getDoctor(dept_id);
            return res;
        }

        protected void onPostExecute(String response) {

            Log.d("response ... ", "Hello  " + response);
            if (response == null || response.equals("0") || response.equals("[]") || response.equals("TimeOut")) {
                Toast.makeText(ExistingPatientActivity.this, "Error in invoking webservice.", Toast.LENGTH_LONG).show();
                loader_layout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String details = jsonObject.getString("details");

                    JSONArray jarrdept = new JSONArray(details);

                    for (int i = 0; i < jarrdept.length(); i++) {

                        JSONObject jobj = jarrdept.getJSONObject(i);

                        String type = jobj.getString("doctor_name");
                        int id = jobj.getInt("id");

                        doctorArray.add(type);
                        doctor_idArray.add(id);
                    }
                    // Creating adapter for spinner
                    ArrayAdapter<String> doctorAdapter = new ArrayAdapter<String>(ExistingPatientActivity.this, android.R.layout.simple_spinner_dropdown_item, doctorArray);
                    doctorspnr.setAdapter(doctorAdapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            loader_layout.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            this.cancel(true);
        }
    }

    private class AsyncCallSubmitExistingPatientWS extends AsyncTask<String, Void, String> {
        ApiClass apiClass;
        String res;
        String jsonString;

        public AsyncCallSubmitExistingPatientWS(String jsonString) {
            this.jsonString = jsonString;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loader_layout.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            apiClass = new ApiClass();
        }

        protected String doInBackground(String... args) {
            res = apiClass.submitExistingPatient(jsonString);
            return res;
        }

        protected void onPostExecute(String response) {
            Log.e("res", "" + res);
            if (response.equals("TimeOut")) {
                Toast.makeText(ExistingPatientActivity.this, "Error in invoking webservice.", Toast.LENGTH_LONG).show();
                loader_layout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String code = jsonObject.getString("code");
                    Log.e("code", "" + code);
                    String message = jsonObject.getString("message");
                    String result = jsonObject.getString("result");
                    if (code.equals("200") || result.equals("Succcess")) {
                        String appointment_id = jsonObject.getString("appointment_id");
                        String doctor_name = jsonObject.getString("doctor_name");
                        successPopup(result, appointment_id, doctor_name, ExistingPatientActivity.this);
                    } else if (code.equals("400") || result.equals("Failure")) {
                        registerPopup("Register", ExistingPatientActivity.this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loader_layout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                this.cancel(true);
            }
        }

    }


}


