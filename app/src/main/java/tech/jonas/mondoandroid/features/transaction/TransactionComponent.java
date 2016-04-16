package tech.jonas.mondoandroid.features.transaction;

import dagger.Component;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.di.scopes.HomeScope;

@HomeScope
@Component(dependencies = ApiComponent.class, modules = TransactionModule.class)
public interface TransactionComponent {
    void inject(TransactionActivity transactionActivity);
}
