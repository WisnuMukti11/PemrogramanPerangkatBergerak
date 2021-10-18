package id.example.arsip_file.API.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("password")
    @Expose
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
