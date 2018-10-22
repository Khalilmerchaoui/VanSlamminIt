package app.vanslamminit;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ListView;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import android.widget.LinearLayout;
import java.util.ArrayList;
import android.widget.TextView;
import java.util.List;


public class SyncResultsActivity extends AppCompatActivity {
    List<String> listTitles;
    Button sync, cancel;
    ListView list;
    File sdCardRoot, Dir;
    String[] AllFiles = new String[50];
    TextView success;
    LinearLayout fail;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_layout);
        sync = (Button)findViewById(R.id.sync);
        cancel = (Button)findViewById(R.id.cancel);
        list = (ListView)findViewById(R.id.listView);
        success = (TextView)findViewById(R.id.success);
        fail = (LinearLayout)findViewById(R.id.fail);

        listTitles = new ArrayList<String>();

         sdCardRoot = Environment.getExternalStorageDirectory();
        Dir = new File(sdCardRoot, "VanSlammin'it");
        if(Dir.exists()) {
            int i = 0;
            for (File f : Dir.listFiles()) {
                if (f.isFile()) {
                    String filenameArray[] = f.getName().split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];
                    if (extension.equals("csv"))
                        listTitles.add(f.getName());
                    AllFiles[i] = f.getName();
                    i++;
                }
            }
        }
        ListAdapter adapter = new ListAdapter(getApplicationContext(), listTitles);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            String[] list;
                                            try {
                                                Intent i = new Intent(SyncResultsActivity.this, ServiceAttemptActivity.class);
                                                list = readCsv(Dir.getAbsolutePath() + File.separator + listTitles.get(position));
                                                i.putExtra("defendant", list[0]);
                                                i.putExtra("case", list[1]);
                                                i.putExtra("server", list[2]);
                                                i.putExtra("date", list[3]);
                                                i.putExtra("time", list[4]);
                                                i.putExtra("result", list[5]);
                                                i.putExtra("path", list[6]);
                                                i.putExtra("latitude", list[7]);
                                                i.putExtra("longitude", list[8]);
                                                startActivity(i);

                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                sync.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int o = 0; o < AllFiles.length; o ++ ) {
                            if( AllFiles[o] != null) {
                                Intent i = new Intent(SyncResultsActivity.this, FileUploadService.class);
                                if (AllFiles[o].endsWith("csv")) {
                                    i.putExtra("type", "csv");
                                }
                                else {
                                    i.putExtra("type", "image");
                                }
                                i.putExtra("filepath", Dir.getAbsolutePath() + File.separator + AllFiles[o]);
                                startService(i);
                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                        if(Dir.list().length != 0) {
                            list.getLayoutParams().height = 400;
                            onResume();
                            fail.setVisibility(View.VISIBLE);
                        }
                        else {
                            success.setVisibility(View.VISIBLE);
                            fail.setVisibility(View.INVISIBLE);
                            list.setVisibility(View.INVISIBLE);
                        }
                            }
                        },5000);
                    }
                });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncResultsActivity.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        listTitles = new ArrayList<String>();

        sdCardRoot = Environment.getExternalStorageDirectory();
        Dir = new File(sdCardRoot, "VanSlammin'it");
        if(Dir.exists()) {
            for (File f : Dir.listFiles()) {
                if (f.isFile()) {
                    String filenameArray[] = f.getName().split("\\.");
                    String extension = filenameArray[filenameArray.length - 1];
                    if (extension.equals("csv"))
                        listTitles.add(f.getName());
                }
            }
        }
        ListAdapter adapter = new ListAdapter(getApplicationContext(), listTitles);
        list.setAdapter(adapter);
    }

    public final String[] readCsv(String CSV_PATH) throws IOException {
        String[] questionList ;
        CSVReader reader = new CSVReader(new FileReader(CSV_PATH));
        questionList = reader.readNext();
        return questionList;
    }
}
