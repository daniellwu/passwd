package com.github.daniellwu.passwd;

import com.github.daniellwu.passwd.group.GroupService;
import com.github.daniellwu.passwd.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Main application entry
 */
@SpringBootApplication
public class PasswdApplication {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

	public static void main(String[] args) {
		SpringApplication.run(PasswdApplication.class, args);
	}

    /**
     * Attempts to access and parse passwd and group, so errors would cause the application to exit early. Better to
     * fail early if we can (at init time) rather than at runtime.
     * @throws IOException
     */
	@PostConstruct
    public void verify() throws IOException {
	    userService.getAll();
	    groupService.getAll();
    }
}
