package com.github.daniellwu.passwd.user;

import com.github.daniellwu.passwd.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class that handles parsing passwd files (e.g. /etc/passwd) and retrieves them
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final String path;

    /**
     * Constructor
     *
     * @param path path injected from application.properties
     */
    public UserService(@Value("${passwd-path}") String path) {
        logger.debug("path: {}", path);
        this.path = path;
    }

    /**
     * Returns a list of users filtered by the queries provided
     *
     * @param name    if present, filters the user by name
     * @param uid     if present, filters the user by user id
     * @param gid     if present, filters the user by group id
     * @param comment if present, filters the user by comment
     * @param home    if present, filters the user by home
     * @param shell   if present, filters the user by shell
     * @return list of users
     * @throws IOException if file cannot be processed
     */
    public List<User> query(Optional<String> name, Optional<Long> uid, Optional<Long> gid, Optional<String> comment,
                            Optional<String> home, Optional<String> shell) throws IOException {
        Stream<User> allUsers = getAll().stream();
        if (name.isPresent()) {
            allUsers = allUsers.filter(user -> user.getName().equals(name.get()));
        }
        if (uid.isPresent()) {
            allUsers = allUsers.filter(user -> user.getUid() == (uid.get()));
        }
        if (gid.isPresent()) {
            allUsers = allUsers.filter(user -> user.getGid() == (gid.get()));
        }
        if (comment.isPresent()) {
            allUsers = allUsers.filter(user -> user.getComment().equals(comment.get()));
        }
        if (home.isPresent()) {
            allUsers = allUsers.filter(user -> user.getHome().equals(home.get()));
        }
        if (shell.isPresent()) {
            allUsers = allUsers.filter(user -> user.getShell().equals(shell.get()));
        }
        return allUsers.collect(Collectors.toList());
    }

    /**
     * Get all users
     *
     * @return list of users
     * @throws IOException if file cannot be processed
     */
    public List<User> getAll() throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        List<User> users = new ArrayList<>();
        while ((st = br.readLine()) != null) {
            if (st.length() != 0 && st.charAt(0) != Constant.COMMENT) {
                users.add(convert(st));
            }
        }
        return users;
    }

    /**
     * Get a single user
     *
     * @param uid user id
     * @return an Optional user (i.e. Optional.empty() if not found)
     * @throws IOException if file cannot be processed
     */
    public Optional<User> get(long uid) throws IOException {
        return getAll().stream().filter(u -> u.getUid() == uid).findAny();
    }

    /**
     * Converts a single line in teh format used by /etc/passwd. name:password:uid:gid:comment:home:shell
     * Must not be null or be a comment line
     *
     * @param input a single line entry in the correct format
     * @return User if conversion is successful
     * @throws IOException if the line cannot be processed
     */
    private User convert(String input) throws IOException {
        String[] fields = input.split(Constant.PASSWD_AND_GROUP_FIELD_DELIMIETER);
        User user = new User();
        if (fields.length == 7) {
            user.setName(fields[0]);
            // field 1 is the masked password field that we're skipping
            user.setUid(Long.parseLong(fields[2]));
            user.setGid(Long.parseLong(fields[3]));
            user.setComment(fields[4]);
            user.setHome(fields[5]);
            user.setShell(fields[6]);
        } else {
            logger.error("User has {} fields. Exactly 7 must be present");
            throw new IOException("bad passwd line " + input);
        }
        return user;
    }
}
