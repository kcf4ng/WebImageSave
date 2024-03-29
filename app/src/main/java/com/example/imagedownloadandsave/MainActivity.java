package com.example.imagedownloadandsave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AskPermission();
        InitialView();
    }

    private void InitialView() {
        btn_save = findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadImage();
            }
        });

        image_view = findViewById(R.id.image_view);
    }

    OutputStream outputStream;
    
//TODO connect get Url >> downloadimage()

    private void DownloadImage() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                URL url = null;
                BufferedReader reader = null;

                StringBuilder stringBuilder;
                Bitmap bitmap = null;
                try
                {
                    // create the HttpURLConnection

                    url = new URL(getResources().getString(R.string.Url_cannotfeed));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 使用甚麼方法做連線
                    connection.setRequestMethod("GET");

                    // 是否添加參數(ex : json...等)
                    //connection.setDoOutput(true);

                    // 設定TimeOut時間
                    connection.setReadTimeout(15*1000);
                    connection.connect();

                    // 伺服器回來的參數
                    InputStream inputStream = new BufferedInputStream(connection.getInputStream());

//                    bitmap= BitmapFactory.decodeStream(inputStream);

                    inputstreamtofile(inputStream);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


                final Bitmap finalBitmap = bitmap;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Image Save to Internal !", Toast.LENGTH_LONG).show();
                        image_view.setImageBitmap(finalBitmap);
                    }
                });

//                Save(bitmap);

            }
        }.start();
    }

    private void Save(Bitmap bitmap){
        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+"/Demo/");

        if(!dir.exists()){
            dir.mkdir();
        }

        File file = new File(dir, System.currentTimeMillis()+".jpg");

        if(file.exists()){
            file.delete();
        }

        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    private void AskPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }

    public void inputstreamtofile(InputStream ins){

        File filepath = Environment.getExternalStorageDirectory();
        File dir = new File(filepath.getAbsolutePath()+"/Demo/"); //image slideshow dir

        if(!dir.exists()){
            dir.mkdir();
        }

        File file = new File(dir, "temp.png"); //temp file name

        if(file.exists()){
            file.delete();
        }


        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

            Log.d("file", "inputstreamtofile: "+file.length());

            if(file.length() < 4096){
                file.delete();
                return;
            }

            File to = new File(dir,"test.png");
            if(to.exists()){
                to.delete();
            }

            file.renameTo(to);

            //temp file
            //length > 4K bytes; 4K is variable

            //yes
            //check 『index』的『png』is exists？
            // yes > delete >> rename temp file
            // no > rename temp file

            //all index success > return server api

            os.close();
            ins.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    Button  btn_save;
    ImageView image_view;

}
