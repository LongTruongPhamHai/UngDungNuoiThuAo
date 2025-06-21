package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import model.Pet;
import model.User;
import repository.ActivityLogRepository;
import repository.PetRepository;
import repository.UserRepository;
import repository.callback.activitylog.AddActLogCallback;
import repository.callback.pet.PetLoadedCallback;
import repository.callback.pet.UpdatePetCallback;
import repository.callback.user.UpdateUserCallback;
import repository.callback.user.UserLoadedCallback;

public class StudyActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_study);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button englishBtn, testBtn, freeBtn, backBtn;
        englishBtn = findViewById(R.id.english_btn);
        testBtn = findViewById(R.id.test_btn);
        freeBtn = findViewById(R.id.free_btn);
        backBtn = findViewById(R.id.back_btn);

        englishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent tới Acitivity học tiếng Anh

                // Test tăng chỉ số (Để tạm)
                UserRepository userRepository = new UserRepository();
                PetRepository petRepository = new PetRepository();
                ActivityLogRepository activityLogRepository = new ActivityLogRepository();

                String type = "Tiếng Anh";
                int score = 0, duration = 0, distance = 0, step = 0;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String addDate = dateFormat.format(new Date());

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String addTime = timeFormat.format(new Date());

                SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String datetime = fullFormat.format(new Date());


                userRepository.getUser(userId, new UserLoadedCallback() {
                    @Override
                    public void onUserLoaded(User nUser) {
                        userRepository.trainingUser(nUser, type, score);
                        userRepository.updateUserStat(userId, nUser, new UpdateUserCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("StudyAct", "Update user stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("StudyAct", "Update user stat failed!");
                            }

                            @Override
                            public void onIncorrectPw() {
                                Log.d("StudyAct", "Update user stat failed!");
                            }

                            @Override
                            public void onUsernameTaken() {
                                Log.d("StudyAct", "Update user stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("StudyAct", "Load user failed!");
                    }
                });

                petRepository.getPet(userId, new PetLoadedCallback() {
                    @Override
                    public void onPetLoaded(Pet nPet) {
                        petRepository.trainingPet(nPet, type, score);
                        petRepository.updatePetStat(userId, nPet, new UpdatePetCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("StudyAct", "Update pet stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("StudyAct", "Update pet stat failed!");
                            }

                            @Override
                            public void onIncorrectPassword() {
                                Log.d("StudyAct", "Update pet stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception errorMessage) {
                        Log.d("StudyAct", "Load pet failed!");
                    }
                });

                activityLogRepository.addActlog(userId, datetime, addDate, addTime, type, duration, distance, step, score, new AddActLogCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("StudyAct", "Add act log success!");
                        Toast.makeText(StudyActivity.this, "Thêm dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(StudyActivity.this, "Thêm dữ liệu thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent tới Acitivity kiểm tra

                // Test tăng chỉ số (Để tạm)
                UserRepository userRepository = new UserRepository();
                PetRepository petRepository = new PetRepository();
                ActivityLogRepository activityLogRepository = new ActivityLogRepository();

                String type = "Kiểm tra";
                int score = (int) (Math.random() * 11), duration = 0, distance = 0, step = 0;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String addDate = dateFormat.format(new Date());

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String addTime = timeFormat.format(new Date());

                SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String datetime = fullFormat.format(new Date());

                userRepository.getUser(userId, new UserLoadedCallback() {
                    @Override
                    public void onUserLoaded(User nUser) {
                        userRepository.trainingUser(nUser, type, score);
                        userRepository.updateUserStat(userId, nUser, new UpdateUserCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("StudyAct", "Update user stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("StudyAct", "Update user stat failed!");
                            }

                            @Override
                            public void onIncorrectPw() {
                                Log.d("StudyAct", "Update user stat failed!");
                            }

                            @Override
                            public void onUsernameTaken() {
                                Log.d("StudyAct", "Update user stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("StudyAct", "Load user failed!");
                    }
                });

                petRepository.getPet(userId, new PetLoadedCallback() {
                    @Override
                    public void onPetLoaded(Pet nPet) {
                        petRepository.trainingPet(nPet, type, score);
                        petRepository.updatePetStat(userId, nPet, new UpdatePetCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("StudyAct", "Update pet stat success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("StudyAct", "Update pet stat failed!");
                            }

                            @Override
                            public void onIncorrectPassword() {
                                Log.d("StudyAct", "Update pet stat failed!");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception errorMessage) {
                        Log.d("StudyAct", "Load pet failed!");
                    }
                });

                activityLogRepository.addActlog(userId, datetime, addDate, addTime, type, duration, distance, step, score, new AddActLogCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("StudyAct", "Add act log success!");
                        Toast.makeText(StudyActivity.this, "Thêm dữ liệu thành công!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(StudyActivity.this, "Thêm dữ liệu thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        freeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSelfStudy = new Intent(StudyActivity.this, SelfStudyActivity.class);
                toSelfStudy.putExtra("userId", userId);
                startActivity(toSelfStudy);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}