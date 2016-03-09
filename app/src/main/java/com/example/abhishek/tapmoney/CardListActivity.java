package com.example.abhishek.tapmoney;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CardListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        if(NfcAdapter.getDefaultAdapter(this)!=null)
        {NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.setNdefPushMessage(null, this, this);}
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("GYFT");
        setContentView(R.layout.activity_card_list);


        ListView listview = (ListView) findViewById(R.id.listview);
        String[] values1 = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };
        String [] values=new String[MainActivity.wallet.Vouchers.size()];
        for(int i=0;i<MainActivity.wallet.Vouchers.size();i++)
        {
            values[i]=values1[i];
        }
        CustomAdapter customAdapter = new CustomAdapter(this, values);
        listview.setAdapter(customAdapter);
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < MainActivity.wallet.Vouchers.size(); ++i) {
            list.add(values[i]);
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card_list, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        reDrawVouchers();
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
        else if (id == R.id.action_new_gyft)
        {
            Intent intent = new Intent(this, NewVoucherActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_refresh)
        {
            finish();
            startActivity(getIntent());
        }

        return super.onOptionsItemSelected(item);
    }

    public void reDrawVouchers()
    {

        readAndSetVouchers();
        ListView listview = (ListView) findViewById(R.id.listview);
        listview.addHeaderView(new View(getApplicationContext()), null, true);
        listview.addFooterView(new View(getApplicationContext()), null, true);
        String[] values1 = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };
        String [] values=new String[MainActivity.wallet.Vouchers.size()];
        for(int i=0;i<MainActivity.wallet.Vouchers.size();i++)
        {
            values[i]=values1[i];
        }
        CustomAdapter customAdapter = new CustomAdapter(this, values);
        listview.setAdapter(customAdapter);
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < MainActivity.wallet.Vouchers.size(); ++i) {

            list.add(values[i]);
        }

        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
            }
        });


    }

    private void readAndSetVouchers() {
        Wallet wallet = MainActivity.wallet;
        String output;
        //String url = "https://gist.githubusercontent.com/abhshkrv/dedeb1b6a61b7430c34d/raw/eb87a2dce3bb41a2f9acb3a1694db1141a0468ef/gistfile1.txt";
        String url = "http://tapmoney-abhshkrv.c9.io/get_vouchers.php";
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            output = EntityUtils.toString(httpEntity);
            Log.d("t2p", output);

            JSONObject jObject  = new JSONObject(output.toLowerCase());
            JSONArray vouchers= (JSONArray) jObject.get("vouchers");
            for(int i=0; i<vouchers.length(); i++)
            {
                System.out.println("The " + i + " element of the array: "+vouchers.get(i));
                Gson gson = new Gson();
                Voucher obj = new Voucher();
//                obj.setId(vouchers.get(i).toString());
                obj = gson.fromJson(vouchers.get(i).toString(), Voucher.class);
                int skip = 0;
                if(obj.getBuyerid()!=MainActivity.wallet.userId&&obj.getOwnerid()!=MainActivity.wallet.userId)
                    continue;
                for(int j=0; j < wallet.Vouchers.size(); j++)
                {
                    if(obj.getId()==wallet.Vouchers.get(j).getId())
                    {
                        wallet.Vouchers.set(j,obj);
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
}
