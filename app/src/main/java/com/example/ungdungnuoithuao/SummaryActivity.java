package com.example.ungdungnuoithuao;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import repository.ActivityLogRepository;
import repository.callback.activitylog.GetDayActLogCallback;

public class SummaryActivity extends AppCompatActivity {
    private RadioButton dayRb, monthRb, yearRb;
    private TableLayout dayStSumTl, daySpSumTl, dayGSumTl,
            mYStSumTl, mYSpSumTl, mYGSumTl;
    private LinearLayout daySelectLl, monthSelectLl, yearSelectLl,
            studyLl, sportLl, gameLl, loadingLl;

    private TableRow avgDayScoreTvTr, avgDayScoreTr, stepTvTr, sumDayStepTr, distanceTvTr, sumDayDistanceTr,
            avgMYScoreTvTr, avgMYScoreTr, mYStepTvTr, mYDistanceTvTr, mYStepTr, mYDistanceTr;
    private ImageButton backDayBtn, nextDayBtn, backMonthBtn, nextMonthBtn, backYearBtn, nextYearBtn;
    private Button backBtn;
    private TextView dayTv, monthTv, yearTv, stTypeTv, spTypeTv,
            sumStDayDurationTv, sumDayLessonTv, avgDayScoreTv,
            sumStMYDurationTv, sumMYLessonTv, avgStMYDurationTv, avgMYLessonTv, avgMYScoreTv,
            sumSpDayDurationTv, sumDayStepTv, sumDayDistanceTv,
            sumSpMYDurationTv, sumMYStepTv, sumMYDistanceTv,
            avgSpMYDurationTv, avgMYStepTv, avgMYDistanceTv,
            sumDayWinTv, sumDayLostTv, sumMYWinTv, sumMYLostTv, avgMYWinTv, avgMYLostTv;

    private String userId, stType, spType;
    private List<String> listStType = Arrays.asList("Tiếng Anh", "Kiểm tra", "Tự học"),
            listSpType = Arrays.asList("Chạy bộ", "Đạp xe", "Yoga");
    private Date currentDate;
    private int stTypeIndex = 0, spTypeIndex = 0;
    private ActivityLogRepository activityLogRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dayRb = findViewById(R.id.day_rb);
        monthRb = findViewById(R.id.month_rb);
        yearRb = findViewById(R.id.year_rb);

        daySelectLl = findViewById(R.id.day_select_ll);
        monthSelectLl = findViewById(R.id.month_select_ll);
        yearSelectLl = findViewById(R.id.year_select_ll);

        studyLl = findViewById(R.id.study_ll);
        sportLl = findViewById(R.id.sport_ll);
        gameLl = findViewById(R.id.game_ll);

        loadingLl = findViewById(R.id.loading_ll);

        dayStSumTl = findViewById(R.id.day_st_summary_tl);
        daySpSumTl = findViewById(R.id.day_sp_summary_tl);
        dayGSumTl = findViewById(R.id.day_g_summary_tl);

        mYStSumTl = findViewById(R.id.m_y_st_summary_tl);
        mYSpSumTl = findViewById(R.id.m_y_sp_summary_tl);
        mYGSumTl = findViewById(R.id.m_y_g_summary_tl);

        avgDayScoreTvTr = findViewById(R.id.avg_day_score_tv_tr);
        avgDayScoreTr = findViewById(R.id.avg_day_score_tr);

        stepTvTr = findViewById(R.id.step_tv_tr);
        sumDayStepTr = findViewById(R.id.sum_day_step_tr);

        distanceTvTr = findViewById(R.id.distance_tv_tr);
        sumDayDistanceTr = findViewById(R.id.sum_day_distance_tr);

        avgMYScoreTvTr = findViewById(R.id.avg_m_y_score_tv_tr);
        avgMYScoreTr = findViewById(R.id.avg_m_y_score_tr);

        mYStepTvTr = findViewById(R.id.sum_m_y_step_tv_tr);
        mYStepTr = findViewById(R.id.sum_m_y_step_tr);

        mYDistanceTvTr = findViewById(R.id.sum_m_y_distance_tv_tr);
        mYDistanceTr = findViewById(R.id.sum_m_y_distance_tr);

        backDayBtn = findViewById(R.id.back_day_btn);
        nextDayBtn = findViewById(R.id.next_day_btn);

        backMonthBtn = findViewById(R.id.back_month_btn);
        nextMonthBtn = findViewById(R.id.next_month_btn);

        backYearBtn = findViewById(R.id.back_year_btn);
        nextYearBtn = findViewById(R.id.next_year_btn);

        backBtn = findViewById(R.id.back_btn);

        dayTv = findViewById(R.id.day_tv);
        monthTv = findViewById(R.id.month_tv);
        yearTv = findViewById(R.id.year_tv);

        stTypeTv = findViewById(R.id.st_type_tv);
        spTypeTv = findViewById(R.id.sp_type_tv);

        sumStDayDurationTv = findViewById(R.id.sum_st_day_duration_tv);
        sumDayLessonTv = findViewById(R.id.sum_day_lesson_tv);
        avgDayScoreTv = findViewById(R.id.avg_day_score_tv);

        sumStMYDurationTv = findViewById(R.id.sum_st_m_y_duration_tv);
        sumMYLessonTv = findViewById(R.id.sum_m_y_lesson_tv);
        avgStMYDurationTv = findViewById(R.id.avg_st_m_y_duration_tv);
        avgMYLessonTv = findViewById(R.id.avg_m_y_lesson_tv);
        avgMYScoreTv = findViewById(R.id.avg_m_y_score_tv);

        sumSpDayDurationTv = findViewById(R.id.sum_sp_day_duration_tv);
        sumDayStepTv = findViewById(R.id.sum_day_step_tv);
        sumDayDistanceTv = findViewById(R.id.sum_day_distance_tv);

        sumSpMYDurationTv = findViewById(R.id.sum_sp_m_y_duration_tv);
        sumMYStepTv = findViewById(R.id.sum_m_y_step_tv);
        sumMYDistanceTv = findViewById(R.id.sum_m_y_distance_tv);

        avgSpMYDurationTv = findViewById(R.id.avg_sp_m_y_duration_tv);
        avgMYStepTv = findViewById(R.id.avg_m_y_step_tv);
        avgMYDistanceTv = findViewById(R.id.avg_m_y_distance_tv);

        sumDayWinTv = findViewById(R.id.sum_day_win_tv);
        sumDayLostTv = findViewById(R.id.sum_day_lost_tv);

        sumMYWinTv = findViewById(R.id.sum_g_m_y_win_tv);
        sumMYLostTv = findViewById(R.id.sum_g_m_y_lost_tv);

        avgMYWinTv = findViewById(R.id.avg_g_m_y_win_tv);
        avgMYLostTv = findViewById(R.id.avg_g_m_y_lost_tv);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        currentDate = new Date();
        String toDayStr = dateFormat.format(currentDate);
        dayTv.setText(toDayStr);

        activityLogRepository = new ActivityLogRepository();

        stType = listStType.get(0);
        spType = listSpType.get(0);

        nextDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                Date nextDate = nextDay(currentDate), todayDate = new Date();

                if (nextDate.after(todayDate)) {
                    Toast.makeText(SummaryActivity.this, "Action not available!", Toast.LENGTH_SHORT).show();
                } else {
                    currentDate = nextDay(currentDate);
                    dayTv.setText(dateFormat.format(currentDate));
                }




//                sportLogRepository.getDaySpLog(userId, dateFormat.format(currentDate), type.get(typeIndex), new GetDaySpLogCallback() {
//                    @Override
//                    public void onSuccess(Map<String, Object> sumDataList) {
//                        loadingLl.setVisibility(View.GONE);
//                        Toast.makeText(SportSumActivity.this, "Loading success!", Toast.LENGTH_SHORT).show();
//                        loadTextView(sumDataList, type.get(typeIndex), stepTvTr, distanceTvTr, sumDayStepTvTr, sumDayDistanceTvTr, sumDayDurationTv, sumDayStepTv, sumDayDistanceTv);
//                    }
//
//                    @Override
//                    public void onFailure(Exception e) {
//                        loadingLl.setVisibility(View.GONE);
//                        Toast.makeText(SportSumActivity.this, "Loading failed!", Toast.LENGTH_SHORT).show();
//                    }
//                });

                loadingLl.setVisibility(View.GONE);

            }
        });

        backDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                currentDate = backDay(currentDate);
                dayTv.setText(dateFormat.format(currentDate));

                loadingLl.setVisibility(View.GONE);

            }
        });


    }

//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        Log.d("SumAct", "Date: " + dateFormat.format(currentDate));
//        Log.d("SumAct", "StType: " + stType);
//        Log.d("SumAct", "SpType: " + spType);
//    }

    private Date nextDay(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    private Date backDay(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }
}