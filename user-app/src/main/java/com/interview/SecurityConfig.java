package com.interview;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public InMemoryUserDetailsManager detailsManager(PasswordEncoder passwordEncoder) {
		final var admin = User.withUsername("springer")
				.password(passwordEncoder.encode("secret"))
				.roles("ADMIN").build();
		final var user=User.withUsername("spring")
				.password(passwordEncoder.encode("secret"))
				.roles("USER").build();
		return new InMemoryUserDetailsManager(user,admin);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		return http
				.csrf().disable()
				.authorizeHttpRequests()
				.antMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
				.antMatchers(HttpMethod.GET, "/users", "/users/**").hasAnyRole("ADMIN", "USER")
				.antMatchers("/users/**").hasRole("ADMIN")
				.antMatchers("/", "/h2-console/**").permitAll()
				.anyRequest().permitAll()
				.and()
				.headers().frameOptions().disable()
				.and().httpBasic()
				//.and().oauth2Login()
				.and()
				.build();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
