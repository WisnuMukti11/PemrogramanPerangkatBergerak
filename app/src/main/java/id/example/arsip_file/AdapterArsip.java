package id.example.arsip_file;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.arialyy.aria.core.Aria;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import id.example.arsip_file.API.APIRequestData;
import id.example.arsip_file.API.RetroServer;
import id.example.arsip_file.API.response.ResponseGetArsip;
import id.example.arsip_file.API.response.ResponseUpdate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterArsip extends RecyclerView.Adapter<AdapterArsip.viewHolder> {
    Context context;
    List<ResponseGetArsip> arsipList = new ArrayList<>();
    ProgressBar progressBar;
    ProgressDialog progressDialog, loading;
    int pos;

    public AdapterArsip(Context context, List<ResponseGetArsip> arsipList) {
        this.context = context;
        this.arsipList = arsipList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_arsip, parent, false);

        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        progressBar = new ProgressBar(context);
        progressDialog = new ProgressDialog(context);

        holder.nomer.setText(arsipList.get(position).getNomor());
        holder.tanggal.setText(arsipList.get(position).getTanggal());
        holder.judul.setText(arsipList.get(position).getJudul());
        holder.kategori.setText(arsipList.get(position).getKategori().getKategori());
        holder.file.setText(arsipList.get(position).getFile().getNamaFile());

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, holder.download);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menumenu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.edit:
                                Intent i = new Intent(context, UpdateArsip.class);
                                i.putExtra("idArsip", arsipList.get(position).getId());
                                i.putExtra("idKategori", arsipList.get(position).getKategoriId());
                                i.putExtra("nomor", arsipList.get(position).getNomor());
                                i.putExtra("tanggal", arsipList.get(position).getTanggal());
                                i.putExtra("judul", arsipList.get(position).getJudul());
                                i.putExtra("kategori", arsipList.get(position).getKategori().getKategori());
                                i.putExtra("url", arsipList.get(position).getFile().getUrlFile());
                                i.putExtra("nama", arsipList.get(position).getFile().getNamaFile());
                                context.startActivity(i);
                                break;
                            case R.id.delete:
                                loading = ProgressDialog.show(
                                        context,
                                        null,
                                        "Loading...",
                                        true,
                                        true
                                );
                                APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
                                Call<ResponseUpdate> call = apiRequestData.hapusArsip(MainActivity2.token, arsipList.get(position).getId());
                                call.enqueue(new Callback<ResponseUpdate>() {
                                    @Override
                                    public void onResponse(Call<ResponseUpdate> call, Response<ResponseUpdate> response) {
                                        if (response.code()==200){
                                            loading.dismiss();
                                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            arsipList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, arsipList.size());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseUpdate> call, Throwable t) {

                                    }
                                });
                                break;
                            case R.id.download:
                                DownloadManager downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                                Uri uri = Uri.parse(arsipList.get(position).getFile().getUrlFile());
                                DownloadManager.Request request = new DownloadManager.Request(uri);
                                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, arsipList.get(position).getFile().getNamaFile());
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                downloadManager.enqueue(request);
                                Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method

    }


    @Override
    public int getItemCount() {
        return arsipList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView nomer, tanggal, judul, kategori, file;
        ImageView download;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            nomer = itemView.findViewById(R.id.nomor);
            tanggal = itemView.findViewById(R.id.tanggal);
            judul = itemView.findViewById(R.id.judul);
            kategori = itemView.findViewById(R.id.kategori);
            file = itemView.findViewById(R.id.file);
            download = itemView.findViewById(R.id.download);
        }
    }

}

