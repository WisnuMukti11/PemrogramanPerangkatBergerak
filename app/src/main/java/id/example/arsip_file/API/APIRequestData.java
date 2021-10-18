package id.example.arsip_file.API;

import java.util.List;
import java.util.Map;

import id.example.arsip_file.API.response.ResgisterResponse;
import id.example.arsip_file.API.response.ResponseAddArsip;
import id.example.arsip_file.API.response.ResponseGetArsip;
import id.example.arsip_file.API.response.ResponseKategori;
import id.example.arsip_file.API.response.ResponseLogin;
import id.example.arsip_file.API.response.ResponseUpdate;
import id.example.arsip_file.API.response.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface APIRequestData {

    @POST("register")
    Call<ResgisterResponse> createRegister(
            @Body User user);

    @POST("login")
    Call<ResponseLogin> createLogin(
            @Body User user);

    @Multipart
    @POST("postarsip")
    Call<ResponseAddArsip> uploadArsip(
            @Header("Authorization") String token,
            @Part MultipartBody.Part photo,
            @PartMap Map<String, RequestBody> String);

    @GET("getkategori")
    Call<List<ResponseKategori>> listKategori(
            @Header("Authorization") String token);

    @GET("getarsip")
    Call<List<ResponseGetArsip>> getArsip(
            @Header("Authorization") String token);

    @Multipart
    @POST("editarsip")
    Call<ResponseUpdate> editArsip(
            @Header("Authorization") String token,
            @Part MultipartBody.Part photo,
            @PartMap Map<String, RequestBody> String);

    @POST("destroy")
    Call<ResponseUpdate> hapusArsip(
            @Header("Authorization") String token,
            @Query("id") int id);
}
