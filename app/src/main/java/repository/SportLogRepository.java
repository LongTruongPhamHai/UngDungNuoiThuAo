package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.SportLog;
import repository.callback.sportlog.AddSpLogCallback;
import repository.callback.sportlog.DeleteSpLogCallback;
import repository.callback.sportlog.GetDaySpLogCallback;
import repository.callback.sportlog.GetSpLogCallback;

public class SportLogRepository {
    private FirebaseFirestore db;

    public SportLogRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addSpLog(String userId, String date, String time, String type, double duration, int distance, int step, AddSpLogCallback callback) {

        Map<String, Object> spLogData = new HashMap<>();
        spLogData.put("userid", userId);
        spLogData.put("date", date);
        spLogData.put("time", time);
        spLogData.put("type", type);
        spLogData.put("duration", duration);
        spLogData.put("distance", distance);
        spLogData.put("step", step);

        db.collection("sportlog").add(spLogData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("SpLogRepo", "Add SpLog success!");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.d("SpLogRepo", "Add SpLog failed. " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void getSpLog(String userId, String date, String time, GetSpLogCallback callback) {
        db.collection("sportlog")
                .whereEqualTo("userid", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("SpLogRepo", "Get splog success!");
                    DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                    SportLog spLog = doc.toObject(SportLog.class);
                    callback.onSuccess(spLog);
                })
                .addOnFailureListener(e -> {
                    Log.d("SpLogRepo", "Get splog failed!");
                    callback.onFailure(e);
                });
    }

    public void getDaySpLog(String userId, String date, String type, GetDaySpLogCallback callback) {
        db.collection("sportlog")
                .whereEqualTo("userid", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("type", type).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("SpLogRepo", "Get splog list success!");
                    List<SportLog> spLogList = new ArrayList<>();
                    SportLog sumData = new SportLog();

                    int sumDistance = 0, sumStep = 0, sumDuration = 0, totalSeconds = 0;
                    for(DocumentSnapshot doc : documentSnapshots) {
                        SportLog data = doc.toObject(SportLog.class);
                        spLogList.add(data);
                        Log.d("SpLogRepo", "data: " + data.getType() + " " + data.getDuration() + " " + data.getDistance() + " " + data.getStep());

                        sumDistance += data.getDistance();
                        sumStep += data.getStep();

//                        String duration = data.getDuration();
//                        String[] parts = duration.split(":");
//                        int hours = Integer.parseInt(parts[0]);
//                        int minutes = Integer.parseInt(parts[1]);
//                        int seconds = Integer.parseInt(parts[2]);
//
//                        totalSeconds += hours * 3600 + minutes * 60 + seconds;

                        double duration = data.getDuration();
                        totalSeconds += (int) Math.round(duration);
                    }
                    int totalHours = totalSeconds / 3600;
                    int totalMinutes = (totalSeconds % 3600) / 60;
                    int totalSecs = totalSeconds % 60;
                    String totalDuration = String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSecs);

                    Log.d("SpLogRepo", "SumDistance: " + String.valueOf(sumDistance));
                    Log.d("SpLogRepo", "SumDuration: " + totalDuration);
                    Log.d("SpLogRepo", "SumStep: " + String.valueOf(sumStep));

                    Map<String, Object> sumDataList = new HashMap<>();
                    sumDataList.put("sumdistance", String.valueOf(sumDistance));
                    sumDataList.put("sumduration", totalDuration);
                    sumDataList.put("sumstep", String.valueOf(sumStep));

                    callback.onSuccess(sumDataList);
                })
                .addOnFailureListener(e -> {
                    Log.d("SpLogRepo", "Get splog list failed!");
                    callback.onFailure(e);
                });
    }

    public void deleteSpLog(String userId, DeleteSpLogCallback callback) {
        db.collection("sportlog").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("SpLogRepo", "Load success");
                    for (DocumentSnapshot doc : documentSnapshots) {
                        doc.getReference().delete()
                                .addOnSuccessListener(v -> {
                                    Log.d("SpLogRepo", "Delete success");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("SpLogRepo", "Delete failed");
                                    callback.onFailure(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("SpLogRepo", "Load failed");
                    callback.onFailure(e);
                });
    }
}
