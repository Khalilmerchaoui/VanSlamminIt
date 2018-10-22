package app.vanslamminit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {
    EditText ftpHost, ftpUser, ftpPassword, ftppath, mysqlhost, mysqluser, mysqlpass, mysqldatabase, imagepath;
    Button reset;
    SharedPreferences prefs;
    SharedPreferences.Editor editor = null;
    String prefName = "Settings";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
               prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        editor = prefs.edit();


        reset = (Button)findViewById(R.id.reset);
        ftpHost = (EditText) findViewById(R.id.hostftp);
        ftpUser = (EditText) findViewById(R.id.userftp);
        ftpPassword = (EditText) findViewById(R.id.passftp);
        ftppath = (EditText) findViewById(R.id.pathftp);
        mysqlhost = (EditText) findViewById(R.id.hostmysql);
        mysqluser = (EditText) findViewById(R.id.usermysql);
        mysqlpass = (EditText) findViewById(R.id.passmysql);
        mysqldatabase = (EditText) findViewById(R.id.databasemysql);
        imagepath = (EditText) findViewById(R.id.pathimage);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("ftpHost", ftpHost.getText().toString());
                editor.putString("ftpUser", ftpUser.getText().toString());
                editor.putString("ftppassword", ftpPassword.getText().toString());
                editor.putString("ftppath", ftppath.getText().toString());
                editor.putString("mysqlhost", mysqlhost.getText().toString());
                editor.putString("mysqlpass", mysqlpass.getText().toString());
                editor.putString("mysqluser", mysqluser.getText().toString());
                editor.putString("mysqldatabase", mysqldatabase.getText().toString());
                editor.putString("imagepath", imagepath.getText().toString());
                editor.apply();

                SettingsActivity.this.finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ftpHost.setText(prefs.getString("ftpHost", ""));
        ftpUser.setText(prefs.getString("ftpUser", ""));
        ftpPassword.setText(prefs.getString("ftppassword", ""));
        ftppath.setText(prefs.getString("ftppath", ""));
        mysqlhost.setText(prefs.getString("mysqlhost", ""));
        mysqlpass.setText(prefs.getString("mysqlpass", ""));
        mysqluser.setText(prefs.getString("mysqluser", ""));
        mysqldatabase.setText(prefs.getString("mysqldatabase", ""));
        imagepath.setText(prefs.getString("imagepath", ""));

    }
}
