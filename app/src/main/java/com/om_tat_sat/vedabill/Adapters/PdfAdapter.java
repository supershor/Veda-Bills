package com.om_tat_sat.vedabill.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.om_tat_sat.vedabill.R;
import com.om_tat_sat.vedabill.apiPassword;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PdfAdapter extends RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {

    private static List<File> pdfFiles;
    private static Context context;
    private final String uuid;

    public PdfAdapter(Context context, List<File> pdfFiles,String uuid) {
        this.context = context;
        this.pdfFiles = pdfFiles;
        this.uuid=uuid;
    }

    @NonNull
    @Override
    public PdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.files_recycler_view, parent, false);
        return new PdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfViewHolder holder, int position) {
        File pdfFile = pdfFiles.get(position);
        holder.invoiceName.setText(pdfFile.getName().split("_")[0]);
        holder.invoiceDate.setText(context.getString(R.string.dated) + extractDateFromFileName(pdfFile.getName()));
        holder.invoiceSent.setText(context.getString(R.string.sent_to) + extractSentToFromFileName(pdfFile.getName()).replace("#"," "));
        holder.generateRandomNumber.setOnClickListener(v -> shareLink(pdfFile.getName(), holder));
        holder.shareFile.setOnClickListener(v -> sharePdf(pdfFile));
    }

    @Override
    public int getItemCount() {
        return pdfFiles.size();
    }

    static class PdfViewHolder extends RecyclerView.ViewHolder {
        TextView invoiceName, invoiceDate, invoiceSent;
        ImageButton generateRandomNumber, shareFile;
        public PdfViewHolder(@NonNull View itemView) {
            super(itemView);
            invoiceName = itemView.findViewById(R.id.invoiceName);
            invoiceDate = itemView.findViewById(R.id.invoiceDate);
            invoiceSent = itemView.findViewById(R.id.invoiceSent);
            generateRandomNumber = itemView.findViewById(R.id.generateRandomNumber);
            shareFile = itemView.findViewById(R.id.shareFile);
            itemView.setOnClickListener(v -> openPdf(getAdapterPosition()));
        }
    }
    private static void openPdf(int position) {
        File pdfFile = pdfFiles.get(position);
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context,context.getString(R.string.no_application_available), Toast.LENGTH_LONG).show();
        }
    }


    private String extractDateFromFileName(String fileName) {
        String[] parts = fileName.split("_");
        return parts.length > 1 ? parts[1] : "N/A";
    }

    private String extractSentToFromFileName(String fileName) {
        String[] parts = fileName.split("_");
        return parts.length > 2 ? parts[2].replace(".pdf", "") : "N/A";
    }

    private void sharePdf(File pdfFile) {
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.putExtra(Intent.EXTRA_TEXT, "Invoice-" + pdfFile.getName());
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(intent, "Share PDF"));
        } catch (Exception e) {
            Toast.makeText(context,context.getString(R.string.no_application_available), Toast.LENGTH_LONG).show();
        }
    }

    private void shareLink(String fileName, PdfViewHolder holder) {
        apiPassword apiPassword=new apiPassword();
        String url = apiPassword.getApiUrl()+"/linkFile"+"?type=link";
        StringRequest linkRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        String link = responseObject.getString("url");
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "Invoice-" + fileName + "\n" + link);
                        context.startActivity(Intent.createChooser(intent, "Share Link"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getString(R.string.failed_to_generate_link), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(context, context.getString(R.string.failed_to_generate_link), Toast.LENGTH_LONG).show();
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                apiPassword password = new apiPassword();
                HashMap<String,String>hm=new HashMap<>();
                hm.put("password", password.getApiPasswordForPdf());
                hm.put("uuid",uuid);
                hm.put("fileName",fileName);
                return hm;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(linkRequest);
    }
}
