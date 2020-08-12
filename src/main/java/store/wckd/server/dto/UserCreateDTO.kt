package store.wckd.server.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserCreateDTO {
    private final String username;
    private final String email;
    private final String password;
}
