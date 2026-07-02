package it.itsacademy.gestione_ordini.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * Récupère le nom de l'utilisateur courant via le header X-User-Name,
 * injecté par la Gateway après validation du JWT.
 * On n'a pas de SecurityContext JWT ici : la sécurité est déléguée à la Gateway.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return Optional.of("SYSTEM");
        }

        HttpServletRequest request = attributes.getRequest();
        String username = request.getHeader("X-User-Name");

        if (username == null || username.isBlank()) {
            return Optional.of("SYSTEM");
        }

        return Optional.of(username);
    }
}
