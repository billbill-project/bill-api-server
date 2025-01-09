package site.billbill.apiserver.api.push.converter;

import java.util.List;
import site.billbill.apiserver.api.push.dto.response.PushResponse;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushResponse;
import site.billbill.apiserver.model.alarm.AlarmListJpaEntity;

public class PushConverter {
    public static PushResponse.GetPushListResponse toViewPushList(List<AlarmListJpaEntity> alarms) {
        List<GetPushResponse> collect = alarms.stream().map(PushConverter::toViewPush).toList();

        return PushResponse.GetPushListResponse.builder()
                .pushList(collect)
                .build();
    }

    public static PushResponse.GetPushResponse toViewPush(AlarmListJpaEntity alarm) {
        return PushResponse.GetPushResponse.builder()
                .title(alarm.getTitle())
                .content(alarm.getContent())
                .pushType(alarm.getPushType())
                .moveToId(alarm.getMoveToId())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}
