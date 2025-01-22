package site.billbill.apiserver.api.push.service;

import site.billbill.apiserver.api.push.dto.request.PushRequest.SendChatPushRequest;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendPushRequest;
import site.billbill.apiserver.api.push.dto.response.PushResponse;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushListResponse;

import java.io.IOException;

public interface PushService {
    boolean sendPush(SendPushRequest request) throws IOException;

    SendPushRequest sendChatPush(SendChatPushRequest request);

    GetPushListResponse getPushList(String beforeTimestamp, String userId);

    PushResponse.GetReviewAlertResponse getReviewAlertService(String userId);
}
