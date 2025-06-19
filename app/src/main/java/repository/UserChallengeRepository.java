package repository;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

import model.Challenge;
import repository.callback.challenge.ChallengeLoadedCallback;
import repository.callback.daychallenge.DayChallengeLoadedCallback;

public class UserChallengeRepository {

    private FirebaseFirestore db;

    public UserChallengeRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addUChallenge(String userId, String date, String type) {

    }

    public void getUChallenge(String userId, String date, String type) {
        DayChallengeRepository dayChallengeRepository = new DayChallengeRepository();
        ChallengeRepository challengeRepository = new ChallengeRepository();
        SportLogRepository sportLogRepository = new SportLogRepository();

        dayChallengeRepository.getDayChallenge(new DayChallengeLoadedCallback() {
            @Override
            public void onSuccess(List<String> challengeList) {
                for (String id : challengeList) {
                    challengeRepository.getChallenge(id, new ChallengeLoadedCallback() {
                        @Override
                        public void onSuccess(Challenge challenge) {
                            if (Objects.equals(challenge.getChallengetype(), type)) {
//                                sportLogRepository.getDaySpLog(userId, date, type, new GetDaySpLogCallback() {
//                                    @Override
//                                    public void onSuccess(List<SportLog> spLogList) {
//                                        Log.d("UCRepo", "ChallengeID: " + id);
//                                        Log.d("UCRepo", "Act type: " + type);
//                                        Log.d("UCRepo", "Challenge type: " + challenge.getChallengetype());
//                                        int totalSeconds = 0;
//
//                                        for (SportLog doc : spLogList) {
//                                            Log.d("UCRepo", "data: " + doc.getUserid() + " "
//                                                    + doc.getDate() + " "
//                                                    + doc.getTime() + " "
//                                                    + doc.getDuration() + " "
//                                                    + doc.getStep() + " "
//                                                    + doc.getDistance() + " "
//                                                    + doc.getType());
//
//                                            String duration = doc.getDuration();
//                                            String[] parts = duration.split(":");
//                                            int hours = Integer.parseInt(parts[0]);
//                                            int minutes = Integer.parseInt(parts[1]);
//                                            int seconds = Integer.parseInt(parts[2]);
//                                            totalSeconds += hours * 3600 + minutes * 60 + seconds;
//                                        }
//
//                                        int totalHours = totalSeconds / 3600;
//                                        int totalMinutes = (totalSeconds % 3600) / 60;
//                                        int totalSecs = totalSeconds % 60;
//
//                                        String totalDuration = String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSecs);
//                                        Log.d("UCRepo", "Total duration: " + totalDuration + " (" + totalSeconds + " seconds)");
//                                    }
//
//                                    @Override
//                                    public void onFailure(Exception e) {
//
//                                    }
//                                });
                            } else {
                                Log.d("UCRepo", "No challenge for " + type);
                            }
                        }

                        @Override
                        public void onError(Exception errorMessage) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception errorMessage) {

            }
        });

//        db.collection("sportlog")
//                .whereEqualTo("userid", userId)
//                .whereEqualTo("date", date)
//                .whereEqualTo("type", type)
//                .get()
//                .addOnSuccessListener(documentSnapshots -> {
//                    for (DocumentSnapshot doc : documentSnapshots) {
//                        Log.d("UCRepo", "Doc: " + doc.getString("type"));
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Log.d("UCRepo", "Load data failed");
//                });
    }

}
