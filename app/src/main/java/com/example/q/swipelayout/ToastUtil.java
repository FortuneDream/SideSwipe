package com.example.q.swipelayout;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by YQ on 2016/8/5.
 */
public class ToastUtil {
    private static Toast toast;
    public static void show(Context context,String content){
        if (toast==null){
            toast=Toast.makeText(context,content,Toast.LENGTH_SHORT);
        }
        toast.setText(content);
        toast.show();
    }
}
