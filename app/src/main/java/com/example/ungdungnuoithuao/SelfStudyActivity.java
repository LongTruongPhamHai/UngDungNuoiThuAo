package com.example.ungdungnuoithuao;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import repository.PetRepository;
import repository.SportLogRepository;
import repository.UserRepository;

public class SelfStudyActivity extends AppCompatActivity {
    private String userId, type;
    private TextView timerTv;
    private boolean timerStarted = true;
    private Timer timer;
    private TimerTask timerTask;
    private int time = 0, score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_self_study);

        userId = getIntent().getStringExtra("userId");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout actOnLl, actEndLl, buttonLl, loadingLl;
        TextView headerTv, messageTv, timeTv, timerRsTv;
        Button startBtn, resumeBtn, pauseBtn, endBtn, backBtn;

        actOnLl = findViewById(R.id.act_on_ll);
        actEndLl = findViewById(R.id.act_end_ll);
        buttonLl = findViewById(R.id.button_ll);
        loadingLl = findViewById(R.id.loading_ll);

        headerTv = findViewById(R.id.header_tv);
        timerTv = findViewById(R.id.timer_tv);
        messageTv = findViewById(R.id.message_tv);
        timeTv = findViewById(R.id.time_tv);
        timerRsTv = findViewById(R.id.timer_rs_tv);

        startBtn = findViewById(R.id.start_btn);
        resumeBtn = findViewById(R.id.resume_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        endBtn = findViewById(R.id.end_btn);
        backBtn = findViewById(R.id.back_btn);

        UserRepository userRepository = new UserRepository();
        PetRepository petRepository = new PetRepository();

        timer = new Timer();

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headerTv.setText(type);
                pauseBtn.setVisibility(View.VISIBLE);
                startBtn.setVisibility(View.GONE);
                backBtn.setVisibility(View.GONE);

                timerStarted = true;
                startTimer();
            }
        });

        resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseBtn.setVisibility(View.VISIBLE);
                resumeBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.GONE);
                timerStarted = true;
                startTimer();
            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeBtn.setVisibility(View.VISIBLE);
                endBtn.setVisibility(View.VISIBLE);
                pauseBtn.setVisibility(View.GONE);
                timerStarted = false;
                timerTask.cancel();
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actOnLl.setVisibility(View.GONE);
                actEndLl.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                resumeBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.GONE);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String addDate = dateFormat.format(new Date());

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String addTime = timeFormat.format(new Date());

                String duration = getTimerText();
                int durationValue = time;

                messageTv.setText("Mỗi phút học là một bước tiến gần hơn đến ước mơ! Bạn đang đi đúng hướng!");

                timeTv.setText(addDate + "\n" + addTime);
                timerRsTv.setText(duration);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Đã cấp quyền truy cập!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền truy cập bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        time++;
                        timerTv.setText(getTimerText());
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
    }

    private String getTimerText() {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60,
                minutes = ((rounded % 86400) % 3600) / 60,
                hours = (rounded % 86400) / 3600;

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}