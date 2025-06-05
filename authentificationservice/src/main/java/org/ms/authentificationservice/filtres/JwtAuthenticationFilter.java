package org.ms.authentificationservice.filtres;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.setFilterProcessesUrl("/login");
        
        this.setAuthenticationSuccessHandler((request, response, authentication) -> {
            setCorsHeaders(response);
            successfulAuthentication(request, response, null, authentication);
        });
        
        this.setAuthenticationFailureHandler((request, response, exception) -> {
            setCorsHeaders(response);
            unsuccessfulAuthentication(request, response, exception);
        });
    }
    
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("attemptAuthentication");
        String username;
        String password;
        
        try {
            if ("application/json".equals(request.getHeader("Content-Type"))) {
                // Handle JSON request
                Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(), Map.class);
                username = credentials.get("username");
                password = credentials.get("password");
            } else {
                // Handle form data
                username = request.getParameter("username");
                password = request.getParameter("password");
            }
            
            if (username == null || password == null) {
                throw new AuthenticationException("Missing credentials") {
                    private static final long serialVersionUID = 1L;
                };
            }
            
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, password);
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new AuthenticationException("Error reading credentials") {
                private static final long serialVersionUID = 1L;
            };
        }
    }
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");
        User user = (User) authResult.getPrincipal();
        String[] roles = new String[user.getAuthorities().size()];
        int index = 0;
        for (GrantedAuthority gi : user.getAuthorities()) {
            roles[index] = gi.getAuthority();
            index++;
        }
        Algorithm algo = Algorithm.HMAC256("ThisIsASecretKeyWith32Characters!!");
        String jwtAccessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 minutes
                .withIssuer(request.getRequestURL().toString())
                .withArrayClaim("roles", roles)
                .sign(algo);
        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
                .withIssuer(request.getRequestURL().toString())
                .sign(algo);
        Map<String, String> mapTokens = new HashMap<>();
        mapTokens.put("access_token", jwtAccessToken);
        mapTokens.put("refresh_token", jwtRefreshToken);
        
        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Cache-Control, Content-Type");
        
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), mapTokens);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", failed.getMessage());
        body.put("path", request.getServletPath());

        // Set CORS headers
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Cache-Control, Content-Type");
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
