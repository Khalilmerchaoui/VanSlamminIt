package app.vanslamminit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class FileUploadService extends Service {

    Bundle extras;
    String ftpHost, ftpUser, ftpPassword, ftppath, mysqlhost, mysqluser, mysqlpass, mysqldatabase, imagepath, filePath, type;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    SharedPreferences prefs;
    String prefName = "Settings";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        extras = intent.getExtras();
        if(extras != null) {
            ftppath = prefs.getString("ftppath", "");
            ftpHost = prefs.getString("ftpHost", "");
            ftpUser = prefs.getString("ftpUser", "");
            ftpPassword = prefs.getString("ftppassword", "");
            mysqlhost = prefs.getString("mysqlhost", "");
            mysqlpass = prefs.getString("mysqlpass", "");
            mysqluser = prefs.getString("mysqluser", "");
            mysqldatabase = prefs.getString("mysqldatabase", "");
            imagepath = prefs.getString("imagepath", "");
            type = extras.getString("type");
            filePath = extras.getString("filepath");


        }
        Toast.makeText(FileUploadService.this, type, Toast.LENGTH_SHORT).show();
        try {
            if(type.equals("csv")) {
                String result = new FileUpload(getApplicationContext()).execute("http://www.vanslaminc.com/app/upload_server.php", filePath, "uploadedfile", ftppath).get();
                if (result.contains("Success")) {
                    new File(filePath).delete();
                }
            }
            else {
                String result = new FileUpload(getApplicationContext()).execute("http://www.vanslaminc.com/app/upload_server.php", filePath, "uploadedfile", imagepath).get();
                if (result.contains("Success")) {
                    new File(filePath).delete();
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }


    public class FileUpload  extends AsyncTask<String, String, String> {

        private Context mContext;
        HttpURLConnection connection = null;
        String responseStr;

        public FileUpload(Context context) {
            mContext = context;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... params) {
            String urlTo = params[0], filepath = params[1], filefield = params[2], Server_path = params[3];
            String twoHyphens = "--";
            String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
            String lineEnd = "\r\n";

            String result = "";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 50 * 1024 * 1024;

            String[] q = filepath.split("/");
            int idx = q.length - 1;


            try {
                File file = new File(filepath);

                FileInputStream fileInputStream = new FileInputStream(file);

                URL url = new URL(urlTo);
                 connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setChunkedStreamingMode(1024 * 500);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"path\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain; charset=UTF-8" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(Server_path + lineEnd);
                outputStream.flush();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + filefield + "\"; filename=\"" + q[idx] + lineEnd);
                //audio :audio/mpeg
                //image image/jpeg
                // outputStream.writeBytes("Content-Type: " + Type + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);

                buffer = new byte[bufferSize];

                int sentBytes = 0;
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    sentBytes += bufferSize;
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);

                // Upload POST Data

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);


                fileInputStream.close();
                outputStream.flush();
                outputStream.close();

                InputStream s = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(s));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                responseStr = response.toString();

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
            return responseStr;
        }
    }
}
