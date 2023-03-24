package ibf2022.batch2.ssf.frontcontroller.models;

import java.io.Serializable;
import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.validation.constraints.Size;

public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;

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

    public JsonObject toJsonObject() {
        return Json.createObjectBuilder()
                    .add("username", this.getUsername())
                    .add("password", this.getPassword())
                    .add("failedLogInAttempts",String.valueOf(this.getFailedLogInAttempts()))
                    .add("authenticated", String.valueOf(this.isAuthenticated()))
                    .add("locked", String.valueOf(this.isLocked()))
                    .build();
    }

    public static User jsonStringToJavaObject(String json) {

        User user = new User(); 
        
        if (json!=null) {
            StringReader sr = new StringReader(json);
			JsonReader jsonReader = Json.createReader(sr);
			JsonObject jsonObject = jsonReader.readObject();
            
            user.setUsername(jsonObject.getString("username"));
            user.setPassword(jsonObject.getString("password"));
            user.setFailedLogInAttempts(Integer.parseInt(jsonObject.getString("failedLogInAttempts")));
            user.setAuthenticated(Boolean.valueOf(jsonObject.getString("authenticated")));
            user.setLocked(Boolean.valueOf(jsonObject.getString("locked")));
        }

        return user;
    }
}
