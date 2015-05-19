package com.example.abhishek.tapmoney;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.nfc.NdefRecord.createMime;


public class NfcSendActivity extends ActionBarActivity  implements NfcAdapter.CreateNdefMessageCallback, NfcAdapter.OnNdefPushCompleteCallback {
    NfcAdapter mNfcAdapter;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_send);
        ImageView tick = (ImageView) findViewById(R.id.successtick);
        TextView success = (TextView) findViewById(R.id.successmsg);
        tick.setVisibility(View.INVISIBLE);
        success.setVisibility(View.INVISIBLE);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Register callback
        int id= getIntent().getIntExtra("voucherID",404);
        Voucher voucher = MainActivity.wallet.getVoucherById(id);
        Gson g = new Gson();
        String jsonString = g.toJson(voucher);
        Log.d("t2p", jsonString);
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        int id= getIntent().getIntExtra("voucherID",404);
        Voucher voucher = MainActivity.wallet.getVoucherById(id);
        Gson g = new Gson();
        String jsonString = g.toJson(voucher);

        Log.d("t2p", jsonString);
        String text = (jsonString);
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMimeRecord(
                        "application/com.example.abhishek.tapmoney", text.getBytes())
                        /**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                         */
                        ,NdefRecord.createApplicationRecord("com.example.abhishek.tapmoney")
                });
        //Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        return msg;
    }

    private static final int MESSAGE_SENT = 1;

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "Gyft sent!", Toast.LENGTH_LONG).show();
                    ImageView tick = (ImageView) findViewById(R.id.successtick);
                    TextView success = (TextView) findViewById(R.id.successmsg);
                    tick.setVisibility(View.VISIBLE);
                    success.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };


    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("url");

        try {
            // Add your data
            //EditText val = (EditText) findViewById(R.id.editText);
            //EditText exp = (EditText) findViewById(R.id.editText2);

            int id= getIntent().getIntExtra("voucherID",404);
            Voucher voucher = new Voucher();
            if(id==404) {
                Log.d("t2p", "Error!");
            }
            else {
                voucher = MainActivity.wallet.getVoucherById(id);
            }


            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("voucherid", String.valueOf(voucher.getId())));
            //nameValuePairs.add(new BasicNameValuePair("expiry", exp.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("userid", String.valueOf(MainActivity.wallet.userId==1?2:1)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
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
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        Log.d("t2p"," YES WORKS");
        textView = (TextView) findViewById(R.id.ins);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        String data = new String(msg.getRecords()[0].getPayload());
        textView.setText(data);
        Gson gson = new Gson();
        Voucher voucher = new Voucher();
        voucher = gson.fromJson(data, Voucher.class);
        int skip = 0;
        for(int j=0; j < MainActivity.wallet.Vouchers.size(); j++)
        {
            if(voucher.getId()==MainActivity.wallet.Vouchers.get(j).getId())
            {
                skip = 1;
                System.out.println("Not adding" + voucher);
                break;
            }
        }
        if(skip==0)
        {
            System.out.println("Adding " + voucher);
            MainActivity.wallet.Vouchers.add(voucher);
        }
        else Log.d("t2p","Voucher already exists");
    }
    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nfc_send, menu);
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
