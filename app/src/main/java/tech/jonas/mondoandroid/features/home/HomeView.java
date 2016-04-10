package tech.jonas.mondoandroid.features.home;

import java.util.List;

import tech.jonas.mondoandroid.ui.model.Transaction;

public interface HomeView {
    void setTitle(String title);

    void startLoginActivity();

    void setTransactions(List<Transaction> transactions);
}
