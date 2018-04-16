package com.wyfinger.icleboir;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.wyfinger.icleboir.util.IabHelper;
import com.wyfinger.icleboir.util.IabResult;
import com.wyfinger.icleboir.util.Purchase;

import static android.content.ContentValues.TAG;

public class AboutActivity extends AppCompatActivity {

    IabHelper payHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // my google keys (it's a very big secret :)
        payHelper = new IabHelper(this, getString(R.string.billing_key));
        // enable debug logging (for a production application, it should be a false).
        payHelper.enableDebugLogging(false);
        payHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
                    findViewById(R.id.btnDonate).setEnabled(false);
                    payHelper = null;
                } else {
                    // Hooray, IAB is fully set up!
                    Log.d(TAG, "In-app Billing set up");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (payHelper != null) {
            try {
                // we must dispose IabHelper resources
                payHelper.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
                Log.d(TAG, "Error at dispose IabHelper (play billing service), see more:");
                e.printStackTrace();
            }
        }
    }

    public void onBackAboutClick(View view) {
        finish();
    }

    public void onDonateClick(View view) {

        try {
            payHelper.launchPurchaseFlow(this, getString(R.string.donate_sku), 0,
                    mPurchaseFinishedListener, "donate");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, "Error launching purchase flow. Another async operation in progress.");
            Toast.makeText(getApplicationContext(),
                    "Some error when launching purchase flow.", Toast.LENGTH_LONG).show();
        }

    }

    private boolean safeStartActivity(Intent intent) {
        try {
            startActivity(intent);
            return true;
        }
        catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void onRankClick(View view) {

        // Runk App: http://blog.cubeactive.com/android-how-to-create-a-rank-this-app-button/

        Intent intent = new Intent(Intent.ACTION_VIEW);

        // try to open Google play
        intent.setData(Uri.parse("market://details?id=com.wyfinger.icleboir"));
        if (!safeStartActivity(intent)) {
            // may be market (Google play) app seems not installed, let's try to open a web browser
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.wyfinger.icleboir"));
            if (!safeStartActivity(intent)) {
                //Well if this also fails, we have run out of options, inform the user.
                Log.d(TAG, "Could not open Android market in Runk App method.");
            }
        }

    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (payHelper == null) return;

            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);
                return;
            }
            Log.d(TAG, "Purchase successful.");
            Toast.makeText(getApplicationContext(), R.string.donate_ok, Toast.LENGTH_LONG).show();
        }
    };

}
