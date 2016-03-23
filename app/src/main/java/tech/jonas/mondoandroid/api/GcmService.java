package tech.jonas.mondoandroid.api;

import retrofit2.http.Body;
import retrofit2.http.PUT;
import rx.Observable;
import tech.jonas.mondoandroid.api.model.RegistrationAnswer;
import tech.jonas.mondoandroid.api.model.RegistrationToken;

public interface GcmService {

    @PUT("/token")
    Observable<RegistrationAnswer> uploadToken(@Body RegistrationToken token);

}