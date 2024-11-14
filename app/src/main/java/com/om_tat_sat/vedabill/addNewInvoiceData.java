package com.om_tat_sat.vedabill;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.om_tat_sat.vedabill.Adapters.ProductAdapter;
import com.om_tat_sat.vedabill.DataHolders.ProductItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class addNewInvoiceData extends AppCompatActivity {

    private FloatingActionButton next;
    private FloatingActionButton prev;
    private FloatingActionButton addNew;
    private LinearLayout senderDetailsSectionLinear;
    private List<ProductItem> updatedProductList;
    private LinearLayout recipientDetailsSectionLinear;
    private LinearLayout productDetailsSectionLinear;
    private LinearLayout bankDetailsSectionLinear;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private int currentStageGlobal=0;
    private boolean saveDataAndContinue=false;
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
    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<ProductItem> productList;
    private HashMap<String, HashMap<String, String>> itemsDataMap;
    private DatabaseReference databaseReferenceItems;
    private DatabaseReference databaseReferenceInformation;
    private DatabaseReference databaseReferenceBankDetails;
    private String userId;
    private String fileNameMainFile;

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
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        next = findViewById(R.id.next);
        prev = findViewById(R.id.prev);
        addNew = findViewById(R.id.addNew);
        senderDetailsSectionLinear = findViewById(R.id.senderDetailsSectionLinear);
        recipientDetailsSectionLinear = findViewById(R.id.recipientDetailsSectionLinear);
        productDetailsSectionLinear = findViewById(R.id.productListSectionLinear);
        bankDetailsSectionLinear = findViewById(R.id.bankDetailsSectionLinear);
        image1=findViewById(R.id.image1);
        image2=findViewById(R.id.image2);
        image3=findViewById(R.id.image3);
        image4=findViewById(R.id.image4);

        productList = new ArrayList<>();
        itemsDataMap = new HashMap<>();

        updatedProductList = new ArrayList<>();
        itemsDataMap = new HashMap<>();
        productAdapter = new ProductAdapter(this, productList, itemsDataMap, updatedProductList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);


        Button buttonAddProduct = findViewById(R.id.buttonAddProduct);
        buttonAddProduct.setOnClickListener(view -> {
            ProductItem newProduct = new ProductItem();
            productAdapter.addProduct(newProduct);
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStageGlobal++;
                changeLinearLayout(currentStageGlobal);
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentStageGlobal--;
                changeLinearLayout(currentStageGlobal);
            }
        });
        generateRandomNumber.setOnClickListener(view -> {
            String randomInvoiceNumber = "INV-"+Math.round(Math.random()*10000000);
            invoiceNumber.setText(randomInvoiceNumber);
        });
        FloatingActionButton addNew = findViewById(R.id.addNew);
        addNew.setOnClickListener(v -> showAddNewDialog());

        userId = FirebaseAuth.getInstance().getUid()==null?"uuid":FirebaseAuth.getInstance().getUid();
        databaseReferenceInformation = FirebaseDatabase.getInstance().getReference("VedaBills/" + userId + "/sendersReciversData");
        databaseReferenceBankDetails = FirebaseDatabase.getInstance().getReference("VedaBills/" + userId + "/BankDetails");
        databaseReferenceItems = FirebaseDatabase.getInstance().getReference("VedaBills/" + userId + "/itemList");

        databaseReferenceInformation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                Toast.makeText(addNewInvoiceData.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                // Handle possible errors
            }
        });
        DatabaseReference itemsReference = FirebaseDatabase.getInstance().getReference("VedaBills/" + userId + "/itemsList");

        databaseReferenceItems.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemsDataMap.clear();  // Clear the map before populating it
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("onDataChange: ", dataSnapshot.toString());
                    HashMap<String, String> itemData = new HashMap<>();
                    itemData.put("hsnCode", snapshot.child("hsnCode").getValue(String.class));
                    itemData.put("description", snapshot.child("description").getValue(String.class));
                    itemData.put("unit", snapshot.child("unit").getValue(String.class));
                    itemData.put("mrp", snapshot.child("mrp").getValue(String.class));
                    itemData.put("quantity", snapshot.child("quantity").getValue(String.class));
                    itemData.put("listPrice", snapshot.child("listPrice").getValue(String.class));
                    itemData.put("discount", snapshot.child("discount").getValue(String.class));
                    itemData.put("cgst", snapshot.child("cgst").getValue(String.class));
                    itemData.put("sgst", snapshot.child("sgst").getValue(String.class));
                    itemsDataMap.put(snapshot.getKey(), itemData);
                }
                // Notify adapter that data has changed if necessary
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Log.e("DatabaseError", databaseError.getMessage());
                Toast.makeText(addNewInvoiceData.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


    }
    private void addCompanyDetails() {
        // Code to show dialog or activity to add company details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Company Details");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_company, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText inputCompanyName = viewInflated.findViewById(R.id.input_company_name);
        final EditText inputCompanyAddress = viewInflated.findViewById(R.id.input_company_address);
        final EditText inputEmail = viewInflated.findViewById(R.id.input_email);
        final EditText inputGstIn = viewInflated.findViewById(R.id.input_gst_in);
        final EditText inputTel = viewInflated.findViewById(R.id.input_tel);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String companyName = inputCompanyName.getText().toString().isEmpty() ? "NA" : inputCompanyName.getText().toString();
            String companyAddress = inputCompanyAddress.getText().toString().isEmpty() ? "NA" : inputCompanyAddress.getText().toString();
            String email = inputEmail.getText().toString().isEmpty() ? "NA" : inputEmail.getText().toString();
            String gstIn = inputGstIn.getText().toString().isEmpty() ? "NA" : inputGstIn.getText().toString();
            String tel = inputTel.getText().toString().isEmpty() ? "NA" : inputTel.getText().toString();

            // Save company details to Firebase using the UUID as a key
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uuid = user.getUid()!=null?user.getUid():"uuid";
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VedaBills").child(uuid).child("sendersReciversData");
                String key = databaseReference.push().getKey();
                if (key != null) {
                    Map<String, String> companyDetails = new HashMap<>();
                    companyDetails.put("companyName", companyName);
                    companyDetails.put("companyAddress", companyAddress);
                    companyDetails.put("email", email);
                    companyDetails.put("gstIn", gstIn);
                    companyDetails.put("tel", tel);
                    databaseReference.child(key).setValue(companyDetails)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, this.getString(R.string.company_added), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("DatabaseError", "Error adding company details", e);
                            });
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void addBankDetails() {
        // Code to show dialog or activity to add bank details
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Bank Details");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_bank, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText inputCifNumber = viewInflated.findViewById(R.id.input_cif_number);
        final EditText inputBranchName = viewInflated.findViewById(R.id.input_branch_name);
        final EditText inputAccountNumber = viewInflated.findViewById(R.id.input_account_number);
        final EditText inputAccountType = viewInflated.findViewById(R.id.input_account_type);
        final EditText inputIfscCode = viewInflated.findViewById(R.id.input_ifsc_code);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String cifNumber = inputCifNumber.getText().toString().isEmpty() ? "NA" : inputCifNumber.getText().toString();
            String branchName = inputBranchName.getText().toString().isEmpty() ? "NA" : inputBranchName.getText().toString();
            String accountNumber = inputAccountNumber.getText().toString().isEmpty() ? "NA" : inputAccountNumber.getText().toString();
            String accountType = inputAccountType.getText().toString().isEmpty() ? "NA" : inputAccountType.getText().toString();
            String ifscCode = inputIfscCode.getText().toString().isEmpty() ? "NA" : inputIfscCode.getText().toString();

            // Save bank details to Firebase using the UUID as a key
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uuid = user.getUid()!=null?user.getUid():"uuid";
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VedaBills").child(uuid).child("BankDetails");
                String key = databaseReference.push().getKey();
                if (key != null) {
                    Map<String, String> bankDetails = new HashMap<>();
                    bankDetails.put("cifNumber", cifNumber);
                    bankDetails.put("branchName", branchName);
                    bankDetails.put("accountNumber", accountNumber);
                    bankDetails.put("accountType", accountType);
                    bankDetails.put("ifscCode", ifscCode);
                    databaseReference.child(key).setValue(bankDetails)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, this.getString(R.string.bank_added), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("DatabaseError", "Error adding bank details", e);
                            });
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showAddNewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.add_new))
                .setItems(new CharSequence[]{this.getString(R.string.add_company_details),this.getString(R.string.add_bank_details),this.getString(R.string.add_items)}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Code to add company details
                            addCompanyDetails();
                            break;
                        case 1:
                            // Code to add bank details
                            addBankDetails();
                            break;
                        case 2:
                            // Code to add items
                            addItems();
                            break;
                    }
                });
        builder.create().show();
    }
    private void addItems() {
        // Code to show dialog or activity to add items
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Items");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_add_items, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText inputDescription = viewInflated.findViewById(R.id.input_description);
        final EditText inputHsnCode = viewInflated.findViewById(R.id.input_hsn_code);
        final EditText inputUnit = viewInflated.findViewById(R.id.input_unit);
        final EditText inputMrp = viewInflated.findViewById(R.id.input_mrp);
        final EditText inputQuantity = viewInflated.findViewById(R.id.input_quantity);
        final EditText inputListPrice = viewInflated.findViewById(R.id.input_list_price);
        final EditText inputDiscount = viewInflated.findViewById(R.id.input_discount);
        final EditText inputCgst = viewInflated.findViewById(R.id.input_cgst);
        final EditText inputSgst = viewInflated.findViewById(R.id.input_sgst);

        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            String description = inputDescription.getText().toString().isEmpty() ? "NA" : inputDescription.getText().toString();
            String hsnCode = inputHsnCode.getText().toString().isEmpty() ? "NA" : inputHsnCode.getText().toString();
            String unit = inputUnit.getText().toString().isEmpty() ? "NA" : inputUnit.getText().toString();
            String mrp = inputMrp.getText().toString().isEmpty() ? "NA" : inputMrp.getText().toString();
            String quantity = inputQuantity.getText().toString().isEmpty() ? "NA" : inputQuantity.getText().toString();
            String listPrice = inputListPrice.getText().toString().isEmpty() ? "NA" : inputListPrice.getText().toString();
            String discount = inputDiscount.getText().toString().isEmpty() ? "NA" : inputDiscount.getText().toString();
            String cgst = inputCgst.getText().toString().isEmpty() ? "NA" : inputCgst.getText().toString();
            String sgst = inputSgst.getText().toString().isEmpty() ? "NA" : inputSgst.getText().toString();

            // Save item details to Firebase using the UUID as a key
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uuid = user.getUid() != null ? user.getUid() : "uuid";
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("VedaBills").child(uuid).child("itemList");
                String key = databaseReference.push().getKey();
                if (key != null) {
                    Map<String, String> itemDetails = new HashMap<>();
                    itemDetails.put("description", description);
                    itemDetails.put("hsnCode", hsnCode);
                    itemDetails.put("unit", unit);
                    itemDetails.put("mrp", mrp);
                    itemDetails.put("quantity", quantity);
                    itemDetails.put("listPrice", listPrice);
                    itemDetails.put("discount", discount);
                    itemDetails.put("cgst", cgst);
                    itemDetails.put("sgst", sgst);
                    databaseReference.child(key).setValue(itemDetails)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, this.getString(R.string.item_added), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("DatabaseError", "Error adding item", e);
                            });
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }




    public void changeLinearLayout(int current_stage){
        switch (current_stage) {
            case 0:{
                Log.e( "changeLinearLayout: ------------------", "0");
                senderDetailsSectionLinear.setVisibility(View.VISIBLE);
                recipientDetailsSectionLinear.setVisibility(View.GONE);
                bankDetailsSectionLinear.setVisibility(View.GONE);
                productDetailsSectionLinear.setVisibility(View.GONE);
                prev.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                image1.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                image2.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                image3.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                image4.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                break;
            }
            case 1:{
                Log.e( "changeLinearLayout: ------------------", "1");
                recipientDetailsSectionLinear.setVisibility(View.VISIBLE);
                bankDetailsSectionLinear.setVisibility(View.GONE);
                productDetailsSectionLinear.setVisibility(View.GONE);
                senderDetailsSectionLinear.setVisibility(View.GONE);
                prev.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                addNew.setVisibility(View.VISIBLE);
                image1.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                image2.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                image3.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                image4.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                break;
            }
            case 2:{
                Log.e( "changeLinearLayout: ------------------", "2");
                recipientDetailsSectionLinear.setVisibility(View.GONE);
                bankDetailsSectionLinear.setVisibility(View.VISIBLE);
                productDetailsSectionLinear.setVisibility(View.GONE);
                senderDetailsSectionLinear.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                addNew.setVisibility(View.VISIBLE);

                image1.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                image2.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                image3.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                image4.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                prev.setVisibility(View.VISIBLE);

                break;
            }
            case 3:{
                Log.e( "changeLinearLayout: ------------------", "3");
                bankDetailsSectionLinear.setVisibility(View.GONE);
                productDetailsSectionLinear.setVisibility(View.VISIBLE);
                senderDetailsSectionLinear.setVisibility(View.GONE);
                recipientDetailsSectionLinear.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                addNew.setVisibility(View.VISIBLE);

                prev.setVisibility(View.VISIBLE);
                image1.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                image2.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                image3.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                image4.setBackground(getResources().getDrawable(R.drawable.hollow_stroke_white));
                break;
            }
            case 4:{
                AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(addNewInvoiceData.this);
                alertDialogBuilder.setCancelable(false)
                                .setMessage("Make sure all data entered are correct.")
                                        .setTitle("Are you sure you want to save the data ?")
                                                .setPositiveButton("Make Pdf", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Log.e( "changeLinearLayout: ------------------", "4");
                                                        productDetailsSectionLinear.setVisibility(View.GONE);
                                                        senderDetailsSectionLinear.setVisibility(View.GONE);
                                                        recipientDetailsSectionLinear.setVisibility(View.GONE);
                                                        bankDetailsSectionLinear.setVisibility(View.GONE);
                                                        prev.setVisibility(View.GONE);
                                                        next.setVisibility(View.GONE);
                                                        addNew.setVisibility(View.GONE);
                                                        image1.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                                                        image2.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                                                        image3.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                                                        image4.setBackground(getResources().getDrawable(R.drawable.full_stroke_white));
                                                        saveDataAndContinue=true;

                                                        //Extract Data and calling api
                                                        extractAndSendData();
                                                        //TODO add api calls and adding of things
                                                    }
                                                })
                                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                currentStageGlobal--;
                                                                dialog.dismiss();
                                                            }
                                                        });
                alertDialogBuilder.show();
            }
        }
        Log.e( "changeLinearLayout: ------------------", current_stage+"");
    }
//    private void extractAndSendData() {
//        HashMap<String, String> invoiceData = new HashMap<>();
//        invoiceData.put("invoice[gstIn]", getNonNullString(senderGSTNumber.getText().toString()));
//        invoiceData.put("invoice[companyTel]", getNonNullString(senderTelephone.getText().toString()));
//        invoiceData.put("invoice[email]", getNonNullString(senderEmail.getText().toString()));
//        invoiceData.put("invoice[address]", getNonNullString(senderAddress.getText().toString()));
//        invoiceData.put("invoice[companyName]", getNonNullString(senderCompanyName.getText().toString()));
//        invoiceData.put("invoice[invoiceNumber]", getNonNullString(invoiceNumber.getText().toString()));
//
//        // Getting date from DatePicker
//        int day = invoiceDatePicker.getDayOfMonth();
//        int month = invoiceDatePicker.getMonth() + 1; // Months are indexed from 0
//        int year = invoiceDatePicker.getYear();
//        String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month, day);
//        invoiceData.put("invoice[dated]", selectedDate);
//
//        invoiceData.put("invoice[placeOfSupply]", getNonNullString(placeOfSupply.getText().toString()));
//
//        invoiceData.put("invoice[billedTo][name]", getNonNullString(billedToCompanyName.getText().toString()));
//        invoiceData.put("invoice[billedTo][address]", getNonNullString(billedToAddress.getText().toString()));
//        invoiceData.put("invoice[billedTo][gstIn]", getNonNullString(billedToGSTNumber.getText().toString()));
//
//        invoiceData.put("invoice[shippedTo][name]", getNonNullString(shippedToCompanyName.getText().toString()));
//        invoiceData.put("invoice[shippedTo][address]", getNonNullString(shippedToAddress.getText().toString()));
//        invoiceData.put("invoice[shippedTo][gstIn]", getNonNullString(shippedToGSTNumber.getText().toString()));
//
//        invoiceData.put("bank[cifNumber]", getNonNullString(cifNumber.getText().toString()));
//        invoiceData.put("bank[branch]", getNonNullString(branchName.getText().toString()));
//        invoiceData.put("bank[accountNo]", getNonNullString(accountNumber.getText().toString()));
//        invoiceData.put("bank[acType]", getNonNullString(accountType.getText().toString()));
//        invoiceData.put("bank[ifsc]", getNonNullString(ifscCode.getText().toString()));
//
//        if (updatedProductList.isEmpty()) {
//            invoiceData.put("products[0][Description]", "N/A");
//            invoiceData.put("products[0][HSN_Code]", "N/A");
//            invoiceData.put("products[0][Qty]", "N/A");
//            invoiceData.put("products[0][Unit]", "N/A");
//            invoiceData.put("products[0][MRP]", "N/A");
//            invoiceData.put("products[0][List_Price]", "N/A");
//            invoiceData.put("products[0][Disc]", "N/A");
//            invoiceData.put("products[0][CGST]", "N/A");
//            invoiceData.put("products[0][SGST]", "N/A");
//        } else {
//            for (int i = 0; i < updatedProductList.size(); i++) {
//                ProductItem product = updatedProductList.get(i);
//                invoiceData.put("products[" + i + "][Description]", getNonNullString(product.getDescription()));
//                invoiceData.put("products[" + i + "][HSN_Code]", getNonNullString(product.getHsnCode()));
//                invoiceData.put("products[" + i + "][Qty]", getNonNullString(product.getQuantity()));
//                invoiceData.put("products[" + i + "][Unit]", getNonNullString(product.getUnit()));
//                invoiceData.put("products[" + i + "][MRP]", getNonNullString(product.getMrp()));
//                invoiceData.put("products[" + i + "][List_Price]", getNonNullString(product.getListPrice()));
//                invoiceData.put("products[" + i + "][Disc]", getNonNullString(product.getDiscount()));
//                invoiceData.put("products[" + i + "][CGST]", getNonNullString(product.getCgst()));
//                invoiceData.put("products[" + i + "][SGST]", getNonNullString(product.getSgst()));
//            }
//        }
//
//        sendDataToServer(invoiceData);
//    }

    private void extractAndSendData() {
        HashMap<String, String> invoiceData = new HashMap<>();
//        invoiceData.put("invoice[gstIn]", getNonNullString(senderGSTNumber.getText().toString()));
        invoiceData.put("invoice[gstIn]", getNonNullString(senderGSTNumber.getText().toString()));
        invoiceData.put("invoice[companyTel]", getNonNullString(senderTelephone.getText().toString()));
        invoiceData.put("invoice[email]", getNonNullString(senderEmail.getText().toString()));
        invoiceData.put("invoice[address]", getNonNullString(senderAddress.getText().toString()));
        invoiceData.put("invoice[companyName]", getNonNullString(senderCompanyName.getText().toString()));
        invoiceData.put("invoice[invoiceNumber]", getNonNullString(invoiceNumber.getText().toString()));

        // Getting date from DatePicker
        int day = invoiceDatePicker.getDayOfMonth();
        int month = invoiceDatePicker.getMonth() + 1; // Months are indexed from 0
        int year = invoiceDatePicker.getYear();
        String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", day, month, year);
        Log.e("extractAndSendData: ", selectedDate);
        invoiceData.put("invoice[dated]", selectedDate);

        invoiceData.put("invoice[placeOfSupply]", getNonNullString(placeOfSupply.getText().toString()));

        invoiceData.put("invoice[billedTo][name]", getNonNullString(billedToCompanyName.getText().toString()));
        invoiceData.put("invoice[billedTo][address]", getNonNullString(billedToAddress.getText().toString()));
        invoiceData.put("invoice[billedTo][gstIn]", getNonNullString(billedToGSTNumber.getText().toString()));

        invoiceData.put("invoice[shippedTo][name]", getNonNullString(shippedToCompanyName.getText().toString()));
        invoiceData.put("invoice[shippedTo][address]", getNonNullString(shippedToAddress.getText().toString()));
        invoiceData.put("invoice[shippedTo][gstIn]", getNonNullString(shippedToGSTNumber.getText().toString()));

        invoiceData.put("bank[cifNumber]", getNonNullString(cifNumber.getText().toString()));
        invoiceData.put("bank[branch]", getNonNullString(branchName.getText().toString()));
        invoiceData.put("bank[accountNo]", getNonNullString(accountNumber.getText().toString()));
        invoiceData.put("bank[acType]", getNonNullString(accountType.getText().toString()));
        invoiceData.put("bank[ifsc]", getNonNullString(ifscCode.getText().toString()));

        if (updatedProductList.isEmpty()) {
            invoiceData.put("products[0][Description]", "N/A");
            invoiceData.put("products[0][HSN_Code]", "N/A");
            invoiceData.put("products[0][Qty]", "N/A");
            invoiceData.put("products[0][Unit]", "N/A");
            invoiceData.put("products[0][MRP]", "N/A");
            invoiceData.put("products[0][List_Price]", "N/A");
            invoiceData.put("products[0][Disc]", "N/A");
            invoiceData.put("products[0][CGST]", "N/A");
            invoiceData.put("products[0][SGST]", "N/A");
        } else {
            for (int i = 0; i < updatedProductList.size(); i++) {
                ProductItem product = updatedProductList.get(i);
                invoiceData.put("products[" + i + "][Description]", getNonNullString(product.getDescription()));
                invoiceData.put("products[" + i + "][HSN_Code]", getNonNullString(product.getHsnCode()));
                invoiceData.put("products[" + i + "][Qty]", getNonNullString(product.getQuantity()));
                invoiceData.put("products[" + i + "][Unit]", getNonNullString(product.getUnit()));
                invoiceData.put("products[" + i + "][MRP]", getNonNullString(product.getMrp()));
                invoiceData.put("products[" + i + "][List_Price]", getNonNullString(product.getListPrice()));
                invoiceData.put("products[" + i + "][Disc]", getNonNullString(product.getDiscount()));
                invoiceData.put("products[" + i + "][CGST]", getNonNullString(product.getCgst()));
                invoiceData.put("products[" + i + "][SGST]", getNonNullString(product.getSgst()));
            }
        }

        Log.e("extractAndSendData: ", invoiceData.toString());
        sendDataToServer(invoiceData);
    }

    private String getNonNullString(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "N-P";
    }



    public void LongCat(String message){
        if(message.length()>100){
            Log.e("LongCat:----------------",message.substring(0,100));
            LongCat(message.substring(100));
        }else{
            Log.e("LongCat:----------------",message);
        }
    }
    //TODO working code
//    private void sendDataToServer(HashMap<String, String> invoiceData) {
//        Log.e("sendDataToServer: ", invoiceData.toString());
//
//        String url = "https://app-z6hgmjxfra-uc.a.run.app/api/pdf";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    // Handle PDF response and save locally
//                    String fileName = invoiceData.get("invoice[invoiceNumber]");
//                    String sentTo = invoiceData.get("invoice[billedTo][name]");
//                    String dated = invoiceData.get("invoice[dated]");
//                    savePdfLocally(response, fileName, dated, sentTo);
//                },
//                error -> {
//                    // Handle error
//                    Log.e("Error.Response", error.toString());
//                    Log.e("Error.Response", error.getMessage());
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                apiPassword password = new apiPassword();
//                invoiceData.put("password", password.getApiPasswordForPdf());
//                return invoiceData;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }
//
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                try {
//                    Log.e("parseNetworkResponse: ",response.data.toString());
//                    String pdfBase64 = Base64.encodeToString(response.data, Base64.DEFAULT);
//                    return Response.success(pdfBase64, HttpHeaderParser.parseCacheHeaders(response));
//                } catch (Exception e) {
//                    return Response.error(new ParseError(e));
//                }
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(postRequest);
//    }


    private void sendDataToServer(HashMap<String, String> invoiceData) {
        Log.e("sendDataToServer: ", invoiceData.toString());
        apiPassword apiPassword=new apiPassword();
        String url = apiPassword.getApiUrl();
        String fileName = invoiceData.get("invoice[invoiceNumber]");
        String sentTo = invoiceData.get("invoice[billedTo][name]");
        String dated = invoiceData.get("invoice[dated]");
        String safeFileName = (fileName == null || fileName.isEmpty()) ? "Unknown" : fileName;
        String safeDated = (dated == null || dated.isEmpty()) ? "UnknownDate" : dated;
        String safeSentTo = (sentTo == null || sentTo.isEmpty()) ? "UnknownRecipient" : sentTo;
        fileNameMainFile = safeFileName + "_" + safeDated + "_" + safeSentTo.replace(" ","#")+ ".pdf";
        Log.e("sendDataToServer:----------------------", fileNameMainFile);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // Handle PDF response and save locally
                    Log.e("Received response: ", String.valueOf(response.length())); // Log the received base64 response
                    savePdfLocally(response, fileNameMainFile);
                },
                error -> {
                    next.setVisibility(View.VISIBLE);
                    addNew.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.VISIBLE);
                    // Handle error
                    Toast.makeText(addNewInvoiceData.this,error.getMessage()!=null?error.getMessage():"Please try again with appropriate data and correct network availability.",Toast.LENGTH_LONG).show();
                    changeLinearLayout(--currentStageGlobal);
                    Log.e("Error.Response", error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                apiPassword password = new apiPassword();
                invoiceData.put("password", password.getApiPasswordForPdf());
                invoiceData.put("uuid",userId);
                invoiceData.put("fileName",fileNameMainFile);
                return invoiceData;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String pdfBase64 = Base64.encodeToString(response.data, Base64.DEFAULT);
                    Log.e("parseNetworkResponse: ", pdfBase64);
                    return Response.success(pdfBase64, HttpHeaderParser.parseCacheHeaders(response));
                } catch (Exception e) {
                    changeLinearLayout(--currentStageGlobal);
                    Log.e("Error.Response", e.toString());
                    next.setVisibility(View.VISIBLE);
                    addNew.setVisibility(View.VISIBLE);
                    prev.setVisibility(View.VISIBLE);
                    Toast.makeText(addNewInvoiceData.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                    return Response.error(new ParseError(e));
                }
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(postRequest);
    }



//    private void sendDataToServer(HashMap<String, String> invoiceData) {
//        Log.e("sendDataToServer: ", invoiceData.toString());
//
//        String url = "https://app-z6hgmjxfra-uc.a.run.app/api/pdf";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    // Handle PDF response and save locally
//                    try {
//                        JSONObject responseObject = new JSONObject(response);
//                        String fileName = responseObject.getString("fileName");
//                        savePdfLocally(responseObject.getString("base64Data"), fileName);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Failed to parse response", Toast.LENGTH_LONG).show();
//                    }
//                },
//                error -> {
//                    // Handle error
//                    Log.e("Error.Response", error.toString());
//                    Log.e("Error.Response", error.getMessage());
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                apiPassword password = new apiPassword();
//                invoiceData.put("password", password.getApiPasswordForPdf());
//                return invoiceData;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }
//
//            @Override
//            protected Response<String> parseNetworkResponse(NetworkResponse response) {
//                try {
//                    String pdfBase64 = Base64.encodeToString(response.data, Base64.DEFAULT);
//                    return Response.success(pdfBase64, HttpHeaderParser.parseCacheHeaders(response));
//                } catch (Exception e) {
//                    return Response.error(new ParseError(e));
//                }
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(postRequest);
//    }


//    private void sendDataToServer(HashMap<String, String> invoiceData) {
//        Log.e("sendDataToServer: ", invoiceData.toString());
//
//        String url = "https://app-z6hgmjxfra-uc.a.run.app/api/pdf";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
//                response -> {
//                    String fileName = invoiceData.get("invoice[invoiceNumber]");
//                    String sentTo = invoiceData.get("invoice[billedTo][name]");
//                    String dated = invoiceData.get("invoice[dated]");
//                    savePdfLocally(response, fileName, dated, sentTo);
//                },
//                error -> {
//                    Log.e("Error.Response", error.toString());
//                    Log.e("Error.Response", error.getMessage());
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                apiPassword password = new apiPassword();
//                invoiceData.put("password", password.getApiPasswordForPdf());
//                return invoiceData;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }
//        };
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(postRequest);
//    }



//    private void savePdfLocally(String base64Data) {
//        try {
//            byte[] pdfData = Base64.decode(base64Data, Base64.DEFAULT);
//            String fileName = "invoice.pdf";
//            File pdfFile = new File(getExternalFilesDir(null), fileName);
//            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
//                fos.write(pdfData);
//                Toast.makeText(this, "PDF saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                openPdf(pdfFile);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show();
//        }
//    }

//    private void savePdfLocally(String base64Data, String fileName, String sentTo, String datedAt) {
//        try {
//            byte[] pdfData = Base64.decode(base64Data, Base64.DEFAULT);
//            File pdfFile = new File(getExternalFilesDir(null), fileName + "-" + datedAt + "-" + sentTo + ".pdf");
//            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
//                fos.write(pdfData);
//                Toast.makeText(this, "PDF saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                // Refresh the RecyclerView in MainActivity
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                openPdf(pdfFile);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show();
//        }
//    }


//    private void savePdfLocally(String base64Data, String fileName, String dated, String sentTo) {
//        try {
//            byte[] pdfData = Base64.decode(base64Data, Base64.DEFAULT);
//            String fullFileName = fileName + "_" + dated + "_" + sentTo + ".pdf";
//            File pdfFile = new File(getExternalFilesDir(null), fullFileName);
//            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
//                fos.write(pdfData);
//                Toast.makeText(this, "PDF saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                // Refresh the RecyclerView in MainActivity
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                openPdf(pdfFile);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show();
//        }
//    }


//    private void savePdfLocally(String base64Data, String fileName, String dated, String sentTo) {
//        try {
//            byte[] pdfData = Base64.decode(base64Data, Base64.DEFAULT);
//            String fullFileName = fileName + "_" + dated + "_" + sentTo + ".pdf";
//            File pdfFile = new File(getExternalFilesDir(null), fullFileName);
//            Log.e("savePdfLocally: Path", pdfFile.getAbsolutePath());
//
//            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
//                fos.write(pdfData);
//                Toast.makeText(this, "PDF saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
//                // Refresh the RecyclerView in MainActivity
//                Intent intent = new Intent(this, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                openPdf(pdfFile);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show();
//        }
//    }

    private void savePdfLocally(String base64Data, String fullFileName) {
        try {
            byte[] pdfData = Base64.decode(base64Data, Base64.DEFAULT);

            // Use "Unknown" if fileName, dated, or sentTo are null or empty

            File pdfFile = new File(getExternalFilesDir(null), fullFileName);

            Log.e("savePdfLocally: Path", pdfFile.getAbsolutePath());

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(pdfData);
                Toast.makeText(this, "PDF saved: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                // Refresh the RecyclerView in MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                openPdf(pdfFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            //TODO
            changeLinearLayout(--currentStageGlobal);
            Log.e("Error.Response", e.toString());
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_LONG).show();
        }
    }
    private void openPdf(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application to view PDF", Toast.LENGTH_LONG).show();
        }
    }
}