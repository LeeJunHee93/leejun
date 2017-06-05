package kr.ac.kumoh.ce.mobile.team25;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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


public class myfrag4_3 extends Activity {
    final int PICK_IMAGE = 100;

    EditText sname, people, etc,ip;
    Button btn;
    String name_Str, pickpath;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myfrag4_3);
        sname = (EditText) findViewById(R.id.sname);
        people = (EditText) findViewById(R.id.people);
        etc = (EditText) findViewById(R.id.etc);
        ip=(EditText)findViewById(R.id.ip);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(myClickListner);
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

    public String POST(String url){
        InputStream is = null;
        String result = "";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        try {
            URL urlCon = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)urlCon.openConnection();
            conn.setDoInput(true); //input 허용
            conn.setDoOutput(true);  // output 허용
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Cookie", login.cookieString);

            conn.connect();

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"sname\"\r\n\r\n" + sname.getText().toString());
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"people\"\r\n\r\n" + people.getText().toString());
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"etc\"\r\n\r\n" + etc.getText().toString());
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ip\"\r\n\r\n" + ip.getText().toString());
            dos.writeBytes(lineEnd);

            Log.i("죽을랑가!!!!!", "!! !!!");

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + pickpath + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

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
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                dos.flush();
            }
            fileInputStream.close();

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            dos.flush(); // finish upload...

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                Log.i("테스트", "성공성공");
            else
                Log.i("테스트", "실패");
            try {
                is = conn.getInputStream();
                if(is != null)
                    result = convertInputStreamToString(is);
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
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

    private class roomPostRequest extends AsyncTask<String, Void, String> {
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
        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));
        Log.i("방 !!", "getRealPathFromURI(), path : " + uri.toString());

        cursor.close();

        return path;
    }

    public String getImageNameToUri(Uri data) {
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
        public void onClick(View v) {
            roomPostRequest roompostrequest = new roomPostRequest();
            roompostrequest.execute(MainActivity.SERVER_IP_PORT + "/host/insert");
            finish();
        }
    };

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();

        return result;
    }
}
