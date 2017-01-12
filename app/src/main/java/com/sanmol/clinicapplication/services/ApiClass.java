package com.sanmol.clinicapplication.services;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;


public class ApiClass {

    public static int REC_CODE = 3;

    public static String masterAPI = "http://7mmc.sanmol.in/backend/web/Index.php?r=";

    //public static String masterAPI = "http://192.168.0.32/hms/backend/web/index.php?r=";
    ArrayList<NameValuePair> postParameters;
    String res;


    public String LogIn(String username, String password) {

        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("username", username));
        postParameters.add(new BasicNameValuePair("password", password));

        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "user/login", postParameters);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getDepartmentReason() {

        try {
            res = CustomHttpClient.executeHttpGet(masterAPI + "departments/get-departments");
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String getDoctor(int dept_id) {
        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("dept_id", "" + dept_id));
        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "doctor/get-doctors", postParameters);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String submitPatient(String jsonString) {
       /* postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("jsonString", jsonString));*/
        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "user/register-patient", jsonString);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public String exitPatient(String phone_no) {
        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("phone_no", phone_no));
        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "user/exit-patient", postParameters);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public String submitExistingPatient(String jsonString) {
       /* postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("jsonString", jsonString));*/
        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "user/validate-patient", jsonString);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


    public String insertUser(String del_ship_id, String del_deliver_id, String del_mode_of_transport, String del_receiver_name, String del_receiver_mob, String del_receiver_idproof, String del_receiver_sign) {

        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("del_ship_id", del_ship_id));
        postParameters.add(new BasicNameValuePair("del_deliver_id", del_deliver_id));
        postParameters.add(new BasicNameValuePair("del_mode_of_transport", del_mode_of_transport));
        postParameters.add(new BasicNameValuePair("del_receiver_name", del_receiver_name));
        postParameters.add(new BasicNameValuePair("del_receiver_mob", del_receiver_mob));
        postParameters.add(new BasicNameValuePair("del_receiver_idproof", del_receiver_idproof));
        postParameters.add(new BasicNameValuePair("del_receiver_sign", del_receiver_sign));

        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "delivery/insert-delivery", postParameters);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String goodsTrack(String mod, String hawbno) {

        postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("mod", mod));
        postParameters.add(new BasicNameValuePair("hawbno", hawbno));

        try {
            res = CustomHttpClient.executeHttpPost(masterAPI + "tracking/goodtrack", postParameters);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        return res;
    }


}
