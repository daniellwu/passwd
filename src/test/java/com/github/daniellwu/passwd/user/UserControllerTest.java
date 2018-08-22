package com.github.daniellwu.passwd.user;

import com.github.daniellwu.passwd.group.Group;
import com.github.daniellwu.passwd.group.GroupService;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the UserController
 */
@RunWith(SpringRunner.class) // needed to use MockBean annotations
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserService userService;

    private User user1;

    @Before
    public void setup() {
        user1 = new User();
        user1.setName("name1");
        user1.setGid(0L);
        user1.setUid(0L);
        user1.setShell("shell1");
        user1.setHome("home1");
        user1.setComment("comment1");
    }

    @Test
    public void getAll() throws Exception {
        User user2 = new User();
        user2.setName("name2");
        user2.setGid(1L);
        user2.setUid(1L);
        user2.setShell("shell2");
        user2.setHome("home2");
        user2.setComment("comment2");

        List<User> expectedList = Arrays.asList(user1, user2);

        Mockito.when(userService.getAll()).thenReturn(expectedList);

        mvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(expectedList.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(user1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gid", Matchers.is((int)user1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uid", Matchers.is((int)user1.getUid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comment", Matchers.is(user1.getComment())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].home", Matchers.is(user1.getHome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].shell", Matchers.is(user1.getShell())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(user2.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].gid", Matchers.is((int)user2.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].uid", Matchers.is((int)user2.getUid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].comment", Matchers.is(user2.getComment())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].home", Matchers.is(user2.getHome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].shell", Matchers.is(user2.getShell())));
    }

    @Test
    public void query() throws Exception {
        Mockito.when(userService.query(Optional.of("name1"), Optional.of(0L), Optional.of(0L), Optional.of("comment1"), Optional.of("home1"), Optional.of("shell1"))).thenReturn(Collections.singletonList(user1));

        // confirm 200 is returned with the item that we expect
        mvc.perform(MockMvcRequestBuilders.get("/users/query?name=name1&uid=0&gid=0&shell=shell1&home=home1&comment=comment1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(user1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gid", Matchers.is((int)user1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].uid", Matchers.is((int)user1.getUid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comment", Matchers.is(user1.getComment())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].home", Matchers.is(user1.getHome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].shell", Matchers.is(user1.getShell())));
    }

    @Test
    public void get() throws Exception {
        Mockito.when(userService.get(0L)).thenReturn(Optional.of(user1));

        // confirm that 404 is returned for a wrong id
        mvc.perform(MockMvcRequestBuilders.get("/users/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // confirm 200 is returned with the item that we expect
        mvc.perform(MockMvcRequestBuilders.get("/users/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(user1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gid", Matchers.is((int)user1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uid", Matchers.is((int)user1.getUid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment", Matchers.is(user1.getComment())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.home", Matchers.is(user1.getHome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shell", Matchers.is(user1.getShell())));
    }

    @Test
    public void getUserGroups() throws Exception {
        Mockito.when(userService.get(0L)).thenReturn(Optional.of(user1));

        Group group1 = new Group();
        group1.setName("groupname1");
        group1.setGid(0);
        group1.setMember(Collections.singletonList("member1"));

        Mockito.when(groupService.query(Optional.empty(), Optional.empty(), Collections.singletonList("name1"))).thenReturn(Collections.singletonList(group1));

        // confirm 200 is returned with the item that we expect
        mvc.perform(MockMvcRequestBuilders.get("/users/0/groups")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(group1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gid", Matchers.is((int)group1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].member", Matchers.is(group1.getMember())));
    }
}