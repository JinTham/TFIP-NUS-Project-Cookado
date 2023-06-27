package tfip.project.Services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tfip.project.Models.User;
import tfip.project.Repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailSvc;
    
    public Optional<String> registerUser(String username, String email) {
        String userId = UUID.randomUUID().toString().substring(0, 8);
        if (userRepo.insertUser(userId, username, email)) {
            emailSvc.sendRegisterEmail(username, email);
            return Optional.of(userId);
        }
        return Optional.empty();
    }

    public Optional<String> checkUsername(String username) {
        return userRepo.getUserIdByName(username);
    }

    public Optional<String> getUsernameById(String userId) {
        return userRepo.getUsernameById(userId);
    }

    public boolean follow(String userId, String authorId) {
        return userRepo.follow(userId, authorId);
    }

    public boolean unfollow(String userId, String authorId) {
        return userRepo.unfollow(userId, authorId);
    }

    public boolean checkfollow(String userId, String authorId) {
        return userRepo.checkfollow(userId, authorId);
    }

    public Optional<List<User>> listfollowee(String userId) {
        return userRepo.listfollowee(userId);
    }

    public Optional<List<User>> listfollower(String userId) {
        return userRepo.listfollower(userId);
    }

    public boolean addPrivilege(String userId) {
        return userRepo.addPrivilege(userId);
    }

    public boolean checkPrivilege(String userId) {
        return userRepo.checkPrivilege(userId);
    }

}
