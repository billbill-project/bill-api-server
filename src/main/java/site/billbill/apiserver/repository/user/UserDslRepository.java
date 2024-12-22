package site.billbill.apiserver.repository.user;

import site.billbill.apiserver.api.users.dto.request.ProfileRequest;

public interface UserDslRepository {
    void withdrawUserById(String userId);

    void updateProfileById(String userId, ProfileRequest request);
}
