package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button userBtn, petBtn, aboutBtn, logoutBtn, backBtn;

        userBtn = findViewById(R.id.user_btn);
        petBtn = findViewById(R.id.pet_btn);
        aboutBtn = findViewById(R.id.about_btn);
        logoutBtn = findViewById(R.id.logout_btn);
        backBtn = findViewById(R.id.back_btn);
        
        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toUser = new Intent(SettingActivity.this, UserActivity.class);
                toUser.putExtra("userId", userId);
                startActivity(toUser);
            }
        });
        
        petBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPet = new Intent(SettingActivity.this, PetActivity.class);
                toPet.putExtra("userId", userId);
                startActivity(toPet);
            }
        });

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMain = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(toMain);
                finishAffinity();
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