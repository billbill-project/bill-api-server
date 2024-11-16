package site.billbill.apiserver.api.users.service;

import site.billbill.apiserver.api.users.dto.response.ProfileResponse;

public interface UserService {
    ProfileResponse getProfileInfo();
    ProfileResponse getProfileInfo(String userId);
    void blockUser(String userId);
}
