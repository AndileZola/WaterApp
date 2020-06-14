package com.waterapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapters.hazardAdapter;
import backend.HttpUtility;
import backend.hazardItems;
import interfaces.forumOnclick;

public class reportActivity extends AppCompatActivity implements forumOnclick
{
    private hazardAdapter adapter;
    public FloatingActionButton fb;
    private RecyclerView recyclerView;
    private List<hazardItems> mhazardItems = new ArrayList<>();
    JSONObject notificationJson = null;
    ProgressDialog b;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.hazardItemsLis);
        LinearLayoutManager layoutManager = new LinearLayoutManager(reportActivity.this);
        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new hazardAdapter(reportActivity.this,mhazardItems);
        adapter.setRecyclerViewClickListen(this);
        recyclerView.setAdapter(adapter);
        final String userid = loginActivity.useid;
        getNotifications(userid);
        fb=(FloatingActionButton)findViewById(R.id.fab);
        fb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(reportActivity.this,writeActivity.class);
                i.putExtra("userid",userid);
                startActivity(i);

            }
        });
    }
    @Override
    public void recyclerViewListClicked(View v, String title, String content, String date, String comments, int position)
    {
       /*Intent i = new Intent(reportActivity.this,readActivity.class);
        Bundle bundle = new Bundle();
        i.putExtra("title", title);
        i.putExtra("content", content);
        i.putExtra("date",date);
        i.putExtra("comments", comments);
        startActivity(i);*/

       // getFragmentManager().beginTransaction().replace(R.id.layout_received,fragment ,"Topic Fragment")
               // .addToBackStack(null).commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(2).setVisible(false);
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
            Intent i = new Intent(reportActivity.this,loginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    public void getNotifications(final String Suserid)
    {
        if(!Suserid.equals(""))
        {
            new AsyncTask<String,String,String>()
            {
                String message=null;
                JSONArray data;
                int success;
                String id= null;
                String title=null;
                String text= null;
                String cdate=null;
                String type=null;
                JSONObject notification=null;
                @Override
                protected void onPreExecute()
                {
                    b = new ProgressDialog(reportActivity.this);
                    reportActivity.this.runOnUiThread(new Runnable()
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
                        String requestURL = "http://192.168.43.90/waterapp/services/getreport.json";
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
                            notificationJson = new JSONObject(responseBuffer.toString());
                            Log.i("TAG",notificationJson.toString());
                            if(notificationJson!=null)
                            {
                                message=notificationJson.getString("msg").toString();
                                success=notificationJson.getInt("success");
                                data=notificationJson.getJSONArray("data");
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
                    //Toast.makeText(getActivity(),data.toString(),Toast.LENGTH_LONG).show();
                    b.dismiss();
                    if(success==1)
                    {
                        hazardItems hi=null;
                        for(int i=0;i<data.length();i++)
                        {
                            try
                            {
                                notification = data.getJSONObject(i);
                                id  =notification.getString("reportId");
                                text=notification.getString("reportcontent");
                                cdate=notification.getString("createdate");
                                title =notification.getString("ReportTitle");
                                hi = new hazardItems(id,title,text,type,cdate);
                                mhazardItems.add(hi);
                                Log.d("TTAG",id);
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
            Toast.makeText(reportActivity.this,"No Notification",Toast.LENGTH_LONG).show();
        }
    }
}
