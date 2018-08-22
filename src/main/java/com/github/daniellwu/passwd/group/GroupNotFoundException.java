package com.github.daniellwu.passwd.group;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception message back to end user to indicate error
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "group not found")
public class GroupNotFoundException extends RuntimeException {
}
