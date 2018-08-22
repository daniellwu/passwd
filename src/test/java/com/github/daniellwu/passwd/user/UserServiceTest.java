package com.github.daniellwu.passwd.user;

import com.github.daniellwu.passwd.Helper;
import com.github.daniellwu.passwd.group.Group;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the UserService class
 */
public class UserServiceTest {

    private UserService userService;

    @Before
    public void setUp() throws Exception {
        userService = new UserService(Helper.getPathFromResource("passwd"));
    }

    @Test(expected = FileNotFoundException.class)
    public void badPath() throws IOException {
        userService = new UserService(Helper.getPathFromResource("")); //empty file name
        userService.getAll();
    }

    @Test(expected = IOException.class)
    public void badFormat() throws IOException {
        userService = new UserService(Helper.getPathFromResource("passwd-bad"));
        userService.getAll();
    }

    @Test
    public void query() throws IOException {
        List<User> users = userService.query(Optional.of("nobody"), Optional.of(-2L), Optional.of(-2L),
                Optional.of("Unprivileged User"), Optional.of("/var/empty"), Optional.of("/usr/bin/false"));
        Assert.assertEquals(1, users.size());
    }

    @Test
    public void getAll() throws IOException {
        List<User> users = userService.getAll();
        Assert.assertEquals(98, users.size());
    }

    @Test
    public void get() throws IOException {
        // test found case
        Optional<User> user = userService.get(0);
        Assert.assertTrue(user.isPresent());

        // test not found case
        user = userService.get(-99);
        Assert.assertFalse(user.isPresent());
    }
}