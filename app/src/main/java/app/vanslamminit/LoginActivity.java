package app.vanslamminit;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor = null;
    String prefName = "Data";

    EditText username, password;
    Button login;
    ProgressDialog progress;
    String user, pass;
    boolean connected = false;
    ConnectivityManager connectivityManager;
    static String URI =  "http://www.vanslaminc.com/app/login_script.php";// "http://www.vanslaminc.com/app/login_script.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        editor = prefs.edit();

        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo() == null) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Connection", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(getResources().getColor(R.color.Action))
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            recreate();
                        }
                    });
            snackbar.show();

        }

        else {
            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    user = username.getText().toString();
                    pass = password.getText().toString();
                    editor.putString("username", user);
                    editor.apply();

                    progress = ProgressDialog.show(LoginActivity.this, "Please wait", "Logging in...", true);
                    new LoginAsync().execute();
                  //  if (connected) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                Intent o = new Intent(LoginActivity.this, MenuActivity.class);
                                startActivity(o);
                                LoginActivity.this.finish();

                            }
                        }, 2800);
                    /*} else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, "Username or Password invalid ", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        }, 2800);
                    }*/


                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    private class LoginAsync extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... uri) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try {
                URL url = new URL(URI);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                return forecastJsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray response = jsonObject.optJSONArray("server_response");
                if(AccountExists(response, user)) {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject json = response.getJSONObject(i);
                                if(user.equals(json.optString("Name").toString()) && pass.equals(json.optString("Password").toString())) {
                                 connected = true;
                                }
                            }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean AccountExists(JSONArray jsonArray, String AccountToFind){
        return jsonArray.toString().contains("\"Name\":\"" + AccountToFind + "\"");
    }
}
