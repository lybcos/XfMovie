package com.example.administrator.xfmovie.entity;

import android.content.Context;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Data {

    private static TopService b;

    public static TopService getData(Context context,String fileName) {
        try {
            InputStream is = context.getAssets().open(fileName+"");
            String text= readTextFromSDcard(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }


    private static String  readTextFromSDcard(InputStream is) throws IOException {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader reader1 = new BufferedReader(reader);
        String str;
        StringBuffer buffer = new StringBuffer();
        while ((str =reader1.readLine()) != null) {
            buffer.append(str);
        }
        return buffer.toString();
    }
}
