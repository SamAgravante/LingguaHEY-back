package edu.cit.lingguahey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static edu.cit.lingguahey.Entity.Permission.ADMIN_CREATE;
import static edu.cit.lingguahey.Entity.Permission.ADMIN_DELETE;
import static edu.cit.lingguahey.Entity.Permission.ADMIN_READ;
import static edu.cit.lingguahey.Entity.Permission.ADMIN_UPDATE;
import static edu.cit.lingguahey.Entity.Permission.USER_DELETE;
import static edu.cit.lingguahey.Entity.Permission.USER_READ;
import static edu.cit.lingguahey.Entity.Permission.USER_UPDATE;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173", // local dev
                    "https://lingguahey.vercel.app" // production
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/lingguahey/auth/**",
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
                //.requestMatchers("/api/lingguahey/activities/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //choices
                //.requestMatchers("/api/lingguahey/choices/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //questions
                //.requestMatchers("/api/lingguahey/questions/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //Scores
                //.requestMatchers("/api/lingguahey/scores/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //Stories
                //.requestMatchers("/api/lingguahey/stories/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/lingguahey/stories/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/stories/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/stories/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/stories/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                //Users
                //.requestMatchers("/api/lingguahey/users/**").hasAnyRole(ADMIN.name())
                .requestMatchers(GET, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), USER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), USER_DELETE.getPermission())

                .anyRequest()
                .authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
                .logoutUrl("/api/lingguahey/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
            );

        return http.build();
    }
}
