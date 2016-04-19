package tech.jonas.mondoandroid.ui.model;

import java.io.Serializable;

public class Spending implements Serializable {
    public final long totalSpend;
    public final long averageSpend;
    public final long noOfTransactions;

    public Spending(long totalSpend, long averageSpend, long noOfTransactions) {
        this.totalSpend = totalSpend;
        this.averageSpend = averageSpend;
        this.noOfTransactions = noOfTransactions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Spending spending = (Spending) o;

        if (totalSpend != spending.totalSpend) return false;
        if (averageSpend != spending.averageSpend) return false;
        return noOfTransactions == spending.noOfTransactions;

    }

    @Override
    public String toString() {
        return "Spending{" +
                "totalSpend=" + totalSpend +
                ", averageSpend=" + averageSpend +
                ", noOfTransactions=" + noOfTransactions +
                '}';
    }
}
