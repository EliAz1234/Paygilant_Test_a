package com.example.paygilant_test_a.Scanovate;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VerificationAsyncTask extends AsyncTask {

    private static final String kSuccess = "success";
    private static final String KEY_VERIFICATION_SCORE = "score";
    private static final String KEY_VERIFICATION_THRESHOLD = "threshold";
    private static final String kERRORS = "errors";

    private AsyncResponse asyncResponse = null;
    private Bitmap imgDocument, imgSelfie;


    public VerificationAsyncTask(Bitmap imgDocument, Bitmap imgSelfie) {
        this.imgDocument = imgDocument;
        this.imgSelfie = imgSelfie;
    }

    @Override
    protected Object doInBackground(Object[] params) {
//        Process.setThreadPriority(Process.THREAD_PRIORITY_MORE_FAVORABLE);

        VerificationServerManager verificationServerManager = VerificationServerManager.getInstance();

        return verificationServerManager.compareImages(imgDocument, imgSelfie);
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        try {
            JSONObject result = new JSONObject(o.toString());
            if (!o.toString().equals("")) {
                if (result.getBoolean(kSuccess)) {
                    asyncResponse.onVerificationFinish(result.getDouble(KEY_VERIFICATION_SCORE), result.getDouble(KEY_VERIFICATION_THRESHOLD));
                } else {

                    JSONArray errors = result.getJSONArray(kERRORS);
                    StringBuilder results = new StringBuilder();
                    for (int i = 0; i < errors.length(); i++) {
                        JSONObject errorData = (JSONObject) errors.get(i);
                        String news = errorData.toString();
                        results.append(news);

                    }
                    asyncResponse.onVerificationError(results.toString());
                }
            } else {
                asyncResponse.onVerificationError("");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            asyncResponse.onVerificationError("Unknown Error");
        }
    }

    public void setAsyncResponse(AsyncResponse delegate) {
        this.asyncResponse = delegate;
    }
}
