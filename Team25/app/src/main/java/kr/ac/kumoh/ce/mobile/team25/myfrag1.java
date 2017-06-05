package kr.ac.kumoh.ce.mobile.team25;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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

public class myfrag1 extends Fragment implements AdapterView.OnItemClickListener {

    String result = "";
    protected ArrayList<roominfo> rArray = new ArrayList<roominfo>();
    protected JSONObject mResult = null;
    protected ListView mList;
    protected roomAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my1, container, false);
        rArray = new ArrayList<roominfo>();
        mAdapter = new roomAdapter(getActivity(), R.layout.listitem1, rArray);
        mList = (ListView) rootView.findViewById(R.id.listview1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));
        homelistGetRequest homelistgetrequest = new homelistGetRequest();
        homelistgetrequest.execute(MainActivity.SERVER_IP_PORT + "/home/list");
        return rootView;
    }

    private class homelistGetRequest extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
                    rArray.add(new roominfo(adid,id, rname, loc,image));
                }

                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
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

    public class roominfo {
        String adminid;
        String id;
        String name;
        String loc;
        String image;

        public roominfo(String adid, String id, String name, String loc, String image) {
            this.adminid = adid;
            this.id = id;
            this.name = name;
            this.loc = loc;
            this.image = image;
        }

        public String getAdminid() {
            return adminid;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLoc() {
            return loc;
        }

        public String getImage() {
            return image;
        }
    }

    static class RoomViewHolder {
        TextView txRoom;
        TextView txLoc;
        NetworkImageView imimage;
    }

    public class roomAdapter extends ArrayAdapter<roominfo> {

        public roomAdapter(Context context, int resource, List<roominfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RoomViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem1, parent, false);
                holder = new RoomViewHolder();
                holder.txRoom = (TextView) convertView.findViewById(R.id.name);
                holder.txLoc = (TextView) convertView.findViewById(R.id.address);
                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.studyroomimage);
                convertView.setTag(holder);

            } else {
                holder = (RoomViewHolder) convertView.getTag();
            }
            holder.txRoom.setText(getItem(position).getName());
            holder.txLoc.setText(getItem(position).getLoc());
            holder.imimage.setImageUrl(MainActivity.SERVER_IP_PORT + getItem(position).getImage(), mImageLoader);
            return convertView;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        String id = mAdapter.getItem(pos).getId();
        String img=mAdapter.getItem(pos).getImage();
        Intent intent=new Intent(getActivity(),myfrag1_1.class);
        intent.putExtra("id",id);
        intent.putExtra("img",img);
        startActivity(intent);
    }

}
