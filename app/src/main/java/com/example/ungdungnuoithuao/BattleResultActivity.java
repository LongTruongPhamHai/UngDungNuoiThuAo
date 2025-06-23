package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import repository.ActivityLogRepository;
import repository.callback.activitylog.AddActLogCallback;

public class BattleResultActivity extends AppCompatActivity {
    private String userId;
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

        userId = getIntent().getStringExtra("userId");

        ActivityLogRepository activityLogRepository = new ActivityLogRepository();

        tvResult = findViewById(R.id.tv_result);
        ivResultImage = findViewById(R.id.iv_result_image);
        tvRewardExp = findViewById(R.id.tv_reward_exp);
        tvRewardCoin = findViewById(R.id.tv_reward_coin);
        btnBackHome = findViewById(R.id.btn_back_home);


        int isWin = getIntent().getIntExtra("isWin", 0);


        int score = 0, duration = 0, distance = 0, step = 0;
        if (isWin == 1) {
            tvResult.setText("Chiến thắng!");
            ivResultImage.setImageResource(R.drawable.ic_win);

            Random random = new Random();
            int expReward = random.nextInt(41) + 40; // Số ngẫu nhiên từ 40 đến 80
            int coinReward = random.nextInt(31) + 20; // Số ngẫu nhiên từ 20 đến 50

            tvRewardExp.setText("+" + expReward + " Kinh nghiệm");
            tvRewardCoin.setText("+" + coinReward + " Xu");

            score = 10;
        } else {
            tvResult.setText("Thất bại!");
            ivResultImage.setImageResource(R.drawable.ic_lose);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String addDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String addTime = timeFormat.format(new Date());

        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String datetime = fullFormat.format(new Date());

        activityLogRepository.addActlog(userId, datetime, addDate, addTime, "Giải trí", duration, distance, step, score, new AddActLogCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(BattleResultActivity.this, "Thêm dữ liệu thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(BattleResultActivity.this, "Thêm dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String userId = getIntent().getStringExtra("userId");
                Intent intent = new Intent(BattleResultActivity.this, GameActivity.class);
//                intent.putExtra("userId", userId);
                startActivity(intent);
                finish(); // Tạm thời đóng activity này
            }
        });
    }
}