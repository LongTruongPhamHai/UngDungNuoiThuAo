package com.example.ungdungnuoithuao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import repository.UserRepository;
import repository.callback.user.UpdateUserCallback;
import repository.callback.user.UserLoadedCallback;

public class UserActivity extends AppCompatActivity {

    private String userId;
    private TextView emailTv, usernameTv, levelTv, levelBarTv;
    private EditText usernameEt;
    private ProgressBar levelBar;
    private LinearLayout loadingLl;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText currentPasswordEt1, currentPasswordEt2, currentPasswordEt3, currentPasswordEt4,
                newPasswordEt, confirmPasswordEt;
        Button saveBtn1, saveBtn2, saveBtn3, saveBtn4, backBtn, backBtn1, backBtn2, backBtn3, backBtn4,
                editEmailBtn, editUsnameBtn, changePasswordBtn, resetAccBtn, deleteAccBtn;
        LinearLayout userInfoLl, editInfoLl, editPwLl, resetAccLl, deleteAccLl, backLl;

        emailTv = findViewById(R.id.email_tv);
        usernameTv = findViewById(R.id.username_tv);
        levelTv = findViewById(R.id.level_tv);
        levelBarTv = findViewById(R.id.level_bar_tv);

        levelBar = findViewById(R.id.level_bar);

        usernameEt = findViewById(R.id.username_et);

        newPasswordEt = findViewById(R.id.new_password_et);
        confirmPasswordEt = findViewById(R.id.confirm_password_et);

        currentPasswordEt1 = findViewById(R.id.current_password_et1);
        currentPasswordEt2 = findViewById(R.id.current_password_et2);
        currentPasswordEt3 = findViewById(R.id.current_password_et3);
        currentPasswordEt4 = findViewById(R.id.current_password_et4);

        saveBtn1 = findViewById(R.id.save_btn1);
        saveBtn2 = findViewById(R.id.save_btn2);
        saveBtn3 = findViewById(R.id.save_btn3);
        saveBtn4 = findViewById(R.id.save_btn4);

        backBtn1 = findViewById(R.id.back_btn1);
        backBtn2 = findViewById(R.id.back_btn2);
        backBtn3 = findViewById(R.id.back_btn3);
        backBtn4 = findViewById(R.id.back_btn4);

        editEmailBtn = findViewById(R.id.edit_email_btn);
        editUsnameBtn = findViewById(R.id.edit_usname_btn);
        changePasswordBtn = findViewById(R.id.change_pw_btn);
        resetAccBtn = findViewById(R.id.reset_acc_btn);
        deleteAccBtn = findViewById(R.id.delete_acc_btn);

        userInfoLl = findViewById(R.id.user_info_ll);
        editInfoLl = findViewById(R.id.edit_info_ll);
        editPwLl = findViewById(R.id.edit_pw_ll);
        resetAccLl = findViewById(R.id.reset_acc_ll);
        deleteAccLl = findViewById(R.id.delete_acc_ll);
        backLl = findViewById(R.id.back_ll);

        loadingLl = findViewById(R.id.loading_ll);
        backBtn = findViewById(R.id.back_btn);

        userRepository = new UserRepository();

        loadUserData(userId);

        editEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRepository.getUser(userId, new UserLoadedCallback() {
                    @Override
                    public void onUserLoaded(User nUser) {
                        String mailContent = "Tên đăng nhập: " + nUser.getUsername() + ".\n"
                                + "Email: " + nUser.getEmail() + ".\n"
                                + "Yêu cầu: Thay đổi địa chỉ email.\n"
                                + "Email mới (Nhập địa chỉ email mới tại đây):";

                        Intent toMail = new Intent(Intent.ACTION_SENDTO);
                        toMail.setData(Uri.parse("mailto:"));
                        toMail.putExtra(Intent.EXTRA_EMAIL, new String[]{"email@example.com"});
                        toMail.putExtra(Intent.EXTRA_SUBJECT, "Thay đổi địa chỉ email");
                        toMail.putExtra(Intent.EXTRA_TEXT, mailContent);

                        if (toMail.resolveActivity(getPackageManager()) != null) {
                            startActivity(toMail);
                        } else {
                            Toast.makeText(UserActivity.this, "Không tìm thấy ứng dụng email.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(UserActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                    }
                });
                
            }
        });

        editUsnameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.GONE);
                editInfoLl.setVisibility(View.VISIBLE);
                backLl.setVisibility(View.GONE);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.GONE);
                editPwLl.setVisibility(View.VISIBLE);
                backLl.setVisibility(View.GONE);
            }
        });

        resetAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.GONE);
                resetAccLl.setVisibility(View.VISIBLE);
                backLl.setVisibility(View.GONE);
            }
        });

        deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.GONE);
                deleteAccLl.setVisibility(View.VISIBLE);
                backLl.setVisibility(View.GONE);
            }
        });

        saveBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                etOk(usernameEt);
                etOk(currentPasswordEt1);
                String username = usernameEt.getText().toString().trim(),
                        currentPassword = currentPasswordEt1.getText().toString().trim();
                if (username.isEmpty()) {
                    Toast.makeText(UserActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    loadingLl.setVisibility(View.GONE);
                    etError(usernameEt);
                } else if (currentPassword.isEmpty()) {
                    Toast.makeText(UserActivity.this, "Vui lòng nhập mật khẩu để cập nhật thông tin!", Toast.LENGTH_SHORT).show();
                    etError(currentPasswordEt1);
                    loadingLl.setVisibility(View.GONE);
                } else {
                    userRepository.getUser(userId, new UserLoadedCallback() {
                        @Override
                        public void onUserLoaded(User nUser) {
                            nUser.setUsername(username);
                            userRepository.updateUser(userId, nUser, currentPassword, new UpdateUserCallback() {
                                @Override
                                public void onSuccess() {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Đổi tên đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    loadUserData(userId);
                                    etOk(usernameEt);
                                    etOk(currentPasswordEt1);
                                    resetPwInput(currentPasswordEt1);
                                    userInfoLl.setVisibility(View.VISIBLE);
                                    editInfoLl.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Đổi tên đăng nhập không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onIncorrectPw() {
                                    etError(currentPasswordEt1);
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Sai mật khẩu! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onUsernameTaken() {
                                    etError(usernameEt);
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Tên đăng nhập đã tồn tại!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        saveBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);

                etOk(newPasswordEt);
                etOk(confirmPasswordEt);
                etOk(currentPasswordEt2);
                String currentPw2 = currentPasswordEt2.getText().toString().trim(),
                        nPassword = newPasswordEt.getText().toString().trim(),
                        cPassword = confirmPasswordEt.getText().toString().trim();
                if (currentPw2.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(UserActivity.this, "Vui lòng nhập mật khẩu để cập nhật thông tin!", Toast.LENGTH_SHORT).show();
                    etError(currentPasswordEt2);
                } else if (nPassword.isEmpty() || cPassword.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(UserActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    etError(newPasswordEt);
                    etError(confirmPasswordEt);
                } else if (nPassword.length() != 8) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(UserActivity.this, "Mật khẩu phải dài 8 ký tự!", Toast.LENGTH_SHORT).show();
                    etError(newPasswordEt);
                } else if (!nPassword.equals(cPassword)) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(UserActivity.this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
                    etError(confirmPasswordEt);
                } else {
                    userRepository.getUser(userId, new UserLoadedCallback() {
                        @Override
                        public void onUserLoaded(User nUser) {
                            String hashedNPw = BCrypt.hashpw(nPassword, BCrypt.gensalt());
                            nUser.setPassword(hashedNPw);
                            userRepository.updateUser(userId, nUser, currentPw2, new UpdateUserCallback() {
                                @Override
                                public void onSuccess() {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                                    loadUserData(userId);
                                    etOk(newPasswordEt);
                                    etOk(confirmPasswordEt);
                                    etOk(currentPasswordEt2);
                                    resetPwInput(newPasswordEt);
                                    resetPwInput(confirmPasswordEt);
                                    resetPwInput(currentPasswordEt2);
                                    userInfoLl.setVisibility(View.VISIBLE);
                                    editPwLl.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Đổi mật khẩu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onIncorrectPw() {
                                    etError(currentPasswordEt2);
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(UserActivity.this, "Sai mật khẩu! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onUsernameTaken() {

                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        saveBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                etOk(currentPasswordEt3);
                String password3 = currentPasswordEt3.getText().toString().trim();
                if (password3.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(UserActivity.this, "Vui lòng nhập mật khẩu để đặt lại tài khoản!", Toast.LENGTH_SHORT).show();
                    etError(currentPasswordEt3);
                } else {
                    userRepository.resetAccount(userId, password3, new UpdateUserCallback() {
                        @Override
                        public void onSuccess() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Đặt lại tài khoản thành công!", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent toHome = new Intent(UserActivity.this, HomeActivity.class);
                                    toHome.putExtra("userId", userId);
                                    startActivity(toHome);
                                    finish();
                                }
                            }, 1000);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Đặt lại tài khoản thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onIncorrectPw() {
                            etError(currentPasswordEt3);
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Sai mật khẩu! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onUsernameTaken() {

                        }
                    });
                }
            }
        });

        saveBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                etOk(currentPasswordEt4);
                String password = currentPasswordEt4.getText().toString().trim();
                if (password.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(UserActivity.this, "Vui lòng nhập mật khẩu để xóa tài khoản!", Toast.LENGTH_SHORT).show();
                    etError(currentPasswordEt4);
                } else {
                    userRepository.deleteUser(userId, password, new UpdateUserCallback() {
                        @Override
                        public void onSuccess() {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Xóa tài khoản thành công! Chào tạm biệt!", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent toMain = new Intent(UserActivity.this, MainActivity.class);
                                    startActivity(toMain);
                                    finishAffinity();
                                }
                            }, 1000);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Xóa tài khoản không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onIncorrectPw() {
                            etError(currentPasswordEt4);
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(UserActivity.this, "Sai mật khẩu! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onUsernameTaken() {

                        }
                    });
                }
            }
        });

        backBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.VISIBLE);
                editInfoLl.setVisibility(View.GONE);
                backLl.setVisibility(View.VISIBLE);
                etOk(usernameEt);
                etOk(currentPasswordEt1);
                resetPwInput(currentPasswordEt1);
            }
        });

        backBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.VISIBLE);
                editPwLl.setVisibility(View.GONE);
                backLl.setVisibility(View.VISIBLE);
                etOk(newPasswordEt);
                etOk(confirmPasswordEt);
                etOk(currentPasswordEt2);
                resetPwInput(newPasswordEt);
                resetPwInput(confirmPasswordEt);
                resetPwInput(currentPasswordEt2);

            }
        });

        backBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.VISIBLE);
                resetAccLl.setVisibility(View.GONE);
                backLl.setVisibility(View.VISIBLE);
                etOk(currentPasswordEt3);
                resetPwInput(currentPasswordEt3);

            }
        });

        backBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfoLl.setVisibility(View.VISIBLE);
                deleteAccLl.setVisibility(View.GONE);
                backLl.setVisibility(View.VISIBLE);
                etOk(currentPasswordEt4);
                resetPwInput(currentPasswordEt4);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadUserData(String userId) {
        loadingLl.setVisibility(View.VISIBLE);
        userRepository.getUser(userId, new UserLoadedCallback() {
            @Override
            public void onUserLoaded(User nUser) {
                emailTv.setText(nUser.getEmail());
                usernameTv.setText(nUser.getUsername());
                usernameEt.setText(nUser.getUsername());
                levelTv.setText("Cấp độ: " + String.valueOf(nUser.getLevel()));
                levelBar.setProgress(nUser.getExp());
                levelBarTv.setText(String.valueOf(nUser.getExp()));
                loadingLl.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Exception e) {
                loadingLl.setVisibility(View.GONE);
                Toast.makeText(UserActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void etOk(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(UserActivity.this, R.drawable.white_box_corner));
    }

    private void etError(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(UserActivity.this, R.drawable.white_warning_box_corner));
    }

    private void resetPwInput(EditText edt) {
        edt.setText("");
    }
}