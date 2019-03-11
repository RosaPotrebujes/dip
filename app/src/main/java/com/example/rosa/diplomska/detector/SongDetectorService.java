package com.example.rosa.diplomska.detector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.rosa.diplomska.volley.AppSingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.rosa.diplomska.detector.DetectedActivitiesIntentService.TAG;

public class SongDetectorService extends Service {
    private final String detectAudioUrl = "http://192.168.1.119/ada_login_api/Source_Files/audioDetect.php";
    private MediaRecorder mRecorder;
    private String mAudioFileName = "";
    private String mAudioSavePathInDevice = null;
    private String mError = "";
    private String detectedSong = "";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"Song detection started.");
        mAudioFileName = Long.toString(System.currentTimeMillis());
        mAudioSavePathInDevice = this.getFilesDir().getPath() + "/" + mAudioFileName + ".m4a";

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                startRecording();
            }
        });
        thread.start();

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startRecording() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        if(pref.getBoolean("music",true)) {
            mediaRecorderReady();
            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void stopRecording() {
        mRecorder.stop();
        volleySendAudio();
    }

    public void mediaRecorderReady() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mAudioSavePathInDevice);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        mRecorder.setAudioSamplingRate(44100);
        mRecorder.setAudioEncodingBitRate(192000);
        mRecorder.setMaxDuration(12000);
        mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
                    Log.i(TAG,"12 seconds recorded.");
                    stopRecording();
                }
            }
        });
    }

    public void volleySendAudio() {
        File f = new File(mAudioSavePathInDevice);
        byte[] bytesArray = fileToBytes(f);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("fun","detectAudio");
        parameters.put("fileType","m4a");
        parameters.put("filename",f.getName());
        parameters.put("fileContent", Base64.encodeToString(bytesArray, Base64.DEFAULT));
        JSONObject jp = new JSONObject(parameters);
        final JsonObjectRequest detectAudioRequest = new JsonObjectRequest(Request.Method.POST, detectAudioUrl, jp, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getInt("success") == 0) {
                        mError = response.getString("message");
                        detectedSong = "";
                    } else {
                        String[] tSong = response.getString("song_name").split("--");
                        //v bazi imam naslov pesmi v obliki "Ime-Priimek--Naslov-Pesmi"
                        detectedSong = tSong[0].replace("-"," ") + " - "
                                + tSong[1].replace("-"," ");
                        mError = "";
                        Log.i(TAG,"Detected song: " + detectedSong);
                        //mOnSongDetectedListener.onSongDetected(detectedSong);
                    }
                    Intent songIntent = new Intent(DetectorConstants.ACTION_SONG_DETECTED);
                    songIntent.putExtra(DetectorConstants.EXTRA_SONG,detectedSong);
                    sendBroadcast(songIntent);
                    stopSelf();
                } catch (JSONException e) {
                    e.printStackTrace(); //Toast.makeText(loginActivity.getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    mError = "Error while parsing response.";
                    detectedSong = "";
                    Log.i(TAG,mError);
                    Intent songIntent = new Intent(DetectorConstants.ACTION_SONG_DETECTED);
                    songIntent.putExtra(DetectorConstants.EXTRA_SONG,detectedSong);
                    sendBroadcast(songIntent);
                    stopSelf();
                }
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                mError = volleyError.toString();
                detectedSong = "";
                Intent songIntent = new Intent(DetectorConstants.ACTION_SONG_DETECTED);
                songIntent.putExtra(DetectorConstants.EXTRA_SONG,detectedSong);
                sendBroadcast(songIntent);
                Log.i(TAG,"volley error: "+mError);
                stopSelf();
            }
        });
        AppSingleton appSingleton = AppSingleton.getInstance(this);
        String tag = "DETECT_AUDIO_TAG";
        detectAudioRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        appSingleton.addToRequestQueue(detectAudioRequest,tag);

        try{
            if(f.delete()){
                Log.i(TAG,"File "+f.getName()+" deleted");
            }else{
                Log.i(TAG,"Delete operation failed.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public byte[] fileToBytes(File f) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int read;
            byte[] buff = new byte[1024];
            while ((read = in.read(buff)) > 0)
            {
                out.write(buff, 0, read);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }
}