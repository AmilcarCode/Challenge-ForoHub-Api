package com.foro.hub.foro_hub_api.controller;

import com.foro.hub.foro_hub_api.domain.user.User;
import com.foro.hub.foro_hub_api.domain.user.UserAuthenticationData;
import com.foro.hub.foro_hub_api.infra.security.DataJWTToken;
import com.foro.hub.foro_hub_api.infra.security.TokenService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<Object> userLogin(@RequestBody @Valid @NotNull UserAuthenticationData userAuthenticationData) {
        System.out.println("Login attempt for user: " + userAuthenticationData.login());
        try {
            var authenticationToken = new UsernamePasswordAuthenticationToken(
                    userAuthenticationData.login(),
                    userAuthenticationData.password()
            );
            var authenticatedUser = authenticationManager.authenticate(authenticationToken);
            System.out.println("User authenticated successfully: " + authenticatedUser.getPrincipal());
            
            var JWTtoken = tokenService.generateToken((User) authenticatedUser.getPrincipal());
            System.out.println("JWT token generated: " + JWTtoken);
            
            // Return the token in the expected format
            var tokenResponse = new DataJWTToken(JWTtoken);
            System.out.println("Response being sent: " + tokenResponse);
            
            return ResponseEntity.ok(tokenResponse);

        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed: Bad credentials for user " + userAuthenticationData.login());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Credentials");

        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing the request: " + e.getMessage());
        }
    }
}
