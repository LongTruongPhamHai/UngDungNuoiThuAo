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

import model.Pet;
import model.User;
import repository.PetRepository;
import repository.UserRepository;
import repository.callback.pet.PetSetupCallback;
import repository.callback.user.RegistrationCallback;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        EditText emailEt, usernameEt, passwordEt, cPasswordEt, petnameEt;
        TextView returnLoginTv;
        Button registerBtn;
        LinearLayout loadingLl;
        UserRepository userRepository;
        PetRepository petRepository;

        emailEt = findViewById(R.id.email_et);
        usernameEt = findViewById(R.id.username_et);
        passwordEt = findViewById(R.id.password_et);
        cPasswordEt = findViewById(R.id.confirm_password_et);
        petnameEt = findViewById(R.id.petname_et);
        returnLoginTv = findViewById(R.id.return_login_tv);
        registerBtn = findViewById(R.id.register_btn);
        loadingLl = findViewById(R.id.loading_ll);

        userRepository = new UserRepository();
        petRepository = new PetRepository();

        returnLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
//                startActivity(toLogin);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                String email = emailEt.getText().toString().trim(),
                        username = usernameEt.getText().toString().trim(),
                        password = passwordEt.getText().toString().trim(),
                        cPassword = cPasswordEt.getText().toString().trim(),
                        petname = petnameEt.getText().toString().trim();
                etOk(emailEt);
                etOk(usernameEt);
                etOk(passwordEt);
                etOk(cPasswordEt);
                etOk(petnameEt);

                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || cPassword.isEmpty() || petname.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    if (email.isEmpty())
                        etError(emailEt);

                    if (username.isEmpty())
                        etError(usernameEt);

                    if (password.isEmpty())
                        etError(passwordEt);

                    if (cPassword.isEmpty())
                        etError(cPasswordEt);

                    if (petname.isEmpty())
                        etError(petnameEt);

                } else if (password.length() != 8) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Mật khẩu dài 8 ký tự!!", Toast.LENGTH_SHORT).show();
                    etError(passwordEt);
                } else if (!password.equals(cPassword)) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                    etError(cPasswordEt);
                } else if (!isValidEmail(email)) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(RegisterActivity.this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                    etError(emailEt);
                } else {
                    User newUser = new User(email, username, password);

                    userRepository.registerUser(newUser, new RegistrationCallback() {
                        @Override
                        public void onEmailTaken() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Email đã tồn tại!", Toast.LENGTH_SHORT).show();
                            etError(emailEt);
                        }

                        @Override
                        public void onUsernameTaken() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                            etError(usernameEt);
                        }

                        @Override
                        public void onSuccess(String userId) {
                            Pet newPet = new Pet(petname, userId);

                            petRepository.registerPet(newPet, new PetSetupCallback() {
                                @Override
                                public void onSuccess() {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(RegisterActivity.this, "Đăng ký tài khoản thành công! Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(toLogin);
                                            finish();
                                        }
                                    }, 1000);
                                }

                                @Override
                                public void onError(Exception e) {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(RegisterActivity.this, "Tạo thú cưng không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Tạo tài khoản không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void etOk(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(RegisterActivity.this, R.drawable.white_box_corner));
    }

    private void etError(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(RegisterActivity.this, R.drawable.white_warning_box_corner));
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}