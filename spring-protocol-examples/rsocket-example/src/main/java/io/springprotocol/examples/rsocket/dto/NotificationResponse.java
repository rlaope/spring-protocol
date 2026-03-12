package io.springprotocol.examples.rsocket.dto;

public class NotificationResponse {

    private String status;
    private String notificationId;

    public NotificationResponse() {
    }

    public NotificationResponse(String status, String notificationId) {
        this.status = status;
        this.notificationId = notificationId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
