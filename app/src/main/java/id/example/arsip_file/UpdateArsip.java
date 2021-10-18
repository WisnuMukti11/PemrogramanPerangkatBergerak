package id.example.arsip_file;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.example.arsip_file.API.APIRequestData;
import id.example.arsip_file.API.RetroServer;
import id.example.arsip_file.API.response.ResponseAddArsip;
import id.example.arsip_file.API.response.ResponseKategori;
import id.example.arsip_file.API.response.ResponseUpdate;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateArsip extends AppCompatActivity {
    private EditText nomorArp,tanggalArp,judulArp,kategoriArp;
    private TextView fileArp;
    private Button btn_simpan, btn_fileChooser;
    ProgressDialog loading;

    Uri resultUri;
    HashMap<String, RequestBody> map = new HashMap<>();
    File file;
    MultipartBody.Part fileToUpload;
    List<ResponseKategori> kategoris = new ArrayList<>();
    int idKategori, idArsip;
    AdapterKategori.ItemClickListener itemClickListener;
    Dialog dialog3;
    String url;

    Dialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_arsip);

        nomorArp = findViewById(R.id.nomorArp);
        tanggalArp = findViewById(R.id.tanggalArp);
        judulArp = findViewById(R.id.judulArp);
        kategoriArp = findViewById(R.id.kategoriArp);
        fileArp = findViewById(R.id.fileArp);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_fileChooser = findViewById(R.id.btn_fileChooser);

        nomorArp.setText(getIntent().getStringExtra("nomor"));
        tanggalArp.setText(getIntent().getStringExtra("tanggal"));
        judulArp.setText(getIntent().getStringExtra("judul"));
        kategoriArp.setText(getIntent().getStringExtra("kategori"));
        idArsip = getIntent().getIntExtra("idArsip",0);
        idKategori = getIntent().getIntExtra("idKategori", 0);
        fileArp.setText(getIntent().getStringExtra("nama"));
        url = getIntent().getStringExtra("url");

        getKategori();

        itemClickListener = (new AdapterKategori.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //set Religion
                kategoriArp.setText(kategoris.get(position).getKategori());
                idKategori = kategoris.get(position).getId();
                dialog2.cancel();
            }
        });

        dialog3 = new Dialog(this);
        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog3.setContentView(R.layout.dialog_calender);
        dialog3.getWindow().setGravity(Gravity.CENTER);
        dialog3.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tanggalArp.setFocusable(false);
        tanggalArp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialCalendarView calendarView;
                dialog3.show();
                calendarView = dialog3.findViewById(R.id.calendarView);

                calendarView.state().edit()
                        .setMinimumDate(CalendarDay.today())
                        .setMaximumDate(CalendarDay.from(CalendarDay.today().getYear(), CalendarDay.today().getMonth(), CalendarDay.today().getDay()+1))
                        .setCalendarDisplayMode(CalendarMode.MONTHS)
                        .commit();

                if (calendarView.getSelectedDate()==null){
                    calendarView.setDateSelected(CalendarDay.today(), true);
                }
                calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                        List<CalendarDay> dayList = new ArrayList<>();
                        Log.i("TAG", "onDateSelected: "+dayList);
                        tanggalArp.setText(date.getYear()+"-"+date.getMonth()+"-"+date.getDay());
//                                        Log.i("TAG", "onDate: "+cal.getMonth());
                        if(calendarView.getSelectedDate() != date){
                            calendarView.setDateSelected(date, true);
                        }
                        dialog3.cancel();
                    }
                });
            }
        });

        kategoriArp.setFocusable(false);
        kategoriArp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog2 = new Dialog(view.getContext());
                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog2.setContentView(R.layout.dialog_kategori);
                dialog2.getWindow().setGravity(Gravity.BOTTOM);
                dialog2.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog2.show();

                RecyclerView recyclerView = dialog2.findViewById(R.id.recyclerviewKat);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                AdapterKategori adapterPort = new AdapterKategori(view.getContext(), kategoris, itemClickListener);
                recyclerView.setAdapter(adapterPort);
            }
        });

        btn_fileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChooserDialog().with(UpdateArsip.this)
                        .withStartFile("/sdcard")
                        .withChosenListener(new ChooserDialog.Result() {
                            @Override
                            public void onChoosePath(String path, File pathFile) {
                                Toast.makeText(UpdateArsip.this, "FOLDER: " + path, Toast.LENGTH_SHORT).show();
                                fileArp.setText(pathFile.getName());
                                file = pathFile;
                            }
                        })
                        .build()
                        .show();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadArsip();
                uploadArsip();
            }
        });
    }

    private void createData(){
        if (nomorArp.getText().toString().equals("")){
            Toast.makeText(this, "Cek data lagi", Toast.LENGTH_SHORT).show();
        }
        else if (tanggalArp.getText().toString().equals("")){
            Toast.makeText(this, "Cek data lagi", Toast.LENGTH_SHORT).show();
        }
        else if (judulArp.getText().toString().equals("")){
            Toast.makeText(this, "Cek data lagi", Toast.LENGTH_SHORT).show();
        }
        else if (kategoriArp.getText().toString().equals("")){
            Toast.makeText(this, "Cek data lagi", Toast.LENGTH_SHORT).show();
        }else {
//            String Fgender = null;
//            if (gender.getText().toString().equals("Male")){
//                Fgender="male";
//            }else if (gender.getText().toString().equals("Female")){
//                Fgender="female";
//            }
//            Log.i("TAG", "createDatad: "+Fgender);
            map.put("nomor", createPartFromString(nomorArp.getText().toString()));
            map.put("tanggal", createPartFromString(tanggalArp.getText().toString()));
            map.put("judul", createPartFromString(judulArp.getText().toString()));
            map.put("kategori_id", createPartFromString(String.valueOf(idKategori)));
            map.put("id", createPartFromString(String.valueOf(idArsip)));
        }

        if (file==null){
            fileToUpload = null;
        }else {
            final RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        }

    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString){
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }

    private void getKategori(){
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<List<ResponseKategori>> call = apiRequestData.listKategori(MainActivity2.token);
        call.enqueue(new Callback<List<ResponseKategori>>() {
            @Override
            public void onResponse(Call<List<ResponseKategori>> call, Response<List<ResponseKategori>> response) {

                if (response.code()==200){
                    kategoris=response.body();
                }
            }

            @Override
            public void onFailure(Call<List<ResponseKategori>> call, Throwable t) {

            }
        });
    }

    public void uploadArsip(){
        loading = ProgressDialog.show(
                UpdateArsip.this,
                null,
                "Loading...",
                true,
                true
        );
        createData();

        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponseUpdate> call = apiRequestData.editArsip(MainActivity2.token, fileToUpload, map);
        call.enqueue(new Callback<ResponseUpdate>() {
            @Override
            public void onResponse(Call<ResponseUpdate> call, Response<ResponseUpdate> response) {
                if (response.code()==200){
                    loading.dismiss();
                    Toast.makeText(UpdateArsip.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(UpdateArsip.this, response.code()+" "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUpdate> call, Throwable t) {
                Toast.makeText(UpdateArsip.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}