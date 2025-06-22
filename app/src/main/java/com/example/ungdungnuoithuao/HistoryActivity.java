package com.example.ungdungnuoithuao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import adapter.ActivityLogAdapter;
import model.ActivityLog;
import repository.ActivityLogRepository;
import repository.UserRepository;
import repository.callback.activitylog.GetListActLogCallback;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView actLogRv;
    private String userId;
    private ActivityLogRepository activityLogRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        
        userId = getIntent().getStringExtra("userId");
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button backBtn = findViewById(R.id.back_btn);
        LinearLayout loadingLl = findViewById(R.id.loading_ll);
        
        actLogRv = findViewById(R.id.act_log_rv);
        actLogRv.setLayoutManager(new LinearLayoutManager(this));

        activityLogRepository = new ActivityLogRepository();

        loadingLl.setVisibility(View.VISIBLE);
        activityLogRepository.getAllActlog(userId, new GetListActLogCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> actLogList) {
                ActivityLogAdapter adapter = new ActivityLogAdapter(actLogList);
                actLogRv.setAdapter(adapter);
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(HistoryActivity.this, "Tải dữ liệu thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(HistoryActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
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