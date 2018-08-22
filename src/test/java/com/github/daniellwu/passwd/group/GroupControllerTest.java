package com.github.daniellwu.passwd.group;

import com.github.daniellwu.passwd.user.User;
import com.github.daniellwu.passwd.user.UserService;
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
 * Tests the GroupController
 */
@RunWith(SpringRunner.class) // needed to use MockBean annotations
@WebMvcTest(GroupController.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserService userService;

    private Group group1;

    @Before
    public void setup() {
        group1 = new Group();
        group1.setName("name1");
        group1.setGid(0);
        group1.setMember(Collections.singletonList("member1"));
    }

    @Test
    public void getAll() throws Exception {
        Group group2 = new Group();
        group2.setName("name2");
        group2.setGid(1);
        group2.setMember(Collections.singletonList("member2"));

        List<Group> expectedList = Arrays.asList(group1, group2);

        Mockito.when(groupService.getAll()).thenReturn(expectedList);

        mvc.perform(MockMvcRequestBuilders.get("/groups")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(expectedList.size())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(group1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gid", Matchers.is((int)group1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].member", Matchers.is(group1.getMember())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", Matchers.is(group2.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].gid", Matchers.is((int)group2.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].member", Matchers.is(group2.getMember())));
    }

    @Test
    public void query() throws Exception {
        Mockito.when(groupService.query(Optional.of("name1"), Optional.of(0L), Arrays.asList("member1"))).thenReturn(Collections.singletonList(group1));

        // confirm 200 is returned with the item that we expect
        mvc.perform(MockMvcRequestBuilders.get("/groups/query?name=name1&gid=0&member=member1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is(group1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].gid", Matchers.is((int)group1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].member", Matchers.is(group1.getMember())));
    }

    @Test
    public void get() throws Exception {
        Mockito.when(groupService.get(0L)).thenReturn(Optional.of(group1));

        // confirm that 404 is returned for a wrong id
        mvc.perform(MockMvcRequestBuilders.get("/groups/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        // confirm 200 is returned with the item that we expect
        mvc.perform(MockMvcRequestBuilders.get("/groups/0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.is(group1.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.gid", Matchers.is((int)group1.getGid())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.member", Matchers.is(group1.getMember())));
    }
}