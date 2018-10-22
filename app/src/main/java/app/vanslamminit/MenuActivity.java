package app.vanslamminit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.support.v7.app.AppCompatActivity;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener{
    Button service, results, settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
        service = (Button)findViewById(R.id.service);
        results = (Button)findViewById(R.id.result);
        settings = (Button)findViewById(R.id.settings);

        service.setOnClickListener(this);
        results.setOnClickListener(this);
        settings.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.service:
                LaunchActivity(ServiceAttemptActivity.class);
                break;
            case R.id.result:
                LaunchActivity(SyncResultsActivity.class);
                break;
            case R.id.settings:
                LaunchActivity(SettingsActivity.class);
                break;
        }
    }

    private void LaunchActivity(Class launch) {
        Intent i = new Intent(MenuActivity.this, launch);
        startActivity(i);
    }
}
