package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class BattleResultActivity extends AppCompatActivity {

    private TextView tvResult;
    private ImageView ivResultImage;
    private TextView tvRewardExp;
    private TextView tvRewardCoin;
    private TextView tvRewardItem;
    private LinearLayout layoutRewardItem;
    private Button btnBackHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_result);

        tvResult = findViewById(R.id.tv_result);
        ivResultImage = findViewById(R.id.iv_result_image);
        tvRewardExp = findViewById(R.id.tv_reward_exp);
        tvRewardCoin = findViewById(R.id.tv_reward_coin);
        btnBackHome = findViewById(R.id.btn_back_home);


        int isWin = getIntent().getIntExtra("isWin", 0);
        Random random = new Random();
        int expReward = random.nextInt(41) + 40; // Số ngẫu nhiên từ 40 đến 80
        int coinReward = random.nextInt(31) + 20; // Số ngẫu nhiên từ 20 đến 50

        tvRewardExp.setText("+" + expReward + " XP");
        tvRewardCoin.setText("+" + coinReward + " Coins");

        if (isWin == 1) {
            tvResult.setText("Chiến thắng!");
            ivResultImage.setImageResource(R.drawable.ic_win);
        } else {
            tvResult.setText("Thất bại!");
            ivResultImage.setImageResource(R.drawable.ic_lose);

        }

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = getIntent().getStringExtra("userId");
                Intent intent = new Intent(BattleResultActivity.this, GameActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish(); // Tạm thời đóng activity này
            }
        });
    }
}