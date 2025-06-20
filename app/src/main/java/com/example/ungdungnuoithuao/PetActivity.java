package com.example.ungdungnuoithuao;

import android.graphics.Color;
import android.os.Bundle;
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

import model.Pet;
import repository.PetRepository;
import repository.callback.pet.PetLoadedCallback;
import repository.callback.pet.UpdatePetCallback;

public class PetActivity extends AppCompatActivity {
    private String userId;
    private PetRepository petRepository;
    private TextView petnameTv, iqStatusTv, physicalStatusTv, spiritStatusTv, overallStatusTv,
            iqScoreTv, physicalScoreTv, spiritScoreTv, overallScoreTv;
    private EditText petnameEt;
    private ProgressBar iqScoreBar, physicalScoreBar, spiritScoreBar, overallScoreBar;
    private LinearLayout loadingLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet);

        userId = getIntent().getStringExtra("userId");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText currentPasswordEt;
        Button editPetnameBtn, saveBtn, backBtn1, backBtn;
        LinearLayout petInfoLl, editInfoLl, backLl;

        petnameTv = findViewById(R.id.petname_tv);
        petnameEt = findViewById(R.id.petname_et);
        currentPasswordEt = findViewById(R.id.current_password_et);
        editPetnameBtn = findViewById(R.id.edit_petname_btn);
        saveBtn = findViewById(R.id.save_btn);
        backBtn1 = findViewById(R.id.back_btn1);

        iqStatusTv = findViewById(R.id.iq_status_tv);
        iqScoreTv = findViewById(R.id.iq_score_tv);
        iqScoreBar = findViewById(R.id.iq_score_bar);

        physicalStatusTv = findViewById(R.id.physical_status_tv);
        physicalScoreTv = findViewById(R.id.physical_score_tv);
        physicalScoreBar = findViewById(R.id.physical_score_bar);

        spiritStatusTv = findViewById(R.id.spirit_status_tv);
        spiritScoreTv = findViewById(R.id.spirit_score_tv);
        spiritScoreBar = findViewById(R.id.spirit_score_bar);

        overallStatusTv = findViewById(R.id.overall_status_tv);
        overallScoreTv = findViewById(R.id.overall_score_tv);
        overallScoreBar = findViewById(R.id.overall_score_bar);

        petInfoLl = findViewById(R.id.pet_info_ll);
        editInfoLl = findViewById(R.id.edit_info_ll);
        loadingLl = findViewById(R.id.loading_ll);
        backLl = findViewById(R.id.back_ll);

        backBtn = findViewById(R.id.back_btn);

        petRepository = new PetRepository();

        loadPetData(userId);

        editPetnameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                petInfoLl.setVisibility(View.GONE);
                editInfoLl.setVisibility(View.VISIBLE);
                backLl.setVisibility(View.GONE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingLl.setVisibility(View.VISIBLE);
                String currentPassword = currentPasswordEt.getText().toString().trim();
                etOk(currentPasswordEt);

                if (currentPassword.isEmpty()) {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(PetActivity.this, "Vui lòng nhập mật khẩu để đổi tên thú cưng!", Toast.LENGTH_SHORT).show();
                    etError(currentPasswordEt);
                } else {
                    String nPetname = petnameEt.getText().toString().trim();
                    petRepository.getPet(userId, new PetLoadedCallback() {
                        @Override
                        public void onPetLoaded(Pet nPet) {
                            nPet.setPetname(nPetname);
                            petRepository.updatePet(userId, nPet, currentPassword, new UpdatePetCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(PetActivity.this, "Đổi tên thú cưng thành công!", Toast.LENGTH_SHORT).show();
                                    loadPetData(userId);
                                    petInfoLl.setVisibility(View.VISIBLE);
                                    editInfoLl.setVisibility(View.GONE);
                                    backLl.setVisibility(View.VISIBLE);
                                    etOk(petnameEt);
                                    etOk(currentPasswordEt);
                                    resetInput(currentPasswordEt);
                                    loadingLl.setVisibility(View.GONE);
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(PetActivity.this, "Đổi tên thú cưng thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onIncorrectPassword() {
                                    loadingLl.setVisibility(View.GONE);
                                    Toast.makeText(PetActivity.this, "Sai mật khẩu! Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                                    etError(currentPasswordEt);
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception errorMessage) {
                            loadingLl.setVisibility(View.GONE);
                            Toast.makeText(PetActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        backBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etOk(petnameEt);
                etOk(currentPasswordEt);
                resetInput(currentPasswordEt);
                petInfoLl.setVisibility(View.VISIBLE);
                editInfoLl.setVisibility(View.GONE);
                backLl.setVisibility(View.VISIBLE);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadPetData(String userId) {
        loadingLl.setVisibility(View.VISIBLE);
        petRepository.getPet(userId, new PetLoadedCallback() {
            @Override
            public void onPetLoaded(Pet nPet) {
                if (nPet != null) {
                    petnameTv.setText(nPet.getPetname());
                    petnameEt.setText(nPet.getPetname());

                    iqScoreTv.setText(String.valueOf(nPet.getIqscore()));
                    iqStatusTv.setText(nPet.getIqStatus());
                    iqStatusTv.setTextColor(Color.parseColor(nPet.getIqColor()));
                    iqScoreBar.setProgress(nPet.getIqscore());

                    physicalScoreTv.setText(String.valueOf(nPet.getPhysicalscore()));
                    physicalStatusTv.setText(nPet.getPhysicalStatus());
                    physicalStatusTv.setTextColor(Color.parseColor(nPet.getPhysicalColor()));
                    physicalScoreBar.setProgress(nPet.getPhysicalscore());

                    spiritScoreTv.setText(String.valueOf(nPet.getSpiritscore()));
                    spiritStatusTv.setText(nPet.getSpiritStatus());
                    spiritStatusTv.setTextColor(Color.parseColor(nPet.getSpiritColor()));
                    spiritScoreBar.setProgress(nPet.getSpiritscore());

                    overallScoreTv.setText(String.valueOf(nPet.getOverallscore()));
                    overallStatusTv.setText(nPet.getOverallStatus());
                    overallStatusTv.setTextColor(Color.parseColor(nPet.getOverallColor()));
                    overallScoreBar.setProgress(nPet.getOverallscore());

                    loadingLl.setVisibility(View.GONE);
                } else {
                    loadingLl.setVisibility(View.GONE);
                    Toast.makeText(PetActivity.this, "Tải dữ liệu không thành công! Vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception errorMessage) {

            }
        });
    }

    private void etOk(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(PetActivity.this, R.drawable.white_box_corner));
    }

    private void etError(EditText edt) {
        edt.setBackground(ContextCompat.getDrawable(PetActivity.this, R.drawable.white_warning_box_corner));
    }

    private void resetInput(EditText edt) {
        edt.setText("");
    }
}