package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.checkerframework.checker.units.qual.A;
import org.mindrot.jbcrypt.BCrypt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.User;
import repository.callback.activitylog.DeleteActLogCallback;
import repository.callback.activitylog.GetLastActLogDateTimeCallback;
import repository.callback.pet.UpdatePetCallback;
import repository.callback.user.CheckUserInfoCallback;
import repository.callback.user.ForgotPwCallback;
import repository.callback.user.LoadTopUserCallback;
import repository.callback.user.LoginCallback;
import repository.callback.user.RegistrationCallback;
import repository.callback.user.UpdateUserCallback;
import repository.callback.user.UserLoadedCallback;

public class UserRepository {
    private FirebaseFirestore db;
    private PetRepository petRepository;
    private ActivityLogRepository activityLogRepository;


    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void registerUser(User newUser, RegistrationCallback callback) {
        db.collection("user").whereEqualTo("email", newUser.getEmail()).get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        db.collection("user").whereEqualTo("username", newUser.getUsername()).get()
                                .addOnSuccessListener(documentSnapshots1 -> {
                                    if (documentSnapshots1.isEmpty()) {
                                        String hashedPassword = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("email", newUser.getEmail());
                                        data.put("username", newUser.getUsername());
                                        data.put("password", hashedPassword);
                                        data.put("exp", 0);
                                        data.put("level", 1);
                                        data.put("coin", 50);

                                        db.collection("user").add(data)
                                                .addOnSuccessListener(documentReference -> {
                                                    Log.d("UserRepo", "Register success!");
                                                    String userId = documentReference.getId();
                                                    callback.onSuccess(userId);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.d("UserRepo", "Register failed! " + e.toString());
                                                    callback.onError(e);
                                                });
                                    } else {
                                        Log.d("UserRepo", "Register failed! Username taken!");
                                        callback.onUsernameTaken();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("UserRepo", "Register on failure username");
                                });
                    } else {
                        Log.d("UserRepo", "Register failed! Email taken!");
                        callback.onEmailTaken();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Register failed! " + e.toString());
                    callback.onError(e);
                });
    }

    public void checkUserInfo(String email, String username, CheckUserInfoCallback callback) {
        db.collection("user").whereEqualTo("email", email).get()
                .addOnSuccessListener(emailSnapshots -> {
                    if (!emailSnapshots.isEmpty()) {
                        db.collection("user").whereEqualTo("username", username).get()
                                .addOnSuccessListener(usernameSnapshots -> {
                                    if (!usernameSnapshots.isEmpty()) {
                                        DocumentSnapshot doc = usernameSnapshots.getDocuments().get(0);
                                        String userId = doc.getId();
                                        callback.onSuccess(userId);
                                    } else {
                                        callback.onUsernameNotFound();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    callback.onFailure(e);
                                });
                    } else {
                        callback.onEmailNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void loginUser(String username, String password, LoginCallback callback) {
        db.collection("user").whereEqualTo("username", username).get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (!documentSnapshots.isEmpty()) {
                        DocumentSnapshot document = documentSnapshots.getDocuments().get(0);
                        String dbPassword = document.getString("password");
                        if (BCrypt.checkpw(password, dbPassword)) {
                            Log.d("UserRepo", "Login success!");
                            String userId = document.getId();
                            Log.d("UserRepo", "UserID: " + userId);
                            callback.onSuccess(userId);
                        } else {
                            Log.d("UserRepo", "Login incorrect Pw!");
                            callback.onIncorrectPassword();
                        }
                    } else {
                        Log.d("UserRepo", "Login username not found!");
                        callback.onUsernameNotFound();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Login failed!");
                    callback.onFailure(e);
                });
    }

    public void getUser(String userId, UserLoadedCallback callback) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        callback.onUserLoaded(user);
                    } else {
                        Log.d("UserRepo", "User not found!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Loading data error!");
                    callback.onFailure(e);
                });
    }

    public void getTopUser(String userId, LoadTopUserCallback callback) {
        db.collection("user").orderBy("level", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("UserRepo", "Get top user success!");
                    List<User> topUser = new ArrayList<>();
                    int userRank = 0;
                    int index = 0;
                    for (DocumentSnapshot doc : documentSnapshots) {
                        User user = doc.toObject(User.class);
                        if (index < 5) {
                            topUser.add(user);
                        }
                        index++;
                    }
                    index = 0;
                    Log.d("UserRepo", "...");
                    for (DocumentSnapshot doc : documentSnapshots) {
                        if (doc.getId().equals(userId)) {
                            userRank = index + 1;
                            break;
                        }
                        index++;
                    }
                    callback.onSuccess(topUser, userRank);
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Get top user failed! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void forgotPw(String userId, String password, ForgotPwCallback callback) {
        db.collection("user").document(userId).update("password", password)
                .addOnSuccessListener(v -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void updateUser(String userId, User user, String iPassword, UpdateUserCallback callback) {
        db.collection("user")
                .whereEqualTo("username", user.getUsername())
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    int usernameRes = 0;
                    Log.d("UserRepo", "Found username duplicates: " + documentSnapshots.size());
                    if (!documentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                            if (!documentSnapshot.getId().equals(userId)) {
                                usernameRes = 1;
                                break;
                            }
                        }
                    }

                    if (usernameRes == 1) {
                        callback.onUsernameTaken();
                    } else {
                        db.collection("user").document(userId).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    Log.d("UserRepo", "Load user success");
                                    String storedPw = documentSnapshot.getString("password");

                                    if (BCrypt.checkpw(iPassword, storedPw)) {
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("email", user.getEmail());
                                        data.put("username", user.getUsername());
                                        data.put("password", user.getPassword());
                                        data.put("exp", user.getExp());
                                        data.put("level", user.getLevel());

                                        db.collection("user").document(userId).update(data)
                                                .addOnSuccessListener(v -> {
                                                    Log.d("UserRepo", "Update user success");
                                                    callback.onSuccess();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.d("UserRepo", "Update user failed! " + e.toString());
                                                    callback.onFailure(e);
                                                });

                                    } else {
                                        Log.d("UserRepo", "Update user failed! Incorrect password!");
                                        callback.onIncorrectPw();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("UserRepo", "Load user failed! " + e.toString());
                                    callback.onFailure(e);
                                });
                    }

                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "!UsernameTaken");
                });
    }

    public void updateUserStat(String userId, User user, UpdateUserCallback callback) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("UserRepo", "Load user success");
                    Map<String, Object> data = new HashMap<>();
                    data.put("exp", user.getExp());
                    data.put("level", user.getLevel());
                    data.put("coin", user.getCoin());

                    db.collection("user").document(userId).update(data)
                            .addOnSuccessListener(v -> {
                                Log.d("UserRepo", "Update user stat success");
                                callback.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.d("UserRepo", "Update user stat failed! " + e.toString());
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Load user failed! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void resetAccount(String userId, String password, UpdateUserCallback callback) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d("UserRepo", "Load user success");
                    String storedPw = documentSnapshot.getString("password");

                    if (BCrypt.checkpw(password, storedPw)) {
                        db.collection("user").document(userId)
                                .update("exp", 0, "level", 1, "coin", 50)
                                .addOnSuccessListener(v -> {
                                    Log.d("UserRepo", "Reset user success!");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("UserRepo", "Reset user failed! " + e.toString());
                                    callback.onFailure(e);
                                });

                        petRepository = new PetRepository();
                        activityLogRepository = new ActivityLogRepository();

                        petRepository.resetPet(userId, new UpdatePetCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("UserRepo", "Reset pet success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("UserRepo", "Reset pet failed! " + e.toString());
                            }

                            @Override
                            public void onIncorrectPassword() {
                                Log.d("UserRepo", "Reset pet failed! Incorrect password!");

                            }
                        });

                        activityLogRepository.deleteActlog(userId, new DeleteActLogCallback() {
                            @Override
                            public void onSuccess() {
                                Log.d("UserRepo", "Delete actLog success!");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Log.d("UserRepo", "Delete actLog failed! " + e.toString());
                            }
                        });


                    } else {
                        Log.d("UserRepo", "Reset user failed! Incorrect password!");
                        callback.onIncorrectPw();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Reset user failed! No user found! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void deleteUser(String userId, String password, UpdateUserCallback callback) {
        db.collection("user").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String storedPw = documentSnapshot.getString("password");

                    if (BCrypt.checkpw(password, storedPw)) {
                        db.collection("user").document(userId)
                                .delete()
                                .addOnSuccessListener(v -> {
                                    Log.d("UserRepo", "Delete user success");

                                    petRepository = new PetRepository();
                                    activityLogRepository = new ActivityLogRepository();

                                    petRepository.deletePet(userId, new UpdatePetCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("UserRepo", "Delete pet success");
                                            callback.onSuccess();
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.d("UserRepo", "Delete pet failed! " + e.toString());
                                            callback.onFailure(e);
                                        }

                                        @Override
                                        public void onIncorrectPassword() {
                                            Log.d("UserRepo", "Delete pet failed! Incorrect password!");
                                            callback.onIncorrectPw();

                                        }
                                    });

                                    activityLogRepository.deleteActlog(userId, new DeleteActLogCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("UserRepo", "Delete actLog success!");
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.d("UserRepo", "Delete actLog failed! " + e.toString());
                                        }
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("UserRepo", "Delete user failed! " + e.toString());
                                    callback.onFailure(e);
                                });
                    } else {
                        Log.d("UserRepo", "Delete user failed! Incorrect password!");
                        callback.onIncorrectPw();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("UserRepo", "Delete user failed! No user found! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void trainingUser(User nUser, String type, int score) {
        switch (type) {

            case "Tiếng Anh":
                nUser.userStudyEnglish();
                Log.d("UserRepo", "Training success!");
                break;

            case "Kiểm tra":
                nUser.userTest(score);
                Log.d("UserRepo", "Training success!");
                break;

            case "Tự học":
            case "Yoga":
                nUser.userTimePractice(score);
                Log.d("UserRepo", "Training success!");
                break;

            case "Chạy bộ":
                nUser.userRun(score);
                Log.d("UserRepo", "Training success!");
                break;

            case "Đạp xe":
                nUser.userBicycle(score);
                Log.d("UserRepo", "Training success!");
                break;

            case "Giải trí":
                nUser.userPlayGame(score);
                Log.d("UserRepo", "Training success!");
                break;

            default:
                Log.w("ActivityProcessor", "Unknown activity type: " + type);
                Log.d("UserRepo", "Training failed!");
                break;
        }
    }
}

























