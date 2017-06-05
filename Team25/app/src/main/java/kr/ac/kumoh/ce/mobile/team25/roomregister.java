package kr.ac.kumoh.ce.mobile.team25;

/**
 * Created by dlgus on 2017-05-01.
 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

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


public class roomregister extends Activity {
    static String pickpath;
    final int PICK_IMAGE = 100;
    TextView tv, tvIsConnected;
    String name_Str;
    static EditText etName, eAddress, eDetail;
    Button btnPost;
    static String strJson = "";
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.roomregister);
        tv = (TextView) findViewById(R.id.tv);

        // get reference to the views
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        etName = (EditText) findViewById(R.id.name);
        eAddress = (EditText) findViewById(R.id.address);
        eDetail = (EditText)findViewById(R.id.detail);
        btnPost = (Button) findViewById(R.id.btnPost);

        //      check if you are connected or not
        if(isConnected()){
            tvIsConnected.setBackgroundColor(0xFF00CC00);
            tvIsConnected.setText("You are conncted");
        }
        else{
            tvIsConnected.setText("You are NOT conncted");
        }

        // add click listener to Button "POST"
        btnPost.setOnClickListener(myClickListner);

    }

    public static String POST(String url){
        InputStream is = null;
        String result = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            // open connection
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();
            httpCon.setDoInput(true); //input 허용
            httpCon.setDoOutput(true);  // output 허용
//            httpCon.setUseCaches(false);   // cache copy를 허용하지 않는다.
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Connection", "Keep-Alive");
            httpCon.setRequestProperty("ENCTYPE", "multipart/form-data");
            httpCon.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            httpCon.connect();

            // write data
            DataOutputStream dos = new DataOutputStream(httpCon.getOutputStream());

            try  {
            }    catch(Exception e)   {

            }

            //text 전송
            String str = "Hi";
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"name\"\r\n\r\n" + etName.getText().toString());
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"address\"\r\n\r\n" + eAddress.getText().toString());
            dos.writeBytes(lineEnd);

            Log.i("죽을랑가!!!!!", "!! !!!");

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            // 파일 전송시 파라메터명은 image 파일명은 camera.jpg로 설정하여 전송
            dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + pickpath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.i("아직 살아있!!!!!", "!! !!!");
            String realpath = pickpath;
            FileInputStream fileInputStream = new FileInputStream(realpath);
            Log.i("우오오오오ㅗ오오오!!!!!", "!! !!!");
            int bytesAvailable = fileInputStream.available();
            Log.i("byteAvailble값값값!!!!!", "!!"+ Integer.toString(bytesAvailable)+"!!!");
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                // Upload file part(s)
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                dos.flush(); // finish upload...
            }
            fileInputStream.close();

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
 //           dos.flush(); // finish upload...

            // receive response as inputStream
            if(httpCon.getResponseCode() == HttpURLConnection.HTTP_OK)
                Log.i("테스트", "성공성공");
            else
                Log.i("테스트", "실패");
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        public HttpAsyncTask() {}
        @Override
        protected String doInBackground(String... urls) {
            return POST(urls[0]);
        }
    }
    private boolean validate(){
        if(etName.getText().toString().trim().equals(""))
            return false;
        else if(eAddress.getText().toString().trim().equals(""))
            return false;
        else
            return true;
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
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
                    // TODO Auto-generated catch block
                    Log.i("파일", "파일에러");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.i("파일", "파일에러");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));
        Log.i("방 !!", "getRealPathFromURI(), path : " + uri.toString());

        cursor.close();

        return path;
    }

    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    View.OnClickListener myClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnPost:
                    if (!validate())
                        Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                   else {
                        // call AsynTask to perform network operation on separate thread
                        HttpAsyncTask httpTask = new HttpAsyncTask();
                        httpTask.execute("http://192.168.0.49:3003/host/insert");
                    }
                    break;
            }
            String temp = "{\"img\""+":"+"\""+name_Str+"\""+","+"\"name\"" + ":" + "\"" + etName.getText().toString() + "\"" + "," + "\"address\"" + ":" + "\"" + eAddress.getText().toString() +
                    "\"" + "," + "\"상세정보\"" + ":" + "\"" + eDetail.getText().toString() + "\"" + "}";
            tv.setText(temp);
        }
    };
}

