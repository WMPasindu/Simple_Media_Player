package com.application.common.alliontechnologies.androidlivestreamingapplication;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView mainVideoView;
    private ImageButton playBtn;
    private TextView currentTime;
    private TextView durationTime;
    private ProgressBar currentProgress;
    private ProgressBar bufferProgress;

    private boolean isPlaying;


    private Uri videoUri;

    private int current = 0;
    private int duration = 0;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPlaying = false;

        mainVideoView = (VideoView) findViewById(R.id.mainVideoView);
        playBtn = (ImageButton) findViewById(R.id.playBtn);
        currentTime = (TextView) findViewById(R.id.currentTime);
        durationTime = (TextView) findViewById(R.id.durationTime);
        currentProgress = (ProgressBar) findViewById(R.id.currentProgress);
        bufferProgress = (ProgressBar) findViewById(R.id.bufferProgress);

        currentProgress.setMax(100);

        String uriPath = "https://firebasestorage.googleapis.com/v0/b/fir-videoapp.appspot.com/o/sample_video.mp4?alt=media&token=fcc1351c-8355-42b3-bdfe-2da7959a8042";
        videoUri = Uri.parse(uriPath);

        mainVideoView.setVideoURI(videoUri);
        mainVideoView.requestFocus();

        mainVideoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                if(what == mp.MEDIA_INFO_BUFFERING_START){

                    bufferProgress.setVisibility(View.VISIBLE);

                }else if(what == mp.MEDIA_INFO_BUFFERING_END){

                    bufferProgress.setVisibility(View.INVISIBLE);

                }

                return false;
            }
        });

        mainVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                duration = mp.getDuration()/1000;
                String durationString = String.format("%02d:%02d", duration / 60, duration % 60);
                durationTime.setText(durationString);

            }
        });

        mainVideoView.start();
        isPlaying = true;
        playBtn.setImageResource(R.drawable.ic_pause_black_24dp);

        new VideProgress().execute();

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying){

                    mainVideoView.pause();
                    isPlaying = false;
                    playBtn.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                }else {

                    mainVideoView.start();
                    isPlaying = true;
                    playBtn.setImageResource(R.drawable.ic_pause_black_24dp);

                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        isPlaying = false;
    }

    public class VideProgress extends AsyncTask<Void,Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            do {

                if(isPlaying) {

                    current = mainVideoView.getCurrentPosition() / 1000;

                    try {

//                        int currentProgress = current * 100 / duration;
                        publishProgress(current);

                    } catch (Exception e) {


                    }
                }

            }while (currentProgress.getProgress() <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            try {

                int currentPercent = values[0] * 100/duration;
                currentProgress.setProgress(currentPercent);

                String currentString = String.format("%02d:%02d", values[0] / 60, values[0] % 60);
                currentTime.setText(currentString);

            }catch (Exception e){

            }
        }
    }
}
