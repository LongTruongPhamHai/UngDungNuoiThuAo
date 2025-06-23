package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MapActivity extends AppCompatActivity {
    private String receivedUserId; // Biến để lưu userId nhận được

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FrameLayout btnA1 = findViewById(R.id.btnAi1);
        FrameLayout btnA2 = findViewById(R.id.btnAi2);
        FrameLayout btnA3 = findViewById(R.id.btnAi3);
        FrameLayout btnA4 = findViewById(R.id.btnAi4);

        btnA1.setOnClickListener(v -> openMainActivityWithMonsterId(1));
        btnA2.setOnClickListener(v -> openMainActivityWithMonsterId(2));
        btnA3.setOnClickListener(v -> openMainActivityWithMonsterId(3));
        btnA4.setOnClickListener(v -> openMainActivityWithMonsterId(4));

        receivedUserId = getIntent().getStringExtra("userId");

        if (receivedUserId == null || receivedUserId.isEmpty()) {
            Log.e("MapActivity", "Error: userId not received from MainActivity2!");
            // Xử lý lỗi hoặc quay lại Activity trước nếu cần
        } else {
            Log.d("MapActivity", "Received userId from MainActivity2: " + receivedUserId); // KIỂM TRA ĐIỂM NÀY
        }
    }


    private void openMainActivityWithMonsterId(int i) {
        Intent toFightActivity = new Intent(MapActivity.this, FightActivity.class);
        toFightActivity.putExtra("userId", receivedUserId); // TRUYỀN userId TẠI ĐÂY
        Log.d("MapActivity", "Passing userId to FightActivity: " + receivedUserId); // KIỂM TRA ĐIỂM NÀY
        startActivity(toFightActivity);
    }
}