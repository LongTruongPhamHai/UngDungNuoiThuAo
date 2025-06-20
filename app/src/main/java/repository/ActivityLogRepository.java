package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.ActivityLog;
import repository.callback.activitylog.AddActLogCallback;
import repository.callback.activitylog.DeleteActLogCallback;
import repository.callback.activitylog.GetActLogCallback;
import repository.callback.activitylog.GetDayActLogCallback;
import repository.callback.activitylog.GetLastActLogDateTimeCallback;

public class ActivityLogRepository {
    private FirebaseFirestore db;

    public ActivityLogRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addActlog(String userId, String datetime, String date, String time, String type, int duration, int distance, int step, int score, AddActLogCallback callback) {

        Map<String, Object> actlogData = new HashMap<>();
        actlogData.put("userid", userId);
        actlogData.put("datetime", datetime);
        actlogData.put("date", date);
        actlogData.put("time", time);
        actlogData.put("type", type);
        actlogData.put("duration", duration);
        actlogData.put("distance", distance);
        actlogData.put("step", step);
        actlogData.put("score", score);

        db.collection("activitylog").add(actlogData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ActlogRepo", "Add Actlog success!");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.d("ActlogRepo", "Add Actlog failed. " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void getActlog(String userId, String date, String time, GetActLogCallback callback) {
        db.collection("activitylog")
                .whereEqualTo("userid", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("ActlogRepo", "Get actlog success!");
                    DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                    ActivityLog actlog = doc.toObject(ActivityLog.class);
                    callback.onSuccess(actlog);
                })
                .addOnFailureListener(e -> {
                    Log.d("ActlogRepo", "Get actlog failed!");
                    callback.onFailure(e);
                });
    }

    public void getLastActLogDateTime(String userId, GetLastActLogDateTimeCallback callback) {
        db.collection("activitylog")
                .orderBy("datetime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                        String storedUId = documentSnapshot.getString("userid");
                        if(storedUId.equals(userId)) {
                            String datetime = documentSnapshot.getString("datetime");
                            Log.d("ActLogRepo", "UserID: " + storedUId + ", Datetime: " + datetime);
                            Log.d("ActLogRepo", "Get lastest actlog datetime success!");
                            callback.onSuccess(datetime);
                            break;
                        } else {
                            Log.d("ActLogRepo", "No data found!");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("ActLogRepo", "Get lastest actlog datetime failed!");
                    callback.onFailure(e);
                });
    }

    public void getDayActlog(String userId, String date, String type, GetDayActLogCallback callback) {
        db.collection("activitylog")
                .whereEqualTo("userid", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("type", type).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("ActlogRepo", "Get actlog list success!");
                    List<ActivityLog> actlogList = new ArrayList<>();
                    ActivityLog sumData = new ActivityLog();

                    int sumDistance = 0, sumStep = 0, sumDuration = 0, totalSeconds = 0;
                    for(DocumentSnapshot doc : documentSnapshots) {
                        ActivityLog data = doc.toObject(ActivityLog.class);
                        actlogList.add(data);
                        Log.d("ActlogRepo", "data: " + data.getType() + " " + data.getDuration() + " " + data.getDistance() + " " + data.getStep());

                        sumDistance += data.getDistance();
                        sumStep += data.getStep();

                        double duration = data.getDuration();
                        totalSeconds += (int) Math.round(duration);
                    }
                    int totalHours = totalSeconds / 3600;
                    int totalMinutes = (totalSeconds % 3600) / 60;
                    int totalSecs = totalSeconds % 60;
                    String totalDuration = String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSecs);

                    Log.d("ActlogRepo", "SumDistance: " + String.valueOf(sumDistance));
                    Log.d("ActlogRepo", "SumDuration: " + totalDuration);
                    Log.d("ActlogRepo", "SumStep: " + String.valueOf(sumStep));

                    Map<String, Object> sumDataList = new HashMap<>();
                    sumDataList.put("sumdistance", String.valueOf(sumDistance));
                    sumDataList.put("sumduration", totalDuration);
                    sumDataList.put("sumstep", String.valueOf(sumStep));

                    callback.onSuccess(sumDataList);
                })
                .addOnFailureListener(e -> {
                    Log.d("ActlogRepo", "Get actlog list failed!");
                    callback.onFailure(e);
                });
    }

    public void deleteActlog(String userId, DeleteActLogCallback callback) {
        db.collection("activitylog").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("ActlogRepo", "Load success");
                    for (DocumentSnapshot doc : documentSnapshots) {
                        doc.getReference().delete()
                                .addOnSuccessListener(v -> {
                                    Log.d("ActlogRepo", "Delete success");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("ActlogRepo", "Delete failed");
                                    callback.onFailure(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("ActlogRepo", "Load failed");
                    callback.onFailure(e);
                });
    }
}
