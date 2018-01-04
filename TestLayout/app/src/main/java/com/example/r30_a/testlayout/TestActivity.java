package com.example.r30_a.testlayout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class TestActivity extends AppCompatActivity {

    EditText edtName,edtPwd;
    Button btnCheck;
    TextView txvHello;
    ImageView imgHead;
    URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        edtName = (EditText)findViewById(R.id.edtname);
        edtPwd = (EditText)findViewById(R.id.edtpwd);
        txvHello = (TextView)findViewById(R.id.txvhello);
        btnCheck = (Button)findViewById(R.id.btncheck);
        imgHead = (ImageView)findViewById(R.id.imgHead);


        //網路連線的工作需由另一個thread執行
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getURLBitmap("http://img.ltn.com.tw/Upload/ent/page/800/2015/02/27/php50LBg0.jpeg");
                //需再另開一個thread，是在main上執行的thread，用本地的imageview加載bitmap
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgHead.setImageBitmap(bitmap);
                    }
                });
            }
        }).start();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtName.getText().toString().equals("luca") &&
                        edtPwd.getText().toString().equals("1234")){
                    txvHello.setText("OK");

                }else {
                    txvHello.setText("Wrong");
                }
            }
        });
    }
    //取得網路圖片存到bitmap的方法
    public static Bitmap getURLBitmap(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
