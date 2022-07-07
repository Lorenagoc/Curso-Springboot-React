package com.groupsoftware.minhasfinancas.service;

import com.groupsoftware.minhasfinancas.model.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.stereotype.Service;

public interface JwtService {

    String gerarToken(Usuario usuario);

    // Informações que há no token
    Claims obterClaims(String token) throws ExpiredJwtException;

    boolean isTokenValido(String token);

    String obterLoginUsuario(String token);
}
