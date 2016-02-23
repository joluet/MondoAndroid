package tech.jonas.mondoandroid.ui.model;

import android.content.Context;

import java.util.List;

import rx.Observable;
import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.api.model.TransactionList;
import tech.jonas.mondoandroid.ui.model.Transaction.DeclineReason;

public class TransactionMapper {

    public static final double CENTS_PER_POUND = 100d;

    public static Observable.Transformer<TransactionList, List<Transaction>> map(Context context) {
        return observable -> observable
                .flatMapIterable(transactionList -> transactionList.transactions)
                .map(transaction -> map(context, transaction))
                .toList();
    }

    private static Transaction map(Context context, tech.jonas.mondoandroid.api.model.Transaction apiTransaction) {
        final double amount = apiTransaction.amount / CENTS_PER_POUND;
        final DeclineReason declineReason;
        if (apiTransaction.declineReason == null) {
            declineReason = null;
        } else {
            declineReason = DeclineReason.parse(apiTransaction.declineReason);
        }
        if (apiTransaction.merchant != null) {
            return new Transaction(context.getString(R.string.formatted_amount_gbp, amount, apiTransaction.merchant.emoji), apiTransaction.description, apiTransaction.category,
                    apiTransaction.settled, declineReason, apiTransaction.merchant.name, apiTransaction.merchant.logo);
        } else {
            return new Transaction(context.getString(R.string.formatted_amount_gbp, amount, ""), apiTransaction.description, apiTransaction.category,
                    apiTransaction.settled, declineReason, apiTransaction.description, "");
        }
    }
}
