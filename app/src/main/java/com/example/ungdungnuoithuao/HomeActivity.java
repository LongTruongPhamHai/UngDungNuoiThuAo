package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import model.Pet;
import model.User;
import repository.PetRepository;
import repository.UserRepository;
import repository.callback.pet.PetLoadedCallback;
import repository.callback.user.UserLoadedCallback;

public class HomeActivity extends AppCompatActivity {
    private TextView usernameTv, levelTv, petnameTv, statusTv;
    private ProgressBar levelBar, overallBar;
    private LinearLayout loadingLl;
    String userId;
    private UserRepository userRepository;
    private PetRepository petRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button studyBtn, sportBtn, gameBtn, guideBtn, leaderboardBtn, summaryBtn, settingBtn;
        LinearLayout userInfoLl, petInfoLl;

        studyBtn = findViewById(R.id.study_btn);
        sportBtn = findViewById(R.id.sport_btn);
        gameBtn = findViewById(R.id.game_btn);
        guideBtn = findViewById(R.id.guide_btn);
        leaderboardBtn = findViewById(R.id.leaderboard_btn);
        summaryBtn = findViewById(R.id.summary_btn);
        settingBtn = findViewById(R.id.setting_btn);

        usernameTv = findViewById(R.id.username_tv);
        levelTv = findViewById(R.id.level_tv);
        petnameTv = findViewById(R.id.petname_tv);
        statusTv = findViewById(R.id.status_tv);

        levelBar = findViewById(R.id.level_bar);
        overallBar = findViewById(R.id.overall_bar);

        userInfoLl = findViewById(R.id.user_info_ll);
        petInfoLl = findViewById(R.id.pet_info_ll);
        loadingLl = findViewById(R.id.loading_ll);

        userRepository = new UserRepository();
        petRepository = new PetRepository();

        userInfoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUser = new Intent(HomeActivity.this, UserActivity.class);
                toUser.putExtra("userId", userId);
                startActivity(toUser);
            }
        });

        petInfoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPet = new Intent(HomeActivity.this, PetActivity.class);
                toPet.putExtra("userId", userId);
                startActivity(toPet);
            }
        });

        studyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toStudy = new Intent(HomeActivity.this, StudyActivity.class);
                toStudy.putExtra("userId", userId);
                startActivity(toStudy);
            }
        });

        sportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSport = new Intent(HomeActivity.this, SportActivity.class);
                toSport.putExtra("userId", userId);
                startActivity(toSport);
            }
        });

//        gameBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        guideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toGuide = new Intent(HomeActivity.this, GuideActivity.class);
                startActivity(toGuide);
            }
        });

        leaderboardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLeaderboard = new Intent(HomeActivity.this, LeaderboardActivity.class);
                toLeaderboard.putExtra("userId", userId);
                startActivity(toLeaderboard);
            }
        });

        summaryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSummary = new Intent(HomeActivity.this, SummaryActivity.class);
                toSummary.putExtra("userId", userId);
                startActivity(toSummary);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSetting = new Intent(HomeActivity.this, SettingActivity.class);
                toSetting.putExtra("userId", userId);
                startActivity(toSetting);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingLl.setVisibility(View.VISIBLE);
        loadUserData(userId);
        loadPetData(userId);
    }

    private void loadUserData(String userId) {
        userRepository.getUser(userId, new UserLoadedCallback() {
            @Override
            public void onUserLoaded(User nUser) {
                nUser.gainLevel();
                usernameTv.setText(nUser.getUsername());
                levelTv.setText("Level " + nUser.getLevel());
                levelBar.setProgress(nUser.getExp());
                loadingLl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Load user error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPetData(String userId) {
        petRepository.getPet(userId, new PetLoadedCallback() {
            @Override
            public void onPetLoaded(Pet nPet) {
                if (nPet != null) {
                    petnameTv.setText(nPet.getPetname());
                    statusTv.setText(nPet.getOverallStatus());
                    overallBar.setProgress(nPet.getOverallscore());
                    loadingLl.setVisibility(View.GONE);
                } else {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Pet data is unavailable!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception errorMessage) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Pet error: " + errorMessage.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}