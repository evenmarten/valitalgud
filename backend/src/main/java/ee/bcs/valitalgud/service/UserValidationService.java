package ee.bcs.valitalgud.service;

import ee.bcs.valitalgud.infrastructure.error.ErrorResponse;
import ee.bcs.valitalgud.infrastructure.exception.UnauthorizedException;
import ee.bcs.valitalgud.persistence.user.User;
import ee.bcs.valitalgud.persistence.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public void validateActiveUser(Integer userId) {
        if (userId == null) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
        ensureExistsAndActive(userId);
    }

    @Transactional(readOnly = true)
    public void ensureExistsAndActive(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED));
        if (!ACTIVE_STATUS.equalsIgnoreCase(user.getStatus())) {
            throw new UnauthorizedException(ErrorResponse.NOT_AUTHENTICATED);
        }
    }
}
