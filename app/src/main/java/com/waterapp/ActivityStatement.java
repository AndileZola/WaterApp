package com.waterapp;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ActivityStatement extends AppCompatActivity
{
    LinearLayout statement;
    String userId,firstname,lastname,meternumber,address,municicipal;
    TextView uaddress,municipal,date,greet,used,bill,issue,message;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_tatement);
        statement = (LinearLayout) findViewById(R.id.statement);
        Log.d("TAG", "W " + String.valueOf(statement.getWidth()));
        Log.d("TAG", "H " + String.valueOf(statement.getHeight()));
        Intent i = getIntent();
        userId      = i.getExtras().getString("userid");
        firstname   = i.getExtras().getString("firstname");
        lastname    = i.getExtras().getString("lastname");
        meternumber = i.getExtras().getString("meternumber");
        address     = i.getExtras().getString("address");
        municicipal = i.getExtras().getString("municicipal");
        findViewById();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String now = sdf.format(Calendar.getInstance().getTime());
        uaddress.setText(address);
        municipal.setText(municicipal);
        date.setText(now);
        greet.setText("Dear " +firstname+" "+lastname);
        String s = MainActivity.totalBill.getText().toString().substring(3,MainActivity.totalBill.getText().toString().indexOf("."));
        int u = Integer.parseInt(s);
        used.setText(String.valueOf(u/2)+" millilitres");
        bill.setText(MainActivity.totalBill.getText().toString().substring(1,MainActivity.totalBill.getText().toString().length()));
        issue.setText(municicipal);
        message.setText("Below is your water bill as of "+now);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                print(statement);
            }
        });
    }
    void findViewById()
    {
        uaddress = (TextView) findViewById(R.id.address);
        municipal= (TextView) findViewById(R.id.municipal);
        date= (TextView) findViewById(R.id.date);
        greet= (TextView) findViewById(R.id.greetings);
        used= (TextView) findViewById(R.id.used);
        bill= (TextView) findViewById(R.id.bill);
        issue= (TextView) findViewById(R.id.issue);
        message = (TextView) findViewById(R.id.msg);
    }
    void print(View v)
    {
        Log.d("TAG","W "+String.valueOf(v.getWidth()));
        Log.d("TAG","H "+String.valueOf(v.getHeight()));
        Toast.makeText(ActivityStatement.this,"Printing statement",Toast.LENGTH_SHORT).show();
        PdfDocument document = new PdfDocument();
        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(v.getWidth(),v.getHeight(),pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        v.draw(page.getCanvas());
        document.finishPage(page);
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyhhmmss");
        String pdfName = "waterbill" + sdf.format(Calendar.getInstance().getTime()) + ".pdf";
        File outputFile = new File("/sdcard/"+ pdfName);
        try
        {
            outputFile.createNewFile();
            OutputStream out = new FileOutputStream(outputFile);
            document.writeTo(out);
            document.close();
            out.close();
            Log.d("PDF","DONE");
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.d("PDF",e.getMessage());
        }

    }

}
