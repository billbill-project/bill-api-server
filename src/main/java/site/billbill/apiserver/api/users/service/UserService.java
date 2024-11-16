package site.billbill.apiserver.api.users.service;

import org.springframework.data.domain.Pageable;
import site.billbill.apiserver.api.users.dto.response.BlacklistResponse;
import site.billbill.apiserver.api.users.dto.response.ProfileResponse;

import java.util.List;

public interface UserService {
    ProfileResponse getProfileInfo();

    ProfileResponse getProfileInfo(String userId);

    void blockUser(String userId);

    List<BlacklistResponse> getBlacklist(Pageable pageable);
}
