package id.example.arsip_file;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import id.example.arsip_file.API.APIRequestData;
import id.example.arsip_file.API.RetroServer;
import id.example.arsip_file.API.response.ResponseGetArsip;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity2 extends AppCompatActivity {
    public static String token;
    RecyclerView recyclerView;
    ImageView tambah;
    List<ResponseGetArsip> list = new ArrayList<>();
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        token = getIntent().getStringExtra("token");
//        Toast.makeText(this, token, Toast.LENGTH_SHORT).show();

        recyclerView = findViewById(R.id.recyclerviewArs);
        tambah = findViewById(R.id.tambah);
        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity2.this, MainActivity.class);
                i.putExtra("token", token);
                startActivity(i);
            }
        });
        getListArsip();


    }

    @Override
    protected void onResume() {
        getListArsip();
        super.onResume();
    }

    private void getListArsip(){
        loading = ProgressDialog.show(
                MainActivity2.this,
                null,
                "Loading...",
                true,
                true
        );

        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<List<ResponseGetArsip>> call = apiRequestData.getArsip(token);
        call.enqueue(new Callback<List<ResponseGetArsip>>() {
            @Override
            public void onResponse(Call<List<ResponseGetArsip>> call, Response<List<ResponseGetArsip>> response) {
                if (response.code()==200){
                    loading.dismiss();
                    list = response.body();

                    Toast.makeText(MainActivity2.this, "Berhasil Get Data", Toast.LENGTH_SHORT).show();
                    AdapterArsip adapterArsip = new AdapterArsip(MainActivity2.this, list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity2.this));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapterArsip);


                }else {
                    Toast.makeText(MainActivity2.this, response.code()+" "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResponseGetArsip>> call, Throwable t) {

            }
        });
        loading.dismiss();
    }
}