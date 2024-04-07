package com.aminogira.soundinputtest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
public class MainActivity extends AppCompatActivity {

    private TextView messageTextView;
    private MediaRecorder mediaRecorder;
    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageTextView = findViewById(R.id.messageTextView);

        // Check for microphone permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        } else {
            startListening();
        }
    }

    private void startListening() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile("/dev/null");
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isListening = true;

            // Start countdown timer
            new CountDownTimer(60000, 1000) {
                public void onTick(long millisUntilFinished) {
                    messageTextView.setText("Hi");
                }

                public void onFinish() {
                    messageTextView.setText("");
                    stopListening();
                }
            }.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopListening() {
        if (mediaRecorder != null && isListening) {
            mediaRecorder.stop();
            mediaRecorder.release();
            isListening = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopListening();
    }
}