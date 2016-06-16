package com.testapp.hv.androidpay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.testapp.hv.androidpay.util.IabHelper;
import com.testapp.hv.androidpay.util.IabHelper.IabAsyncInProgressException;
import com.testapp.hv.androidpay.util.IabResult;
import com.testapp.hv.androidpay.util.Purchase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
//http://developer.mobogenie.com/docs/cypaysdk.htm //MOBOGENIE WALLET
//https://stripe.com/docs/mobile/android

//google wallet
//https://www.youtube.com/watch?v=1K6BgPozBpo
//https://www.youtube.com/watch?v=fwRIXReE7CM
//<uses-permission android:name="com.android.vending.BILLING" />
//16 STeps!!! http://www.theappguruz.com/blog/implement-in-app-purchase-version-3
//https://www.youtube.com/watch?v=DgcJPIRpfSk&list=PLHA2ScLnJMfMFC_Yh3gAYHdMJcgFKfjNI
//FUll Work  http://stackoverflow.com/questions/17206706/consumable-in-app-purchase-in-android
//https://developers.google.com/admob/android/iap
//VIP PREMIUM https://www.gaffga.de/implementing-in-app-billing-for-android/

public class MainActivity extends Activity {
    Button button1, b2, m1, m2;
    String inappId = "android.test.purchased";
    ServiceConnection con;
    ArrayList<String> skuList = new ArrayList<String>();
    IabHelper mHelper;
    String base64EncodedPublicKey = null;
    IInAppBillingService mservice;
//http://stackoverflow.com/questions/8735931/android-in-app-billing-tutorial
//https://blahti.wordpress.com/2014/07/30/how-to-add-in-app-billing-in-android-part-1/
//https://developer.xamarin.com/guides/ios/application_fundamentals/in-app_purchasing/part_3_-_purchasing_consumable_products/
// VVIP http://www.chupamobile.com/tutorial-android/how-to-integrate-in-app-purchase-billing-in-android-269

    public void MainConsumer() {
        //				    	- See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.dpuf	//	- See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.dpuf
        Purchase pp = null;
        try {
            pp = new Purchase("inapp", "{\"packageName\":\"com.testapp.hv.androidpay\"," +
                    "\"orderId\":\"transactionId.android.test.purchased\"," +
                    "\"productId\":\"android.test.purchased\",\"developerPayload\":\"\",\"purchaseTime\":0," +
                    "\"purchaseState\":0,\"purchaseToken\":\"inapp:com.testapp.hv.androidpay:android.test.purchased\"}",
                    "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            mHelper.consumeAsync(pp, mConsumeFinishedListener);
        } catch (IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = (Button) findViewById(R.id.b1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    mHelper.launchPurchaseFlow(MainActivity.this, inappId, 1001,
                            mPurchaseFinishedListener, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//			    		- See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.dpuf
            }
        });
        b2 = (Button) findViewById(R.id.b2);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainConsumer();
            }
        });

        m1 = (Button) findViewById(R.id.Sub);
        m1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                            inappId, "sub", "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                try {
                    startIntentSenderForResult(pendingIntent.getIntentSender(),
                            1002, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                            Integer.valueOf(0));
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(setupStarted);
//        http://www.codeproject.com/Articles/1009240/How-to-Implement-Android-In-App-Purchases
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    IInAppBillingService mService;
    Bundle buyIntentBundle;
    PendingIntent pendingIntent;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
        }
    };
    public void Subscribe() throws RemoteException {
        String developerPayload = null;
        Bundle bundle = mService.getBuyIntent(3, "com.testapp.hv.android",
                "android.test.purchased", "subs", developerPayload);

/*
        PendingIntent pendingIntent = bundle.getParcelable(RESPONSE_BUY_INTENT);
        if (bundle.getInt(String.valueOf(1002)) == BILLING_RESPONSE_RESULT_OK) {
            // Start purchase flow (this brings up the Google Play UI).
            // Result will be delivered through onActivityResult().
            startIntentSenderForResult(pendingIntent, RC_BUY, new Intent(),
                    Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
        }
*/
    }

    IabHelper.OnIabSetupFinishedListener setupStarted = new IabHelper.OnIabSetupFinishedListener() {
        public void onIabSetupFinished(IabResult result) {
            if (!result.isSuccess()) {
                Toast.makeText(getApplicationContext(), "!! Not Started !!",
                        Toast.LENGTH_SHORT).show();
                // Oh noes, there was a problem.
            } else {
                Toast.makeText(getApplicationContext(), "!! Started !!",
                        Toast.LENGTH_SHORT).show();
            }
            // Hooray, IAB is fully set up!
        }
    };


    // VVVVVIP  http://www.techotopia.com/index.php/Integrating_Google_Play_In-app_Billing_into_an_Android_Application_%E2%80%93_A_Tutorial
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            // TODO Auto-generated method stub
            if (result.isSuccess()) {
                Toast.makeText(getApplicationContext(), "!! Purchase Success !!" + result,
                        Toast.LENGTH_SHORT).show();
                return;
            } else if (result.isFailure()) {
                Toast.makeText(getApplicationContext(), "!! Purchase Failed !!" + result,
                        Toast.LENGTH_SHORT).show();
                MainConsumer();
                try {
                    mHelper.launchPurchaseFlow(MainActivity.this, inappId, 1001,
                            mPurchaseFinishedListener, null);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return;
            } else if (purchase.getSku().equals("SKU_GAS")) {
                Toast.makeText(getApplicationContext(), "!! Gas \n Consumed !!",
                        Toast.LENGTH_SHORT).show();
                // consume the gas and update the UI
            } else if (purchase.getSku().equals("SKU_PREMIUM")) {
                Toast.makeText(getApplicationContext(), "!! Premium !!",
                        Toast.LENGTH_SHORT).show();
                // give user access to premium content and update the UI
            } else if (purchase.getSku().equals(inappId)) {
                Toast.makeText(getApplicationContext(), "!! Android.Test.Purchased !!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };
    // This One  http://www.theappguruz.com/blog/implement-in-app-purchase-version-3
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (mHelper == null) return;
            if (result.isSuccess()) {
                Toast.makeText(getApplicationContext(), "Consumed: " + result,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error while consuming: " + result,
                        Toast.LENGTH_SHORT).show();
            }
//Log.d(TAG, "End consumption flow.");
        }
    };
//- See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.dpuf

    //http://www.techotopia.com/index.php/An_Android_Studio_Google_Play_In-app_Billing_Tutorial
//- See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.3GPHSbdr.dpuf - See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.3GPHSbdr.dpuf - See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.3GPHSbdr.dpuf - See more at: http://www.theappguruz.com/blog/implement-in-app-purchase-version-3#sthash.TdQbqvyh.3GPHSbdr.dpuf
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        System.exit(0);
    }


    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(setupStarted);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    Toast.makeText(getApplicationContext(), "You have bought the " + sku + ". Excellent choice," + " adventurer!",
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Bought Failed",
                        Toast.LENGTH_SHORT).show();
            }
        }
/*   Intent intent1 = new Intent(MainActivity.this,MainActivity.class);
      startActivity(intent1);
  finish();
*/
    }


}