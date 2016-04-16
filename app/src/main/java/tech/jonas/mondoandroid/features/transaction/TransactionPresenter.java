package tech.jonas.mondoandroid.features.transaction;

import tech.jonas.mondoandroid.ui.model.UiTransaction;

public interface TransactionPresenter {

    void onBindView(UiTransaction transaction);

    void onUnBindView();

}
