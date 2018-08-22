package com.github.daniellwu.passwd.group;

import com.github.daniellwu.passwd.Helper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the GroupService class
 */
public class GroupServiceTest {

    private GroupService groupService;

    @Before
    public void setUp() throws Exception {
        groupService = new GroupService(Helper.getPathFromResource("group"));
    }

    @Test(expected = FileNotFoundException.class)
    public void badPath() throws IOException {
        groupService = new GroupService(Helper.getPathFromResource("")); //empty file name
        groupService.getAll();
    }

    @Test(expected = IOException.class)
    public void badFormat() throws IOException {
        groupService = new GroupService(Helper.getPathFromResource("group-bad"));
        groupService.getAll();
    }

    @Test
    public void getAll() throws IOException {
        List<Group> groups = groupService.getAll();
        Assert.assertEquals(125, groups.size());
    }

    @Test
    public void get() throws IOException {
        // test found case
        Optional<Group> group = groupService.get(0);
        Assert.assertTrue(group.isPresent());

        // test not found case
        group = groupService.get(-99);
        Assert.assertFalse(group.isPresent());
    }

    @Test
    public void query() throws IOException {
        List<Group> groups = groupService.query(Optional.of("daemon"), Optional.of(1L), Collections.singletonList("root"));
        Assert.assertEquals(1, groups.size());
    }
}