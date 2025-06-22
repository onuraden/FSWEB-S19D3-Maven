package com.workintech.s19d2.service;


import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.entity.Role;
import com.workintech.s19d2.repository.MemberRepository;
import com.workintech.s19d2.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthenticationService {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_ADMIN = "ADMIN";
    private MemberRepository memberRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationService(MemberRepository memberRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Member register(String email, String password) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isPresent()) {
            throw new RuntimeException("User with given email already exist!");
        }

        String encodedPassword = passwordEncoder.encode(password);

        List<Role> roles = new ArrayList<>();
        //addRoleAdmin(roles);
        addRoleUser(roles);

        Member member = new Member();
        member.setEmail(email);
        member.setPassword(encodedPassword);
        member.setRoles(roles);

        return memberRepository.save(member);

    }

    private void addRoleAdmin(List<Role> roles) {
        Optional<Role> adminRole = roleRepository.findByAuthority(ROLE_ADMIN);
        if (!adminRole.isPresent()) {
            Role roleAdminEntity = new Role();
            roleAdminEntity.setAuthority(ROLE_ADMIN);
            roles.add(roleRepository.save(roleAdminEntity));
        } else {
            roles.add(adminRole.get());
        }
    }

    private void addRoleUser(List<Role> roles) {
        Optional<Role> userRole = roleRepository.findByAuthority(ROLE_USER);
        if (!userRole.isPresent()) {
            Role roleUserEntity = new Role();
            roleUserEntity.setAuthority(ROLE_USER);
            roles.add(roleRepository.save(roleUserEntity));
        } else {
            roles.add(userRole.get());
        }
    }
}
