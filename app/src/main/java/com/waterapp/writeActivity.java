package com.waterapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import backend.HttpUtility;
import backend.Municipality;
import backend.Province;

public class writeActivity extends AppCompatActivity
{
    String title,content,date,comments,Uprovince,Umunicipality;
    TextView subject,contents,dates,comment;
    EditText cont,tit;
    Spinner province_Spinner,municipality_Spinner;
    ArrayAdapter<String> ProvinceAdapter;
    ArrayAdapter<String> MunicipalityAdapter;
    ArrayList<String> provinces = new ArrayList<>();
    ArrayList<String> municipalities = new ArrayList<>();
    JSONObject jData = null;
    JSONObject userJson = null;
    JSONArray data;
    Province provincesArray[];
    Municipality municipalityArray[];
    ProgressDialog b;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        cont=(EditText) findViewById(R.id.cont);
        final EditText ttle = (EditText)findViewById(R.id.ttl);
        Button sub=(Button)findViewById(R.id.submit_id);
        sub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userid = getIntent().getExtras().getString("userid").toString();
                report(userid,ttle.getText().toString(),cont.getText().toString());
            }
        });
    }
    public void report(final String userid,final String title,final String report)
    {
        new AsyncTask<String,String,String>()
        {
            int succes;
            String message;
            JSONObject data;
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                b = new ProgressDialog(writeActivity.this);
                writeActivity.this.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        b.setTitle("");
                        b.setCancelable(true);
                        b.setMessage("Please wait...");
                        b.show();
                    }
                });
            }
            @Override
            protected String doInBackground(String... param)
            {
                try
                {
                    Map<String,String> params = new HashMap<String,String>();
                    String requestURL = "http://192.168.43.90/waterapp/services/addreport.json";
                    params.put("userid",userid);
                    params.put("reporttitle",title);
                    params.put("reportcontent",report);
                    try
                    {
                        HttpUtility.sendPostRequest(requestURL,params);
                        String[] response = HttpUtility.readMultipleLinesRespone();
                        StringBuffer responseBuffer = new StringBuffer();
                        for(String line : response)
                        {
                            responseBuffer.append(line).append("\n");
                        }
                        userJson = new JSONObject(responseBuffer.toString());
                        succes=userJson.getInt("success");
                        message=userJson.getString("msg").toString();
                    }
                    catch (IOException ex) {ex.printStackTrace();}
                    HttpUtility.disconnect();
                }
                catch (Exception e){return null;}
                return null;
            }
            @Override
            protected void onPostExecute(String s)
            {
                b.dismiss();
                //Toast.makeText(reportActivity.this,"Report sent!",Toast.LENGTH_LONG).show();
            }
        }.execute();
    }
}
