package com.waterapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import backend.HttpUtility;
import backend.Municipality;
import backend.Province;

public class registerActivity extends AppCompatActivity
{

    Spinner province_Spinner,municipality_Spinner;
    TextView name,surname,idnum,password,password2,email,cellphone,address,members,cars,rooms,garden;
    String   Uname,Usurname,Uidnum,Upassword,Uconfirmpass,Uemail,Ucellphone,Uaddress,Umembers,Ucars,Urooms,Ugarden,Uprovince,Umunicipality;
    ArrayAdapter<String> ProvinceAdapter;
    ArrayAdapter<String> MunicipalityAdapter;
    ArrayList<String> provinces = new ArrayList<>();
    ArrayList<String> municipalities = new ArrayList<>();
    LinearLayout back;
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
        setContentView(R.layout.activity_register);
        Button submit;
        name= (TextView) findViewById(R.id.name);
        surname= (TextView) findViewById(R.id.surname);
        cellphone= (TextView) findViewById(R.id.cellnumber);
        email= (TextView) findViewById(R.id.email);
        password= (TextView) findViewById(R.id.pass1);
        password2= (TextView) findViewById(R.id.pass2);
        idnum= (TextView) findViewById(R.id.idnumber);
        address= (TextView) findViewById(R.id.address);
        garden= (TextView) findViewById(R.id.name);
        submit      = (Button) findViewById(R.id.register_id);
        province_Spinner = (Spinner) findViewById(R.id.province_spinner);
        ProvinceAdapter = new ArrayAdapter<String>(registerActivity.this,R.layout.spinner_item,provinces);
        province_Spinner.setAdapter(ProvinceAdapter);
        provincesArray = new Province[9];
        getProvinces();
        municipality_Spinner = (Spinner) findViewById(R.id.municipality_spinner);
        MunicipalityAdapter = new ArrayAdapter<String>(registerActivity.this,R.layout.spinner_item,municipalities);
        municipality_Spinner.setAdapter(MunicipalityAdapter);

        province_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                Uprovince = provincesArray[position].getId().toString();
                if (!province_Spinner.getSelectedItem().toString().equals(""))
                {
                    getMunicipalities(Uprovince);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView){}
        });
        municipality_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                if (!municipality_Spinner.getSelectedItem().toString().equals(""))
                {
                    Umunicipality = municipalityArray[position].getId().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Uname   = name.getText().toString();
                Usurname=surname.getText().toString();
                Uidnum=idnum.getText().toString();
                Upassword=password.getText().toString();
                Uconfirmpass=password2.getText().toString();
                Uemail=email.getText().toString();
                Ucellphone=cellphone.getText().toString();
                Uaddress=address.getText().toString();
                signup(Uname,Usurname,Uidnum,Upassword,Uconfirmpass,Uemail,Ucellphone,Uaddress,Umembers,Ucars,Urooms,Ugarden,Uprovince,Umunicipality);
            }
        });
    }
    public void getProvinces()
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                b = new ProgressDialog(registerActivity.this);
                registerActivity.this.runOnUiThread(new Runnable()
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
            protected Void doInBackground(Void... params)
            {
                try
                {
                    URL url = new URL("http://192.168.43.90/waterapp/services/listprovinces.json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";
                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();
                    jData = new JSONObject(json.toString());
                    if (jData.getInt("success") != 1)
                    {
                        System.out.println("Cancelled");
                        return null;
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Exception " + e.getMessage());
                    return null;
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void Void)
            {
                if(jData != null)
                {
                    JSONObject parentObject;
                    JSONObject province;
                    String provinceId = "", provinceName = "";
                    try
                    {
                        parentObject = new JSONObject(jData.toString());
                        data = parentObject.getJSONArray("data");
                        for (int x = 0; x < data.length(); x++)
                        {
                            Province prov = new Province();
                            province = data.getJSONObject(x);
                            provinceId = province.getString("provinceId");
                            provinceName = province.getString("provincename");
                            prov.setProvince(provinceId,provinceName);
                            provincesArray[x] = prov;
                            provinces.add(provincesArray[x].getName());
                        }
                        ProvinceAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {e.printStackTrace();}
                }
                b.dismiss();
            }
        }.execute();
    }
    public void getMunicipalities(final String provinceid)
    {
        MunicipalityAdapter.clear();
        new AsyncTask<String,String,String>()
        {
            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                b = new ProgressDialog(registerActivity.this);
                registerActivity.this.runOnUiThread(new Runnable()
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
                    String requestURL = "http://192.168.43.90/waterapp/services/listmunicipalitybyprovinceid.json";
                    params.put("provinceid",provinceid);
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
                if(userJson != null)
                {
                    String message = "", success = "";
                    JSONObject parentObject;
                    JSONObject province;
                    String provinceId = "", provinceName = "";
                    try
                    {
                        parentObject = new JSONObject(userJson.toString());
                        data = parentObject.getJSONArray("data");
                        municipalityArray = new Municipality[data.length()];
                        for(int x = 0;x < data.length(); x++)
                        {
                            Municipality mun = new Municipality();
                            province = data.getJSONObject(x);
                            provinceId = province.getString("municipalityId");
                            provinceName = province.getString("municipalityname");
                            mun.setMunicipality(provinceId,provinceName);
                            municipalityArray[x] = mun;
                            municipalities.add(municipalityArray[x].getName());
                        }
                        MunicipalityAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {e.printStackTrace();}
                }
                b.dismiss();
            }
        }.execute();
    }
    public void signup(final String Sname,final String Ssurname,final String Sidnum,final String Spassword,final String Sconfirmpass,final String Semail,final String Scellphone,final String Saddress,final String Smembers,final String Scars,final String Srooms,final String Sgarden,final String Sprovince,final String Smunicipality)
    {
        new AsyncTask<String,String,String>()
        {
            String res="";
            @Override
            protected void onPreExecute()
            {
                b = new ProgressDialog(registerActivity.this);
                registerActivity.this.runOnUiThread(new Runnable()
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
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... param)
            {
                try
                {
                    Map<String,String> params = new HashMap<String,String>();
                    String requestURL = "http://192.168.43.90/waterapp/services/adduser.json";
                    params.put("firstname",Sname);
                    params.put("lastname",Ssurname);
                    params.put("idnumber",Sidnum);
                    params.put("telephone",Scellphone);
                    params.put("emailaddress",Semail);
                    //params.put("province",Sprovince);
                    params.put("municipality",Smunicipality);
                    params.put("physicaladdress",Saddress);
                    params.put("password",Spassword);
                    params.put("passwordcomp",Sconfirmpass);
                    params.put("cars","2");
                    params.put("members","2");
                    params.put("gardensize","2");
                    params.put("rooms","2");
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
                        res = userJson.getString("msg");
                        Log.d("TAGG",res.toString());
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
                Toast.makeText(registerActivity.this,"Registration succesful",Toast.LENGTH_LONG).show();
                b.dismiss();
            }
        }.execute();
    }
}
