package com.example.ungdungnuoithuao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.common.collect.Table;

import java.util.Arrays;
import java.util.List;

import model.User;
import repository.UserRepository;
import repository.callback.user.LoadTopUserCallback;
import repository.callback.user.UserLoadedCallback;

public class LeaderboardActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TableRow user1Tr, user2Tr, user3Tr, user4Tr, user5Tr, userTr, skipTr;
        TextView usname1Tv, usname2Tv, usname3Tv, usname4Tv, usname5Tv,
                lvl1Tv, lvl2Tv, lvl3Tv, lvl4Tv, lvl5Tv,
                userRankTv, thisUsnameTv, thisUsLvlTv;
        Button backBtn;
        LinearLayout loadingLl;

        UserRepository userRepository = new UserRepository();

        user1Tr = findViewById(R.id.user1_tr);
        user2Tr = findViewById(R.id.user2_tr);
        user3Tr = findViewById(R.id.user3_tr);
        user4Tr = findViewById(R.id.user4_tr);
        user5Tr = findViewById(R.id.user5_tr);

        usname1Tv = findViewById(R.id.usname1_tv);
        usname2Tv = findViewById(R.id.usname2_tv);
        usname3Tv = findViewById(R.id.usname3_tv);
        usname4Tv = findViewById(R.id.usname4_tv);
        usname5Tv = findViewById(R.id.usname5_tv);

        lvl1Tv = findViewById(R.id.lvl1_tv);
        lvl2Tv = findViewById(R.id.lvl2_tv);
        lvl3Tv = findViewById(R.id.lvl3_tv);
        lvl4Tv = findViewById(R.id.lvl4_tv);
        lvl5Tv = findViewById(R.id.lvl5_tv);

        userTr = findViewById(R.id.user_tr);
        skipTr = findViewById(R.id.skip_tr);

        userRankTv = findViewById(R.id.user_rank_tv);
        thisUsnameTv = findViewById(R.id.this_usname_tv);
        thisUsLvlTv = findViewById(R.id.this_uslvl_tv);

        loadingLl = findViewById(R.id.loading_ll);
        loadingLl.setVisibility(View.VISIBLE);

        backBtn = findViewById(R.id.back_btn);

        List<TableRow> listUserTr = Arrays.asList(user1Tr, user2Tr, user3Tr, user4Tr, user5Tr);

        List<TextView> usnameLv = Arrays.asList(usname1Tv, usname2Tv, usname3Tv, usname4Tv, usname5Tv),
                lvlTv = Arrays.asList(lvl1Tv, lvl2Tv, lvl3Tv, lvl4Tv, lvl5Tv);

        skipTr.setVisibility(View.GONE);
        userTr.setVisibility(View.GONE);
        user1Tr.setVisibility(View.GONE);
        user2Tr.setVisibility(View.GONE);
        user3Tr.setVisibility(View.GONE);
        user4Tr.setVisibility(View.GONE);
        user5Tr.setVisibility(View.GONE);

        userRepository.getTopUser(userId, new LoadTopUserCallback() {
            @Override
            public void onSuccess(List<User> topUser, int userRank) {
                for (int i = 0; i < topUser.size(); i++) {
                    listUserTr.get(i).setVisibility(View.VISIBLE);
                    loadTopUserInfo(topUser.get(i), usnameLv.get(i), lvlTv.get(i));
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(LeaderboardActivity.this, "Load leader board successful!", Toast.LENGTH_SHORT).show();
                }
                if (userRank > 5) {
                    userTr.setVisibility(View.VISIBLE);
                    userRankTv.setText(String.valueOf(userRank));
                    userRepository.getUser(userId, new UserLoadedCallback() {
                        @Override
                        public void onUserLoaded(User nUser) {
                            loadTopUserInfo(nUser, thisUsnameTv, thisUsLvlTv);
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(LeaderboardActivity.this, "Load leader board successful!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(LeaderboardActivity.this, "Load leader board failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (userRank == 6) skipTr.setVisibility(View.GONE);
                    else skipTr.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception exception) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(LeaderboardActivity.this, "Load leader board failed!", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void loadTopUserInfo(User user, TextView usnameTv, TextView lvlTv) {
        usnameTv.setText(user.getUsername());
        lvlTv.setText(String.valueOf(user.getLevel()));
    }
}