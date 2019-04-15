package com.example.paygilant_test_a.Scanovate;

import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VerificationServerManager {
    private static VerificationServerManager ourInstance = new VerificationServerManager();
    private final String charset = "UTF-8";
    private String boundary;
    private final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;

    public static VerificationServerManager getInstance() {
        return ourInstance;
    }


    public VerificationServerManager() {
    }

    /**
     * This method initializes a new HTTP POST request with content type
     * is set to multipart/form-data
     *
     * @throws IOException
     */
    public void initHttpRequest() throws IOException {
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";

        URL url = new URL(DemoGlobals.FACE_VERIFICATION_URL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                true);
    }

    public byte[] convertBitmap2ByteArray(Bitmap image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public String compareImages(Bitmap image1, Bitmap image2) {
        StringBuilder response = new StringBuilder();
        try {
            initHttpRequest();

            addJPEGPart("first_image", convertBitmap2ByteArray(image1), "image1.jpg");
            addJPEGPart("second_image", convertBitmap2ByteArray(image2), "image2.jpg");
            addFormField("caseId", "1234");

            List<String> responseList = null;
            responseList = finish();

            for (String line : responseList) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.toString();
    }


    public void addJPEGPart(String fieldName, byte[] jpegData, String fileName)
            throws IOException {

        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: image/jpeg")
                .append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        outputStream.write(jpegData, 0, jpegData.length);
        outputStream.flush();

        writer.append(LINE_FEED);
        writer.flush();
    }


    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }

    public void addStringPart(String fieldName, String fileName)
            throws IOException {

        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: image/jpeg")
                .append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        outputStream.flush();

        writer.append(LINE_FEED);
        writer.flush();
    }


    /**
     * Completes the request and receives response from the server.
     *
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     * @throws IOException
     */
    private List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();

        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();

        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
    }

}