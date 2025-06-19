package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import model.DayChallenge;
import repository.callback.daychallenge.DayChallengeLoadedCallback;

public class DayChallengeRepository {
    private FirebaseFirestore db;

    public DayChallengeRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void setDayChallenge() {
        List<String> challengeId = Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08");
        Collections.shuffle(challengeId);
        List<String> random3Task = challengeId.subList(0, 3);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        db.collection("daychallenge").whereEqualTo("datechallenge", todayStr).get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        DayChallenge daytask = new DayChallenge(todayStr, random3Task);
                        Log.d("DayChallengeRepo", "Set day challenge success");
                        db.collection("daychallenge").add(daytask);
                    } else {
                        Log.d("DayChallengeRepo", "Today challenge has been added");
                    }
                });
    }

    public void getDayChallenge(DayChallengeLoadedCallback callback) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayStr = sdf.format(new Date());

        ChallengeRepository challengeRepository = new ChallengeRepository();

        db.collection("daychallenge").whereEqualTo("datechallenge", todayStr).get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (!documentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                        List<String> challengeIdList = (List<String>) doc.get("challengeidlist");
                        Log.d("DayChallengeRepo", "Get day challenge success");
                        callback.onSuccess(challengeIdList);
                    } else {
                        Log.d("DayChallengeRepo", "Day challenge empty");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("DayChallengeRepo", "Get day challenge failed");
                    callback.onFailure(e);
                });
    }
}


























