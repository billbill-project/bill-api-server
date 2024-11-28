package site.billbill.apiserver.api.push.service;

import site.billbill.apiserver.api.push.dto.request.PushRequest;

public interface PushService {
    boolean sendPush(PushRequest request);
}
