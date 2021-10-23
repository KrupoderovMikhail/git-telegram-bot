package com.krupoderov.telegrambot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class GiteaWebHook {

    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("ref")
    @Expose
    private String ref;
    @SerializedName("before")
    @Expose
    private String before;
    @SerializedName("after")
    @Expose
    private String after;
    @SerializedName("compare_url")
    @Expose
    private String compareUrl;
    @SerializedName("commits")
    @Expose
    private List<Commit> commits = null;
    @SerializedName("repository")
    @Expose
    private Repository repository;
    @SerializedName("pusher")
    @Expose
    private Pusher pusher;
    @SerializedName("sender")
    @Expose
    private Sender sender;
}
