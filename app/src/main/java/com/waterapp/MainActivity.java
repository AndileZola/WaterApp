package com.waterapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.usageAdapter;
import backend.HttpUtility;
import backend.usageModel;

public class MainActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private static usageAdapter adapter;
    //private static List<usageModel> data;
    private List<usageModel> mUsageItems = new ArrayList<>();
    JSONObject usageJson = null;
    ProgressDialog b;
    String userId,firstname,lastname,meternumber,address,municicipal;
    TextView Susername,Smeternumber;
    public static TextView totalBill;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.busesList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        adapter = new usageAdapter(MainActivity.this,mUsageItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        Intent i = getIntent();
        userId      = i.getExtras().getString("userid");
        getUsage(userId);
        firstname   = i.getExtras().getString("firstname");
        lastname    = i.getExtras().getString("lastname");
        meternumber = i.getExtras().getString("meternumber");
        address     = i.getExtras().getString("address");
        municicipal = i.getExtras().getString("municicipal");

        Susername    = (TextView) findViewById(R.id.username);
        Smeternumber = (TextView) findViewById(R.id.meternumber);
        totalBill    = (TextView) findViewById(R.id.total);

        Susername.setText(": "+firstname+" "+lastname);
        Smeternumber.setText(": "+meternumber);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(MainActivity.this,reportActivity.class);
                startActivity(i);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.action_logout)
        {
            Intent i = new Intent(MainActivity.this,loginActivity.class);
            startActivity(i);
        }
        else if(id == R.id.action_print)
        {
            Intent i = new Intent(MainActivity.this,ActivityStatement.class);
            i.putExtra("userid", userId);
            i.putExtra("firstname", firstname);
            i.putExtra("lastname", lastname);
            i.putExtra("meternumber", meternumber);
            i.putExtra("address", address);
            i.putExtra("municicipal", municicipal);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public void getUsage(final String Suserid)
    {
        if(!Suserid.equals(""))
        {
            new AsyncTask<String,String,String>()
            {
                String message=null;
                JSONArray data;
                int success;
                String usageid,usagetypename,volume,date,sumvolume,usagetypeid;
                JSONObject usage=null;
                @Override
                protected void onPreExecute()
                {
                    b = new ProgressDialog(MainActivity.this);
                    MainActivity.this.runOnUiThread(new Runnable()
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
                        String requestURL = "http://192.168.43.90/waterapp/services/getusage.json";
                        params.put("userid",Suserid);
                        try
                        {
                            HttpUtility.sendPostRequest(requestURL, params);
                            String[] response = HttpUtility.readMultipleLinesRespone();
                            StringBuffer responseBuffer = new StringBuffer();
                            for(String line : response)
                            {
                                responseBuffer.append(line).append("\n");
                            }
                            usageJson = new JSONObject(responseBuffer.toString());
                            Log.i("TAG", usageJson.toString());
                            if(usageJson!=null)
                            {
                                message=usageJson.getString("msg").toString();
                                success=usageJson.getInt("success");
                                data=usageJson.getJSONArray("data");
                            }
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
                    if(success==1)
                    {
                        usageModel um=null;
                        for(int i=0;i<data.length();i++)
                        {
                            try
                            {
                                usage = data.getJSONObject(i);
                                usageid        = usage.getString("usageId");
                                usagetypename  = usage.getString("usagetypename");
                                date           = usage.getString("createddate");
                                usagetypeid    = usage.getString("usagetypeId");
                                sumvolume      = usage.getString("SumVolume");
                                volume         = usage.getString("Volume");
                                um = new usageModel(usageid,usagetypeid,usagetypename,volume,date,String.valueOf(Integer.parseInt(sumvolume)*2.00));
                                mUsageItems.add(um);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }finally
                            {
                                adapter.notifyDataSetChanged();
                            }
                        }
                        //adapter = new hazardAdapter(getActivity(),mhazardItems);
                        //recyclerView.setAdapter(adapter);
                    }
                }
            }.execute();
        }
        else
        {
            Toast.makeText(MainActivity.this, "No data", Toast.LENGTH_LONG).show();
        }
    }

}
