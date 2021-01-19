package de.volkerfaas.kafka.deployment;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers(HttpMethod.GET,
                                "/static/**",
                                "/*.json",
                                "/*.ico",
                                "/*.png",
                                "/login.html",
                                "/actuator/health/**",
                                "/actuator/prometheus",
                                "/api/oauth2/registrations"
                        ).permitAll()
                        .antMatchers(HttpMethod.POST, "/api/jobs").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .defaultAuthenticationEntryPointFor(new Http403ForbiddenEntryPoint(), new AntPathRequestMatcher("/api/**"))
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/ws"),
                                new AntPathRequestMatcher("/ws/**"),
                                new AntPathRequestMatcher("/api/jobs", HttpMethod.POST.name()),
                                new RegexRequestMatcher("/api/jobs/[0-9]+", HttpMethod.PUT.name()),
                                new AntPathRequestMatcher("/logout", HttpMethod.POST.name())

                        )
                )
                .headers(headers -> headers
                        .frameOptions()
                        .sameOrigin()
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login.html")
                        .defaultSuccessUrl("/index.html", true)
                        .failureHandler((request, response, exception) -> {
                            final DefaultRedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                            final String message = URLEncoder.encode(exception.getMessage(), StandardCharsets.UTF_8.toString());
                            redirectStrategy.sendRedirect(request, response, "/login.html?error=" + message);
                        })
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login.html?signedout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
    }

}
