package tech.jonas.mondoandroid.api.model;

import com.google.gson.annotations.SerializedName;

public class PushMessage {
    @SerializedName("data")
    public final Transaction transaction;

    public PushMessage(Transaction transaction) {
        this.transaction = transaction;
    }
}
