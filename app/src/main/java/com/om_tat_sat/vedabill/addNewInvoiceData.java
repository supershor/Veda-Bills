package com.om_tat_sat.vedabill;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class addNewInvoiceData extends AppCompatActivity {

    private AutoCompleteTextView senderCompanyName;
    private AutoCompleteTextView senderGSTNumber;
    private AutoCompleteTextView senderAddress;
    private AutoCompleteTextView senderTelephone;
    private AutoCompleteTextView senderEmail;
    private AutoCompleteTextView placeOfSupply;
    private AutoCompleteTextView invoiceNumber;
    private DatePicker invoiceDatePicker;
    private Button generateRandomNumber;
    private AutoCompleteTextView billedToCompanyName;
    private AutoCompleteTextView billedToGSTNumber;
    private AutoCompleteTextView billedToAddress;
    private AutoCompleteTextView shippedToCompanyName;
    private AutoCompleteTextView shippedToGSTNumber;
    private AutoCompleteTextView shippedToAddress;
    private AutoCompleteTextView cifNumber;
    private AutoCompleteTextView branchName;
    private AutoCompleteTextView accountNumber;
    private AutoCompleteTextView accountType;
    private AutoCompleteTextView ifscCode;
    private DatabaseReference databaseReferenceItems;
    private DatabaseReference databaseReferenceInformation;
    private DatabaseReference databaseReferenceBankDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_invoice_data);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        senderCompanyName = findViewById(R.id.senderCompanyName);
        senderGSTNumber = findViewById(R.id.senderGSTNumber);
        invoiceDatePicker = findViewById(R.id.invoiceDatePicker);
        senderAddress = findViewById(R.id.senderAddress);
        senderTelephone = findViewById(R.id.senderTelephone);
        senderEmail = findViewById(R.id.senderEmail);
        placeOfSupply = findViewById(R.id.placeOfSupply);
        invoiceNumber = findViewById(R.id.invoiceNumber);
        generateRandomNumber = findViewById(R.id.generateRandomNumber);
        billedToCompanyName = findViewById(R.id.billedToCompanyName);
        billedToGSTNumber = findViewById(R.id.billedToGSTNumber);
        billedToAddress = findViewById(R.id.billedToAddress);
        shippedToCompanyName = findViewById(R.id.shippedToCompanyName);
        shippedToGSTNumber = findViewById(R.id.shippedToGSTNumber);
        shippedToAddress = findViewById(R.id.shippedToAddress);
        cifNumber = findViewById(R.id.cifNumber);
        branchName = findViewById(R.id.branchName);
        accountNumber = findViewById(R.id.accountNumber);
        accountType = findViewById(R.id.accountType);
        ifscCode = findViewById(R.id.ifscCode);

        generateRandomNumber.setOnClickListener(view -> {
            String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
            String randomInvoiceNumber = "INV-" + currentDate+"-"+Math.round(Math.random()*10000);
            invoiceNumber.setText(randomInvoiceNumber);
        });

        String userId = FirebaseAuth.getInstance().getUid()==null?"uuid":FirebaseAuth.getInstance().getUid();
        databaseReferenceInformation = FirebaseDatabase.getInstance().getReference("VedaBills/" + userId + "/sendersReciversData");
        databaseReferenceBankDetails = FirebaseDatabase.getInstance().getReference("VedaBills/" + userId + "/BankDetails");

        databaseReferenceInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> companyNames = new ArrayList<>();
                ArrayList<String> gstNumbers = new ArrayList<>();
                ArrayList<String> addresses = new ArrayList<>();
                ArrayList<String> telephones = new ArrayList<>();
                ArrayList<String> emails = new ArrayList<>();
                HashMap<String, HashMap<String, String>> dataMap = new HashMap<>();
                ArrayList<String> cifNumbers = new ArrayList<>();
                ArrayList<String> branchNames = new ArrayList<>();
                ArrayList<String> accountNumbers = new ArrayList<>();
                ArrayList<String> accountTypes = new ArrayList<>();
                ArrayList<String> ifscCodes = new ArrayList<>();
                HashMap<String, HashMap<String, String>> bankDataMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("onDataChange: ", snapshot.toString());
                    String companyName = snapshot.child("companyName").getValue(String.class);
                    String companyAddress = snapshot.child("companyAddress").getValue(String.class);
                    String gstIn = snapshot.child("gstIn").getValue(String.class);
                    String telephone = snapshot.child("tel").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);


                    companyNames.add(companyName);
                    gstNumbers.add(gstIn);
                    addresses.add(companyAddress);
                    telephones.add(telephone);
                    emails.add(email);

                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("companyName", companyName);
                    temp.put("companyAddress", companyAddress);
                    temp.put("gstIn", gstIn);
                    temp.put("tel", telephone);
                    temp.put("email", email);
                    dataMap.put(companyName, temp);
                }

                ArrayAdapter<String> companyAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, companyNames);
                ArrayAdapter<String> gstAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, gstNumbers);
                ArrayAdapter<String> addressAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, addresses);
                ArrayAdapter<String> telephoneAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, telephones);
                ArrayAdapter<String> emailAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, emails);
                ArrayAdapter<String> billedCompanyAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, companyNames);
                ArrayAdapter<String> billedGSTAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, gstNumbers);
                ArrayAdapter<String> billedAddressAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, addresses);
                ArrayAdapter<String> shippedCompanyAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, companyNames);
                ArrayAdapter<String> shippedGSTAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, gstNumbers);
                ArrayAdapter<String> shippedAddressAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, addresses);
                senderCompanyName.setAdapter(companyAdapter);
                senderGSTNumber.setAdapter(gstAdapter);
                senderAddress.setAdapter(addressAdapter);
                senderTelephone.setAdapter(telephoneAdapter);
                senderEmail.setAdapter(emailAdapter);
                billedToCompanyName.setAdapter(billedCompanyAdapter);
                billedToGSTNumber.setAdapter(billedGSTAdapter);
                billedToAddress.setAdapter(billedAddressAdapter);
                shippedToCompanyName.setAdapter(shippedCompanyAdapter);
                shippedToGSTNumber.setAdapter(shippedGSTAdapter);
                shippedToAddress.setAdapter(shippedAddressAdapter);

                senderCompanyName.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedCompanyName = companyAdapter.getItem(position);
                    if (dataMap.containsKey(selectedCompanyName)) {
                        senderGSTNumber.setText(dataMap.get(selectedCompanyName).get("gstIn"));
                        senderAddress.setText(dataMap.get(selectedCompanyName).get("companyAddress"));
                        senderTelephone.setText(dataMap.get(selectedCompanyName).get("tel"));
                        senderEmail.setText(dataMap.get(selectedCompanyName).get("email"));
                    }
                });

                senderGSTNumber.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedGSTNumber = gstAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("gstIn").equals(selectedGSTNumber)) {
                            senderCompanyName.setText(key);
                            senderAddress.setText(dataMap.get(key).get("companyAddress"));
                            senderTelephone.setText(dataMap.get(key).get("tel"));
                            senderEmail.setText(dataMap.get(key).get("email"));
                            break;
                        }
                    }
                });

                senderAddress.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedAddress = addressAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("companyAddress").equals(selectedAddress)) {
                            senderCompanyName.setText(key);
                            senderGSTNumber.setText(dataMap.get(key).get("gstIn"));
                            senderTelephone.setText(dataMap.get(key).get("tel"));
                            senderEmail.setText(dataMap.get(key).get("email"));
                            break;
                        }
                    }
                });

                senderTelephone.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedTelephone = telephoneAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("tel").equals(selectedTelephone)) {
                            senderCompanyName.setText(key);
                            senderGSTNumber.setText(dataMap.get(key).get("gstIn"));
                            senderAddress.setText(dataMap.get(key).get("companyAddress"));
                            senderEmail.setText(dataMap.get(key).get("email"));
                            break;
                        }
                    }
                });

                senderEmail.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedEmail = emailAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("email").equals(selectedEmail)) {
                            senderCompanyName.setText(key);
                            senderGSTNumber.setText(dataMap.get(key).get("gstIn"));
                            senderAddress.setText(dataMap.get(key).get("companyAddress"));
                            senderTelephone.setText(dataMap.get(key).get("tel"));
                            break;
                        }
                    }
                });
                billedToCompanyName.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedCompanyName = billedCompanyAdapter.getItem(position);
                    if (dataMap.containsKey(selectedCompanyName)) {
                        billedToGSTNumber.setText(dataMap.get(selectedCompanyName).get("gstIn"));
                        billedToAddress.setText(dataMap.get(selectedCompanyName).get("companyAddress"));
                    }
                });

                billedToGSTNumber.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedGSTNumber = billedGSTAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("gstIn").equals(selectedGSTNumber)) {
                            billedToCompanyName.setText(key);
                            billedToAddress.setText(dataMap.get(key).get("companyAddress"));
                            break;
                        }
                    }
                });

                billedToAddress.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedAddress = billedAddressAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("companyAddress").equals(selectedAddress)) {
                            billedToCompanyName.setText(key);
                            billedToGSTNumber.setText(dataMap.get(key).get("gstIn"));
                            break;
                        }
                    }
                });

                shippedToCompanyName.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedCompanyName = shippedCompanyAdapter.getItem(position);
                    if (dataMap.containsKey(selectedCompanyName)) {
                        shippedToGSTNumber.setText(dataMap.get(selectedCompanyName).get("gstIn"));
                        shippedToAddress.setText(dataMap.get(selectedCompanyName).get("companyAddress"));
                    }
                });

                shippedToGSTNumber.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedGSTNumber = shippedGSTAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("gstIn").equals(selectedGSTNumber)) {
                            shippedToCompanyName.setText(key);
                            shippedToAddress.setText(dataMap.get(key).get("companyAddress"));
                            break;
                        }
                    }
                });

                shippedToAddress.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedAddress = shippedAddressAdapter.getItem(position);
                    for (String key : dataMap.keySet()) {
                        if (dataMap.get(key).get("companyAddress").equals(selectedAddress)) {
                            shippedToCompanyName.setText(key);
                            shippedToGSTNumber.setText(dataMap.get(key).get("gstIn"));
                            break;
                        }
                    }
                });

                placeOfSupply.setOnItemClickListener((parent, view, position, id) -> {
                    // TODO: Extract data from an API call when the value of placeOfSupply changes
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });
        // Handle the DatePicker value
        invoiceDatePicker.init(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                (view, year, monthOfYear, dayOfMonth) -> { String selectedDate = String.format(Locale.getDefault(),
                        "%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
            Log.d("Invoice Date", "Selected Date: " + selectedDate);
        });

        databaseReferenceBankDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> cifNumbers = new ArrayList<>();
                ArrayList<String> branchNames = new ArrayList<>();
                ArrayList<String> accountNumbers = new ArrayList<>();
                ArrayList<String> accountTypes = new ArrayList<>();
                ArrayList<String> ifscCodes = new ArrayList<>();
                HashMap<String, HashMap<String, String>> bankDataMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("onDataChange: ", snapshot.toString());
                    String cif = snapshot.child("cifNumber").getValue(String.class);
                    String branch = snapshot.child("branchName").getValue(String.class);
                    String accountNo = snapshot.child("accountNumber").getValue(String.class);
                    String accountTp = snapshot.child("accountType").getValue(String.class);
                    String ifsc = snapshot.child("ifscCode").getValue(String.class);

                    cifNumbers.add(cif);
                    branchNames.add(branch);
                    accountNumbers.add(accountNo);
                    accountTypes.add(accountTp);
                    ifscCodes.add(ifsc);

                    HashMap<String, String> temp = new HashMap<>();
                    temp.put("cifNumber", cif);
                    temp.put("branchName", branch);
                    temp.put("accountNumber", accountNo);
                    temp.put("accountType", accountTp);
                    temp.put("ifscCode", ifsc);
                    bankDataMap.put(cif, temp);
                }

                ArrayAdapter<String> cifAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, cifNumbers);
                ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, branchNames);
                ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, accountNumbers);
                ArrayAdapter<String> accountTypeAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, accountTypes);
                ArrayAdapter<String> ifscAdapter = new ArrayAdapter<>(addNewInvoiceData.this, android.R.layout.simple_dropdown_item_1line, ifscCodes);

                cifNumber.setAdapter(cifAdapter);
                branchName.setAdapter(branchAdapter);
                accountNumber.setAdapter(accountAdapter);
                accountType.setAdapter(accountTypeAdapter);
                ifscCode.setAdapter(ifscAdapter);

                cifNumber.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedCif = cifAdapter.getItem(position);
                    if (bankDataMap.containsKey(selectedCif)) {
                        branchName.setText(bankDataMap.get(selectedCif).get("branchName"));
                        accountNumber.setText(bankDataMap.get(selectedCif).get("accountNumber"));
                        accountType.setText(bankDataMap.get(selectedCif).get("accountType"));
                        ifscCode.setText(bankDataMap.get(selectedCif).get("ifscCode"));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
            }
        });

    }
}