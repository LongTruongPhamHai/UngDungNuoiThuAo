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

import model.Pet;
import model.SportLog;
import model.StudyLog;
import model.User;
import repository.PetRepository;
import repository.SportLogRepository;
import repository.StudyLogRepository;
import repository.UserRepository;
import repository.callback.pet.PetLoadedCallback;
import repository.callback.pet.UpdatePetCallback;
import repository.callback.sportlog.GetSpLogCallback;
import repository.callback.studylog.AddStLogCallback;
import repository.callback.studylog.GetStLogCallback;
import repository.callback.user.UpdateUserCallback;
import repository.callback.user.UserLoadedCallback;

public class SelfStudyActivity extends AppCompatActivity {
    private String userId, type = "Tự học";
    private TextView timerTv;
    private boolean timerStarted = true;
    private Timer timer;
    private TimerTask timerTask;
    private int time = 0;

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
        StudyLogRepository studyLogRepository = new StudyLogRepository();

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
                int durationValue = time, score = 0;

                messageTv.setText("Mỗi phút học là một bước tiến gần hơn đến ước mơ! Bạn đang đi đúng hướng!");

                userRepository.getUser(userId, new UserLoadedCallback() {
                    @Override
                    public void onUserLoaded(User nUser) {
                        userRepository.trainingUser(nUser, type, score);
                        userRepository.updateUserStat(userId, nUser, new UpdateUserCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("SelfStudyAct", "Update user stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("SelfStudyAct", "Update user stat failed!");
                            }

                            @Override
                            public void onIncorrectPw() {
                                Log.d("SelfStudyAct", "Update user stat failed!");
                            }

                            @Override
                            public void onUsernameTaken() {
                                Log.d("SelfStudyAct", "Update user stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("SelfStudyAct", "Load user failed!");
                    }
                });

                petRepository.getPet(userId, new PetLoadedCallback() {
                    @Override
                    public void onPetLoaded(Pet nPet) {
                        petRepository.trainingPet(nPet, type, score);
                        petRepository.updatePetStat(userId, nPet, new UpdatePetCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("SelfStudyAct", "Update pet stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("SelfStudyAct", "Update pet stat failed!");
                            }

                            @Override
                            public void onIncorrectPassword() {
                                Log.d("SelfStudyAct", "Update pet stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception errorMessage) {
                        Log.d("SelfStudyAct", "Load pet failed!");
                    }
                });

                studyLogRepository.addStLog(userId, addDate, addTime, type, durationValue, score, new AddStLogCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("SelfStudyAct", "Add sp log success!");
                        studyLogRepository.getStLog(userId, addDate, addTime, new GetStLogCallback() {
                            @Override
                            public void onSuccess(StudyLog stLog) {
                                Log.d("SelfStudyAct", "Get st log success");
                                timeTv.setText(stLog.getTime() + "\n" + stLog.getDate());
                                timerRsTv.setText(duration);
                                loadingLl.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                loadingLl.setVisibility(View.GONE);
                                Log.d("SelfStudyAct", "Get sp log failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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