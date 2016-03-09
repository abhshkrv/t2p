package com.example.abhishek.tapmoney;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Abhishek on 13/5/2015.
 */
public class Wallet {

    ArrayList<Voucher> Vouchers = new ArrayList<Voucher>();
    public static final int userId = 1;
    public ArrayList<Voucher> getVouchers() {
        return Vouchers;
    }

    public void setVouchers(ArrayList<Voucher> vouchers) {
        Vouchers = vouchers;
    }

    public Voucher getVoucherById (int id)
    {
        for (int i=0; i < Vouchers.size();i++)
        {   Voucher v = Vouchers.get(i);
            if(v.getId()==id)
                return v;
        }
        Log.d("t2p", "Voucher not found");
        return null;
    }

}
