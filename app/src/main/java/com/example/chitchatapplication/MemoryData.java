package com.example.chitchatapplication;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

//The main purpose was to have texts and users stored locally but I noticed it will take me a lot more time that I expected....
//If needed I can implement it :)

public class MemoryData {

    public static void saveData(String data, Context context){
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("data.txt", Context.MODE_PRIVATE);
            fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            fileOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getData(Context context){
        String data = "";
        try {
            FileInputStream fs = context.openFileInput("data.txt");
            InputStreamReader isr = new InputStreamReader(fs);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
