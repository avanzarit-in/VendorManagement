package com.avanzarit.apps.gst.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("customerMail")
@Component
public class CustomerMailProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String auth;
    private String starttls;
    private boolean sendEmail;
    private String fromMailId;
    private boolean debug;
    private String updatePasswordMessage;
    private String updatePasswordSubject;
    private String loginReminderMessage;
    private String loginReminderSubject;
    private String signature;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getStarttls() {
        return starttls;
    }

    public void setStarttls(String starttls) {
        this.starttls = starttls;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public void setSendEmail(boolean sendEmail) {
        this.sendEmail = sendEmail;
    }

    public String getFromMailId() {
        return fromMailId;
    }

    public void setFromMailId(String fromMailId) {
        this.fromMailId = fromMailId;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isFromMailIdDifferent() {
        return !username.equals(fromMailId);
    }

    public String getUpdatePasswordMessage() {
        return updatePasswordMessage;
    }

    public void setUpdatePasswordMessage(String updatePasswordMessage) {
        this.updatePasswordMessage = updatePasswordMessage;
    }

    public String getUpdatePasswordSubject() {
        return updatePasswordSubject;
    }

    public void setUpdatePasswordSubject(String updatePasswordSubject) {
        this.updatePasswordSubject = updatePasswordSubject;
    }

    public String getLoginReminderMessage() {
        return loginReminderMessage;
    }

    public void setLoginReminderMessage(String loginReminderMessage) {
        this.loginReminderMessage = loginReminderMessage;
    }

    public String getLoginReminderSubject() {
        return loginReminderSubject;
    }

    public void setLoginReminderSubject(String loginReminderSubject) {
        this.loginReminderSubject = loginReminderSubject;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
