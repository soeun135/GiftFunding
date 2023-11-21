package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.Signup;


public interface UserService {
    Signup.Response singUp(Signup.Request request);
}
