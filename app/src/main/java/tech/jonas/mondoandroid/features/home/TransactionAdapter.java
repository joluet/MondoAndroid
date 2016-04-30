package tech.jonas.mondoandroid.features.home;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderAdapter;
import tech.jonas.mondoandroid.R;
import tech.jonas.mondoandroid.ui.custom.AmountView;
import tech.jonas.mondoandroid.ui.model.UiTransaction;
import tech.jonas.mondoandroid.utils.DateUtils;

public class TransactionAdapter extends RecyclerView.Adapter implements StickyHeaderAdapter<RecyclerView.ViewHolder> {

    private final Context appContext;
    private List<UiTransaction> transactionList = new LinkedList<>();
    private OnTransactionClickListener onTransactionClickListener;

    public TransactionAdapter(Context appContext) {
        this.appContext = appContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final UiTransaction transaction = transactionList.get(position);
        TransactionViewHolder transactionViewHolder = (TransactionViewHolder) holder;
        if (transaction.isDeclined()) {
            transactionViewHolder.tvTitle.setText(appContext.getString(R.string.transaction_declined, transaction.merchantName));
        } else if(transaction.hasMerchant()) {
            transactionViewHolder.tvTitle.setText(transaction.merchantName);
        } else {
            transactionViewHolder.tvTitle.setText(transaction.description);
        }
        if(transaction.isPositiveAmount) {
            transactionViewHolder.amountView.setTextColor(ContextCompat.getColor(appContext, R.color.green));
        } else {
            transactionViewHolder.amountView.setTextColor(ContextCompat.getColor(appContext, R.color.colorPrimaryDark));
        }
        transactionViewHolder.amountView.setAmount(transaction.pounds, transaction.cents);
        Glide.with(appContext)
                .load(transaction.merchantLogo)
                .centerCrop()
                .into(transactionViewHolder.ivLogo);

        final int transactionLogoSize = appContext.getResources().getDimensionPixelSize(R.dimen.transaction_merchant_logo_size);
        Glide.with(appContext)
                .load(transaction.merchantLogo)
                .centerCrop()
                .preload(transactionLogoSize, transactionLogoSize);

        transactionViewHolder.itemView.setOnClickListener(v -> {
            if (onTransactionClickListener != null) {
                onTransactionClickListener.onClick(transaction, transactionViewHolder.ivLogo,
                        transactionViewHolder.tvTitle, transactionViewHolder.amountView);
            }
        });
    }

    public void setTransactions(List<UiTransaction> categories) {
        int previousCount = transactionList.size();
        transactionList.clear();
        notifyItemRangeRemoved(0, previousCount);
        transactionList.addAll(categories);
        notifyItemRangeInserted(0, categories.size());
    }

    public void setOnTransactionClickListener(OnTransactionClickListener onTransactionClickListener) {
        this.onTransactionClickListener = onTransactionClickListener;
    }

    @Override
    public long getHeaderId(int position) {
        try {
            return DateUtils.getDayOfYear(transactionList.get(position).created);
        } catch (ParseException e) {
            throw new RuntimeException("Couldn't parse date: " + transactionList.get(position).created);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_date_header, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            ((HeaderViewHolder) holder).headerTitleView.setText(DateUtils.getDateString(transactionList.get(position).created, appContext));
        } catch (ParseException e) {
            throw new RuntimeException("Couldn't parse date: " + transactionList.get(position).created);
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    public interface OnTransactionClickListener {
        void onClick(UiTransaction transaction, View... views);
    }

    private class TransactionViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvTitle;
        public final AmountView amountView;
        public final ImageView ivLogo;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            ivLogo = (ImageView) itemView.findViewById(R.id.iv_logo);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            amountView = (AmountView) itemView.findViewById(R.id.amount);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        public final TextView headerTitleView;

        public HeaderViewHolder(View view) {
            super(view);
            headerTitleView = (TextView) itemView.findViewById(R.id.tv_title);
        }
    }
}
