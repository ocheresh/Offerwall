package com.ocheresh.offerwall;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MessagesApi {

    @GET("{id}")
    Call<String> messages(@Path("id") String categId);

}
