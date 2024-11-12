package com.om_tat_sat.vedabill;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Loading_Page extends AppCompatActivity {

    private static final String TAG = "Loading_Page";
    private static final int RC_SIGN_IN = 9001;  // Request code for Google Sign-In
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_page);

        // Initialize Firebase Auth
        Log.d(TAG, "Initializing Firebase Auth");
        mAuth = FirebaseAuth.getInstance();

        // Setup Google Sign-In options

        Log.d(TAG, "Setting up Google Sign-In options");
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken()
//                .requestEmail()
//                .build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                  .requestIdToken()
                  .requestEmail()
                  .build();
        // Build a GoogleSignInClient with the options specified by gso.
        Log.d(TAG, "Building GoogleSignInClient");
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Log.d(TAG, "Applying window insets");
            return insets;
        });

        // Set up Google Sign-In button click listener
        Log.d(TAG, "Setting up Google Sign-In button click listener");
        findViewById(R.id.loading_page_google).setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Log.d(TAG, "Google Sign-In button clicked");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        Log.d(TAG, "Starting Google Sign-In intent");
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "Handling Google Sign-In result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            Log.w(TAG, "Unhandled requestCode: " + requestCode);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.d(TAG, "Google Sign-In successful: " + account.getEmail());
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "Google Sign-In failed with status code: " + e.getStatusCode(), e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        Log.d(TAG, "Authenticating with Firebase using Google token");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Firebase user: " + user.getEmail());
                        // Show a toast message
                        Toast.makeText(Loading_Page.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        // Show a toast message
                        Toast.makeText(Loading_Page.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
