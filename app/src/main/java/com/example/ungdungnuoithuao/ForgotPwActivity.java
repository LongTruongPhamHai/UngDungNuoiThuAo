package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mindrot.jbcrypt.BCrypt;

import model.User;
import repository.PetRepository;
import repository.UserRepository;
import repository.callback.user.CheckUserInfoCallback;
import repository.callback.user.ForgotPwCallback;
import repository.callback.user.UpdateUserCallback;

public class ForgotPwActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_pw);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText emailEt, usernameEt, passwordEt, cPasswordEt;
        Button saveBtn, backBtn;
        LinearLayout loadingLl;
        UserRepository userRepository;
        PetRepository petRepository;

        emailEt = findViewById(R.id.email_et);
        usernameEt = findViewById(R.id.username_et);
        passwordEt = findViewById(R.id.password_et);
        cPasswordEt = findViewById(R.id.confirm_password_et);
        saveBtn = findViewById(R.id.save_btn);
        loadingLl = findViewById(R.id.loading_ll);
        backBtn = findViewById(R.id.back_btn);

        userRepository = new UserRepository();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                String email = emailEt.getText().toString().trim(),
                        username = usernameEt.getText().toString().trim(),
                        password = passwordEt.getText().toString().trim(),
                        cPassword = cPasswordEt.getText().toString().trim();
                etOk(emailEt);
                etOk(usernameEt);
                etOk(passwordEt);
                etOk(cPasswordEt);

                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || cPassword.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(ForgotPwActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    if (email.isEmpty())
                        etError(emailEt);

                    if (username.isEmpty())
                        etError(usernameEt);

                    if (password.isEmpty())
                        etError(passwordEt);

                    if (cPassword.isEmpty())
                        etError(cPasswordEt);

                } else if (!isValidEmail(email)) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(ForgotPwActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    etError(emailEt);
                } else {
                    userRepository.checkUserInfo(email, username, new CheckUserInfoCallback() {
                        @Override
                        public void onSuccess(String userId) {
                            if (password.length() != 8) {
                                loadingLl.setVisibility(View.GONE);
                                Toast.makeText(ForgotPwActivity.this, "Mật khẩu dài 8 ký tự!!", Toast.LENGTH_SHORT).show();
                                etError(passwordEt);
                            } else if (!password.equals(cPassword)) {
                                loadingLl.setVisibility(View.GONE);
                                Toast.makeText(ForgotPwActivity.this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                                etError(cPasswordEt);
                            } else {
                                String nPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                                userRepository.forgotPw(userId, nPassword, new ForgotPwCallback() {
                                    @Override
                                    public void onSuccess() {
                                        loadingLl.setVisibility(View.GONE);
                                        Toast.makeText(ForgotPwActivity.this, "Đổi mật khẩu thành công! Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent toLogin = new Intent(ForgotPwActivity.this, LoginActivity.class);
                                                startActivity(toLogin);
                                                finish();
                                            }
                                        }, 1000);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        loadingLl.setVisibility(View.GONE);
                                        Toast.makeText(ForgotPwActivity.this, "Đổi mật khẩu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(ForgotPwActivity.this, "Kiểm tra thông tin không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onEmailNotFound() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(ForgotPwActivity.this, "Email không đúng! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                            etError(emailEt);
                        }

                        @Override
                        public void onUsernameNotFound() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(ForgotPwActivity.this, "Tên đăng nhập không đúng! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                            etError(usernameEt);
                        }
                    });
                }
            }
        });
    }

    public void etOk(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(ForgotPwActivity.this, R.drawable.white_box_corner));
    }

    public void etError(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(ForgotPwActivity.this, R.drawable.white_warning_box_corner));
    }

    public boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
