package com.example.mygitapplication2.infra.network;

public class ApiStatus {
    public static final int ON_MAINTENANCE  = 500;
    public static final int SERVICE_UNAVAILABLE  = 502;
    public static final int SESSION_EXPIRED = 401;

    public static final String SHOW_PROGRESS = "SHOW_PROGRESS";
    public static final String HIDE_PROGRESS = "HIDE_PROGRESS";

    private String status;
    private boolean isCancelable;

    public ApiStatus(String status, boolean isCancelable) {
        this.status = status;
        this.isCancelable = isCancelable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCancelable() {
        return isCancelable;
    }

    public void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }
}
