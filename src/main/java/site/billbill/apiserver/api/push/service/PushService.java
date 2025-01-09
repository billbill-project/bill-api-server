package site.billbill.apiserver.api.push.service;

import site.billbill.apiserver.api.push.dto.request.PushRequest;

import java.io.IOException;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushListResponse;

public interface PushService {
    boolean sendPush(PushRequest request) throws IOException;

    GetPushListResponse getPushList(String beforeTimestamp, String userId);
}
