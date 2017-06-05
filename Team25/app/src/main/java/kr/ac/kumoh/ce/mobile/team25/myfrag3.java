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

public class myfrag3 extends Fragment {
    protected ArrayList<reservinfo> rArray = new ArrayList<reservinfo>();
    protected ListView mList;
    protected reservAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;

    TextView name;
    TextView phone;
    TextView email;
    String result="";
    int value;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my3, container, false);
        rArray = new ArrayList<reservinfo>();
        mAdapter = new reservAdapter(getActivity(), R.layout.listitem3, rArray);
        mList = (ListView) rootView.findViewById(R.id.reservlist);
        mList.setAdapter(mAdapter);
        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));
        name=(TextView)rootView.findViewById(R.id.name);
        phone=(TextView)rootView.findViewById(R.id.phone);
        email=(TextView)rootView.findViewById(R.id.email);
        myPageGetRequest mypagegetrequest = new myPageGetRequest();
        mypagegetrequest.execute(MainActivity.SERVER_IP_PORT + "/my/info");

        return rootView;
    }
    public void goLogin(){
        Intent intent = new Intent(getActivity(), login.class);
        startActivity(intent);
    }

    private class myPageGetRequest extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                if(login.cookieString != "")
                    conn.setRequestProperty("Cookie", login.cookieString);
                conn.setDoInput(true);

                Log.i("task", "연결?");
                conn.connect();
                Log.i("task", "연결!");
                if(conn.getResponseCode() == 404){
                    goLogin();
                    return null;
                }
                Log.i("task", "비트맵?");
                Log.i("responce code",""+conn.getResponseCode());

                InputStream inputStream = conn.getInputStream();

                if(inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            }catch(IOException e){
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String str) {
            if(str == null) return;
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONObject user = jsResult.getJSONObject("user");
                JSONArray reservationlist = jsResult.getJSONArray("reservations");
                Log.i("reservationlist.length", ""+reservationlist.length());
                String id = user.getString("id");
                String dpName = user.getString("displayName");
                name.setText(dpName);
                Log.i("str", str);

                for(int i=0; i < reservationlist.length(); i++) {
                    JSONObject jsonObject = reservationlist.getJSONObject(i);
                    String image=jsonObject.getString("img");
                    String idd= jsonObject.getString("id");
                    String rname = jsonObject.getString("studyroom");
                    String sname = jsonObject.getString("room");
                    String address = jsonObject.getString("address");
                    String date = jsonObject.getString("date");
                    String time = jsonObject.getString("time");
                    String people = jsonObject.getString("number");

                    rArray.add(new reservinfo(image,idd,rname,sname,address,date,time,people));
                }

                mAdapter.notifyDataSetChanged();
            }
            catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            return result;
        }
    }
    public class reservinfo {
        String image;
        String id;
        String rname;
        String sname;
        String address;
        String date;
        String time;
        String people;

        public reservinfo(String image,String id,String rname, String sname, String address, String date, String time,String people) {
            this.image=image;
            this.id=id;
            this.rname=rname;
            this.sname=sname;
            this.address=address;
            this.date=date;
            this.time=time;
            this.people=people;
        }

        public String getRname() { return rname; }
        public String getSname() {
            return sname;
        }
        public String getAddress() {
            return address;
        }
        public String getDate() {
            return date;
        }
        public String getTime() {
            return time;
        }
        public String getPeople() {
            return people;
        }
        public String getImage() {
            return image;
        }

    }

    static class reservViewHolder {
        TextView txrname;
        TextView txsname;
        TextView txaddress;
        TextView txdate;
        TextView txtime;
        TextView txpeople;
        NetworkImageView imimage;
    }

    public class reservAdapter extends ArrayAdapter<reservinfo> {

        public reservAdapter(Context context, int resource, List<reservinfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            reservViewHolder holder;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem3, parent, false);
                holder = new reservViewHolder();
                holder.txrname = (TextView) convertView.findViewById(R.id.rname);
                holder.txsname = (TextView) convertView.findViewById(R.id.sname);
                holder.txaddress = (TextView) convertView.findViewById(R.id.address);
                holder.txdate = (TextView) convertView.findViewById(R.id.date);
                holder.txtime = (TextView) convertView.findViewById(R.id.time);
                holder.txpeople = (TextView) convertView.findViewById(R.id.people);
                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.reservimage);
                convertView.setTag(holder);
            } else {
                holder = (reservViewHolder) convertView.getTag();
            }

            holder.txrname.setText(getItem(position).getRname());
            holder.txsname.setText(getItem(position).getSname());
            holder.txaddress.setText(getItem(position).getAddress());
            holder.txdate.setText(getItem(position).getDate());
            holder.txtime.setText(getItem(position).getTime());
            holder.txpeople.setText(getItem(position).getPeople());
            holder.imimage.setImageUrl(MainActivity.SERVER_IP_PORT + "/" + getItem(position).getImage(), mImageLoader);
            return convertView;
        }
    }
}