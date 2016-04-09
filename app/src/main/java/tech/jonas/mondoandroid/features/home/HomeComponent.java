package tech.jonas.mondoandroid.features.home;

import dagger.Component;
import tech.jonas.mondoandroid.api.ApiComponent;
import tech.jonas.mondoandroid.di.scopes.HomeScope;

@HomeScope
@Component(dependencies = ApiComponent.class, modules = HomeModule.class)
public interface HomeComponent {
    void inject(MainActivity mainActivity);
}
