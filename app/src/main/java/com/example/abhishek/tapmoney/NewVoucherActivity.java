package com.example.abhishek.tapmoney;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NewVoucherActivity extends ActionBarActivity {

    public static int isBuying = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_voucher);
    }

    public void buyVoucher(View view)
    {
        Toast.makeText(this, "Processing Request...", Toast.LENGTH_LONG).show();
        postNewVoucher();
        onBackPressed();
    }
    public void postNewVoucher() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://pure-harbor-29950.herokuapp.com/create_voucher.php");

        try {
            // Add your data
            EditText val = (EditText) findViewById(R.id.editText);
            EditText exp = (EditText) findViewById(R.id.editText2);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("value", val.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("expiry", exp.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("userid", String.valueOf(MainActivity.wallet.userId)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.d("t2p",org.apache.http.util.EntityUtils.toString(response.getEntity()));
        } catch (ClientProtocolException e) {
            Toast.makeText(this, "Check your internet", Toast.LENGTH_LONG).show();
            // TODO Auto-generated catch block
        } catch (IOException e) {
            Toast.makeText(this, "Check your internet", Toast.LENGTH_LONG).show();
            // TODO Auto-generated catch block
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buy_voucher, menu);
        return true;
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
