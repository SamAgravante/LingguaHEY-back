package edu.cit.lingguahey.Entity;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static edu.cit.lingguahey.Entity.Permission.ADMIN_CREATE;
import static edu.cit.lingguahey.Entity.Permission.ADMIN_DELETE;
import static edu.cit.lingguahey.Entity.Permission.ADMIN_READ;
import static edu.cit.lingguahey.Entity.Permission.ADMIN_UPDATE;
import static edu.cit.lingguahey.Entity.Permission.USER_CREATE;
import static edu.cit.lingguahey.Entity.Permission.USER_DELETE;
import static edu.cit.lingguahey.Entity.Permission.USER_READ;
import static edu.cit.lingguahey.Entity.Permission.USER_UPDATE;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_CREATE;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_DELETE;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_READ;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_UPDATE;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {
    USER(
        Set.of(
            USER_CREATE,
            USER_READ,
            USER_UPDATE,
            USER_DELETE
        )
    ),
    ADMIN(
        Set.of(
            ADMIN_CREATE,
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            USER_CREATE,
            USER_READ,
            USER_UPDATE,
            USER_DELETE
        )
    ),
    TEACHER(
        Set.of(
            TEACHER_CREATE,
            TEACHER_READ,
            TEACHER_UPDATE,
            TEACHER_DELETE
        )
    );

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
            .stream()
            .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
            .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}
