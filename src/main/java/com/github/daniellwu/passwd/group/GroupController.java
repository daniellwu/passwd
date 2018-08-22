package com.github.daniellwu.passwd.group;

import com.github.daniellwu.passwd.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for the /groups endpoints
 */
@RequestMapping("/groups")
@RestController
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Retrieves all groups
     *
     * @return list of groups
     * @throws IOException if file cannot be processed
     */
    @GetMapping
    public List<Group> getAll() throws IOException {
        return groupService.getAll();
    }

    @GetMapping(path = "/query")
    public List<Group> query(Optional<String> name, Optional<Long> gid, String[] member) throws IOException {
        return groupService.query(name, gid, Arrays.asList(member));
    }

    /**
     * Get a single group
     *
     * @param gid group id
     * @return group with the specified group id
     * @throws IOException           if the file cannot be processed
     * @throws UserNotFoundException if the group cannot be found
     */
    @GetMapping(path = "/{gid}")
    public Group get(@PathVariable long gid) throws IOException {
        Optional<Group> optionalGroup = groupService.get(gid);
        if (!optionalGroup.isPresent()) {
            throw new GroupNotFoundException();
        }
        return optionalGroup.get();
    }
}
