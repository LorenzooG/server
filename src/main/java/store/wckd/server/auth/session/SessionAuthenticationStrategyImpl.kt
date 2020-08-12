package store.wckd.server.auth.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import store.wckd.server.dto.LoginResponseDTO;
import store.wckd.server.entity.User;
import store.wckd.server.service.JwtService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SessionAuthenticationStrategyImpl implements SessionAuthenticationStrategy {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JwtService jwtService;

    public SessionAuthenticationStrategyImpl(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthentication(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SessionAuthenticationException {
        Object principal = authentication.getPrincipal();

        if (principal == null)
            throw new SessionAuthenticationException("Principal is null");

        if (!(principal instanceof User))
            throw new SessionAuthenticationException("Principal is not user");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jwtString = jwtService.encodeJwt((User) principal);

        try (PrintWriter writer = response.getWriter()) {
            writer.print(objectMapper.writeValueAsString(new LoginResponseDTO(jwtString)));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
