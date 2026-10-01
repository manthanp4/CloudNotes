package com.example.cloudnotes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    EditText edtTitle, edtContent;
    Button btnEditNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editNote), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtTitle = findViewById(R.id.etNoteTitle);
        edtContent = findViewById(R.id.etNoteContent);
        btnEditNote = findViewById(R.id.editNote_button);

        String documentId = getIntent().getStringExtra("noteId");
        String oldTitle = getIntent().getStringExtra("title");
        String oldContent = getIntent().getStringExtra("content");

        edtTitle.setText(oldTitle);
        edtContent.setText(oldContent);

        btnEditNote.setOnClickListener(v -> {
            String newTitle = edtTitle.getText().toString().trim();
            String newContent = edtContent.getText().toString().trim();

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(EditNoteActivity.this, "Please enter title and content", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();

            if (user == null) {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("title", newTitle);
            updates.put("content", newContent);

            db.collection("notes")
                    .document(documentId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Update Successful", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error updating note: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }
}