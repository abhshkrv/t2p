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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class MainActivity extends ActionBarActivity {

    public static Wallet wallet = new Wallet();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Intent intent = new Intent(this, CardListActivity.class);
        startActivity(intent);
        reDrawVouchers();
        /*Button beamButton = (Button) findViewById(R.id.send1);
        beamButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("listener","called");

                startActivity(new Intent(MainActivity.this, NfcSendActivity.class).putExtra("voucherID", wallet.Vouchers.get(0).getId()));
            }
        });*/

    }

    public void newVoucher(View view)
    {
       Intent intent = new Intent(this, NewVoucherActivity.class);
       startActivity(intent);
    }
    public void newScreen(View view)
    {
        Intent intent = new Intent(this, CardListActivity.class);
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


        for(int i=wallet.Vouchers.size()-1;i>=0;i--)
        {   final Voucher voucher = wallet.Vouchers.get(i);
            TableRow tr_head = new TableRow(this);
            tr_head.setId(voucher.getId());
            //tr_head.setBackgroundColor(Color.GRAY);
            tr_head.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tr_head.setPadding(0,15,0,15);

            TextView t = new TextView(this);
// setting gravity to "center"
            t.setTextColor(Color.WHITE);
            t.setTypeface(null, Typeface.BOLD);
            t.setPadding(0, 150, 125, 0);
            t.setGravity(Gravity.CENTER);

            //Drawable bm = BitmapFactory.decodeResource(getResources(), R.drawable.card_pic3);
            BitmapDrawable bm = writeOnDrawable(R.drawable.card_pic3, String.valueOf(voucher.getValue()) + " $");
            t.setBackground(bm);
            t.setText(toCCNumber(voucher.getPan()));
            if(voucher.getOwnerid()!=wallet.userId)
                t.getBackground().mutate().setAlpha(70);
            writeTextOnDrawable(t.getBackground().mutate(),String.valueOf(voucher.getValue()));
            if(voucher.getOwnerid()==wallet.userId)
            {
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, PayWaveActivity.class).putExtra("voucherID", voucher.getId()));
                    }
                });
            }
            if(voucher.getBuyerid()==wallet.userId||voucher.getOwnerid()==wallet.userId) {
                tr_head.addView(t);
            }

            LinearLayout LL = new LinearLayout(this);
            LL.setBackgroundColor(Color.CYAN);
            LL.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams LLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
            LL.setWeightSum(1);
            LL.setGravity(Gravity.START);
            LL.setLayoutParams(LLParams);

            ImageButton sendButton = new ImageButton(this);
            sendButton.setId(voucher.getId() * 100);// define id that must be unique
            sendButton.setImageResource(R.drawable.gfytit);//setText("Send"); // set the text for the header
            //sendButton.setPadding(2, 2, 2, 2); // set the padding (if required)
            sendButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            sendButton.setBackgroundColor(Color.parseColor("#FFCECFCE"));
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, NfcSendActivity.class).putExtra("voucherID", voucher.getId()));
                }
            });
            //LL.addView(sendButton);
            /*
            BootstrapButton payWave = new BootstrapButton(this);

            payWave.setId(voucher.getId() * 100);// define id that must be unique
            payWave.setText("VpW"); // set the text for the header
            payWave.setPadding(1, 1, 1, 1); // set the padding (if required)
            //tr_head.addView(payWave); // add the column to the table row here
            //LL.addView(payWave);
*/
            if(voucher.getOwnerid()==wallet.userId) {
                tr_head.addView(sendButton); // add the column to the table row here

            }
            if(voucher.getOwnerid()==wallet.userId||voucher.getBuyerid()==wallet.userId){
                tl.addView(tr_head);
            }
        }

    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setFakeBoldText(Boolean.TRUE);

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, 2*bm.getWidth()/4, bm.getHeight()/2, paint);

        return new BitmapDrawable(getResources(),bm);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public String toCCNumber(String input)
    {
        String s = input;
        String s1 = s.substring(0, 4);
        String s2 = s.substring(4, 8);
        String s3 = s.substring(8, 12);
        String s4 = s.substring(12, 16);

        String dashedString = s1 + " " + s2 + " " + s3+ " "+ s4;

        return dashedString;
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

        reDrawVouchers();
    }

    private void readAndSetVouchers() {

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
            for(int i=0; i<vouchers.length(); i++){
                System.out.println("The " + i + " element of the array: "+vouchers.get(i));
                Gson gson = new Gson();
                Voucher obj = new Voucher();
//                obj.setId(vouchers.get(i).toString());
                obj = gson.fromJson(vouchers.get(i).toString(), Voucher.class);
                int skip = 0;
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
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    private BitmapDrawable writeTextOnDrawable(Drawable drawable, String text)
    {
        Bitmap bm = drawableToBitmap(drawable).copy(Bitmap.Config.ARGB_8888, true);
        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(15);
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
