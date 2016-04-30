package tech.jonas.mondoandroid.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import tech.jonas.mondoandroid.R;

public class AmountView extends LinearLayout {
    private TextView poundsView;
    private TextView centsView;

    public AmountView(Context context) {
        super(context);
        init(context);
    }

    public AmountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AmountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.amount_view, this);

        try {
            poundsView = (TextView) findViewById(R.id.tv_amount);
            centsView = (TextView) findViewById(R.id.tv_amount_cents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAmount(long pounds, long cents) {
        poundsView.setText(String.valueOf(pounds));
        centsView.setText(getContext().getString(R.string.amount_cents, cents));
    }

    public void setTextColor(int color) {
        poundsView.setTextColor(color);
        centsView.setTextColor(color);
    }
}
