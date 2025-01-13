package site.billbill.apiserver.scheduler.TaskScheduler.Manager;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.billbill.apiserver.api.push.dto.request.PushRequest;
import site.billbill.apiserver.api.push.service.PushService;
import site.billbill.apiserver.common.enums.alarm.PushType;
import site.billbill.apiserver.common.enums.exception.ErrorCode;
import site.billbill.apiserver.exception.CustomException;
import site.billbill.apiserver.model.post.BorrowHistJpaEntity;
import site.billbill.apiserver.model.post.ReviewAlertJpaEntity;
import site.billbill.apiserver.repository.borrowPosts.BorrowHistRepository;
import site.billbill.apiserver.repository.borrowPosts.ReviewAlertRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
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
    private final ReviewAlertRepository reviewAlertRepository;

     // 작업 참조를 저장하는 Map
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @EventListener(ApplicationReadyEvent.class)
    public void InitializeScheduler() {
        List<ReviewAlertJpaEntity> reviewAlerts = reviewAlertRepository.findAllByStatus("PENDING");
        // 스케줄러 재등록
        reviewAlerts.forEach(reviewAlert -> ReviewNotification(reviewAlert.getBorrowHist()));

    }
    public void ReviewNotification(BorrowHistJpaEntity borrowHist) {

        LocalDateTime time = borrowHist.getEndedAt().plusDays(1).atStartOfDay().plusHours(9);
        PushRequest request=PushRequest.builder()
                .userId(borrowHist.getBorrower().getUserId())
                .title("물건을 잘 이용하셨나요?")
                .pushType(PushType.REVIEW_ALERT)
                .content(borrowHist.getBorrower().getNickname()+"님! 이용하신 <"+borrowHist.getItem().getTitle()+"> 는 어떠셨나요? 이용 후기를 남겨주세요!")
                .moveToId(String.valueOf(borrowHist.getBorrowSeq()))
                .build();
         // 작업 스케줄링 및 참조 저장
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> SendReviewNotification(request, borrowHist),
                Date.from(time.atZone(ZoneId.systemDefault()).toInstant())
        );
        scheduledTasks.put(borrowHist.getBorrowSeq(), future);
        log.info("리뷰 요청 작업이 {}에 예약되었습니다.", time);
    }
    @Async
    @Transactional
    public void SendReviewNotification(PushRequest request,BorrowHistJpaEntity borrowHist) {
        try {
                log.info("리뷰 요청 작업이 {}에 시작되었습니다.", LocalDateTime.now());
                ReviewAlertJpaEntity reviewAlert=reviewAlertRepository.findOneByBorrowHist(borrowHist);
                if(reviewAlert==null){
                    //해당 스케줄러 백업 등록
                    reviewAlert = ReviewAlertJpaEntity.builder()
                            .borrowHist(borrowHist)
                            .status("PENDING")
                            .build();
                    ReviewAlertJpaEntity savedReviewAlert=reviewAlertRepository.save(reviewAlert);
                    savedReviewAlert.setStatus("COMPLETED");
                    reviewAlert=savedReviewAlert;
                }else{
                    reviewAlert.setStatus("COMPLETED");
                }

                reviewAlertRepository.save(reviewAlert);

                if(!borrowHistRepository.CheckUsersForReviews(borrowHist)){
                    pushService.sendPush(request);
                }

            } catch (IOException e) {
                throw new CustomException(ErrorCode.BadRequest, " 리뷰 관련 알림 오류입니다. 담당자에게 문의해주세요", HttpStatus.BAD_REQUEST);
            }
    }

    @Transactional
    public void CanceledReviewNotification(BorrowHistJpaEntity borrowHist) {
        ScheduledFuture<?> future=scheduledTasks.get(borrowHist.getBorrowSeq());
        if(future!=null){
            future.cancel(false);
            scheduledTasks.remove(borrowHist.getBorrowSeq());
            log.info("스케줄러 작업이 취소되었습니다: {}", borrowHist.getBorrowSeq());
        }
        //ReviewAlert 상태 업데이트
        ReviewAlertJpaEntity reviewAlert = reviewAlertRepository.findOneByBorrowHist(borrowHist);
        if (reviewAlert != null) {
            reviewAlert.setStatus("CANCELED");
            reviewAlertRepository.save(reviewAlert);
            log.info("ReviewAlert 상태가 CANCELED로 업데이트되었습니다: {}", borrowHist.getBorrowSeq());
        }
    }


}
