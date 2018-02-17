package com.wyfinger.icleboir;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.wyfinger.icleboir.util.IabHelper;
import com.wyfinger.icleboir.util.IabResult;
import com.wyfinger.icleboir.util.Purchase;

import static android.content.ContentValues.TAG;

public class AboutActivity extends AppCompatActivity {

    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //
        mHelper = new IabHelper(this, getString(R.string.billing_key));
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                }
                if (mHelper == null) return;
                // Hooray, IAB is fully set up!
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            try {
                mHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
        mHelper = null;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onDonateClick(View view) {

        try {
            mHelper.launchPurchaseFlow(this, getString(R.string.donate_sku), 0,
                    mPurchaseFinishedListener, "donate");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, "Error launching purchase flow. Another async operation in progress.");
        }

    }

    public void onRankClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.wyfinger.icleboir"));
        startActivity(intent);
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            Log.d(TAG, "Purchase successful.");
            Toast.makeText(getApplicationContext(), R.string.donate_ok, Toast.LENGTH_LONG).show();
        }
    };

}
