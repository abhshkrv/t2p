package com.example.abhishek.tapmoney;

/**
 * Created by Abhishek on 25/7/2015.
 */


        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.PorterDuff;
        import android.media.Image;
        import android.view.LayoutInflater;
        import android.view.MenuInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.ViewGroup;
        import android.view.animation.AlphaAnimation;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.PopupMenu;
        import android.widget.TextView;
        import android.widget.Toast;


public class CustomAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public CustomAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    public static void buttonEffect(View button){
        button.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.card_fragment, parent, false);
        //TextView textView = (TextView) rowView.findViewById(R.id.label);
        //ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        //textView.setText(values[position]);
        // change the icon for Windows and iPhone
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (MainActivity.wallet.Vouchers.size() - position>0)
        {
            final Voucher voucher = MainActivity.wallet.Vouchers.get(MainActivity.wallet.Vouchers.size()-position-1);
            TextView tv = (TextView) rowView.findViewById(R.id.textView3);
            tv.setText(toCCNumber(voucher.getPan()));
            TextView value = (TextView) rowView.findViewById(R.id.textView4);
            value.setText(String.valueOf(voucher.getValue() + " $"));
            Button iv = (Button) rowView.findViewById(R.id.imageButton3);
            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.6F);
            buttonEffect(iv);
            iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //v.startAnimation(buttonClick);
                context.startActivity(new Intent(context, NfcSendActivity.class).putExtra("voucherID", voucher.getId()));
            }
        });
            Button pW = (Button) rowView.findViewById(R.id.imageButton4);
            buttonEffect(pW);
            pW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, PayWaveActivity.class).putExtra("voucherID", voucher.getId()));
                }
            });

            Button menu = (Button) rowView.findViewById(R.id.imageButton5);
            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(v);
                }
            });

            if(voucher.getOwnerid()!=MainActivity.wallet.userId)
            {
                Button iv1 = (Button) rowView.findViewById(R.id.imageButton3);
                //iv1.setVisibility(View.GONE);
                iv1.setAlpha(0.2f);
                iv1.setClickable(false);
                Button iv2 = (Button) rowView.findViewById(R.id.imageButton4);
                //iv2.setVisibility(View.GONE);
                iv2.setAlpha(0.2f);
                iv2.setClickable(false);
                Button iv3 = (Button) rowView.findViewById(R.id.imageButton5);
                //iv3.setVisibility(View.GONE);
                iv3.setAlpha(0.2f);
                iv3.setClickable(false);
                ImageView card = (ImageView) rowView.findViewById(R.id.icon1);
                card.setAlpha(0.2f);

                ImageView disable = (ImageView) rowView.findViewById(R.id.disable);
                disable.setVisibility(View.VISIBLE);
            }
        }
        return rowView;
    }
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_main, popup.getMenu());
        popup.show();
    }
    public String toCCNumber(String input)
    {
        String s = input;
        String s1 = s.substring(0, 4);
        String s2 = s.substring(4, 8);
        String s3 = s.substring(8, 12);
        String s4 = s.substring(12, 16);

        String dashedString = s1 + "  " + s2 + "  " + s3+ "  "+ s4;

        return dashedString;
    }
}
