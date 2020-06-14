package com.waterapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import backend.HttpUtility;

public class loginActivity extends AppCompatActivity
{
    TextView signUp,forgotPass;
    Button login;
    JSONObject loginJson = null;
    ProgressDialog b;
    EditText edEmail,edPassword;
    String Semail,Spassword;
    public static String useid;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button)findViewById(R.id.login_id);
        signUp = (TextView)findViewById(R.id.signup_id);
        edEmail = (EditText)findViewById(R.id.email);
        edPassword = (EditText)findViewById(R.id.password);
        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Semail = edEmail.getText().toString();
                Spassword = edPassword.getText().toString();
                Login(Semail,Spassword);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(loginActivity.this,registerActivity.class);
                startActivity(i);
            }
        });
    }

    public void Login(final String temail,final String tpassword)
    {
        if(!temail.equals("")&&!tpassword.equals(""))
        {
            new AsyncTask<String,String,String>()
            {
                String message=null;
                String userId,firstname,lastname,meternumber,address,municicipal;
                JSONObject data;
                int success;
                @Override
                protected void onPreExecute()
                {
                    b = new ProgressDialog(loginActivity.this);
                    loginActivity.this.runOnUiThread(new Runnable()
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
                        String requestURL = "http://192.168.43.90/waterapp/services/login.json";
                        params.put("email",temail);
                        params.put("password",tpassword);
                        try
                        {
                            HttpUtility.sendPostRequest(requestURL,params);
                            //Log.d("hh","jjyj");
                            String[] response = HttpUtility.readMultipleLinesRespone();
                            StringBuffer responseBuffer = new StringBuffer();
                            for(String line : response)
                            {
                                responseBuffer.append(line).append("\n");
                            }
                            loginJson = new JSONObject(responseBuffer.toString());
                            if(loginJson!=null)
                            {
                                message=loginJson.getString("msg").toString();
                                success=loginJson.getInt("success");
                                data=loginJson.getJSONObject("data");
                                userId      =data.getString("userId").toString();
                                firstname   =data.getString("firstname").toString();
                                lastname    =data.getString("lastname").toString();
                                meternumber =data.getString("meternumber").toString();
                                address     =data.getString("physicaladdress").toString();
                                municicipal =data.getString("municipalityname").toString();
                                useid = userId;
                                //usageModel = new UserModel(userId,firstname,lastname);
                                //UserModel(String Userid,String Firstname,String Lastname)
                            }
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            Log.e("ee",ex.getMessage());
                        }
                        HttpUtility.disconnect();
                    }
                    catch (Exception e){return null;}
                    return null;
                }
                @Override
                protected void onPostExecute(String s)
                {
                    b.dismiss();
                    //Toast.makeText(loginActivity.this,"is "+message, Toast.LENGTH_SHORT).show();
                    Toast.makeText(loginActivity.this,firstname, Toast.LENGTH_SHORT).show();
                    if(success==1)
                    {
                        Intent i = new Intent(loginActivity.this,MainActivity.class);
                        i.putExtra("userid", userId);
                        i.putExtra("firstname", firstname);
                        i.putExtra("lastname", lastname);
                        i.putExtra("meternumber", meternumber);
                        i.putExtra("address", address);
                        i.putExtra("municicipal", municicipal);
                        //Log.d("SM", "user details " + userId + " - " + firstname + " - " + lastname);
                        startActivity(i);
                    }
                }
            }.execute();
        }
        else
        {
            Toast.makeText(loginActivity.this,"Please fill all fields",Toast.LENGTH_LONG).show();
        }
    }
}
