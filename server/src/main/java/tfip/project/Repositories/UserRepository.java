package tfip.project.Repositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import tfip.project.Models.User;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // USER_INFO table
    private static final String SQL_INSERT_USER = "insert ignore into user_info (user_id, username, email, privilege) values (?,?,?,?)";
    private static final String SQL_SELECT_USER = "select * from user_info where username=?";
    private static final String SQL_SELECT_USERID = "select * from user_info where user_id=?";
    private static final String SQL_UPDATE_USER_PRIVILEGE = "update user_info set privilege=true where user_id=?";
    private static final String SQL_SELECT_USER_PRIVILEGE = "select privilege from user_info where user_id=?";

    public boolean insertUser(String userId, String username, String email) {
        int insert = jdbcTemplate.update(SQL_INSERT_USER, userId, username, email, false);
        return insert>0;
    }

    public Optional<String> getUserIdByName(String username) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_USER, username);
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.of(rs.getString("user_id"));
    }

    public Optional<String> getUsernameById(String userId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_USERID, userId);
        if (!rs.next()) {
            return Optional.empty();
        }
        return Optional.of(rs.getString("username"));
    }

    public boolean addPrivilege(String userId) {
        int update = jdbcTemplate.update(SQL_UPDATE_USER_PRIVILEGE, userId);
        return update>0;
    }

    public boolean checkPrivilege(String userId) {
        return jdbcTemplate.queryForObject(SQL_SELECT_USER_PRIVILEGE, boolean.class, userId);
    }

    // FOLLOW_INFO table
    private static final String SQL_INSERT_FOLLOW = "insert ignore into follow_info (follower_id, followee_id) values (?,?)";
    private static final String SQL_DELETE_UNFOLLOW = "delete from follow_info where follower_id=? and followee_id=?";
    private static final String SQL_SELECT_CHECKFOLLOW = "select * from follow_info where follower_id=? and followee_id=?";
    private static final String SQL_SELECT_LISTFOLLOWEE = "select * from follow_info where follower_id=?";
    private static final String SQL_SELECT_LISTFOLLOWER = "select * from follow_info where followee_id=?";
    
    public boolean follow(String userId, String authorId) {
        int insert = jdbcTemplate.update(SQL_INSERT_FOLLOW, userId, authorId);
        return insert>0;
    }

    public boolean unfollow(String userId, String authorId) {
        int delete = jdbcTemplate.update(SQL_DELETE_UNFOLLOW, userId, authorId);
        return delete>0;
    }

    public boolean checkfollow(String userId, String authorId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_CHECKFOLLOW, userId, authorId);
        if (!rs.next()) {
            return false;
        }
        return true;
    }

    public Optional<List<User>> listfollowee(String userId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_LISTFOLLOWEE, userId);
        List<User> followees = new LinkedList<>();
        while (rs.next()) {
            User followee = new User();
            String followee_id = rs.getString("followee_id");
            followee.setUserId(followee_id);
            followee.setUsername(this.getUsernameById(followee_id).get());
            followees.add(followee);
        }
        if (followees==null || followees.size()<=0){
            return Optional.empty();
        }
        return Optional.of(followees);
    }

    public Optional<List<User>> listfollower(String userId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_LISTFOLLOWER, userId);
        List<User> followers = new LinkedList<>();
        while (rs.next()) {
            User follower = new User();
            String follower_id = rs.getString("follower_id");
            follower.setUserId(follower_id);
            follower.setUsername(this.getUsernameById(follower_id).get());
            followers.add(follower);
        }
        if (followers==null || followers.size()<=0){
            return Optional.empty();
        }
        return Optional.of(followers);
    }

}
