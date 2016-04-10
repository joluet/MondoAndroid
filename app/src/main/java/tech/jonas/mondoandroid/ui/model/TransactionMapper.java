package tech.jonas.mondoandroid.ui.model;

import java.util.List;

import rx.Observable;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.ui.model.Transaction.DeclineReason;

public class TransactionMapper {

    public static final double CENTS_PER_POUND = 100d;

    public static Observable.Transformer<TransactionList, List<Transaction>> map(HomeStringProvider stringProvider) {
        return observable -> observable
                .flatMapIterable(transactionList -> transactionList.transactions)
                .map(transaction -> map(stringProvider, transaction))
                .toList();
    }

    public static Transaction map(HomeStringProvider stringProvider, tech.jonas.mondoandroid.api.model.Transaction apiTransaction) {
        final double amount = apiTransaction.amount / CENTS_PER_POUND;
        final DeclineReason declineReason;
        if (apiTransaction.declineReason == null) {
            declineReason = null;
        } else {
            declineReason = DeclineReason.parse(apiTransaction.declineReason);
        }
        if (apiTransaction.merchant != null) {
            return new Transaction(stringProvider.getFormattedGbp(amount, apiTransaction.merchant.emoji), apiTransaction.description, apiTransaction.category,
                    apiTransaction.created, declineReason, apiTransaction.merchant.name, apiTransaction.merchant.logo);
        } else {
            return new Transaction(stringProvider.getFormattedGbp(amount, ""), apiTransaction.description, apiTransaction.category,
                    apiTransaction.created, declineReason, apiTransaction.description, "");
        }
    }
}
