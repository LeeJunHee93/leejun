package kr.ac.kumoh.ce.mobile.team25;

/**
 * Created by dlgus on 2017-04-24.
 */
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.app.Activity.RESULT_OK;


public class myfrag2 extends Fragment {
    private Uri mImageCaptureUri;
    private static final int PICK_FROM_CAMERA=0;
    Button Camerabtn, Openbtn, Lockbtn, Returnbtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my2, container, false);
        Camerabtn=(Button)rootView.findViewById(R.id.camera);
        Openbtn=(Button)rootView.findViewById(R.id.open);
        Lockbtn =(Button)rootView.findViewById(R.id.lock);
        Returnbtn=(Button)rootView.findViewById(R.id.bannap);

//        keyinfoGetRequest keyinfogetrequest = new keyinfoGetRequest();
//        keyinfogetrequest.execute();

        Camerabtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doTakePhotoAction();
                keyCameraPostRequest keycamerapostrequest = new keyCameraPostRequest();
                keycamerapostrequest.execute(MainActivity.SERVER_IP_PORT + "/key/camera");
            }
        });

        Returnbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyGetRequest keygetrequest = new keyGetRequest();
                keygetrequest.execute(MainActivity.SERVER_IP_PORT + "/key/return");
            }
        });

        Openbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyGetRequest keygetrequest = new keyGetRequest();
                keygetrequest.execute(MainActivity.SERVER_IP_PORT + "/key/open");
            }
        });

        Lockbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keyGetRequest keygetrequest = new keyGetRequest();
                keygetrequest.execute(MainActivity.SERVER_IP_PORT + "key/lock");
            }
        });

        return rootView;
    }

    public void doTakePhotoAction() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String url="tmp_"+String.valueOf(System.currentTimeMillis())+".jpg";
        mImageCaptureUri=Uri.fromFile(new File(Environment.getExternalStorageDirectory(),url));
        Log.i("사진파일",""+mImageCaptureUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
        startActivityForResult(intent,PICK_FROM_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode){
            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.actoin.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);

                break;
            }
        }
    }

    private class keyCameraPostRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            InputStream is = null;
            String result = "";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            try {
                URL urlCon = new URL(urls[0]);
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

                Log.i("죽을랑가!!!!!", "!! !!!");

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"image\";filename=\"" + mImageCaptureUri.toString() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                String realpath = mImageCaptureUri.toString();
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
                    dos.flush(); // finish upload...
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
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";

        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    private class keyGetRequest extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", login.cookieString);
                conn.setDoInput(true);

                Log.i("keyGetRequest", "연결?");
                conn.connect();
                Log.i("keyGetRequest", "연결!");

                InputStream inputStream = conn.getInputStream();
                Log.i("keyGetRequest", "받아옴!");

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class keyinfoGetRequest extends AsyncTask<String, Integer, String> {
        String result;

        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Cookie", login.cookieString);
                conn.setDoInput(true);

                Log.i("task", "연결?");
                conn.connect();
                Log.i("task", "연결!");

                Log.i("task", "비트맵?");
                InputStream inputStream = conn.getInputStream();

                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONArray jsonMainNode=jsResult.getJSONArray("list");

                for(int i=0 ;i<jsonMainNode.length();i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String adid=jsonChildNode.getString("adminId");
                    Log.i("adminid", adid);
                    String id=jsonChildNode.getString("id");
                    Log.i("id", id);
                    String rname = jsonChildNode.getString("name");
                    Log.i("rname", rname);
                    String loc = jsonChildNode.getString("address");
                    Log.i("loc", loc);
                    String image = jsonChildNode.getString("img");
                    Log.i("image", image);
                }
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
