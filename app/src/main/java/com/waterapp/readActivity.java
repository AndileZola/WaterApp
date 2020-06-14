package com.waterapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class readActivity extends AppCompatActivity
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
        setContentView(R.layout.activity_read);
        cont=(EditText) findViewById(R.id.cont);
        province_Spinner = (Spinner)findViewById(R.id.province_spinner);
        ProvinceAdapter = new ArrayAdapter<String>(readActivity.this,R.layout.spinner_item,provinces);
        province_Spinner.setAdapter(ProvinceAdapter);
        provincesArray = new Province[9];
        getProvinces();
        municipality_Spinner = (Spinner)findViewById(R.id.municipality_spinner);
        MunicipalityAdapter = new ArrayAdapter<String>(readActivity.this,R.layout.spinner_item,municipalities);
        municipality_Spinner.setAdapter(MunicipalityAdapter);

        province_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView,int position, long id)
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
        Button sub=(Button)findViewById(R.id.submit_id);
        sub.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String userid = getIntent().getExtras().getString("userid").toString();
                // Toast.makeText(topicFragment)
                report(userid,cont.getText().toString(),tit.getText().toString(),Umunicipality);
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
                b = new ProgressDialog(readActivity.this);
                readActivity.this.runOnUiThread(new Runnable()
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
                b = new ProgressDialog(readActivity.this);
                readActivity.this.runOnUiThread(new Runnable()
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
    public void report(final String userid,final String report,final String title,final String municipalityid)
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
                b = new ProgressDialog(readActivity.this);
                readActivity.this.runOnUiThread(new Runnable()
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
                    String requestURL = "http://192.168.43.90/waterapp/services/notice.json";
                    params.put("userid",userid);
                    // params.put("title","");
                    params.put("noticecontent",report);
                    params.put("municipalid",municipalityid);
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
                Toast.makeText(readActivity.this,"Report sent!", Toast.LENGTH_LONG).show();
            }
        }.execute();
    }
}
