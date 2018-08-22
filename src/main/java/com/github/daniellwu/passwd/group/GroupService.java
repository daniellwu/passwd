package com.github.daniellwu.passwd.group;

import com.github.daniellwu.passwd.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class that handles parsing group files (e.g. /etc/group) and retrieves them
 */
@Service
public class GroupService {

    public static final String GROUP_MEMBER_DELIMIETER = ",";
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private final String path;

    /**
     * Constructor
     *
     * @param path path injected from application.properties
     */
    public GroupService(@Value("${group-path}") String path) {
        logger.debug("path: {}", path);
        this.path = path;
    }

    /**
     * Get all groups
     *
     * @return list of groups
     * @throws IOException if file cannot be processed
     */
    public List<Group> getAll() throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        List<Group> groups = new ArrayList<>();
        while ((st = br.readLine()) != null) {
            if (st.length() != 0 && st.charAt(0) != Constant.COMMENT) {
                groups.add(convert(st));
            }
        }
        return groups;
    }

    /**
     * Returns a list of groups filtered by the queries provided
     *
     * @param name   if present, filters the group by name
     * @param gid    if present, filters the group by group id
     * @param member if present, filters the group by its members
     * @return list of groups
     * @throws IOException if file cannot be processed
     */
    public List<Group> query(Optional<String> name, Optional<Long> gid, List<String> member) throws IOException {
        Stream<Group> allGroups = getAll().stream();
        if (name.isPresent()) {
            allGroups = allGroups.filter(group -> group.getName().equals(name.get()));
        }
        if (gid.isPresent()) {
            allGroups = allGroups.filter(group -> group.getGid() == (gid.get()));
        }
        if (!member.isEmpty()) {
            allGroups = allGroups.filter(group -> group.getMember().containsAll(member));
        }

        return allGroups.collect(Collectors.toList());
    }

    /**
     * Get a single group
     *
     * @param gid group id
     * @return an Optional group (i.e. Optional.empty() if not found)
     * @throws IOException if file cannot be processed
     */
    public Optional<Group> get(long gid) throws IOException {
        return getAll().stream().filter(u -> u.getGid() == gid).findAny();
    }


    /**
     * Converts a single line in the format used by /etc/group. name:password:gid:member1,member2,etc. Must not be null
     * or be a comment line.
     *
     * @param input a single line entry in the correct format
     * @return Group if conversion is successful,
     * @throws IOException if the line cannot be processed
     */
    private Group convert(String input) throws IOException {
        String[] fields = input.split(Constant.PASSWD_AND_GROUP_FIELD_DELIMIETER);
        Group group = new Group();
        if (fields.length == 3 || fields.length == 4) { //members are optional
            group.setName(fields[0]);
            // field 1 is the masked password field that we're skipping
            group.setGid(Long.parseLong(fields[2]));
            if (fields.length == 4) {
                group.setMember(Arrays.asList(fields[3].split(GROUP_MEMBER_DELIMIETER)));
            } else {
                // no member list
                group.setMember(Collections.emptyList());
            }
        } else {
            logger.error("Group has {} fields. At least 3 must be present", fields.length);
            throw new IOException("bad group line " + input);
        }
        return group;
    }
}
