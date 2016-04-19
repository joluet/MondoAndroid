package tech.jonas.mondoandroid.ui.model;

import java.util.List;
import java.util.Map;

import rx.Observable;
import tech.jonas.mondoandroid.api.model.Merchant;
import tech.jonas.mondoandroid.api.model.Transaction;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.features.home.HomeStringProvider;
import tech.jonas.mondoandroid.ui.model.UiTransaction.DeclineReason;

public class TransactionMapper {

    public static final double CENTS_PER_POUND = 100d;

    public static Observable.Transformer<TransactionList, List<UiTransaction>> map(HomeStringProvider stringProvider, Map<Merchant, Spending> spendingPerMerchant) {
        return observable -> observable
                .flatMapIterable(transactionList -> transactionList.transactions)
                .map(transaction -> map(stringProvider, transaction, spendingPerMerchant))
                .toList();
    }

    public static UiTransaction map(HomeStringProvider stringProvider, Transaction apiTransaction) {
        return map(stringProvider, apiTransaction, null);
    }

    public static UiTransaction map(HomeStringProvider stringProvider, Transaction apiTransaction, Map<Merchant, Spending> spendingPerMerchant) {
        final double amount = Math.abs(apiTransaction.amount) / CENTS_PER_POUND;
        final DeclineReason declineReason;
        if (apiTransaction.declineReason == null) {
            declineReason = null;
        } else {
            declineReason = DeclineReason.parse(apiTransaction.declineReason);
        }
        if (apiTransaction.merchant != null && spendingPerMerchant != null) {
            return new UiTransaction(apiTransaction.id, stringProvider.getFormattedGbp(amount, apiTransaction.merchant.emoji), apiTransaction.description, apiTransaction.category,
                    apiTransaction.created, declineReason, apiTransaction.merchant.name, apiTransaction.merchant.logo, spendingPerMerchant.get(apiTransaction.merchant));
        } else {
            return new UiTransaction(apiTransaction.id, stringProvider.getFormattedGbp(amount, ""), apiTransaction.description, apiTransaction.category,
                    apiTransaction.created, declineReason, apiTransaction.description, "", null);
        }
    }
}
