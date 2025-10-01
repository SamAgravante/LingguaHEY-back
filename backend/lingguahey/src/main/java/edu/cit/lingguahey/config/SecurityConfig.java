package edu.cit.lingguahey.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import static edu.cit.lingguahey.Entity.Permission.TEACHER_CREATE;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_DELETE;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_READ;
import static edu.cit.lingguahey.Entity.Permission.TEACHER_UPDATE;
import static edu.cit.lingguahey.Entity.Permission.USER_DELETE;
import static edu.cit.lingguahey.Entity.Permission.USER_READ;
import static edu.cit.lingguahey.Entity.Permission.USER_UPDATE;
import static edu.cit.lingguahey.Entity.Permission.USER_CREATE;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:5173", // local dev
                    "https://lingguahey.vercel.app", // production
                    "https://lingguahey.netlify.app"
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
                    "/api/lingguahey/auth/verify-email**",
                    "/v2/api-docs",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/swagger-resources",
                    "/swagger-resources/**",
                    "/configuration/ui",
                    "/configuration/security",
                    "/swagger-ui/**",
                    "/webjars/**",
                    "/swagger-ui.html",
                    "/ws/**"
                )
                .permitAll()
                //Activities
                .requestMatchers(GET, "/api/lingguahey/classrooms/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/classrooms/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), TEACHER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/classrooms/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/classrooms/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), TEACHER_DELETE.getPermission())
                
                //Choices
                .requestMatchers(GET, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), TEACHER_CREATE.getPermission(), USER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/choices/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), TEACHER_DELETE.getPermission())
                
                //Questions
                .requestMatchers(GET, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), TEACHER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/questions/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), TEACHER_DELETE.getPermission())
                
                //Scores
                .requestMatchers(GET, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), TEACHER_CREATE.getPermission(), USER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/scores/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), TEACHER_DELETE.getPermission())
                
                //Activities
                .requestMatchers(GET, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), USER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/activities/**").hasAnyAuthority(ADMIN_DELETE.getPermission())
                
                //Live Activities
                .requestMatchers(GET, "/api/lingguahey/live-activities/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/live-activities/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), TEACHER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/live-activities/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/live-activities/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), TEACHER_DELETE.getPermission())
                
                //Users
                .requestMatchers(GET, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), USER_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/users/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), USER_DELETE.getPermission(), TEACHER_DELETE.getPermission())

                //TTS
                .requestMatchers(GET, "/api/lingguahey/tts/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/tts/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), USER_CREATE.getPermission(), TEACHER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/tts/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/tts/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), TEACHER_DELETE.getPermission())

                //Lobby
                .requestMatchers(GET, "/api/lingguahey/lobby/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission(), TEACHER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/lobby/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), USER_CREATE.getPermission(), TEACHER_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/lobby/**").hasAnyAuthority(ADMIN_UPDATE.getPermission(), USER_UPDATE.getPermission(), TEACHER_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/lobby/**").hasAnyAuthority(ADMIN_DELETE.getPermission(), USER_DELETE.getPermission(), TEACHER_DELETE.getPermission())

                //Gacha
                .requestMatchers(POST, "/api/lingguahey/gacha/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), USER_CREATE.getPermission())

                //Inventory
                .requestMatchers(GET, "/api/lingguahey/inventory/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/inventory/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), USER_CREATE.getPermission())

                //Potion Shop
                .requestMatchers(POST, "/api/lingguahey/potion-shop/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), USER_CREATE.getPermission())

                //Monster Editor
                .requestMatchers(GET, "/api/lingguahey/monsters/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/monsters/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/monsters/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/monsters/**").hasAnyAuthority(ADMIN_DELETE.getPermission())

                //Level Editor
                .requestMatchers(GET, "/api/lingguahey/levels/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/levels/**").hasAnyAuthority(ADMIN_CREATE.getPermission())
                .requestMatchers(PUT, "/api/lingguahey/levels/**").hasAnyAuthority(ADMIN_UPDATE.getPermission())
                .requestMatchers(DELETE, "/api/lingguahey/levels/**").hasAnyAuthority(ADMIN_DELETE.getPermission())

                // Game
                .requestMatchers(GET, "/api/lingguahey/levels/**").hasAnyAuthority(ADMIN_READ.getPermission(), USER_READ.getPermission())
                .requestMatchers(POST, "/api/lingguahey/levels/**").hasAnyAuthority(ADMIN_CREATE.getPermission(), USER_CREATE.getPermission())

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
