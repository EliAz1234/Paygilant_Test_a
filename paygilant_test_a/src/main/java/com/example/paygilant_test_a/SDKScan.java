package com.example.paygilant_test_a;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.example.paygilant_test_a.Scanovate.AsyncResponse;
import com.example.paygilant_test_a.Scanovate.VerificationAsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import scanovate.ocr.common.ScanListener;
import scanovate.ocr.face.FaceOCRManager;


public class SDKScan {

    public Context context;

    public View view;
    Activity activity;
    FaceOCRManager faceOCRManager;
    public static final String PATH_IMAGE_DIR= "Paygilant_image";

    public SDKScan(Context context, View view, Activity activity) {
        this.context = context;
        this.view = view;
        this.activity = activity;
    }

    public void startNewScreenListener() {
        FrameLayout layout = new FrameLayout(context);
        FrameLayout.LayoutParams layoutparams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT
                , ViewGroup.LayoutParams.FILL_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        layout.setLayoutParams(layoutparams);
        layout.setVisibility(View.INVISIBLE);
        if ((view instanceof ViewGroup)) {
            ((ViewGroup) view).addView(layout);
        }
        faceOCRManager = new FaceOCRManager();
        faceOCRManager.setTimeoutInSeconds(16);
        try {
            // Boolean isSelfie determine if FaceManager open fron or back camera
            faceOCRManager.init(layout, activity, true);
        } catch (Exception e) {
                Log.e("CAMERA", "Exception :" + e.toString());
        }
        faceOCRManager.setScanListener(new ScanListener() {
            @Override
            public void onScanSuccess(HashMap<String, Object> resultValues) {
                //read data from dictionary. see return values section for details
                    Log.d("StopScan", System.currentTimeMillis() + "");
                try {
                    faceOCRManager.free();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final Bitmap faceImage = (Bitmap) resultValues.get(FaceOCRManager.FACE_SCAN_RESULT_FACE_IMAGE);

                //if you have some work on UI Thread use runOnUiThread
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = getImage(context.getFilesDir() + File.separator + PATH_IMAGE_DIR + "test.png");
                        if (bitmap == null) {
                            saveImage(context.getFilesDir() + File.separator + PATH_IMAGE_DIR + "test.png", faceImage);
                            Toast.makeText(context, "Successful Enrolment", Toast.LENGTH_LONG).show();
                        } else {
                            final VerificationAsyncTask verificationAsyncTask = new VerificationAsyncTask(faceImage, bitmap);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    verificationAsyncTask.setAsyncResponse(new AsyncResponse() {
                                        @Override
                                        public void onVerificationFinish(double verificationScore, double threshold) {

                                            if (BuildConfig.DEBUG) {
                                                Log.d("SCORE_FACE", verificationScore + "," + threshold);
                                            }
                                            if (verificationScore > threshold) {
                                                Toast.makeText(context, "Risk_Low_Score:" + verificationScore + "threshold:" + threshold, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(context, "Risk_High_Score:" + verificationScore + "threshold:" + threshold, Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onVerificationError(String error) {

                                        }
                                    });
                                    verificationAsyncTask.execute();
                                }
                            });
                        }
//                        Intent intent = new Intent(ScanActivity.this,MainActivity.class);
//                        startActivity(intent);
//
//                        finish();
                    }
                });
            }

            @Override
            public void onScanCanceled() {
                try {
                    faceOCRManager.free();
//                    activity.finish();
                } catch (Exception e) {
                    Log.e("CAMERA", "Exception: " + e.toString());
                }
            }

            @Override
            public void onScanFailed(final HashMap<String, Object> resultValues) {
                //read data from dictionary. see return values section for details
//                Bitmap lastImage = (Bitmap) resultValues.get(FaceOCRManager.FACE_SCAN_RESULT_INPUT_IMAGE);

                //if you have some work on UI Thread use runOnUiThread
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "ScanFailed ", Toast.LENGTH_LONG).show();

                    }
                });
            }
        });
        try {
            faceOCRManager.startScan();
            Log.d("StartScan", System.currentTimeMillis() + "");
        } catch (Exception e) {
            Log.e("CAMERA", "Exception :" + e.toString());
        }
    }
    void saveImage(String pthAndFylTtlVar, Bitmap iptBmjVar)
    {
        try
        {
            FileOutputStream fylBytWrtrVar = new FileOutputStream(pthAndFylTtlVar);
            iptBmjVar.compress(Bitmap.CompressFormat.PNG, 100, fylBytWrtrVar);
            fylBytWrtrVar.close();
        }
        catch (Exception errVar) { errVar.printStackTrace(); }
    }
    Bitmap getImage(String pthAndFylTtlVar)
    {
        return BitmapFactory.decodeFile(pthAndFylTtlVar);
    }

}
