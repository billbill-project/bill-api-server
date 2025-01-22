package site.billbill.apiserver.api.push.service;

import site.billbill.apiserver.api.push.dto.request.PushRequest;

import java.io.IOException;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendChatPushRequest;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendPushRequest;

import site.billbill.apiserver.api.push.dto.response.PushResponse;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushListResponse;

public interface PushService {
    boolean sendPush(PushRequest.SendPushRequest request) throws IOException;
    SendPushRequest sendChatPush(SendChatPushRequest request);
    boolean sendPush(PushRequest request) throws IOException;

    GetPushListResponse getPushList(String beforeTimestamp, String userId);

    PushResponse.GetReviewAlertResponse getReviewAlertService(String userId);
}
