package com.example.cloudnotes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    EditText edtNoteTitle, edtNoteContent;
    Button btnSaveNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addNote), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtNoteTitle = findViewById(R.id.etNoteTitle);
        edtNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.saveNote_button);

        btnSaveNote.setOnClickListener(v -> {
            String title = edtNoteTitle.getText().toString().trim();
            String content = edtNoteContent.getText().toString().trim();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please enter Title and Content", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser usr = mAuth.getCurrentUser();
            if (usr == null) {
                Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = usr.getUid();
            Map<String, Object> note = new HashMap<>();
            note.put("title",title);
            note.put("content",content);
            note.put("userId",usr.getUid());
            note.put("createdAt", FieldValue.serverTimestamp());

            db.collection("notes")
                    .add(note)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show();
                        edtNoteTitle.setText("");
                        edtNoteContent.setText("");
                        Intent intent = new Intent(AddNoteActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }
}