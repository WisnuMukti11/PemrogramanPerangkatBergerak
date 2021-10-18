package id.example.arsip_file.API.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResgisterResponse {
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
