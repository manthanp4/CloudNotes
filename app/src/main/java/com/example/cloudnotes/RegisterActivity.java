package com.example.cloudnotes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextView txtTitle;
    EditText edtEml, edtPaswrd;
    Button btnRgstr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        txtTitle = findViewById(R.id.tvRegistration);
        edtEml = findViewById(R.id.email_edit_text);
        edtPaswrd = findViewById(R.id.password_edit_text);
        btnRgstr = findViewById(R.id.register_button);

        btnRgstr.setOnClickListener(v -> {
            String email = edtEml.getText().toString().trim();
            String password = edtPaswrd.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Regsitration Successfull", Toast.LENGTH_SHORT).show();
                            edtEml.setText("");
                            edtPaswrd.setText("");
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Registration Failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}