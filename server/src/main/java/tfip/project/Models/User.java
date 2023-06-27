package tfip.project.Models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class User {

    String userId;
    String username;
    
    public User() {
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("userId",this.getUserId())
                .add("username",this.getUsername())
                .build();
    }

}
