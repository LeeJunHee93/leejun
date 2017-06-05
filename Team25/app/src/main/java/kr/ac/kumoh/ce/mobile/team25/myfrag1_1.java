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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class myfrag1_1 extends Activity {

    String result = "";
    protected ArrayList<myfrag1_1.sroominfo> rArray = new ArrayList<myfrag1_1.sroominfo>();


    protected JSONObject mResult = null;
    protected ListView mList;
    protected myfrag1_1.sroomAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my1_1);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String img=intent.getStringExtra("img");
        rArray = new ArrayList<myfrag1_1.sroominfo>();
        mAdapter = new myfrag1_1.sroomAdapter(this, R.layout.listitem2, rArray);
        mList = (ListView)findViewById(R.id.listview2);
        mList.setAdapter(mAdapter);

        Cache cache = new DiskBasedCache(this.getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(this)));
        myfrag1_1.back task = new myfrag1_1.back();
        task.execute("http://192.168.0.58:3003/home/info/"+id);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                String roomid = mAdapter.getItem(pos).getId();
                Intent intent = new Intent(myfrag1_1.this, myfrag1_2.class);
                intent.putExtra("roomid", roomid);
                startActivity(intent);
            }
        });

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
                JSONObject studyroom = jsResult.getJSONObject("studyroom");
                String img = studyroom.getString("img");
                String name = studyroom.getString("name");
                String address = studyroom.getString("address");
                NetworkImageView immg=(NetworkImageView)findViewById(R.id.room);
                immg.setImageUrl("http://192.168.0.58:3003/"+img,mImageLoader);
                TextView nname=(TextView)findViewById(R.id.rname);
                nname.setText(name);
                TextView adr=(TextView)findViewById(R.id.address);
                adr.setText(address);
                JSONArray jsonMainNode=jsResult.getJSONArray("rooms");

                for(int i=0 ;i<jsonMainNode.length();i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                    String id=jsonChildNode.getString("id");
                    String ip=jsonChildNode.getString("ip");
                    String sname = jsonChildNode.getString("name");
                    String max=jsonChildNode.getString("max");
                    String desc=jsonChildNode.getString("description");
                    String image = jsonChildNode.getString("img");

                    rArray.add(new myfrag1_1.sroominfo(id,ip,sname,max,desc,image));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(myfrag1_1.this, "Error" + e.toString(), Toast.LENGTH_LONG).show();
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
        private String id;
        private String ip;
        private String img;
        private String sroom;
        private String max;
        private String etc;

        public sroominfo(String id,String ip,String sroom, String max, String etc,String img) {
            this.id=id;
            this.ip=ip;
            this.img=img;
            this.sroom = sroom;
            this.max = max;
            this.etc = etc;

        }
        public String getId(){return id;}
        public String getip(){return ip;}
        public String getImg(){return img;}
        public String getSroom() {
            return sroom;
        }

        public String getMax() {
            return max;
        }

        public String getEtc() {
            return etc;
        }

    }

    static class sroomViewHolder {
        TextView txsroom;
        TextView txmax;
        TextView txetc;
        NetworkImageView imimage;
    }

    public class sroomAdapter extends ArrayAdapter<myfrag1_1.sroominfo> {

        public sroomAdapter(Context context, int resource, List<myfrag1_1.sroominfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            myfrag1_1.sroomViewHolder holder;
            if (convertView == null) {
                convertView = myfrag1_1.this.getLayoutInflater().inflate(R.layout.listitem2, parent, false);
                holder = new myfrag1_1.sroomViewHolder();
                holder.txsroom = (TextView) convertView.findViewById(R.id.sname);
                holder.txmax = (TextView) convertView.findViewById(R.id.max)                ;
                holder.txetc = (TextView) convertView.findViewById(R.id.etc);
                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.srimage);
                convertView.setTag(holder);

            } else {
                holder = (myfrag1_1.sroomViewHolder) convertView.getTag();
            }
            holder.txsroom.setText(getItem(position).getSroom());
            holder.txmax.setText(getItem(position).getMax());
            holder.txetc.setText(getItem(position).getEtc());
            holder.imimage.setImageUrl("http://192.168.0.58:3003/" + getItem(position).getImg(), mImageLoader);
            return convertView;
        }
    }
}
