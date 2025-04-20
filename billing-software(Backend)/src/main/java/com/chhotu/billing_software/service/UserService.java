package com.chhotu.billing_software.service;

import com.chhotu.billing_software.io.UserRequest;
import com.chhotu.billing_software.io.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);
}
