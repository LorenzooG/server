package store.wckd.server.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponseDTO {
    private final long id;
    private final String username;
    private final String email;
}
