package site.billbill.apiserver.api.push.converter;

import java.util.List;
import site.billbill.apiserver.api.push.dto.response.PushResponse;
import site.billbill.apiserver.api.push.dto.response.PushResponse.GetPushResponse;
import site.billbill.apiserver.model.alarm.AlarmListJpaEntity;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ItemsJpaEntity;

import static com.mysql.cj.util.TimeUtil.DATE_FORMATTER;

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
    public static PushResponse.GetReviewAlertResponse toReviewAlert(BorrowHistJpaEntity borrowHist) {
        return PushResponse.GetReviewAlertResponse.builder()
                .itemId(borrowHist.getItem().getId())
                .title(borrowHist.getItem().getTitle())
                .image(borrowHist.getItem().getImages().get(0))
                .startDate(borrowHist.getStartedAt().format(DATE_FORMATTER))
                .endDate(borrowHist.getEndedAt().format(DATE_FORMATTER))
                .build();

    }
}
