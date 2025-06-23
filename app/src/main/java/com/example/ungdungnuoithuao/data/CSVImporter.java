package com.example.ungdungnuoithuao.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CSVImporter {
    private static final String TAG = "CSVImporter";
    private static final int BATCH_SIZE = 500;

    public static void importCSVToFirestore(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        try {
            if (Objects.requireNonNull(context.getAssets().list("")).length == 0 || !isFileExists(context)) {
                Toast.makeText(context, "File dictionary.csv không tồn tại!", Toast.LENGTH_SHORT).show();
                return;
            }

            CSVReader reader = new CSVReader(new InputStreamReader(context.getAssets().open("dictionary.csv"))); // Khởi tạo đúng
            reader.readNext(); // Bỏ qua dòng tiêu đề
            String[] line;
            int count = 0;

            while ((line = reader.readNext()) != null) {
                if (line.length < 3) {
                    Log.w(TAG, "Dòng không hợp lệ: " + String.join(",", line));
                    continue;
                }

                Map<String, Object> wordData = new HashMap<>();
                wordData.put("word", line[0].trim());
                wordData.put("pos", line[1].trim());
                wordData.put("def", line[2].trim());

                batch.set(db.collection("dictionary").document(line[0].trim()), wordData);
                count++;

                if (count % BATCH_SIZE == 0) {
                    commitBatch(batch, count);
                    batch = db.batch();
                }
            }

            if (count % BATCH_SIZE != 0) {
                commitBatch(batch, count);
            }

            reader.close();
            Toast.makeText(context, "Nhập dữ liệu thành công: " + count + " từ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi nhập dữ liệu: " + e.getMessage(), e);
            Toast.makeText(context, "Lỗi nhập dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isFileExists(Context context) {
        try {
            context.getAssets().open("dictionary.csv").close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void commitBatch(WriteBatch batch, int count) {
        if (count % BATCH_SIZE == 0 || count > 0) {
            batch.commit().addOnSuccessListener(aVoid -> Log.d("CSVImporter", "Batch commit thành công: " + count))
                    .addOnFailureListener(e -> Log.e("CSVImporter", "Batch commit thất bại: " + e.getMessage(), e));
        }
    }
}
