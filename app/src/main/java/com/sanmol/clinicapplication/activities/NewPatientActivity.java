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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;



public class NewPatientActivity extends AppCompatActivity {
    EditText pfname, plname, pcontact, pemail, ppass, pconfirmpass;
    Button submitBTN;
    static TextView tvdob;
    ImageView ivdob;
    RadioGroup rggender;
    RadioButton rbgender, rbmale, rbfemale, rbother;
    DatabaseAdapter databaseAdapter;
    Toolbar toolbar;
    FrameLayout loader_layout;
    String gender, department, doctor, reason;
    MaterialBetterSpinner deptspnr, doctorspnr, reasonspnr;
    List<String> deptArray = new ArrayList<String>();
    List<Integer> dept_idArray = new ArrayList<Integer>();
    List<String> doctorArray = new ArrayList<String>();
    List<Integer> doctor_idArray = new ArrayList<Integer>();
    List<String> reasonArray = new ArrayList<String>();
    List<Integer> reason_idArray = new ArrayList<Integer>();
    static final int DATE_DIALOG_ID = 99;
    int dept_id, reason_id, doctor_id;
    public String title = "Success";
    public static int npaFlag = 0;
    long userInteractionTime = 0;
    JSONObject jsonobj;
    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_patient);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Checking In");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent main = new Intent(NewPatientActivity.this, MainActivity.class);
                startActivity(main);
                finish();
            }
        });


        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter = databaseAdapter.open();

        initialize();
        new GetDepartmentReasonWS().execute();

        if (getIntent().getExtras() != null) {


            String contact = getIntent().getExtras().getString("contact");
            String email = getIntent().getExtras().getString("email");
            String dob = getIntent().getExtras().getString("dob");
            int deptPos = (getIntent().getExtras().getInt("department_id")) - 1;
            int doctorPos = (getIntent().getExtras().getInt("doctor_id")) - 1;
            int reasonPos = (getIntent().getExtras().getInt("reason_id")) - 1;
            Log.e("ID's New ", "department_id " + deptPos + " doctor_id " + doctorPos + " reason_id " + reasonPos);
            pcontact.setText(contact);
            pemail.setText(email);
            tvdob.setText(dob);
            /*deptspnr.setSelection(deptPos);
            doctorspnr.setSelection(doctorPos);
            reasonspnr.setSelection(reasonPos);*/

        }

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

        submitBTN.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String fname = pfname.getText().toString();
                String lname = plname.getText().toString();
                String contact = pcontact.getText().toString();
                String email = pemail.getText().toString();
                Boolean emailValid = MainActivity.isValidEmail(email);
                String dob = tvdob.getText().toString();

                if (rbmale.isChecked())
                    gender = rbmale.getText().toString();
                else if (rbfemale.isChecked())
                    gender = rbfemale.getText().toString();
                else
                    gender = rbother.getText().toString();


                if (fname.equals("")) {
                    pfname.setError("First name cannot be empty.");
                } else if (lname.equals("")) {
                    plname.setError("Last name cannot be empty.");
                } else if (contact.equals("")) {
                    pcontact.setError("Contact number cannot be empty.");
                } else if (dob.equals("")) {
                    tvdob.setError("Choose your birthdate.");
                } else if (email.equals("")) {
                    pemail.setError("Email cannot be empty.");
                } else if (!emailValid) {
                    pemail.setError("Email-ID is invalid.");
                } else {

                    // databaseAdapter.insertEntry(fname, lname, dob, department, doctor, reason, contact, email);//, password, confirmPassword);
                    JSONArray jsonArray = new JSONArray();
                    jsonobj = new JSONObject();

                    try {

                        jsonobj.put("first_name", pfname.getText().toString());
                        jsonobj.put("last_name", plname.getText().toString());
                        jsonobj.put("dob", tvdob.getText().toString());
                        jsonobj.put("email_id", pemail.getText().toString());
                        jsonobj.put("phone_no", pcontact.getText().toString());
                        jsonobj.put("gender", gender);
                        jsonobj.put("dept_id", dept_id);
                        jsonobj.put("doctor_id", doctor_id);
                        jsonobj.put("reason_id", reason_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonArray.put(jsonobj);

                    jsonString = jsonArray.toString().replaceFirst("\\[", "").replaceAll("\\}\\]\\}\\]", "}]}");
                    jsonString = jsonArray.toString().substring(1, jsonArray.toString().length() - 1);
                    Log.d("jsonString", "" + jsonString);
                    new AsyncCallSubmitPatientWS(jsonString).execute();

                }
            }
        });

        ivdob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dFragment = new DatePickerFragment();

                // Show the date picker dialog fragment
                dFragment.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        rggender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbgender = (RadioButton) findViewById(checkedId);
                gender = rbgender.getText().toString();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // flag = 1;
        Intent main = new Intent(NewPatientActivity.this, MainActivity.class);
        // main.putExtra("flag", "" + flag);
        startActivity(main);
        finish();
    }

    public void successPopup(final String titleStr, final String msg, final String code, final String data, final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View dialogSignature = layoutInflater.inflate(R.layout.success_popup, null);

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                context);

        final TextView tvmsg, tvappointmentid, tvroomno, tvdoctorname;
        String appointment_id = "0", doctor_name = "", room_no;
        alertDialogBuilder.setView(dialogSignature);
        TextView title = (TextView) dialogSignature.findViewById(R.id.title);
        title.setText(titleStr);

        tvmsg = (TextView) dialogSignature.findViewById(R.id.tvmsg);
        tvappointmentid = (TextView) dialogSignature.findViewById(R.id.tvappointmentid);
        tvroomno = (TextView) dialogSignature.findViewById(R.id.tvroomno);
        tvdoctorname = (TextView) dialogSignature.findViewById(R.id.tvdoctorname);

        if (code.equals("200")) {
            JSONObject subJsonObject = null;
            try {
                Log.e("success", "");
                subJsonObject = new JSONObject(data);
                appointment_id = subJsonObject.getString("appointment_id");
                doctor_name = subJsonObject.getString("doctor_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tvappointmentid.setVisibility(View.VISIBLE);
            tvroomno.setVisibility(View.VISIBLE);
            tvdoctorname.setVisibility(View.VISIBLE);
            tvappointmentid.setText("Your Appointment ID : " + appointment_id);
            tvroomno.setText("Your Room No : " + appointment_id);
            tvdoctorname.setText("Doctor Name : " + doctor_name);
        } else if (code.equals("400")) {
            Log.e("failure", "");
            tvmsg.setVisibility(View.VISIBLE);
            tvmsg.setText(msg);
        }


        Button dialog_btn_positive = (Button) dialogSignature.findViewById(R.id.dialog_btn_positive);
        dialog_btn_positive.setText("OK");
        final android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        new CountDownTimer(150000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent main = new Intent(NewPatientActivity.this, MainActivity.class);
                startActivity(main);
            }
        }.start();

        dialog_btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pfname.setText("");
                plname.setText("");
                pcontact.setText("");
                pemail.setText("");
                tvdob.setText("");
                Intent main = new Intent(NewPatientActivity.this, NewPatientActivity.class);
                startActivity(main);
//
            }
        });
    }

    public void initialize() {
        pfname = (EditText) findViewById(R.id.et_pfname);
        plname = (EditText) findViewById(R.id.et_plname);
        pcontact = (EditText) findViewById(R.id.et_pcontact);
        pemail = (EditText) findViewById(R.id.et_pemail);
        loader_layout = (FrameLayout) findViewById(R.id.loading);

        tvdob = (TextView) findViewById(R.id.tv_dob);

        ivdob = (ImageView) findViewById(R.id.iv_dob);

        rggender = (RadioGroup) findViewById(R.id.rg_gender);
        rbmale = (RadioButton) findViewById(R.id.rb_male);
        rbfemale = (RadioButton) findViewById(R.id.rb_female);
        rbother = (RadioButton) findViewById(R.id.rb_other);

        deptspnr = (MaterialBetterSpinner) findViewById(R.id.deptspnr);
        doctorspnr = (MaterialBetterSpinner) findViewById(R.id.doctorspnr);
        reasonspnr = (MaterialBetterSpinner) findViewById(R.id.reasonspnr);

        submitBTN = (Button) findViewById(R.id.new_patient);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseAdapter.close();
    }


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
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            // Create a TextView programmatically.
            TextView tv = new TextView(getActivity());

            // Create a TextView programmatically
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
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
            tvdob.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("/").append(mDay).append("/")
                    .append(mYear).append(" "));
            Log.d("Date:", "" + tvdob.getText().toString());


        }
    }

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
                Toast.makeText(NewPatientActivity.this, "Network Error.", Toast.LENGTH_LONG).show();
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
                    ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(NewPatientActivity.this, android.R.layout.simple_spinner_dropdown_item, deptArray);
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
                    ArrayAdapter<String> reasonsAdapter = new ArrayAdapter<String>(NewPatientActivity.this, android.R.layout.simple_spinner_dropdown_item, reasonArray);
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
                Toast.makeText(NewPatientActivity.this, "Network error", Toast.LENGTH_LONG).show();
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
                    ArrayAdapter<String> doctorAdapter = new ArrayAdapter<String>(NewPatientActivity.this, android.R.layout.simple_spinner_dropdown_item, doctorArray);
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

    private class AsyncCallSubmitPatientWS extends AsyncTask<String, Void, String> {
        ApiClass apiClass;
        String res;
        String jsonString;

        public AsyncCallSubmitPatientWS(String jsonString) {
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
            res = apiClass.submitPatient(jsonString);
            return res;
        }

        protected void onPostExecute(String response) {
            Log.e("res", "" + res);
            if (response.equals("TimeOut")) {
                Toast.makeText(NewPatientActivity.this, "Error invoking in webservice.", Toast.LENGTH_LONG).show();
                loader_layout.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    String code = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    String result = jsonObject.getString("result");
                    String data = jsonObject.getString("data");


                    successPopup(result, message, code, data, NewPatientActivity.this);

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
