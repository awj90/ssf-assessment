package ibf2022.batch2.ssf.frontcontroller.models;

import java.io.Serializable;

import jakarta.validation.constraints.Size;

public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final long MAX_ALLOWABLE_LOG_IN_ATTEMPTS = 3L;

    @Size(min=2, message="Username must be at least 2 characters in length")
    private String username;

    @Size(min=2, message="Password must be at least 2 characters in length")
    private String password;

    private int failedLogInAttempts;
    private boolean authenticated;
    private boolean locked;

    public User() {}

    public User(String username) {
        this.username = username;
        this.failedLogInAttempts = 0;
        this.authenticated = false;
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
    public int getFailedLogInAttempts() {
        return failedLogInAttempts;
    }
    public void setFailedLogInAttempts(int failedLogInAttempts) {
        this.failedLogInAttempts = failedLogInAttempts;
    }
    public boolean isAuthenticated() {
        return authenticated;
    }
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
    public void incrFailedLogInAttempts() {
        failedLogInAttempts++;
    }
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
    }    
}
