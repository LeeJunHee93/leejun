package kr.ac.kumoh.ce.mobile.team25;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Toast;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class login extends Activity {
    public static OAuthLogin mOAuthLoginModule;
    private static OAuthLoginButton mOAuthLoginButton;
    private static Context mContext;
    String token;
    static String json="", cookieString="";
    loginPostRequest loginpostrequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginpostrequest = new loginPostRequest();

        mContext = getApplicationContext();
        setContentView(R.layout.mylogin);

        mOAuthLoginModule = OAuthLogin.getInstance();
        mOAuthLoginModule.init(
                mContext
                , "XrgiqeOS9rEeJJ35jxmt"
                , "PyWxIsxzTq"
                , "6097461"
        );
        mOAuthLoginButton = (OAuthLoginButton) findViewById(R.id.buttonOAuthLoginImg);
        mOAuthLoginButton.setOAuthLoginHandler(mOAuthLoginHandler);
        mOAuthLoginButton.setBgResourceId(R.drawable.naverbtn);

    }

    private OAuthLoginHandler mOAuthLoginHandler = new OAuthLoginHandler() {
        @Override
        public void run(boolean success) {
            if (success) {
                String accessToken = mOAuthLoginModule.getAccessToken(mContext);
                String refreshToken = mOAuthLoginModule.getRefreshToken(mContext);
                long expiresAt = mOAuthLoginModule.getExpiresAt(mContext);
                String tokenType = mOAuthLoginModule.getTokenType(mContext);
                Log.i("출력", accessToken);
                token = accessToken;
                loginpostrequest.execute();
                finish();

            } else {
                String errorCode = mOAuthLoginModule.getLastErrorCode(mContext).getCode();
                String errorDesc = mOAuthLoginModule.getLastErrorDesc(mContext);
                Toast.makeText(mContext, "errorCode:" + errorCode
                        + ", errorDesc:" + errorDesc, Toast.LENGTH_SHORT).show();
            }
        }


    };

    public class loginPostRequest extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... unused) {
            try {
                Log.i("login", "execute시작!");

                String COOKIES_HEADER = "Set-Cookie";

                String apiURL = MainActivity.SERVER_IP_PORT + "/auth/naver";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                Log.i("login", "연결!?!?!?!");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestMethod("POST");
                Log.i("login", "연결1/2");

                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setDefaultUseCaches(false);

                Log.i("login", "연결직전");
                con.connect();
                Log.i("login", "연결!");

                JSONObject data = new JSONObject();
                data.accumulate("access_token", token);
                json = data.toString();
                Log.i("JSONdata", json);

                OutputStream wr = con.getOutputStream();
                wr.write(json.getBytes("utf-8"));
                wr.flush();
                wr.close();

                Map<String, List<String>> headerFields = con.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if(cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        String cookieName = HttpCookie.parse(cookie).get(0).getName();
                        String cookieValue = HttpCookie.parse(cookie).get(0).getValue();

                        cookieString = cookieName + "=" + cookieValue;

                        CookieManager.getInstance().setCookie(MainActivity.SERVER_IP_PORT, cookieString);
                    }
                }
                Log.i("login", "쓰기성공!");

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                    Log.i("login", "정상");


                } else {  // 에러 발생
                    Log.i("login", "에러!");
                }

            } catch (Exception e) {
                System.out.println(e);
            }
            return null;
        }

    }
    public void gbtnclick(View v) {
        Log.i("server request", "google");
    }

}
