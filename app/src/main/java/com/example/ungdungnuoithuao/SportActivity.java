package com.example.ungdungnuoithuao;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import model.Pet;
import model.SportLog;
import model.User;
import repository.ChallengeRepository;
import repository.DayChallengeRepository;
import repository.PetRepository;
import repository.SportLogRepository;
import repository.UserChallengeRepository;
import repository.UserRepository;
import repository.callback.pet.PetLoadedCallback;
import repository.callback.pet.UpdatePetCallback;
import repository.callback.sportlog.AddSpLogCallback;
import repository.callback.sportlog.GetSpLogCallback;
import repository.callback.user.UpdateUserCallback;
import repository.callback.user.UserLoadedCallback;

public class SportActivity extends AppCompatActivity implements SensorEventListener {
    private String userId, type;
    private TextView timerTv, distanceTv, stepTv;

    private boolean timerStarted = true;
    private Timer timer;
    private TimerTask timerTask;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false, isCounting = false;

    private boolean isTracking = false, isPaused = false;
    private float totalDistance = 0f;
    private Location lastLocation = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private int time = 0, distance = 0, step = 0, score, displayedSteps = 0, initialStep = -1;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sport);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        LinearLayout selectLl, startLl,
                actOnLl, distanceLL, stepLl,
                distanceRsLl, stepRsLl,
                buttonLl, loadingLl;
        ScrollView actEndLl;
        TextView headerTv, messageTv, timeTv, timerRsTv, distanceRsTv, stepRsTv;
        Button startBtn, resumeBtn, pauseBtn, endBtn, backBtn, back2Btn;
        Spinner typeSp;

        selectLl = findViewById(R.id.select_ll);
        startLl = findViewById(R.id.start_ll);
        actOnLl = findViewById(R.id.act_on_ll);
        distanceLL = findViewById(R.id.distance_ll);
        stepLl = findViewById(R.id.step_ll);
        actEndLl = findViewById(R.id.act_end_ll);
        distanceRsLl = findViewById(R.id.distance_rs_ll);
        stepRsLl = findViewById(R.id.step_rs_ll);
        buttonLl = findViewById(R.id.button_ll);
        loadingLl = findViewById(R.id.loading_ll);

        headerTv = findViewById(R.id.header_tv);
        timerTv = findViewById(R.id.timer_tv);
        distanceTv = findViewById(R.id.distance_tv);
        stepTv = findViewById(R.id.step_tv);
        messageTv = findViewById(R.id.message_tv);
        timeTv = findViewById(R.id.time_tv);
        timerRsTv = findViewById(R.id.timer_rs_tv);
        distanceRsTv = findViewById(R.id.distance_rs_tv);
        stepRsTv = findViewById(R.id.step_rs_tv);

        startBtn = findViewById(R.id.start_btn);
        resumeBtn = findViewById(R.id.resume_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        endBtn = findViewById(R.id.end_btn);
        backBtn = findViewById(R.id.back_btn);
        back2Btn = findViewById(R.id.back2_btn);

        typeSp = findViewById(R.id.type_sp);

        List<String> sportType = Arrays.asList("Lựa chọn môn thể thao", "Chạy bộ", "Đạp xe", "Yoga");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, sportType);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        typeSp.setAdapter(adapter);

        typeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = typeSp.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        UserRepository userRepository = new UserRepository();
        PetRepository petRepository = new PetRepository();
        SportLogRepository sportLogRepository = new SportLogRepository();

        timer = new Timer();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACTIVITY_RECOGNITION}, 1001);
            }
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        } else {
            Log.d("SpAct", "Step sensor not available!");
            Toast.makeText(this, "Cảm biến không khả dụng! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (isTracking && !isPaused) {
                    for (Location location : locationResult.getLocations()) {
                        if (lastLocation != null) {
                            float trackDistance = lastLocation.distanceTo(location);
                            totalDistance += trackDistance;
                            distanceTv.setText(String.valueOf(Math.round(totalDistance)) + " m");
                        }
                        lastLocation = location;
                    }
                }
            }
        };

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("Lựa chọn môn thể thao")) {
                    Toast.makeText(SportActivity.this, "Vui lòng chọn môn thể thao!", Toast.LENGTH_SHORT).show();
                } else {
                    headerTv.setText(type);
                    pauseBtn.setVisibility(View.VISIBLE);
                    startBtn.setVisibility(View.GONE);
                    backBtn.setVisibility(View.GONE);
                    selectLl.setVisibility(View.GONE);
                    startLl.setVisibility(View.VISIBLE);

                    switch (type) {
                        case "Chạy bộ":
                            distanceLL.setVisibility(View.VISIBLE);
                            stepLl.setVisibility(View.VISIBLE);
                            break;

                        case "Đạp xe":
                            distanceLL.setVisibility(View.VISIBLE);
                            stepLl.setVisibility(View.GONE);
                            break;

                        case "Yoga":
                            distanceLL.setVisibility(View.GONE);
                            stepLl.setVisibility(View.GONE);
                            break;
                    }
                    timerStarted = true;
                    startTimer();
                    startCountStep();
                    if (!isTracking) {
                        isTracking = true;
                        isPaused = false;
                        totalDistance = 0f;
                        lastLocation = null;
                        startLocationUpdates();
                    }
                }
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
                startCountStep();
                if (isTracking && isPaused) {
                    isPaused = false;
                }
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
                if (isCounting) {
                    sensorManager.unregisterListener(SportActivity.this);
                    isCounting = false;
                }
                if (isTracking) {
                    isPaused = true;
                }
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actOnLl.setVisibility(View.GONE);
                actEndLl.setVisibility(View.VISIBLE);
                resumeBtn.setVisibility(View.GONE);
                endBtn.setVisibility(View.GONE);
                back2Btn.setVisibility(View.VISIBLE);

                if (isTracking) {
                    isTracking = false;
                    isPaused = false;
                    stopLocationUpdates();
                    lastLocation = null;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String addDate = dateFormat.format(new Date());

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String addTime = timeFormat.format(new Date());

                String duration = getTimerText();
                int durationValue = time;
                distance = Math.round(totalDistance);
                step = displayedSteps;

                switch (type) {
                    case "Chạy bộ":
                        score = step;
                        messageTv.setText("Bạn đã bứt phá giới hạn!\nHãy giữ vững phong độ nhé!");
                        distanceRsLl.setVisibility(View.VISIBLE);
                        stepRsLl.setVisibility(View.VISIBLE);
                        break;

                    case "Đạp xe":
                        score = distance;
                        step = 0;
                        messageTv.setText("Đạp hết ga – khỏe hết mình!\nTuyệt vời lắm!");
                        distanceRsLl.setVisibility(View.VISIBLE);
                        stepRsLl.setVisibility(View.GONE);
                        break;

                    case "Yoga":
                        score = durationValue;
                        distance = 0;
                        step = 0;
                        messageTv.setText(" Bình yên đến từ bên trong!\nBạn đã làm rất tốt!");
                        distanceRsLl.setVisibility(View.GONE);
                        stepRsLl.setVisibility(View.GONE);
                        break;
                }

                userRepository.getUser(userId, new UserLoadedCallback() {
                    @Override
                    public void onUserLoaded(User nUser) {
                        userRepository.trainingUser(nUser, type, score);
                        userRepository.updateUserStat(userId, nUser, new UpdateUserCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("SportAct", "Update user stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("SportAct", "Update user stat failed!");
                            }

                            @Override
                            public void onIncorrectPw() {
                                Log.d("SportAct", "Update user stat failed!");
                            }

                            @Override
                            public void onUsernameTaken() {
                                Log.d("SportAct", "Update user stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("SportAct", "Load user failed!");
                    }
                });

                petRepository.getPet(userId, new PetLoadedCallback() {
                    @Override
                    public void onPetLoaded(Pet nPet) {
                        petRepository.trainingPet(nPet, type, score);
                        petRepository.updatePetStat(userId, nPet, new UpdatePetCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("SportAct", "Update pet stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("SportAct", "Update pet stat failed!");
                            }

                            @Override
                            public void onIncorrectPassword() {
                                Log.d("SportAct", "Update pet stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception errorMessage) {
                        Log.d("SportAct", "Load pet failed!");
                    }
                });

                sportLogRepository.addSpLog(userId, addDate, addTime, type, durationValue, distance, step, new AddSpLogCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("SportAct", "Add sp log success!");
                        sportLogRepository.getSpLog(userId, addDate, addTime, new GetSpLogCallback() {
                            @Override
                            public void onSuccess(SportLog spLog) {
                                Log.d("SportAct", "Get sp log success");
                                timeTv.setText(spLog.getTime() + "\n" + spLog.getDate());
                                timerRsTv.setText(duration);
                                distanceRsTv.setText(String.valueOf(spLog.getDistance()) + " m");
                                stepRsTv.setText(String.valueOf(spLog.getStep()));
                                loadingLl.setVisibility(View.GONE);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                loadingLl.setVisibility(View.GONE);
                                Log.d("SportAct", "Get sp log failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("SportAct", "Add sp log failed! " + e.toString());
                    }
                });
            }
        });

        back2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back2Btn.setVisibility(View.GONE);
                startLl.setVisibility(View.GONE);
                selectLl.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
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
    protected void onPause() {
        super.onPause();
        if (isCounting) {
            sensorManager.unregisterListener(this);
            isCounting = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int totalSteps = (int) event.values[0];

        if (initialStep == -1) {
            initialStep = totalSteps;
        }

        displayedSteps = totalSteps - initialStep;
        stepTv.setText(String.valueOf(displayedSteps));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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

    private void startCountStep() {
        if (!isCounting && isSensorPresent) {
            sensorManager.registerListener(SportActivity.this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isCounting = true;
            Toast.makeText(SportActivity.this, "Bắt đầu hoạt động!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000L) // 5s
                .setMinUpdateIntervalMillis(2000L)
                .build();

        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}