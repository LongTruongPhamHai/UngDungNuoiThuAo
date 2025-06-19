package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

import model.Pet;
import model.User;
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

//                db.collection("user").document(userId).get()
//                        .addOnSuccessListener(documentSnapshot -> {
//                            Log.d("UserRepo", "Load user success");
//                            String storedPw = documentSnapshot.getString("password");
//
//
//                            if (BCrypt.checkpw(password, storedPw)) {
//                                Map<String, Object> data = new HashMap<>();
//                                data.put("petname", nPet.getPetname());
//                                data.put("iqscore", nPet.getIqscore());
//                                data.put("physicalscore", nPet.getPhysicalscore());
//                                data.put("spiritscore", nPet.getSpiritscore());
//
//                                db.collection("pet").whereEqualTo("userid", userId).get()
//                                        .addOnSuccessListener(documentSnapshots -> {
//                                            DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
//                                            doc.getReference().update(data)
//                                                    .addOnSuccessListener(v -> {
//                                                        Log.d("PetRepo", "Update pet success");
//                                                        callback.onSuccess();
//                                                    })
//                                                    .addOnFailureListener(e -> {
//                                                        Log.d("PetRepo", "Update pet failed! " + e.toString());
//                                                        callback.onFailure(e);
//                                                    });
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            Log.d("PetRepo", "Update pet failed! No pet found! " + e.toString());
//                                            callback.onFailure(e);
//                                        });
//                            }
//                        })
//                        .addOnFailureListener(e -> {
//
//                        })

    //        db.collection("pet").whereEqualTo("userid", userId).get()
//                .addOnSuccessListener(documentSnapshots -> {
//                    DocumentSnapshot petDoc = documentSnapshots.getDocuments().get(0);
//                    String petId = petDoc.getId();
//                    Map<String, Object> data = new HashMap<>();
//                    data.put("petname", nPet.getPetname());
//                    data.put("iqscore", nPet.getIqscore());
//                    data.put("physicalscore", nPet.getPhysicalscore());
//                    data.put("spiritscore", nPet.getPhysicalscore());
//
//                    db.collection("pet").document(petId).update(data)
//                            .addOnSuccessListener(v -> {
//                                Log.d("PetRepo", "Update pet success");
//                                callback.onSuccess();
//                            })
//                            .addOnFailureListener(e -> {
//                                Log.d("PetRepo", "Update pet failed! " + e.toString());
//                                callback.onFailure(e);
//                            });
//                })
//                .addOnFailureListener(e -> {
//                    Log.d("PetRepo", "Update pet failed! No pet found! " + e.toString());
//                    callback.onFailure(e);
//                });

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
            case "run":
                nPet.petRun(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "cycle":
                nPet.petBicycle(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "walk":
                nPet.petWalk(score);
                Log.d("PetRepo", "Training success!");
                break;

            case "yoga":
            case "bodyweight":
            case "aerobic":
                nPet.petTimeSport(score);
                Log.d("PetRepo", "Training success!");
                break;

            default:
                Log.w("ActivityProcessor", "Unknown activity type: " + type);
                break;
        }
    }
}
