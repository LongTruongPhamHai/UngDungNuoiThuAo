package repository;

import android.util.Log;
import android.widget.Toast;

import com.example.ungdungnuoithuao.StudyActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import model.Pet;
import model.User;
import repository.callback.activitylog.AddActLogCallback;
import repository.callback.activitylog.GetLastActLogDateTimeCallback;
import repository.callback.pet.PetLoadedCallback;
import repository.callback.pet.PetSetupCallback;
import repository.callback.pet.UpdatePetCallback;
import repository.callback.user.UserLoadedCallback;

public class PetRepository {

    private FirebaseFirestore db;
    private UserRepository userRepository;

    public PetRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void registerPet(Pet newPet, PetSetupCallback callback) {
        Map<String, Object> pet = new HashMap<>();
        pet.put("petname", newPet.getPetname());
        pet.put("iqscore", newPet.getIqscore());
        pet.put("physicalscore", newPet.getPhysicalscore());
        pet.put("spiritscore", newPet.getSpiritscore());
        pet.put("userid", newPet.getUserid());

        db.collection("pet")
                .add(pet)
                .addOnSuccessListener(documentReference -> {
                    Log.d("PetRepo", "Add pet success!");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.d("PetRepo", "Add pet failed");
                    callback.onError(e);
                });
    }

    public void getPet(String userId, PetLoadedCallback callback) {
        db.collection("pet").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Pet nPet = queryDocumentSnapshots.getDocuments().get(0).toObject(Pet.class);
                        Log.d("PetRepo", "Load pet success");
                        callback.onPetLoaded(nPet);
                    } else {
                        Log.d("PetRepo", "Load no pet data");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("PetRepo", "Load pet failed");
                    callback.onFailure(e);
                });
    }

    public void updatePet(String userId, Pet nPet, String password, UpdatePetCallback callback) {
        userRepository = new UserRepository();
        userRepository.getUser(userId, new UserLoadedCallback() {
            @Override
            public void onUserLoaded(User nUser) {
                String storedPw = nUser.getPassword();

                if (BCrypt.checkpw(password, storedPw)) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("petname", nPet.getPetname());
                    data.put("iqscore", nPet.getIqscore());
                    data.put("physicalscore", nPet.getPhysicalscore());
                    data.put("spiritscore", nPet.getSpiritscore());

                    db.collection("pet").whereEqualTo("userid", userId).get()
                            .addOnSuccessListener(documentSnapshots -> {
                                DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                                doc.getReference().update(data)
                                        .addOnSuccessListener(v -> {
                                            Log.d("PetRepo", "Update pet success");
                                            callback.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.d("PetRepo", "Update pet failed! " + e.toString());
                                            callback.onFailure(e);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Log.d("PetRepo", "Update pet failed! No pet found! " + e.toString());
                                callback.onFailure(e);
                            });
                } else {
                    Log.d("PetRepo", "Incorrect password!");
                    callback.onIncorrectPassword();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("PetRepo", "Update pet failed! No user found! " + e.toString());
                callback.onFailure(e);
            }
        });
    }

    public void updatePetStat(String userId, Pet nPet, UpdatePetCallback callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("iqscore", nPet.getIqscore());
        data.put("physicalscore", nPet.getPhysicalscore());
        data.put("spiritscore", nPet.getSpiritscore());

        db.collection("pet").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                    doc.getReference().update(data)
                            .addOnSuccessListener(v -> {
                                Log.d("PetRepo", "Update pet stat success");
                                callback.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.d("PetRepo", "Update pet stat failed! " + e.toString());
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("PetRepo", "Update pet failed! No pet found! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void resetPet(String userId, UpdatePetCallback callback) {
        db.collection("pet").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    DocumentSnapshot petDoc = documentSnapshots.getDocuments().get(0);
                    String petId = petDoc.getId();
                    Map<String, Object> data = new HashMap<>();
                    data.put("iqscore", 50);
                    data.put("physicalscore", 50);
                    data.put("spiritscore", 50);

                    db.collection("pet").document(petId).update(data)
                            .addOnSuccessListener(v -> {
                                Log.d("PetRepo", "Reset pet success!");
                                callback.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.d("PetRepo", "Reset pet failed! " + e.toString());
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("PetRepo", "Reset pet failed! No pet found! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void deletePet(String userId, UpdatePetCallback callback) {
        db.collection("pet").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    DocumentSnapshot petDoc = documentSnapshots.getDocuments().get(0);
                    petDoc.getReference().delete()
                            .addOnSuccessListener(v -> {
                                Log.d("PetRepo", "Delete pet success!");
                                callback.onSuccess();
                            })
                            .addOnFailureListener(e -> {
                                Log.d("PetRepo", "Delete pet failed! " + e.toString());
                                callback.onFailure(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.d("PetRepo", "Delete pet failed! No pet found! " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void trainingPet(Pet nPet, String type, int score) {
        switch (type) {
            case "Tiếng Anh":
                nPet.petEnglish();
                Log.d("PetRepo", "Training success!");
                break;

            case "Kiểm tra":
                nPet.petTest(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "Tự học":
                nPet.petSelfStudy(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "Chạy bộ":
                nPet.petRun(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "Đạp xe":
                nPet.petBicycle(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "Yoga":
                nPet.petYoga(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "Giải trí":
                nPet.petPlayGame(score);
                Log.d("PetRepo", "Training success!");
                break;

            default:
                Log.w("ActivityProcessor", "Unknown activity type: " + type);
                break;
        }
    }

    public void checkOfflineTime(String userId) {
        ActivityLogRepository activityLogRepository = new ActivityLogRepository();
        activityLogRepository.getLastActLogDateTime(userId, new GetLastActLogDateTimeCallback() {
            @Override
            public void onSuccess(String datetime) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date oldDate = format.parse(datetime);

                    Date now = new Date();

                    long diffInMillis = now.getTime() - oldDate.getTime();

                    if (diffInMillis > 24 * 60 * 60 * 1000) {
                        Log.d("PetRepo", "Quá 24 giờ");
                        Log.d("PetRepo", "UserId: " + userId);
                        getPet(userId, new PetLoadedCallback() {
                            @Override
                            public void onPetLoaded(Pet nPet) {
                                nPet.petOffline();
                                Map<String, Object> data = new HashMap<>();
                                data.put("iqscore", nPet.getIqscore());
                                data.put("physicalscore", nPet.getPhysicalscore());
                                data.put("spiritscore", nPet.getSpiritscore());

                                updatePetStat(userId, nPet, new UpdatePetCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("PetRepo", "Update pet stat success");

                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                        String addDate = dateFormat.format(new Date());

                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                                        String addTime = timeFormat.format(new Date());

                                        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                        String datetime = fullFormat.format(new Date());

                                        ActivityLogRepository activityLogRepository = new ActivityLogRepository();
                                        activityLogRepository.addActlog(userId, datetime, addDate, addTime, "Offline", 0, 0, 0, 0, new AddActLogCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.d("PetRepo", "Add act log success!");
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("PetRepo", "Update pet stat failed!");
                                    }

                                    @Override
                                    public void onIncorrectPassword() {
                                        Log.d("PetRepo", "Update pet stat failed!");
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception errorMessage) {
                                Log.d("PetRepo", "Update pet stat failed!");
                            }
                        });
                    } else {
                        Log.d("PetRepo", "Chưa 24 giờ");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d("PetRepo", "Lỗi parse");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("PetRepo", "Lỗi " + e.toString());
            }
        });
    }

//    public void petOffline(String userId) {
//        Log.d("PetRepo", "UserId: " + userId);
//        getPet(userId, new PetLoadedCallback() {
//            @Override
//            public void onPetLoaded(Pet nPet) {
//                nPet.petOffline();
//                Map<String, Object> data = new HashMap<>();
//                data.put("iqscore", nPet.getIqscore());
//                data.put("physicalscore", nPet.getPhysicalscore());
//                data.put("spiritscore", nPet.getSpiritscore());
//
//                db.collection("pet").whereEqualTo("userid", userId).get()
//                        .addOnSuccessListener(documentSnapshots -> {
//                            DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
//                            doc.getReference().update(data)
//                                    .addOnSuccessListener(v -> {
//                                        Log.d("PetRepo", "Update pet stat success");
//
//                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//                                        String addDate = dateFormat.format(new Date());
//
//                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//                                        String addTime = timeFormat.format(new Date());
//
//                                        SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//                                        String datetime = fullFormat.format(new Date());
//
//                                        ActivityLogRepository activityLogRepository = new ActivityLogRepository();
//                                        activityLogRepository.addActlog(userId, datetime, addDate, addTime, "Offline", 0, 0, 0, 0, new AddActLogCallback() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("StudyAct", "Add act log success!");
//                                            }
//
//                                            @Override
//                                            public void onFailure(Exception e) {
//                                            }
//                                        });
//                                    })
//                                    .addOnFailureListener(e -> {
//                                        Log.d("PetRepo", "Update pet stat failed! " + e.toString());
//
//                                    });
//                        })
//                        .addOnFailureListener(e -> {
//                            Log.d("PetRepo", "Update pet failed! No pet found! " + e.toString());
//                        });
//            }
//
//            @Override
//            public void onFailure(Exception errorMessage) {
//
//            }
//        });
}
