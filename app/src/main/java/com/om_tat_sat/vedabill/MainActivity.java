package com.om_tat_sat.vedabill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.om_tat_sat.vedabill.Adapters.PdfAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addNewInvoice;
    RecyclerView pdfList;
    PdfAdapter pdfAdapter;
    Set<File> pdfFilesSet;
    List<File> pdfFiles;
    FirebaseAuth mAuth;
    SharedPreferences app_language;
    int language = 0;
    DatabaseReference databaseReferencePdfs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load and apply language setting before setting the content view
        app_language = getSharedPreferences("app_language", MODE_PRIVATE);
        language = app_language.getInt("current_language", 0);
        applyLanguage();

        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pdfList = findViewById(R.id.pdf_list);
        pdfList.setLayoutManager(new LinearLayoutManager(this));
        mAuth = FirebaseAuth.getInstance();
        addNewInvoice = findViewById(R.id.add_new_invoice);
        addNewInvoice.setOnClickListener(v -> {
            // Open addNewInvoiceData activity
            Intent intent = new Intent(MainActivity.this, addNewInvoiceData.class);
            startActivity(intent);
        });
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, Loading_Page.class));
            finish();
        }

        loadPdfFilesAndSetAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_language) {
            // Handle language change here if needed
            return true;
        } else if (id == R.id.action_logout) {
            // Logout
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Loading_Page.class));
            finish();
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPdfFilesAndSetAdapter() {
        pdfFilesSet = new HashSet<>(loadPdfFiles());

        // Fetch PDF files from Firebase database
        databaseReferencePdfs = FirebaseDatabase.getInstance().getReference("VedaBills").child(mAuth.getUid()).child("pdfs");
        databaseReferencePdfs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot pdfSnapshot : snapshot.getChildren()) {
                        String fileName = pdfSnapshot.getValue(String.class);
                        File pdfFile = new File(getExternalFilesDir(null), fileName);
                        pdfFilesSet.add(pdfFile);
                    }
                }
                // Convert Set to List and sort the files by last modified date in descending order
                pdfFiles = new ArrayList<>(pdfFilesSet);
                pdfFiles.sort((file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));

                // Set adapter after loading all PDF files
                pdfAdapter = new PdfAdapter(MainActivity.this, pdfFiles, mAuth.getUid() != null ? mAuth.getUid() : "uuid");
                pdfList.setAdapter(pdfAdapter);
                Log.e("PDF Files Loaded", pdfFiles.toString());
                Log.e("Adapter Set", "PDF Adapter is set with " + pdfFiles.size() + " items.");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("DatabaseError", "Error fetching PDF files from database: " + error.getMessage());
            }
        });
    }

    private List<File> loadPdfFiles() {
        // Load PDF files from the external files directory
        List<File> files = new ArrayList<>();
        File dir = getExternalFilesDir(null);
        Log.e("loadPdfFiles: Directory", (dir != null) ? dir.toString() : "Directory is null");

        if (dir != null && dir.exists()) {
            File[] pdfFiles = dir.listFiles((d, name) -> name.endsWith(".pdf"));
            if (pdfFiles != null) {
                files.addAll(Arrays.asList(pdfFiles));
            }
        }
        return files;
    }

    private void applyLanguage() {
        if (language == 0) {
            changeLanguage("en");
        } else if (language == 1) {
            changeLanguage("hi");
        }
    }

    private void changeLanguage(String language) {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }
}
