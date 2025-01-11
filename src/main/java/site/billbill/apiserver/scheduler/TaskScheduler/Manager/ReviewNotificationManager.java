package site.billbill.apiserver.scheduler.TaskScheduler.Manager;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import site.billbill.apiserver.api.borrowPosts.dto.request.PostsRequest;
import site.billbill.apiserver.api.borrowPosts.dto.response.PostsResponse;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.api.push.service.PushService;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.BorrowHistRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@AllArgsConstructor
@Component
@Log4j2
public class ReviewNotificationManager {
    private final TaskScheduler taskScheduler;
    private final PushService pushService;
    private final BorrowHistRepository borrowHistRepository;
    public void ReviewNotification(BorrowHistJpaEntity borrowHist) {

        LocalDateTime time = borrowHist.getEndedAt().plusDays(1).atStartOfDay().plusHours(9);
        PushRequest request=PushRequest.builder()
                .userId(borrowHist.getBorrower().getUserId())
                .title("물건을 잘 이용하셨나요?")
                .pushType(PushType.REVIEW)
                .content(borrowHist.getBorrower().getNickname()+"님! 이용하신 "+borrowHist.getItem().getTitle()+"는 어떠셨나요? 이용 후기를 남겨주세요!")
                .moveToId(borrowHist.getItem().getId())
                .build();
        taskScheduler.schedule(
                ()-> SendReviewNotification(request,borrowHist),
                Date.from(time.atZone(ZoneId.of("Asia/Seoul")).toInstant())
        );
        log.info("리뷰 요청 작업이 {}에 예약되었습니다.", time);
    }
    @Async
    public void SendReviewNotification(PushRequest request,BorrowHistJpaEntity borrowHist) {
        try {
                if(!borrowHistRepository.CheckUsersForReviews(borrowHist)){
                    pushService.sendPush(request);
                }

            } catch (IOException e) {
                throw new CustomException(ErrorCode.BadRequest, " 리뷰 관련 알림 오류입니다. 담당자에게 문의해주세요", HttpStatus.BAD_REQUEST);
            }
    }
    //거래취소할시 태스크에서 취소하는 로직 짜야함

}
