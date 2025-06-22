package com.example.ungdungnuoithuao;

import android.os.Bundle;
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
    private ImageButton backDayBtn, nextDayBtn, backMonthBtn, nextMonthBtn, backYearBtn, nextYearBtn,
            nextStTypeBtn, backStTypeBtn, nextSpTypeBtn, backSpTypeBtn;
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

        activityLogRepository = new ActivityLogRepository();

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

        nextStTypeBtn = findViewById(R.id.next_st_type_btn);
        backStTypeBtn = findViewById(R.id.back_st_type_btn);

        nextSpTypeBtn = findViewById(R.id.next_sp_type_btn);
        backSpTypeBtn = findViewById(R.id.back_sp_type_btn);

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

        stType = listStType.get(0);
        spType = listSpType.get(0);

        stTypeTv.setText(stType);
        spTypeTv.setText(spType);

        loadStTextView(userId, toDayStr, stType);
        loadSpTextView(userId, dateFormat.format(currentDate), listSpType.get(spTypeIndex));
        loadGameTextView(userId, toDayStr, "Giải trí");

        nextDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date nextDate = nextDay(currentDate), todayDate = new Date();

                if (nextDate.after(todayDate)) {
                    Toast.makeText(SummaryActivity.this, "Hôm nay là ngày mới nhất rồi!", Toast.LENGTH_SHORT).show();
                } else {
                    currentDate = nextDay(currentDate);
                    dayTv.setText(dateFormat.format(currentDate));

                    loadStTextView(userId, dateFormat.format(currentDate), stType);
                    loadSpTextView(userId, dateFormat.format(currentDate), listSpType.get(spTypeIndex));
                    loadGameTextView(userId, dateFormat.format(currentDate), "Giải trí");
                }
            }
        });

        backDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate = backDay(currentDate);
                dayTv.setText(dateFormat.format(currentDate));

                loadStTextView(userId, dateFormat.format(currentDate), stType);
                loadSpTextView(userId, dateFormat.format(currentDate), listSpType.get(spTypeIndex));
                loadGameTextView(userId, dateFormat.format(currentDate), "Giải trí");
            }
        });

        nextStTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stTypeIndex < listStType.size() - 1) {
                    stTypeIndex++;
                } else {
                    stTypeIndex = 0;
                }

                stTypeTv.setText(listStType.get(stTypeIndex));
                loadStTextView(userId, dateFormat.format(currentDate), listStType.get(stTypeIndex));
            }
        });

        backStTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stTypeIndex > 0) {
                    stTypeIndex--;
                } else {
                    stTypeIndex = listStType.size() - 1;
                }

                stTypeTv.setText(listStType.get(stTypeIndex));
                loadStTextView(userId, dateFormat.format(currentDate), listStType.get(stTypeIndex));
            }
        });

        nextSpTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spTypeIndex < listSpType.size() - 1) {
                    spTypeIndex++;
                } else {
                    spTypeIndex = 0;
                }

                spTypeTv.setText(listSpType.get(spTypeIndex));
                loadSpTextView(userId, dateFormat.format(currentDate), listSpType.get(spTypeIndex));
            }
        });

        backSpTypeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spTypeIndex > 0) {
                    spTypeIndex--;
                } else {
                    spTypeIndex = listSpType.size() - 1;
                }
                spTypeTv.setText(listSpType.get(spTypeIndex));
                loadSpTextView(userId, dateFormat.format(currentDate), listSpType.get(spTypeIndex));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

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

    private void loadStTextView(String userId, String date, String type) {
        loadingLl.setVisibility(View.VISIBLE);
        int result = 0;
        activityLogRepository.getDayActlog(userId, date, type, new GetDayActLogCallback() {
            @Override
            public void onSuccess(Map<String, Object> sumDataList) {
                sumStDayDurationTv.setText(sumDataList.get("sumduration").toString());
                sumDayLessonTv.setText(sumDataList.get("lesson").toString());
                avgDayScoreTv.setText(sumDataList.get("avgscore").toString());

                avgDayScoreTvTr.setVisibility(View.GONE);
                avgDayScoreTr.setVisibility(View.GONE);

                if (type.equals("Kiểm tra")) {
                    avgDayScoreTvTr.setVisibility(View.VISIBLE);
                    avgDayScoreTr.setVisibility(View.VISIBLE);
                }
                loadingLl.setVisibility(View.GONE);
//                Toast.makeText(SummaryActivity.this, "Tải dữ liệu thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(SummaryActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpTextView(String userId, String date, String type) {
        loadingLl.setVisibility(View.VISIBLE);
        activityLogRepository.getDayActlog(userId, date, type, new GetDayActLogCallback() {
            @Override
            public void onSuccess(Map<String, Object> sumDataList) {
                sumSpDayDurationTv.setText(sumDataList.get("sumduration").toString());
                sumDayStepTv.setText(sumDataList.get("sumstep").toString());
                sumDayDistanceTv.setText(sumDataList.get("sumdistance").toString());

                stepTvTr.setVisibility(View.GONE);
                distanceTvTr.setVisibility(View.GONE);
                sumDayStepTr.setVisibility(View.GONE);
                sumDayDistanceTr.setVisibility(View.GONE);

                if (type.equals("Chạy bộ")) {
                    stepTvTr.setVisibility(View.VISIBLE);
                    distanceTvTr.setVisibility(View.VISIBLE);
                    sumDayStepTr.setVisibility(View.VISIBLE);
                    sumDayDistanceTr.setVisibility(View.VISIBLE);
                }
                if (type.equals("Đạp xe")) {
                    distanceTvTr.setVisibility(View.VISIBLE);
                    sumDayDistanceTr.setVisibility(View.VISIBLE);
                }
                loadingLl.setVisibility(View.GONE);
//                Toast.makeText(SummaryActivity.this, "Tải dữ liệu thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(SummaryActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadGameTextView(String userId, String date, String type) {
        loadingLl.setVisibility(View.VISIBLE);
        activityLogRepository.getDayActlog(userId, date, type, new GetDayActLogCallback() {
            @Override
            public void onSuccess(Map<String, Object> sumDataList) {
                sumDayWinTv.setText(sumDataList.get("wincount").toString());
                sumDayLostTv.setText(sumDataList.get("lostcount").toString());
                loadingLl.setVisibility(View.GONE);
//                Toast.makeText(SummaryActivity.this, "Tải dữ liệu thành công!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(SummaryActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}