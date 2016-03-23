package tech.jonas.mondoandroid.api;

import java.util.Map;

import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import tech.jonas.mondoandroid.api.model.AccessTokenResponse;
import tech.jonas.mondoandroid.api.model.Accounts;
import tech.jonas.mondoandroid.api.model.Balance;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.api.model.WebhookResponse;

public interface MondoService {

    @GET("/transactions")
    Observable<TransactionList> getTransactions(@Query("account_id") String accountId, @Query("expand[]") String expand);

    @GET("/balance")
    Observable<Balance> getBalance(@Query("account_id") String accountId);

    @GET("/accounts")
    Observable<Accounts> getAccounts();

    @FormUrlEncoded
    @POST("/oauth2/token")
    Observable<AccessTokenResponse> getAccessToken(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/webhooks")
    Observable<WebhookResponse> registerWebhook(@Field("account_id") String accountId, @Field("url") String url);

}