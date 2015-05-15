package com.example.abhishek.tapmoney;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Debug;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.validation.Validator;


public class MainActivity extends ActionBarActivity {

    public static Wallet wallet = new Wallet();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        readAndSetVouchers();
        Button beamButton = (Button) findViewById(R.id.send1);
        beamButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("listener","called");

                startActivity(new Intent(MainActivity.this, NfcSendActivity.class).putExtra("voucherID", wallet.Vouchers.get(0).getId()));
            }
        });

    }

    public void newVoucher(View view)
    {
       Intent intent = new Intent(this, NewVoucherActivity.class);
       startActivity(intent);
    }
    private void cleanTable(TableLayout table) {

        int childCount = table.getChildCount();

        // Remove all rows except the first one
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }
    }
    public void reDrawVouchers()
    {
        readAndSetVouchers();
        TableLayout tl = (TableLayout) findViewById(R.id.main_table);
        cleanTable(tl);


        for(int i=0;i<wallet.Vouchers.size();i++)
        {   final Voucher voucher = wallet.Vouchers.get(i);
            TableRow tr_head = new TableRow(this);
            tr_head.setId(voucher.getId());
            tr_head.setBackgroundColor(Color.GRAY);
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            TextView label_date = new TextView(this);
            label_date.setId(voucher.getId());
            label_date.setText(voucher.getPan());
            label_date.setTextColor(Color.WHITE);
            label_date.setPadding(5, 5, 5, 5);
            tr_head.addView(label_date);// add the column to the table row here

            TextView label_weight_kg = new TextView(this);
            label_weight_kg.setId(voucher.getId() * 10);// define id that must be unique
            label_weight_kg.setText(String.valueOf(voucher.getValue())+" $"); // set the text for the header
            label_weight_kg.setTextColor(Color.WHITE); // set the color
            label_weight_kg.setPadding(5, 5, 5, 5); // set the padding (if required)
            tr_head.addView(label_weight_kg); // add the column to the table row here

            Button sendButton = new Button(this);
            sendButton.setId(voucher.getId() * 100);// define id that must be unique
            sendButton.setText("Send"); // set the text for the header
            sendButton.setPadding(2, 2, 2, 2); // set the padding (if required)
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(new Intent(MainActivity.this, NfcSendActivity.class).putExtra("voucherID", voucher.getId()));
                }
            });
            tr_head.addView(sendButton); // add the column to the table row here

            Button payWave = new Button(this);
            payWave.setId(voucher.getId() * 100);// define id that must be unique
            payWave.setText("VpW"); // set the text for the header
            payWave.setPadding(2, 2, 2, 2); // set the padding (if required)
            tr_head.addView(payWave); // add the column to the table row here

            tl.addView(tr_head);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void sendNFC(View view)
    {
        //create nfc intent

        Log.d("listener","c2");
    }

    public void refreshData(View view)
    {
        //create nfc intent
        Log.d("t2p","Refreshing...");
        Context context = getApplicationContext();
        CharSequence text = "Refreshing...";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        readAndSetVouchers();
        reDrawVouchers();
    }
    @Override
    public void onResume() {
        super.onResume();

        readAndSetVouchers();
    }

    private void readAndSetVouchers() {

        String output;
        String url = "https://gist.githubusercontent.com/abhshkrv/dedeb1b6a61b7430c34d/raw/eb87a2dce3bb41a2f9acb3a1694db1141a0468ef/gistfile1.txt";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
            Log.d("t2p", output);

            JSONObject jObject  = new JSONObject(output);
            JSONArray vouchers= (JSONArray) jObject.get("vouchers");
            for(int i=0; i<vouchers.length(); i++){
                System.out.println("The " + i + " element of the array: "+vouchers.get(i));
                Gson gson = new Gson();
                Voucher obj = new Voucher();
                obj = gson.fromJson(vouchers.get(i).toString(), Voucher.class);
                int skip = 0;
                for(int j=0; j < wallet.Vouchers.size(); j++)
                {
                    if(obj.getId()==wallet.Vouchers.get(j).getId())
                    {
                        skip = 1;
                        System.out.println("Not adding" + vouchers.get(i));
                        break;
                    }
                }
                if(skip==0)
                {
                    System.out.println("Adding " + vouchers.get(i));
                    wallet.Vouchers.add(obj);
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            Context context = getApplicationContext();
            CharSequence text = "Check internet settings";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            e.printStackTrace();
        }
    }

    private BitmapDrawable writeTextOnDrawable(int drawableId, String text)
    {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(11);
        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);
        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 5, 5, paint);
        return new BitmapDrawable(getResources(), bm);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
