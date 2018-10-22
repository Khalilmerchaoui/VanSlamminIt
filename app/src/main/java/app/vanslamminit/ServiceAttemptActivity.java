package app.vanslamminit;

        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Matrix;
        import android.net.Uri;
        import android.os.Bundle;
        import android.os.Environment;
        import android.provider.MediaStore;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.UiSettings;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import android.widget.EditText;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.opencsv.CSVWriter;
        import android.widget.Spinner;
        import android.widget.ImageView;
        import android.widget.ImageButton;

        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.Calendar;
        import java.util.List;
        import java.util.Locale;

public class ServiceAttemptActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    Button save, cancel;
    double latitude, longitude;
    GpsService gps;
    EditText date, time;
    Spinner spinner;
    EditText coordinates, server, defendant, casename, logText;
    String d, dcsv = "", t, tcsv = "";

    ImageView default_image;
    ImageButton pic, gallery, gpsmarker;
    String [] result_list = new String[] {"SAN", "DEF SERVED", "AR SERVED", "DEFENDANT MOVED", "DNR", "DEFUNK", "VACANT", "REFUSED", "AVOIDING", "INVALID ADD", "POSTED"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service);

        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        username = prefs.getString("username", "");

        date = (EditText)findViewById(R.id.date);
        time = (EditText)findViewById(R.id.time);

        pic = (ImageButton)findViewById(R.id.picture);
        gallery = (ImageButton)findViewById(R.id.gallery);
        gpsmarker = (ImageButton)findViewById(R.id.gps);
        default_image = (ImageView)findViewById(R.id.default_image);

        SimpleDateFormat dateF = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        SimpleDateFormat timeF = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        d = dateF.format(Calendar.getInstance().getTime());
        t = timeF.format(Calendar.getInstance().getTime());


        date.setText(d);
        time.setText(t);

        spinner = (Spinner)findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>(Arrays.asList(result_list));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, R.layout.spinner_item,list);

        dataAdapter.setDropDownViewResource
                (R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);

        coordinates = (EditText)findViewById(R.id.gpscoordinates);
        logText = (EditText)findViewById(R.id.loged_text);
        casename = (EditText)findViewById(R.id.casename);
        defendant = (EditText)findViewById(R.id.defendant);
        server = (EditText)findViewById(R.id.server);

        gps = new GpsService(ServiceAttemptActivity.this);

        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        server.setText(username);
        coordinates.setText("Latitude: " + latitude + " | Longitude:" + longitude);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            defendant.setText(extras.getString("defendant", "") + "");
            casename.setText(extras.getString("case", ""));
            server.setText(extras.getString("server", ""));
            date.setText(extras.getString("date", ""));
            time.setText(extras.getString("time", ""));
            if(extras.getString("date", "").length() != 0 && extras.getString("time", "").length() != 0) {
                dcsv = extras.getString("date", "").replace("/", "");
                tcsv = extras.getString("time", "").replace(":", "");
            }
            latitude = Double.parseDouble(extras.getString("latitude", ""));
            longitude = Double.parseDouble(extras.getString("longitude", ""));
            spinner.setSelection(SelectedItem(extras.getString("result", "")));
            if (extras.getString("path", "").length() == 0) {
                default_image.setImageResource(R.drawable.default_image);
                default_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                default_image.setImageBitmap(BitmapFactory.decodeFile(extras.getString("path", "")));
            }
        }

        if(dcsv == "" && tcsv == "") {
            dcsv = d.replace("/", "");
            tcsv = t.replace(":", "");
        }
        save = (Button)findViewById(R.id.save);
        cancel = (Button)findViewById(R.id.cancel);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        save.setOnClickListener(this);
        cancel.setOnClickListener(this);
        gpsmarker.setOnClickListener(this);
        gallery.setOnClickListener(this);
        pic.setOnClickListener(this);

    }
    GoogleMap mMap;
    UiSettings  settings ;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng lat = new LatLng(latitude, longitude);
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        mMap.addMarker(new MarkerOptions()
                .position(lat)
                .icon(BitmapDescriptorFactory.defaultMarker()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lat));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13.0f));


        settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setRotateGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setMyLocationButtonEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                latitude = latLng.latitude;
                longitude = latLng.longitude;

                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });

    }

    SharedPreferences prefs;
    String prefName = "Data", username;
    private void ExportInCSV (EditText d, EditText c, EditText s, EditText da, EditText t, Spinner r, String filepath, double lat, double lon, EditText log) {

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "VanSlammin'it");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {

            String csv = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/VanSlammin'it" + File.separator + username + "_" + dcsv + "_" + tcsv + ".csv";
            CSVWriter writer = null;
            try {
                writer = new CSVWriter(new FileWriter(csv));
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<String[]> data = new ArrayList<String[]>();
            data.add(new String[] {d.getText().toString(), c.getText().toString(), s.getText().toString(),
                da.getText().toString(), t.getText().toString(), r.getSelectedItem().toString(), filepath
                , lat +"", lon+"", log.getText().toString()});

            if (writer != null) {
                writer.writeAll(data);
            }
            try {
                if (writer != null) {
                    writer.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }        }

    }

    int SELECT_PHOTO = 100;
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.save:

                try {
                    if(!selected)
                    saveBitmap(username + "_" + dcsv + "_" + tcsv, rotated, 100);
                    else {
                        ResaveImage(username + "_" + dcsv + "_" + tcsv, imgDecodableString);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ExportInCSV(defendant, casename, server,
                        date, time, spinner, imagePath, latitude,
                        longitude, logText);
                ServiceAttemptActivity.this.finish();
                break;
            case R.id.cancel:
                ServiceAttemptActivity.this.finish();
                break;
            case R.id.picture:
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1);
                break;
            case R.id.gallery:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
            case R.id.gps:
                coordinates.setText("Latitude: " + String.format("%.8f", latitude) + " | Longitude:" + String.format("%.8f", longitude));
                break;
        }
    }
    Bitmap photo, rotated;
    boolean selected = false;
    String imgDecodableString;
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotated = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
            default_image.setImageBitmap(rotated);
        }

        if(resultCode == RESULT_OK && requestCode == SELECT_PHOTO) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
            selected = true;
            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap SelectedImage = BitmapFactory.decodeStream(imageStream);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotated = Bitmap.createBitmap(SelectedImage, 0, 0, SelectedImage.getWidth(), SelectedImage.getHeight(), matrix, true);
            default_image.setImageBitmap(rotated);
        }
    }

    String imagePath ;
    public  void saveBitmap(String filename, Bitmap bitmap, int value)
            throws IOException {
        if(bitmap != null) {
            File f;
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, value, bytes);
            File folder = new File(Environment.getExternalStorageDirectory() + "/VanSlammin'it");
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                f = new File(Environment.getExternalStorageDirectory() + "/VanSlammin'it/" + filename + ".jpeg");
                f.createNewFile();
                imagePath = f.getAbsolutePath();

                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();

            }
          }
        }

    private void ResaveImage(String filename, String filePath) throws IOException {

        File image = new File(filePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(),true);
        saveBitmap(filename, bitmap, 40);
    }

    private int SelectedItem(String item) {
        int position = 0;
        for (int i = 0; i < spinner.getAdapter().getCount(); i ++) {
            if( spinner.getItemAtPosition(i) == item)
                 position = i;
        }
        return position;
    }

}