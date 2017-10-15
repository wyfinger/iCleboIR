package com.wyfinger.icleboir;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // no title
        setContentView(R.layout.activity_about);
    }

    public void onDonateClick(View view) {
        Toast.makeText(getApplicationContext(),
                "it is't released", Toast.LENGTH_SHORT);
    }

    public void onRankClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.wyfinger.icleboir"));
        startActivity(intent);
    }


}