package id.example.arsip_file;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import id.example.arsip_file.API.APIRequestData;
import id.example.arsip_file.API.RetroServer;
import id.example.arsip_file.API.response.ResgisterResponse;
import id.example.arsip_file.API.response.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText email, pass;
    Button register;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.etRegisterEmail);
        pass = findViewById(R.id.etRegisterPassword);
        register = findViewById(R.id.btnRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void createRegister(User user){
        loading = ProgressDialog.show(
                Register.this,
                null,
                "Loading...",
                true,
                true
        );
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResgisterResponse> call = apiRequestData.createRegister(user);
        call.enqueue(new Callback<ResgisterResponse>() {
            @Override
            public void onResponse(Call<ResgisterResponse> call, Response<ResgisterResponse> response) {
                if (response.code()==200){
                    loading.dismiss();
                    Toast.makeText(Register.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Register.this, Login.class));
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ResgisterResponse> call, Throwable t) {

            }
        });
    }

    public void register(){
        if (email.getText().toString().equals("")){
            Toast.makeText(this, "Cek Form", Toast.LENGTH_SHORT).show();
        }else if (pass.getText().toString().equals("")){
            Toast.makeText(this, "Cek Form", Toast.LENGTH_SHORT).show();
        }else {
            createRegister(new User(email.getText().toString(), pass.getText().toString()));
        }
    }
}