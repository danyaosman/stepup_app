package com.stepup.counter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.stepup.counter.R;
import com.stepup.counter.databinding.SignupBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class SignUp extends AppCompatActivity {
    DBHelper dbHelper;
    SignupBinding binding;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView googlebtn = findViewById(R.id.googlebtn);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        binding = SignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct!=null){
            String welcome = "Welcome " + acct.getDisplayName() + "!";
            Intent intent = new Intent(SignUp.this, Home.class);
        }

        binding.googlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        dbHelper = new DBHelper(this);

        binding.signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strNewUser = binding.newUsername.getText().toString();
                String strNewPw = binding.newPassword.getText().toString();
                if (!strNewUser.isEmpty() && !strNewPw.isEmpty()) {
                    Boolean checkUser = dbHelper.checkUser(strNewUser);
                    if (checkUser == false) {
                        if(dbHelper.insertData(strNewUser,strNewPw)){
                            Toast.makeText(SignUp.this, "Signed up successfully! Please log in.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else Toast.makeText(SignUp.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(SignUp.this, "Username already taken", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    void signIn(){
        if (gsc != null) {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent,1000);
        } else {
            Toast.makeText(SignUp.this, "Google Sign-In Client is not initialized", Toast.LENGTH_SHORT).show();
        }
    }
}
