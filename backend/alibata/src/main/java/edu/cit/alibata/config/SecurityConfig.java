package edu.cit.alibata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static edu.cit.alibata.Entity.Permission.ADMIN_CREATE;
import static edu.cit.alibata.Entity.Permission.ADMIN_DELETE;
import static edu.cit.alibata.Entity.Permission.ADMIN_READ;
import static edu.cit.alibata.Entity.Permission.ADMIN_UPDATE;
import static edu.cit.alibata.Entity.Permission.USER_DELETE;
import static edu.cit.alibata.Entity.Permission.USER_READ;
import static edu.cit.alibata.Entity.Permission.USER_UPDATE;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/alibata/auth/**",
                    "/v2/api-docs",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/swagger-ui.html"
                )
                .permitAll()
                //activities
                //.requestMatchers("/api/alibata/activities/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/alibata/activities/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/alibata/activities/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/alibata/activities/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/alibata/activities/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //choices
                //.requestMatchers("/api/alibata/choices/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/alibata/choices/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/alibata/choices/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/alibata/choices/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/alibata/choices/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //questions
                //.requestMatchers("/api/alibata/questions/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/alibata/questions/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/alibata/questions/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/alibata/questions/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/alibata/questions/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //Scores
                //.requestMatchers("/api/alibata/scores/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/alibata/scores/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/alibata/scores/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/alibata/scores/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/alibata/scores/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //Stories
                //.requestMatchers("/api/alibata/stories/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/alibata/stories/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/alibata/stories/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/alibata/stories/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/alibata/stories/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //Users
                //.requestMatchers("/api/alibata/users/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/alibata/users/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/alibata/users/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/alibata/users/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), USER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/alibata/users/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), USER_DELETE.getPermission())

                .anyRequest()
                .authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/api/alibata/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            );

        return http.build();
    }
}
