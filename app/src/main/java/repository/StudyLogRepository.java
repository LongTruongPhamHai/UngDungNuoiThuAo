package repository;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.StudyLog;
import model.StudyLog;
import repository.callback.studylog.AddStLogCallback;
import repository.callback.studylog.DeleteStLogCallback;
import repository.callback.studylog.GetDayStLogCallback;
import repository.callback.studylog.GetStLogCallback;

public class StudyLogRepository {
    private FirebaseFirestore db;

    public StudyLogRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void addStLog(String userId, String date, String time, String type, double duration, int score, AddStLogCallback callback) {

        Map<String, Object> stLogData = new HashMap<>();
        stLogData.put("userid", userId);
        stLogData.put("date", date);
        stLogData.put("time", time);
        stLogData.put("type", type);
        stLogData.put("duration", duration);
        stLogData.put("score", score);

        db.collection("studylog").add(stLogData)
                .addOnSuccessListener(documentReference -> {
                    Log.d("StLogRepo", "Add StLog success!");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.d("StLogRepo", "Add StLog failed. " + e.toString());
                    callback.onFailure(e);
                });
    }

    public void getStLog(String userId, String date, String time, GetStLogCallback callback) {
        db.collection("studylog")
                .whereEqualTo("userid", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("time", time).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("StLogRepo", "Get stLog success!");
                    DocumentSnapshot doc = documentSnapshots.getDocuments().get(0);
                    StudyLog stLog = doc.toObject(StudyLog.class);
                    callback.onSuccess(stLog);
                })
                .addOnFailureListener(e -> {
                    Log.d("StLogRepo", "Get stLog failed!");
                    callback.onFailure(e);
                });
    }

    public void getDayStLog(String userId, String date, String type, GetDayStLogCallback callback) {
        db.collection("studylog")
                .whereEqualTo("userid", userId)
                .whereEqualTo("date", date)
                .whereEqualTo("type", type).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("StLogRepo", "Get stLog list success!");
                    List<StudyLog> stLogList = new ArrayList<>();
                    StudyLog sumData = new StudyLog();

                    int totalSeconds = 0;
                    double totalScore = 0, dataSize = documentSnapshots.size();
                    for(DocumentSnapshot doc : documentSnapshots) {
                        StudyLog data = doc.toObject(StudyLog.class);
                        stLogList.add(data);
                        Log.d("StLogRepo", "data: " + data.getType() + " " + data.getDuration() + " " + data.getScore());

                        totalScore += data.getScore();
                        double duration = data.getDuration();
                        totalSeconds += (int) Math.round(duration);
                    }
                    double avgScore = totalScore / dataSize;
                    int totalHours = totalSeconds / 3600;
                    int totalMinutes = (totalSeconds % 3600) / 60;
                    int totalSecs = totalSeconds % 60;
                    String totalDuration = String.format("%02d:%02d:%02d", totalHours, totalMinutes, totalSecs);

                    Log.d("StLogRepo", "SumDuration: " + totalDuration);
                    Log.d("StLogRepo", "AvgScore: " + String.format("%.2f", avgScore));

                    Map<String, Object> sumDataList = new HashMap<>();
                    sumDataList.put("sumduration", totalDuration);
                    sumDataList.put("avgscore", String.format("%.2f", avgScore));

                    callback.onSuccess(sumDataList);
                })
                .addOnFailureListener(e -> {
                    Log.d("StLogRepo", "Get stLog list failed!");
                    callback.onFailure(e);
                });
    }

    public void deleteStLog(String userId, DeleteStLogCallback callback) {
        db.collection("studylog").whereEqualTo("userid", userId).get()
                .addOnSuccessListener(documentSnapshots -> {
                    Log.d("StLogRepo", "Load success");
                    for (DocumentSnapshot doc : documentSnapshots) {
                        doc.getReference().delete()
                                .addOnSuccessListener(v -> {
                                    Log.d("StLogRepo", "Delete success");
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("StLogRepo", "Delete failed");
                                    callback.onFailure(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("StLogRepo", "Load failed");
                    callback.onFailure(e);
                });
    }
}
