package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import repository.UserRepository;
import repository.callback.user.LoginCallback;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText usernameEt, passwordEt;
        TextView forgotPwTv;
        Button loginBtn, registerBtn;
        LinearLayout loadingLl;
        UserRepository userRepository;

        usernameEt = findViewById(R.id.username_et);
        passwordEt = findViewById(R.id.password_et);
        forgotPwTv = findViewById(R.id.forgot_pw_tv);
        loginBtn = findViewById(R.id.login_btn);
        registerBtn = findViewById(R.id.register_btn);
        loadingLl = findViewById(R.id.loading_ll);

        userRepository = new UserRepository();

        forgotPwTv.setVisibility(View.GONE);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                resetPwInput(passwordEt);
                startActivity(toRegister);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);

                String username = usernameEt.getText().toString().trim(),
                        password = passwordEt.getText().toString().trim();
                etOk(usernameEt);
                etOk(passwordEt);

                if (username.isEmpty() || password.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    if (username.isEmpty())
                        etError(usernameEt);

                    if (password.isEmpty())
                        etError(passwordEt);
                } else {
                    userRepository.loginUser(username, password, new LoginCallback() {
                        @Override
                        public void onSuccess(String userId) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công! Chào mừng " + username, Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                Intent toHome = new Intent(LoginActivity.this, HomeActivity.class);
                                toHome.putExtra("userId", userId);
                                Log.d("UserRepo", userId);
                                startActivity(toHome);
                                finish();
                            }, 1000);
                        }

                        @Override
                        public void onIncorrectPassword() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Sai mật khẩu! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                            forgotPwTv.setVisibility(View.VISIBLE);
                            etError(passwordEt);
                        }

                        @Override
                        public void onUsernameNotFound() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại!", Toast.LENGTH_SHORT).show();
                            etError(usernameEt);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Đăng nhập không thành công! Vui lòng thử lại trong giây lát!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        forgotPwTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toForgotPw = new Intent(LoginActivity.this, ForgotPwActivity.class);
                startActivity(toForgotPw);
            }
        });
    }

    public void etOk(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.white_box_corner));
    }

    public void etError(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.white_warning_box_corner));
    }

    public void resetPwInput(EditText edt) {
        edt.setText("");
    }
}