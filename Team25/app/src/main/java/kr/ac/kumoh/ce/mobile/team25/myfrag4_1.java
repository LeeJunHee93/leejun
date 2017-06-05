package kr.ac.kumoh.ce.mobile.team25;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class myfrag4_1 extends Activity {
    final int PICK_IMAGE = 100;

    EditText e1;
    Button btn;
    String name_Str, pickpath;
    Uri uri;
    private WebView webView;
    TextView result;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myfrag4_1);
        e1 = (EditText) findViewById(R.id.name);
        result = (TextView) findViewById(R.id.result);
        init_webView();
        handler = new Handler();


        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(myClickListner);

    }

    public void init_webView() {
        webView = (WebView) findViewById(R.id.address);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        webView.addJavascriptInterface(new AndroidBridge(), "TestApp");
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/daum.html");
    }

    private class AndroidBridge {

        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    result.setText(String.format("(%s) %s %s", arg1, arg2, arg3));

                    init_webView();
                }
            });
        }
    }

    public void imgclick(View v) {
        doTakeAlbumAction();
    }

    public void doTakeAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    public String POST(String url) {
        InputStream is = null;
        String result = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            URL urlCon = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlCon.openConnection();
            conn.setDoInput(true); //input 허용
            conn.setDoOutput(true);  // output 허용
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Cookie", login.cookieString);

            conn.connect();

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeUTF(twoHyphens + boundary + lineEnd);
            dos.writeUTF("Content-Disposition: form-data; name=\"name\"\r\n\r\n" + e1.getText().toString());
            dos.writeUTF(lineEnd);
            dos.writeUTF(twoHyphens + boundary + lineEnd);
            dos.writeUTF("Content-Disposition: form-data; name=\"address\"\r\n\r\n" + result.toString());
            dos.writeUTF(lineEnd);
            Log.i("죽을랑가!!!!!", "!! !!!");

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + pickpath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            String realpath = pickpath;
            FileInputStream fileInputStream = new FileInputStream(realpath);
            Log.i("우오오오오ㅗ오오오!!!!!", "!! !!!");
            int bytesAvailable = fileInputStream.available();
            Log.i("byteAvailble값값값!!!!!", "!!" + Integer.toString(bytesAvailable) + "!!!");
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                dos.flush(); // finish upload...
            }
            fileInputStream.close();

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush(); // finish upload...

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                Log.i("테스트", "성공성공");
            else
                Log.i("테스트", "실패");
            try {
                is = conn.getInputStream();
                if (is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class studyroomPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    uri = data.getData();
                    name_Str = getImageNameToUri(uri);
                    pickpath = getRealPathFromURI(uri);
                    Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    ImageView image = (ImageView) findViewById(R.id.rimg);

                    image.setImageBitmap(image_bitmap);
                    Log.i("파일", "파일성공");

                } catch (FileNotFoundException e) {
                    Log.i("파일", "파일에러");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("파일", "파일에러");
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));
        Log.i("방 !!", "getRealPathFromURI(), path : " + uri.toString());
        cursor.close();

        return path;
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/") + 1);

        return imgName;
    }

    View.OnClickListener myClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(myfrag4_1.this, myfrag4_2.class);
            studyroomPostRequest studyroompostrequest = new studyroomPostRequest();
            studyroompostrequest.execute(MainActivity.SERVER_IP_PORT + "/host/add");
            startActivity(intent);
        }
    };

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}