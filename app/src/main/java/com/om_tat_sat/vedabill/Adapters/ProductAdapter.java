package com.om_tat_sat.vedabill.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.om_tat_sat.vedabill.DataHolders.ProductItem;
import com.om_tat_sat.vedabill.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductItem> productList;
    private Context context;
    private HashMap<String, HashMap<String, String>> itemsDataMap;
    private List<ProductItem> updatedProductList;

    public ProductAdapter(Context context, List<ProductItem> productList, HashMap<String, HashMap<String, String>> itemsDataMap, List<ProductItem> updatedProductList) {
        this.context = context;
        this.productList = productList;
        this.itemsDataMap = itemsDataMap;
        this.updatedProductList = updatedProductList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductItem product = productList.get(position);
        holder.editDescription.setText(product.getDescription());
        holder.editHSNCode.setText(product.getHsnCode());
        holder.editUnit.setText(product.getUnit());
        holder.editMRP.setText(product.getMrp());
        holder.editQuantity.setText(product.getQuantity());
        holder.editListPrice.setText(product.getListPrice());
        holder.editDiscount.setText(product.getDiscount());



        ArrayAdapter<String> descriptionAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("description"));
        holder.editDescription.setAdapter(descriptionAdapter);

        ArrayAdapter<String> hsnCodeAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("hsnCode"));
        holder.editHSNCode.setAdapter(hsnCodeAdapter);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("unit"));
        holder.editUnit.setAdapter(unitAdapter);

        ArrayAdapter<String> mrpAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("mrp"));
        holder.editMRP.setAdapter(mrpAdapter);

        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("quantity"));
        holder.editQuantity.setAdapter(quantityAdapter);

        ArrayAdapter<String> listPriceAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("listPrice"));
        holder.editListPrice.setAdapter(listPriceAdapter);

        ArrayAdapter<String> discountAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, extractAttributeList("discount"));
        holder.editDiscount.setAdapter(discountAdapter);



        // Ensure data is captured even if user manually edits text
        holder.editDiscount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });
        holder.editListPrice.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });
        holder.editHSNCode.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });
        holder.editUnit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });
        holder.editMRP.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });
        holder.editQuantity.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });
        holder.editDiscount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }
        });



        holder.spinnerSGST.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        holder.spinnerCGST.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e( "onItemSelected:-------","-"+position);
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //TODO add selected listeners and auto set on them here

    }
    private void updateAllFieldsBasedOnSelection(ProductViewHolder holder, String selectedValue, String key) {
        for (String datasetKey : itemsDataMap.keySet()) {
            Map<String, String> attributes = itemsDataMap.get(datasetKey);
            if (attributes != null && attributes.get(key).equals(selectedValue)) {
                holder.editDescription.setText(attributes.getOrDefault("description", ""));
                holder.editHSNCode.setText(attributes.getOrDefault("hsnCode", ""));
                holder.editUnit.setText(attributes.getOrDefault("unit", ""));
                holder.editMRP.setText(attributes.getOrDefault("mrp", ""));
                holder.editListPrice.setText(attributes.getOrDefault("listPrice", ""));
                holder.spinnerCGST.setSelection(getIndex(holder.spinnerCGST, attributes.getOrDefault("cgst", "")));
                holder.spinnerSGST.setSelection(getIndex(holder.spinnerSGST, attributes.getOrDefault("sgst", "")));
                updateProductListWithSelectedValues(holder, holder.getAdapterPosition());
                break;
            }
        }
    }

    // Helper method to get index of an item in a spinner
    private int getIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0; // Default selection if not found
    }

    private List<String> extractAttributeList(String attribute) {
        List<String> attributeList = new ArrayList<>();
        for (HashMap<String, String> itemData : itemsDataMap.values()) {
            attributeList.add(itemData.get(attribute));
        }
        return attributeList;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        AutoCompleteTextView editDescription, editHSNCode, editUnit, editMRP, editQuantity, editListPrice, editDiscount;
        Spinner spinnerCGST, spinnerSGST;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            editDescription = itemView.findViewById(R.id.editDescription);
            editHSNCode = itemView.findViewById(R.id.editHSNCode);
            editUnit = itemView.findViewById(R.id.editUnit);
            editMRP = itemView.findViewById(R.id.editMRP);
            editQuantity = itemView.findViewById(R.id.editQuantity);
            editListPrice = itemView.findViewById(R.id.editListPrice);
            editDiscount = itemView.findViewById(R.id.editDiscount);
            spinnerCGST = itemView.findViewById(R.id.spinnerCGST);
            spinnerSGST = itemView.findViewById(R.id.spinnerSGST);
        }
    }

    public void addProduct(ProductItem product) {
        productList.add(product);
        notifyItemInserted(productList.size() - 1);
    }

    public List<ProductItem> getProductList() {
        List<ProductItem> updatedProductList = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            ProductItem product = productList.get(i);
            updatedProductList.add(new ProductItem(
                    product.getDescription(),
                    product.getHsnCode(),
                    product.getUnit(),
                    product.getMrp(),
                    product.getQuantity(),
                    product.getListPrice(),
                    product.getDiscount(),
                    String.valueOf(product.getCgst()),
                    String.valueOf(product.getSgst())
            ));
        }
        return updatedProductList;
    }

    public void updateExternalProductList(int position, ProductItem product) {
        if (position < updatedProductList.size()) {
            updatedProductList.set(position, product);
        } else {
            updatedProductList.add(product);
        }
    }


    // Helper method to update product list
    private void updateProductListWithSelectedValues(ProductViewHolder holder, int position) {
        ProductItem updatedProduct = new ProductItem(
                holder.editDescription.getText().toString(),
                holder.editHSNCode.getText().toString(),
                holder.editUnit.getText().toString(),
                holder.editMRP.getText().toString(),
                holder.editQuantity.getText().toString(),
                holder.editListPrice.getText().toString(),
                holder.editDiscount.getText().toString(),
                holder.spinnerCGST.getSelectedItem().toString(),
                holder.spinnerSGST.getSelectedItem().toString()
        );
        Log.e("updateProductListWithSelectedValues:----------",holder.spinnerCGST.getSelectedItem().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.spinnerSGST.getSelectedItem().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.editDescription.getText().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.editHSNCode.getText().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.editListPrice.getText().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.editMRP.getText().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.editUnit.getText().toString());
        Log.e("updateProductListWithSelectedValues:----------",holder.editDiscount.getText().toString());
        updateExternalProductList(position, updatedProduct);
    }


}