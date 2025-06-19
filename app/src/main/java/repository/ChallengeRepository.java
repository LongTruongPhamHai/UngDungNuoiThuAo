package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import model.Challenge;
import repository.callback.challenge.ChallengeLoadedCallback;

public class ChallengeRepository {

    private FirebaseFirestore db;

    public ChallengeRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addChallenges() {
        List<Challenge> challenges = new ArrayList<>();

        challenges.add(new Challenge("Walk 1.000 steps", "walk", "step", 1000));
        challenges.add(new Challenge("Walk for 60 minutes", "walk", "step", 60));
        challenges.add(new Challenge("Run 2 km", "run", "distance", 2));
        challenges.add(new Challenge("Run for 30 minutes", "run", "duration", 30));
        challenges.add(new Challenge("Ride your bike for 5km", "cycle", "distance", 5));
        challenges.add(new Challenge("Ride your bike for 30 minutes", "cycle", "duration", 30));
        challenges.add(new Challenge("Do bodyweight exercises for 60 minutes", "bodyweight", "duration", 60));
        challenges.add(new Challenge("Do yoga for 30 minutes", "yoga", "duration", 30));
        challenges.add(new Challenge("Do a 30 minutes aerobic session", "aerobic", "duration", 30));

        for (int i = 0; i < challenges.size(); i++) {
            Challenge c = challenges.get(i);
            String id = String.format("%02d", i);
            c.setChallengeid(id);

            db.collection("challenge")
                    .document(id)
                    .set(c)
                    .addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }

    public void getChallenge(String challengeId, ChallengeLoadedCallback callback) {
        db.collection("challenge").whereEqualTo("challengeid", challengeId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                    Challenge challenge = doc.toObject(Challenge.class);
                    Log.d("ChallengeRepo", "Get challenge success!");
                    callback.onSuccess(challenge);
                })
                .addOnFailureListener(e -> {
                    Log.d("ChallengeRepo", "Get challenge failed!");
                    callback.onError(e);
                });
    }

//    public void checkChallenge(String userId, String challengeId) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        String date = sdf.format(new Date());
//
//        db.collection("challenge").document(challengeId).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    String type = documentSnapshot.getString("challengetype"),
//                            unit = documentSnapshot.getString("challengeunit");
//                    Long max = documentSnapshot.getLong("challengemax");
////                    Log.d("ChRepo", "type: " + type + "\nunit: " + unit + "\nmax: " + max);
//
//                    db.collection("sportlog")
//                            .whereEqualTo("userid", userId)
//                            .whereEqualTo("date", date)
//                            .whereEqualTo("type", type)
//                            .get()
//                            .addOnSuccessListener(documentSnapshots -> {
//                                Long progress = 0L;
//                                for (DocumentSnapshot doc : documentSnapshots) {
////                                    SportLog data = doc.toObject(SportLog.class);
////                                    Log.d("ChRepo", "SpLog type: " + data.getType());
////                                    double value = data.getDuration();
//                                    Log.d("ChRepo", "Progress: " + String.valueOf(doc.get("duration")));
////                                    progress += value;
////                                    Log.d("ChRepo", "progress: " + String.valueOf(progress));
//                                }
//                                Log.d("ChRepo", "Type: " + type + ", Progress: " + String.valueOf(progress) + " " + unit);
//                            });
//                });
//    }
}
