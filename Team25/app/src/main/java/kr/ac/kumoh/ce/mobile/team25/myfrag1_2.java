package kr.ac.kumoh.ce.mobile.team25;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 60974 on 2017-04-04.
 */

public class myfrag1_2 extends Activity implements View.OnClickListener {
    Button b[] = new Button[24];
    Button reservation;
    int button[]= {R.id.b1,R.id.b2,R.id.b3,R.id.b4,R.id.b5,R.id.b6,R.id.b7,R.id.b8,R.id.b9,R.id.b10,R.id.b11,R.id.b12,R.id.b13,R.id.b14,R.id.b15,R.id.b16,R.id.b17,R.id.b18,R.id.b19,R.id.b20,R.id.b21,R.id.b22,R.id.b23,R.id.b24};

    boolean check[] = new boolean[24];
    int number =0;
    String reservationDate;
    String start_time, end_time;
    String roomId;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_my1_2);

        roomId = getIntent().getStringExtra("roomid");
        tv=(TextView)findViewById(R.id.tx);
        CalendarView calendar= (CalendarView)findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int date) {
                Toast.makeText(getApplicationContext(),date+"/"+month+"/"+year,Toast.LENGTH_LONG).show();;
                reservationDate = year + "-" + month + "-" + date;
            }
        });

        for(int i=0; i<24; i++) {
            b[i] = (Button) findViewById(button[i]);
            b[i].setOnClickListener(this);
            b[i].setBackgroundColor(Color.parseColor("#ffffff"));
            check[i]=false;
        }

        reservation = (Button)findViewById(R.id.reservation);
        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0; i<24; i++) {
                    if(check[i] == true ) {
                        start_time = ""+i;
                        for(int j=i+1; j<24; j++) {
                            if(check[j] != true) {
                                end_time = ""+j;
                                break;
                            }
                        }
                        break;
                    }
                }
                reservationPostRequest reservationpostrequest = new reservationPostRequest();
                reservationpostrequest.execute();
            }
        });
    }

    public void onClick(View v){
        for(int i=0;i<24;i++) {
            if (v.getId() ==button[i]) {
                if(check[i]==false) {
                    b[i].setBackgroundColor(Color.parseColor("#C9CDF8"));
                    check[i]=true;
                }
                else {
                    b[i].setBackgroundColor(Color.parseColor("#ffffff"));
                    check[i]=false;
                }
            }
        }
    }

    public void minus(View v){
        if(number <=0) {
            number = 0;
            tv.setText(Integer.toString(number));
        }
        else {
            number--;
            tv.setText(Integer.toString(number));
        }
    }

    public void plus(View v){
        number++;
        tv.setText(Integer.toString(number));
    }

    public class reservationPostRequest extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            String json;
            try {
                Log.i("roomRequest", "execute시작!");

                String apiURL = MainActivity.SERVER_IP_PORT + "/home/reservation/"+roomId;
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestMethod("POST");
                if(login.cookieString != "")
                    con.setRequestProperty("Cookie", login.cookieString);

                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setDefaultUseCaches(false);

                Log.i("roomRequest", "연결직전");
                con.connect();
                Log.i("roomRequest", "연결!");

                JSONObject data = new JSONObject();
                data.accumulate("date", reservationDate);
                data.accumulate("start_time", start_time);
                data.accumulate("end_time", end_time);
                data.accumulate("number", number);

                json = data.toString();
                Log.i("JSONdata", json);

                OutputStream wr = con.getOutputStream();
                wr.write(json.getBytes("utf-8"));
                wr.flush();
                wr.close();

                Log.i("roomRequest", "쓰기성공!");

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("roomRequest", "정상");
                } else {
                    Log.i("roomRequest", "에러!");
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }
    }
}
