package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import model.User;
import repository.UserRepository;
import repository.callback.user.UserLoadedCallback;

public class AboutActivity extends AppCompatActivity {
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView emailTv, phoneTv;
        Button backBtn;

        emailTv = findViewById(R.id.email_tv);
        phoneTv = findViewById(R.id.phone_tv);
        backBtn = findViewById(R.id.back_btn);

        emailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRepository userRepository = new UserRepository();
                userRepository.getUser(userId, new UserLoadedCallback() {
                    @Override
                    public void onUserLoaded(User nUser) {
                        String mailContent = "Tên đăng nhập: " + nUser.getUsername() + ".\n"
                                + "Email: " + nUser.getEmail() + ".\n"
                                + "Yêu cầu: ";

                        Intent toMail = new Intent(Intent.ACTION_SENDTO);
                        toMail.setData(Uri.parse("mailto:"));
                        toMail.putExtra(Intent.EXTRA_EMAIL, new String[]{"email@example.com"});
                        toMail.putExtra(Intent.EXTRA_SUBJECT, "Yêu cầu của người dùng");
                        toMail.putExtra(Intent.EXTRA_TEXT, mailContent);

                        if (toMail.resolveActivity(getPackageManager()) != null) {
                            startActivity(toMail);
                        } else {
                            Toast.makeText(AboutActivity.this, "Không tìm thấy ứng dụng email.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(AboutActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        phoneTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
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