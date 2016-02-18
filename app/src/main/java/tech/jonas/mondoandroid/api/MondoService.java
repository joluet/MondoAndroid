package tech.jonas.mondoandroid.api;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import tech.jonas.mondoandroid.api.model.Balance;
import tech.jonas.mondoandroid.api.model.TransactionList;

public interface MondoService {

    @GET("/transactions")
    Observable<TransactionList> getTransactions(@Query("account_id") String accountId, @Query("expand[]") String expand);

    @GET("/balance")
    Observable<Balance> getBalance(@Query("account_id") String accountId);

}