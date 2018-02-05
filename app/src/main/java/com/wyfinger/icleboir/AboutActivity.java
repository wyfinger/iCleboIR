package com.wyfinger.icleboir;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.android.vending.billing.IInAppBillingService;
import com.wyfinger.icleboir.util.IabHelper;
import com.wyfinger.icleboir.util.IabResult;
import com.wyfinger.icleboir.util.Inventory;
import com.wyfinger.icleboir.util.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

/*class InAppProduct {

    public String productId;
    public String storeName;
    public String storeDescription;
    public String price;
    public boolean isSubscription;
    public int priceAmountMicros;
    public String currencyIsoCode;

    public String getSku() {
        return productId;
    }

    String getType() {
        return isSubscription ? "subs" : "inapp";
    }

}*/

public class AboutActivity extends Activity {

    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // no title
        setContentView(R.layout.activity_about);
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

    public void onDonateClick(View view) {

        try {
            mHelper.launchPurchaseFlow(this, getString(R.string.donate_sku), 0,
                    mPurchaseFinishedListener, "donate");
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG, "Error launching purchase flow. Another async operation in progress.");
        }

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


    public void onRankClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.wyfinger.icleboir"));
        startActivity(intent);
    }


}