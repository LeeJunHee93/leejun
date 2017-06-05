package kr.ac.kumoh.ce.mobile.team25;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag4_2 extends Activity {
    Button sbtn;
    String result = "";
    protected ArrayList<myfrag4_2.sroominfo> rArray = new ArrayList<myfrag4_2.sroominfo>();


    protected JSONObject mResult = null;
    protected ListView mList;
    protected myfrag4_2.sroomAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myfrag4_2);
        sbtn=(Button)findViewById(R.id.SRR);
        rArray = new ArrayList<myfrag4_2.sroominfo>();
        mAdapter = new myfrag4_2.sroomAdapter(this, R.layout.listitem2, rArray);
        mList = (ListView)findViewById(R.id.SR_list);
        mList.setAdapter(mAdapter);

        Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(this)));
        myfrag4_2.back task = new myfrag4_2.back();
        task.execute("http://192.168.0.58:3003/host/info");

    }
    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");
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
                    rArray.add(new myfrag4_2.sroominfo(adid,id,rname, loc,image));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(myfrag4_2.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            Log.i("실행", "완료");
            return result;
        }
    }

    public class sroominfo {

        String ip;
        String sname;
        String people;
        String desc;
        String image;

        public sroominfo( String ip,String sname,String people,String desc,String image) {

            this.ip=ip;
            this.sname=sname;
            this.people=people;
            this.desc=desc;
            this.image=image;
        }

        public String getIp() {
            return ip;
        }
        public String getSname() {
            return sname;
        }
        public String getPeople() {
            return people;
        }
        public String getDesc() {
            return desc;
        }
        public String getImage() {
            return image;
        }
    }

    static class sRoomViewHolder {
        TextView txsroom;
        TextView txpeople;
        TextView txdesc;
        NetworkImageView imimage;
    }

    public class sroomAdapter extends ArrayAdapter<myfrag4_2.sroominfo> {

        public sroomAdapter(Context context, int resource, List<myfrag4_2.sroominfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            myfrag4_2.sRoomViewHolder holder;
            if (convertView == null) {
                convertView = myfrag4_2.this.getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                holder = new myfrag4_2.sRoomViewHolder();
                holder.txsroom = (TextView) convertView.findViewById(R.id.sname);
                holder.txpeople = (TextView) convertView.findViewById(R.id.max);
                holder.txdesc = (TextView) convertView.findViewById(R.id.etc);

                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.srimage);
                convertView.setTag(holder);

            } else {
                holder = (myfrag4_2.sRoomViewHolder) convertView.getTag();
            }
            holder.txsroom.setText(getItem(position).getSname());
            holder.txpeople.setText(getItem(position).getPeople());
            holder.txdesc.setText(getItem(position).getDesc());
            holder.imimage.setImageUrl("http://192.168.0.58:3003/" + getItem(position).getImage(), mImageLoader);
            return convertView;
        }
    }

    public void SRRclick(View v){
        Intent intent = new Intent(myfrag4_2.this, myfrag4_3.class);
        startActivity(intent);
    }
}
