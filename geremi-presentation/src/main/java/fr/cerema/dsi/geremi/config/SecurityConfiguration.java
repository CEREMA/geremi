package fr.cerema.dsi.geremi.config;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import fr.cerema.dsi.commons.security.filters.AppCorsFilter;
import fr.cerema.dsi.geremi.config.service.CustomUsersDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

  @Autowired
  private CustomUsersDetailsService usersDetailsService;

  @Autowired
  private AuthenticationTokenFilter authenticationTokenFilter;

  @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
  private String jwkSetUri;

  @Value("${spring.security.oauth2.proxy.hostname:#{null}}")
  private String proxyHostname;

  @Value("${spring.security.oauth2.proxy.port:#{null}}")
  private Integer proxyPort;


  @Bean
  @Profile("dev-atos | rec-atos")
  public JwtDecoder jwtDecoder() {
    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    if(StringUtils.isNotEmpty(this.proxyHostname) && Objects.nonNull(this.proxyPort)){
      Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostname, proxyPort));
      requestFactory.setProxy(proxy);
    }
    return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).restOperations(new RestTemplate(requestFactory)).build();
  }



  @Bean
  private static CorsConfigurationSource configurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOriginPatterns(List.of("*"));
    config.setAllowCredentials(true);
    //config.addAllowedOriginPattern("*");
    config.setAllowedHeaders(List.of("*"));
    config.setMaxAge(36000L);
    config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.cors().configurationSource(configurationSource())
      .and()
      .csrf().disable()
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .anonymous()
      .and()
      .authorizeHttpRequests()
      .requestMatchers("/api-docs/**").permitAll()
      .requestMatchers("/api-open/**").permitAll()
      .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .requestMatchers("/api/**").authenticated()
      .requestMatchers("/**").permitAll()
      .and()
      .addFilterBefore(new AppCorsFilter(), UsernamePasswordAuthenticationFilter.class)
      .oauth2ResourceServer()
      .jwt();

    return http.build();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(usersDetailsService);
  }
}
