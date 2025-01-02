package site.billbill.apiserver.api.push.service;

import site.billbill.apiserver.api.push.dto.request.PushRequest;

import java.io.IOException;

public interface PushService {
    boolean sendPush(PushRequest request) throws IOException;
}
