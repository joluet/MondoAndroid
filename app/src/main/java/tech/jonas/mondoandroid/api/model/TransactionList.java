package tech.jonas.mondoandroid.api.model;

import java.util.List;

public class TransactionList {
    public final List<Transaction> transactions;

    public TransactionList(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
