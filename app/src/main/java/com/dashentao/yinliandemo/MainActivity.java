package com.dashentao.yinliandemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author dashentao
 * @date 2015 9-23
 * @since V 1.0
 */
public class MainActivity extends AppCompatActivity {
    private Button button1;
    private RelativeLayout container;
    private static final String TN_URL_01 = "http://101.231.204.84:8091/sim/getacptn";
    private static final String R_SUCCESS = "success";
    private static final String R_FAIL = "fail";
    private static final String R_CANCEL = "cancel";
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // “00” – 银联正式环境
            // “01” – 银联测试环境，该环境中不发生真实交易
            String tn = (String) msg.obj;
            if (!TextUtils.isEmpty(tn)) {
                // 测试环境
                String serverMode = "01";
                UPPayAssistEx.startPayByJAR(MainActivity.this,
                        PayActivity.class, null, null, tn, serverMode);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.button1);
        container = (RelativeLayout) findViewById(R.id.container1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MyThread().start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase(R_SUCCESS)) {
            Snackbar.make(container, R.string.pay_success, Snackbar.LENGTH_LONG).show();
        } else if (str.equalsIgnoreCase(R_FAIL)) {
            Snackbar.make(container, R.string.pay_fail, Snackbar.LENGTH_LONG).show();
        } else if (str.equalsIgnoreCase(R_CANCEL)) {
            Snackbar.make(container, R.string.pay_cancel, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * 获取tn线程
     *
     * @author JamesTao
     */
    private class MyThread extends Thread {
        public MyThread() {
        }

        @Override
        public void run() {
            super.run();
            String tn = null;
            InputStream is;
            try {
                String url = TN_URL_01;
                URL myURL = new URL(url);
                URLConnection ucon = myURL.openConnection();
                ucon.setConnectTimeout(120 * 1000);
                is = ucon.getInputStream();
                int i = -1;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((i = is.read()) != -1) {
                    baos.write(i);
                }

                tn = baos.toString();
                is.close();
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Message msg = mHandler.obtainMessage();
            msg.obj = tn;
            mHandler.sendMessage(msg);
        }
    }
}
