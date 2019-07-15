package com.sudoajay.statusonline.HelperClass;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.sudoajay.statusonline.R;



public class CustomToast {

    public static void ToastIt(final Context mContext , final String mes, final int size){
        Toast toast = Toast.makeText(mContext, mes, size);
        View view = toast.getView();
//Gets the actual oval background of the Toast then sets the colour filter
        view.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.toastBackgroundColor)
                , PorterDuff.Mode.SRC_IN);
//Gets the TextView from the Toast so it can be editted
        TextView text = view.findViewById(android.R.id.message);
        text.setTextColor(ContextCompat.getColor(mContext, R.color.toastTextColor));

        toast.show();
    }


}
