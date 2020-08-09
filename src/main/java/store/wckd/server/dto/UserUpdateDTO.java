package store.wckd.server.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserUpdateDTO {
    private final String username;
    private final String email;
    private final String password;
}
