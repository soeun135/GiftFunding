package com.soeun.GiftFunding.service;

import com.soeun.GiftFunding.dto.Signin;
import com.soeun.GiftFunding.dto.Signup;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService{

    Signup.Response singUp(Signup.Request request);

    Signin.Response signIn(Signin.Request request);
}
