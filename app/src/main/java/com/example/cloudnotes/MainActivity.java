package com.example.cloudnotes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    TextView txtWelcome;
    LinearLayout notesContainer;
    Button btnAddNote, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth =  FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        txtWelcome = findViewById(R.id.tvWelcome);
        btnAddNote = findViewById(R.id.addNote_button);
        btnLogout = findViewById(R.id.logout_button);
        notesContainer = findViewById(R.id.notesContainer);

        if (user!=null) {
            txtWelcome.setText("Welcome " + user.getEmail() +" to Cloud Notes");
        }

        btnAddNote.setOnClickListener(v -> {
            //Add Notes Logic
            Intent intent = new Intent(MainActivity.this,AddNoteActivity.class);
            startActivity(intent);
            finish();
        });


        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(this, "Logged out Successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        loadNotes();
    }

    private void loadNotes() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user==null) {
            return;
        }

        String uid = user.getUid();
        notesContainer.removeAllViews();

        db.collection("notes")
                .whereEqualTo("userId",uid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        TextView emptyText = new TextView(this);
                        emptyText.setText("No notes found.");
                        emptyText.setTextSize(18);
                        notesContainer.addView(emptyText);
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String documentId = document.getId();
                            String title = document.getString("title");
                            String content = document.getString("content");

                            // Container for one note
                            LinearLayout noteLayout = new LinearLayout(this);
                            noteLayout.setOrientation(LinearLayout.VERTICAL);

                            // Note text
                            TextView noteText = new TextView(this);
                            noteText.setText("Title: " + title + "\nContent: " + content + "\n");
                            noteText.setTextSize(18);

                            // Button container
                            LinearLayout buttonLayout = new LinearLayout(this);
                            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

                            // Edit button
                            Button btnEdit = new Button(this);
                            btnEdit.setText("EDIT");

                            // Delete button
                            Button btnDelete = new Button(this);
                            btnDelete.setText("DELETE");

                            // Add buttons to button layout
                            buttonLayout.addView(btnEdit);
                            buttonLayout.addView(btnDelete);

                            // Add text and buttons to note layout
                            noteLayout.addView(noteText);
                            noteLayout.addView(buttonLayout);

                            // Add complete note to notesContainer
                            notesContainer.addView(noteLayout);

                            // Edit button logic
                            btnEdit.setOnClickListener(v -> {
                                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                                intent.putExtra("documentId", documentId);
                                intent.putExtra("title", title);
                                intent.putExtra("content", content);

                                startActivity(intent);
                            });

                            btnDelete.setOnClickListener(v -> {
                                db.collection("notes")
                                        .document(documentId)
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                                            loadNotes();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Delete Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });
                            });

                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load Notes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}