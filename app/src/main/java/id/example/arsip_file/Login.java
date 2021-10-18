package id.example.arsip_file;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import id.example.arsip_file.API.APIRequestData;
import id.example.arsip_file.API.RetroServer;
import id.example.arsip_file.API.response.ResponseLogin;
import id.example.arsip_file.API.response.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    EditText email, pass;
    Button login;
    TextView register;
    ProgressDialog loading;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.etEmail);
        pass = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.tvCreateAccount);

        editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });
    }

    public void createLogin(User user){
        loading = ProgressDialog.show(
                Login.this,
                null,
                "Loading...",
                true,
                true
        );
        APIRequestData apiRequestData = RetroServer.konekRetrofit().create(APIRequestData.class);
        Call<ResponseLogin> call = apiRequestData.createLogin(user);
        call.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                if (response.code()==200){
                    loading.dismiss();
                    editor.putString("token", response.body().getToken());
                    editor.apply();
                    Toast.makeText(Login.this, response.body().getStatus(), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, Dashboard.class);
//                    i.putExtra("token", response.body().getToken());
                    startActivity(i);
                    finish();
                }else {
                    Toast.makeText(Login.this, response.code() + " "+response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                Toast.makeText(Login.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(){
        if (email.getText().toString().equals("")){
            Toast.makeText(this, "Cek Form", Toast.LENGTH_SHORT).show();
        }else if (pass.getText().toString().equals("")){
            Toast.makeText(this, "Cek Form", Toast.LENGTH_SHORT).show();
        }else {
            createLogin(new User(email.getText().toString(), pass.getText().toString()));
        }
    }
}