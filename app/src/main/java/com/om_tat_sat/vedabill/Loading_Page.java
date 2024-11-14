package com.om_tat_sat.vedabill;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.om_tat_sat.vedabill.Adapters.SimpleTextWatcher;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Loading_Page extends AppCompatActivity {

    private LottieAnimationView verifyAnim;
    private static final String TAG = "Loading_Page";
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    SharedPreferences app_language;
    int language = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading_page);

        // Initialize Firebase Auth
        Log.d(TAG, "Initializing Firebase Auth");
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Loading_Page.this, MainActivity.class));
            finish();
        }

        // Initialize SharedPreferences for language selection
        app_language = getSharedPreferences("app_language", MODE_PRIVATE);
        language = app_language.getInt("current_language", 0);

        // Initialize UI elements
        EditText phoneNumberField = findViewById(R.id.phone_number);
        EditText otpCodeField = findViewById(R.id.otp_code);
        phoneNumberField.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                findViewById(R.id.loading_page_phoneNumber).setVisibility(View.VISIBLE);
            }
        });

        verifyAnim = findViewById(R.id.verifyAnim);
        findViewById(R.id.loading_page_phoneNumber).setOnClickListener(v -> {
            String phoneNumber = "+91" + phoneNumberField.getText().toString().replace("+91","");
            verifyAnim.setVisibility(View.VISIBLE);
            findViewById(R.id.loading_page_phoneNumber).setVisibility(View.GONE);
            startPhoneNumberVerification(phoneNumber);
        });

        findViewById(R.id.verify_button).setOnClickListener(v -> {
            String code = otpCodeField.getText().toString();
            if (!code.isEmpty()) {
                verifyPhoneNumberWithCode(mVerificationId, code);
            } else {
                Toast.makeText(Loading_Page.this, this.getString(R.string.enter_otp_code), Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize callbacks for phone authentication
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                verifyAnim.setVisibility(View.GONE);
                findViewById(R.id.loading_page_phoneNumber).setVisibility(View.GONE);
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                verifyAnim.setVisibility(View.GONE);
                findViewById(R.id.loading_page_phoneNumber).setVisibility(View.VISIBLE);
                Log.w(TAG, "onVerificationFailed", e);
                Toast.makeText(Loading_Page.this, Loading_Page.this.getString(R.string.verification_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                verifyAnim.setVisibility(View.GONE);
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                findViewById(R.id.otp_code).setVisibility(View.VISIBLE);
                findViewById(R.id.loading_page_phoneNumber).setVisibility(View.GONE);
                findViewById(R.id.verify_button).setVisibility(View.VISIBLE);
                Toast.makeText(Loading_Page.this, Loading_Page.this.getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
            }
        };

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Log.d(TAG, "Applying window insets");
            return insets;
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        assert user != null;
                        Log.d(TAG, "Firebase user: " + user.getPhoneNumber());
                        // Show a toast message
                        Toast.makeText(Loading_Page.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        // Show language selection dialog
                        showLanguageSelectionDialog();
                    } else {
                        verifyAnim.setVisibility(View.GONE);
                        findViewById(R.id.loading_page_phoneNumber).setVisibility(View.VISIBLE);
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(Loading_Page.this, Loading_Page.this.getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLanguageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");

        String[] languages = {"English", "Hindi"};
        int checkedItem = language;

        builder.setSingleChoiceItems(languages, checkedItem, (dialog, which) -> language = which);

        builder.setPositiveButton("OK", (dialog, which) -> {
            SharedPreferences.Editor editor = app_language.edit();
            editor.putInt("current_language", language);
            editor.apply();
            applyLanguage();
            navigateToMainActivity();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            navigateToMainActivity();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void applyLanguage() {
        if (language == 0) {
            change_language("en");
        } else if (language == 1) {
            change_language("hi");
        }
    }

    private void navigateToMainActivity() {
        startActivity(new Intent(Loading_Page.this, MainActivity.class));
        finish();
    }

    public void change_language(String language) {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
