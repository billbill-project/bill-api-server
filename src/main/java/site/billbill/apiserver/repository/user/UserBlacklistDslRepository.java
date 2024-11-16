package site.billbill.apiserver.repository.user;

import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.api.users.dto.response.BlacklistResponse;

import java.util.List;

public interface UserBlacklistDslRepository {
    List<BlacklistResponse> getBlacklistByUserId(String userId, Pageable pageable);
}
