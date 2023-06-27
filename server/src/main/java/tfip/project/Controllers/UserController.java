package tfip.project.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import tfip.project.Models.User;
import tfip.project.Services.UserService;

@Controller
@RequestMapping(path="/api/user")
public class UserController {

    @Autowired
    private UserService userSvc;
    
    @GetMapping(path="/register")
    public ResponseEntity<JsonObject> registerUser(@RequestParam String username, @RequestParam String email) {
        Optional<String> opt = userSvc.registerUser(username, email);
        if (opt.isEmpty()){
            JsonObject jo = Json.createObjectBuilder().add("msg","This username/email has been taken!").build();
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
        }
        String userId = opt.get();
        JsonObject jo = Json.createObjectBuilder().add("msg",userId).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
    }

    @GetMapping(path="/login")
    public ResponseEntity<JsonObject> checkUser(@RequestParam String username) {
        Optional<String> opt = userSvc.checkUsername(username);
        if (opt.isEmpty()){
            JsonObject jo = Json.createObjectBuilder().add("msg","This username does not exist!").build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
        }
        String userId = opt.get();
        JsonObject jo = Json.createObjectBuilder().add("msg",userId).build();
            return ResponseEntity.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @GetMapping(path="/follow/{userId}")
    public ResponseEntity<JsonObject> follow(@RequestParam String authorId, @PathVariable String userId) {
        boolean insert = userSvc.follow(userId, authorId);
        if (insert){
            JsonObject jo = Json.createObjectBuilder().add("msg","Follow succcessful").build();
            return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg","Follow unsuccessful").build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
    }

    @GetMapping(path="/unfollow/{userId}")
    public ResponseEntity<JsonObject> unfollow(@RequestParam String authorId, @PathVariable String userId) {
        boolean delete = userSvc.unfollow(userId, authorId);
        if (delete){
            JsonObject jo = Json.createObjectBuilder().add("msg","Unfollow successful").build();
            return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg","Unfollow unsuccessful").build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
    }

    @GetMapping(path="/checkfollow/{userId}")
    public ResponseEntity<JsonObject> checkfollow(@RequestParam String authorId, @PathVariable String userId) {
        boolean check = userSvc.checkfollow(userId, authorId);
        JsonObject jo = Json.createObjectBuilder().add("msg",check).build();
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
    }

    @GetMapping(path="/listfollowee/{userId}")
    public ResponseEntity<String> listfollowee(@PathVariable String userId) {
        Optional<List<User>> opt = userSvc.listfollowee(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No followees");
        }
        List<User> followees = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (User followee : followees) {
            jarrBuilder.add(followee.toJSON());
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

    @GetMapping(path="/listfollower/{userId}")
    public ResponseEntity<String> listfollower(@PathVariable String userId) {
        Optional<List<User>> opt = userSvc.listfollower(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No followers");
        }
        List<User> followers = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (User follower : followers) {
            jarrBuilder.add(follower.toJSON());
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

    @GetMapping(path="/privilege/{userId}")
    public ResponseEntity<JsonObject> checkPrivilege(@PathVariable String userId) {
        boolean check = userSvc.checkPrivilege(userId);
        JsonObject jo = Json.createObjectBuilder().add("msg",check).build();
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
    }

    @GetMapping(path="/addprivilege/{userId}")
    public ResponseEntity<JsonObject> addPrivilegeToUser(@PathVariable String userId) {
        boolean update = userSvc.addPrivilege(userId);
        if (update){
            JsonObject jo = Json.createObjectBuilder().add("msg","Add privilege succcessful").build();
            return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg","Add privilege unsuccessful").build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(jo);
    }

}
