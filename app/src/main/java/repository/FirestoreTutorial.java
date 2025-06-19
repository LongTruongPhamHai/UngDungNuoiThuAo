package repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import model.ModelClass;

public class FirestoreTutorial {

    private FirebaseFirestore db;

    public FirestoreTutorial() {
        db = FirebaseFirestore.getInstance();
    }

    public void addData() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", "abc");
        data.put("email", "abc@example.com");
        data.put("password", "hashed_password");

        db.collection("users") // tên collection
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Thêm thành công với ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Lỗi khi thêm tài liệu", e);
                });
    }

    public void replaceData() {
        Map<String, Object> data = new HashMap<>();
        data.put("username", "abc");
        data.put("email", "abc@example.com");
        data.put("password", "hashed_password");
        String userId = "user123";

        db.collection("users").document(userId)
                .set(data) // ghi đè toàn bộ tài liệu
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void readData() {
        db.collection("users").document("user123")
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String username = document.getString("username");
                    }
                });

        // Nhieu data
        db.collection("users").document("user123")
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String username = document.getString("username");
                    }
                });
    }

    public void updateData() {
        db.collection("users").document("user123")
                .update("level", 2, "exp", 100)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void deleteData() {
        db.collection("users").document("user123")
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void deleteField() {
        db.collection("users").document("user123")
                .update("exp", FieldValue.delete());
    }

    // Su dung Class

    public void addDataWithClass() {
        ModelClass data = new ModelClass("001", "ABC", 10);
        // Tu set id
        db.collection("users").document("01").set(data);
        // Id tu sinh
        db.collection("users").add(data);
    }

    public void readDataWithClass() {
        db.collection("users")
                .document("01")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ModelClass model = documentSnapshot.toObject(ModelClass.class);
                        // sử dụng model...
                    }
                });
    }

    public  void updateDataWithClass() {
        // Cach 1
        db.collection("users")
                .document("01")
                .update("name", "Updated Name", "value", 123);

        // Cach 2
        ModelClass updated = new ModelClass("01", "Updated Name", 123);
        db.collection("users").document("01").set(updated);
    }

    public void deleteDataWithClass() {
        db.collection("users")
                .document("01")
                .delete();
    }
}
