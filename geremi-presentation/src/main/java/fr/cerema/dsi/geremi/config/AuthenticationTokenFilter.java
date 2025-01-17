package fr.cerema.dsi.geremi.config;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import fr.cerema.dsi.commons.security.auth.jwt.exceptions.InvalidTokenException;
import fr.cerema.dsi.commons.security.exceptions.NoRoleForUsersServiceException;
import fr.cerema.dsi.geremi.config.service.CustomUsersDetailsService;
import fr.cerema.dsi.geremi.entities.Profil;
import fr.cerema.dsi.geremi.entities.RefEtat;
import fr.cerema.dsi.geremi.entities.User;
import fr.cerema.dsi.geremi.enums.Etat;
import fr.cerema.dsi.geremi.enums.Role;
import fr.cerema.dsi.geremi.services.ProfilService;
import fr.cerema.dsi.geremi.services.RefEtatService;
import fr.cerema.dsi.geremi.services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationTokenFilter extends OncePerRequestFilter {

  // Nommé différemment de "logger" car celui-ci existe déjà dans le GenericBeanFilter (JCL)
  private static final Logger slfLogger = LoggerFactory.getLogger(AuthenticationTokenFilter.class);

  private static final String JWT_EMAIL = "email";
  public static final String JWT_PRENOM = "given_name";
  public static final String JWT_NOM = "family_name";

  private static final String JWT_BEARER = "Bearer";
  public static final String AUTHORIZATION_HEADER = "Authorization";


  private static final String JWT_TYPE = "typ";

  @Value("${info.app.issuer}")
  private String urlSSO;

  @Value("${info.app.audience}")
  private String tokenAudience;


  private final CustomUsersDetailsService usersDetailsService;

  private final UserService userService;

  private final ProfilService profilService;

  private final RefEtatService refEtatService;

  public AuthenticationTokenFilter(CustomUsersDetailsService usersDetailsService, UserService userService, ProfilService profilService, RefEtatService refEtatService) {
    this.usersDetailsService = usersDetailsService;
    this.userService = userService;
    this.profilService = profilService;
    this.refEtatService = refEtatService;
  }


  @Override
  protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                  @NonNull FilterChain filterChain) throws ServletException, IOException {
    final String requestTokenHeader = request.getHeader(AUTHORIZATION_HEADER);
    if (requestTokenHeader != null && !StringUtils.isEmpty(requestTokenHeader)) {
      // TODO Voir si spring sécu a un parser déjà fait
      String jwtToken = this.resolveToken(requestTokenHeader);
      if (jwtToken != null) {
        String[] splitToken = jwtToken.split("\\.");
        String unsignedToken = splitToken[0] + "." + splitToken[1] + ".";
        Jwt<Header, Claims> jwsClaims = Jwts.parserBuilder().build().parseClaimsJwt(unsignedToken);
        this.isValidToken(jwsClaims);
        String username = (String) jwsClaims.getBody().get(JWT_EMAIL);

        if (username != null) {
          UserDetails usersDetails;
          try {
            usersDetails = this.usersDetailsService.loadUserByUsername(username);
          } catch (UsernameNotFoundException | NoRoleForUsersServiceException e){
            User user = new User();
            user.setMail(username);
            user.setNom((String) jwsClaims.getBody().get(JWT_NOM));
            user.setPrenom((String) jwsClaims.getBody().get(JWT_PRENOM));
            user.setDateCreation(LocalDate.now());
            List<Profil> profPub = profilService.findByLibelle(Role.PUBLIC.getLibelle());
            if (profPub != null && profPub.size() == 1) {
              user.setProfil(profPub.get(0));
            }
            List<RefEtat> etatEnAttente  = refEtatService.findByLibelle(Etat.EN_ATTENTE.getLibelle());
            if (etatEnAttente != null && etatEnAttente.size() == 1) {
              user.setRefEtat(etatEnAttente.get(0));
            }
            this.userService.create(user);
            usersDetails = this.usersDetailsService.loadUserByUsername(username);
          }

          UsernamePasswordAuthenticationToken usersnamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(
              usersDetails, null, usersDetails.getAuthorities());
          usersnamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext()
            .setAuthentication(usersnamePasswordAuthenticationToken);
        }
      }
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(String requestTokenHeader) {
    if (requestTokenHeader.startsWith(JWT_BEARER)) {
      return requestTokenHeader.substring(7);
    }
    return null;
  }


  private void isValidToken(Jwt<Header, Claims> jwtToken) {
    this.checkType(jwtToken.getBody());
    this.checkIssuer(jwtToken.getBody());
    this.checkAudience(jwtToken.getBody());
  }

  private void checkType(Claims jwsClaims) {
    String type = jwsClaims.get(JWT_TYPE, String.class);

    if (!StringUtils.equalsIgnoreCase(type, JWT_BEARER)) {
      slfLogger.warn("Type non conforme, type: {}, attendu: {}", type, JWT_BEARER);
      throw new InvalidTokenException("Le jeton d'authentification n'est pas du bon type.");
    }
  }

  private void checkIssuer(Claims jwsClaims) {
    String issuer = jwsClaims.getIssuer();

    if (!StringUtils.equalsIgnoreCase(issuer, urlSSO)) {
      slfLogger.warn("Issuer non conforme, issuer: {}, attendu: {}", issuer, urlSSO);
      throw new InvalidTokenException(
        "Le jeton d'authentification ne provient pas de la bonne source.");
    }
  }

  private void checkAudience(Claims jwsClaims) {
    String audience = jwsClaims.getAudience();

    if (!StringUtils.containsAnyIgnoreCase(audience, tokenAudience)) {
      slfLogger.warn(
        "Audience non conforme, attendue: {}, fournie: {}", tokenAudience, audience);
      throw new InvalidTokenException("Le jeton d'authentification ne nous est pas destiné.");
    }
  }

}
