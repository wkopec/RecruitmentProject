package com.recruitmentproject;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {

    @GET("/geo")
    Call<List<LocationList>> getLocations(
            @Query("lat") double latitude,
            @Query("lng") double longitude
    );

}
