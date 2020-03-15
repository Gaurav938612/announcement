package com.example.announcement.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterFace {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAUkqTEKo:APA91bHs7hGpdhIjiMnJjRDCXjZEscwZXypOIT3EsD00OMHmlkjxMvTj0IUiAf9bMJTq2db7NLFksbn9tPunO3VD8UrjEAFaaBPDNmV6x4wCiMjoZIeiUpMqsI6KID2OKPFFZsY9vuxL"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
