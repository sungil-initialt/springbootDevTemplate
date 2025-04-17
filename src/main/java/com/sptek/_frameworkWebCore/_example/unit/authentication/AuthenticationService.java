package com.sptek._frameworkWebCore._example.unit.authentication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service

public class AuthenticationService {

    @PreAuthorize("hasAuthority(T(com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)")
    public String iNeedAut() {
        return "I Need Auth";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String iNeedRole() {
        return "I Need Role";
    }

}
