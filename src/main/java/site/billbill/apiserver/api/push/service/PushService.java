package site.billbill.apiserver.api.push.service;

import site.billbill.apiserver.api.push.dto.request.PushRequest;

import java.io.IOException;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendChatPushRequest;
import site.billbill.apiserver.api.push.dto.request.PushRequest.SendPushRequest;

public interface PushService {
    boolean sendPush(PushRequest.SendPushRequest request) throws IOException;
    SendPushRequest sendChatPush(SendChatPushRequest request);
}
