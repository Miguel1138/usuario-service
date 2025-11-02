package br.com.scripta_api.usuario_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticatedFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService; // Injetado do ApplicationConfig

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // 2. Verificar se o cabeçalho existe e se começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Se não, deixa passar para o próximo filtro
            return;
        }

        // 3. Extrair o token (removendo o "Bearer ")
        final String jwt = authHeader.substring(7);

        // 4. Pedir ao JwtTokenProvider para extrair a matrícula (username)
        final String matricula = jwtTokenProvider.extrairMatricula(jwt);

        // 5. Verifica se a matrícula foi extraída E se o usuário ainda NÃO está autenticado
        //    (Evita re-validar a cada filtro na mesma requisição)
        if (matricula != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Carrega os detalhes do usuário do banco (usando o UserDetailsService)
            //    Isso usa nosso UsuarioRepository (adapter) por baixo dos panos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(matricula);

            // 7. Valida se o token pertence a este usuário e se não expirou
            if (jwtTokenProvider.isTokenValido(jwt, userDetails)) {

                // 8. CRIA O CRACHÁ (Authentication object)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,    // O usuário (principal)
                        null,           // Credenciais (não usamos senha aqui)
                        userDetails.getAuthorities() // Os papéis (ROLE_ALUNO, etc)
                );

                // Adiciona detalhes da requisição (ex: IP) ao crachá
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. A ETAPA CRUCIAL: Coloca o "crachá" no contexto de segurança
                //    O Spring Security agora "sabe" que este usuário está autenticado
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Passa a requisição (agora autenticada) para o próximo filtro
        filterChain.doFilter(request, response);
    }
}
