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
public class TaskSchedulerManager {
    private final TaskScheduler taskScheduler;
    private final PushService pushService;
    private final BorrowHistRepository borrowHistRepository;
    private final ReviewAlertRepository reviewAlertRepository;

     // 작업 참조를 저장하는 Map
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    @EventListener(ApplicationReadyEvent.class)
    public void InitializeScheduler() {
        List<ReviewAlertJpaEntity> reviewAlerts = reviewAlertRepository.findAllByStatusAndType("PENDING","REVIEW");
        List<ReviewAlertJpaEntity> returnAlerts = reviewAlertRepository.findAllByStatusAndType("PENDING","RETURN");
        // 스케줄러 재등록
        reviewAlerts.forEach(reviewAlert -> ReviewNotification(reviewAlert.getBorrowHist()));
        returnAlerts.forEach(returnAlert -> ReturnNotification(returnAlert.getBorrowHist()));
    }



    public void ReturnNotification(BorrowHistJpaEntity borrowHist) {
        LocalDateTime time = borrowHist.getEndedAt().minusDays(1).atStartOfDay().plusHours(6);
        String content = borrowHist.getBorrower().getNickname() + "님! <" + borrowHist.getItem().getTitle() + ">의 반납일이 하루 남았습니다!";
        ScheduleNotification(borrowHist, "RETURN", "물건을 잘 이용하고 계신가요?", content, time, PushType.RETURN_ALERT);
    }

    public void ReviewNotification(BorrowHistJpaEntity borrowHist) {
        LocalDateTime time = borrowHist.getEndedAt().plusDays(1).atStartOfDay().plusHours(9);
        String content = borrowHist.getBorrower().getNickname() + "님! 이용하신 <" + borrowHist.getItem().getTitle() + "> 는 어떠셨나요? 이용 후기를 남겨주세요!";
        ScheduleNotification(borrowHist, "REVIEW", "물건을 잘 이용하셨나요?", content, time, PushType.REVIEW_ALERT);
    }





    @Transactional
    public void CanceledReviewNotification(BorrowHistJpaEntity borrowHist) {
         // 리뷰 작업 취소
        String reviewTaskKey = borrowHist.getBorrowSeq() + "_REVIEW";
        ScheduledFuture<?> reviewFuture = scheduledTasks.get(reviewTaskKey);
        if (reviewFuture != null) {
            reviewFuture.cancel(false);
            scheduledTasks.remove(reviewTaskKey);
            log.info("리뷰 작업이 취소되었습니다: {}", borrowHist.getBorrowSeq());
        }

        // 반납 작업 취소
        String returnTaskKey = borrowHist.getBorrowSeq() + "_RETURN";
        ScheduledFuture<?> returnFuture = scheduledTasks.get(returnTaskKey);
        if (returnFuture != null) {
            returnFuture.cancel(false);
            scheduledTasks.remove(returnTaskKey);
            log.info("반납 작업이 취소되었습니다: {}", borrowHist.getBorrowSeq());
        }

        // 리뷰 알림 상태 업데이트
        ReviewAlertJpaEntity reviewAlert = reviewAlertRepository.findOneByBorrowHistAndType(borrowHist, "REVIEW");
        if (reviewAlert != null) {
            reviewAlert.setStatus("CANCELED");
            reviewAlertRepository.save(reviewAlert);
            log.info("ReviewAlert 상태가 CANCELED로 업데이트되었습니다: {}", borrowHist.getBorrowSeq());
        }

        // 반납 알림 상태 업데이트
        ReviewAlertJpaEntity returnAlert = reviewAlertRepository.findOneByBorrowHistAndType(borrowHist, "RETURN");
        if (returnAlert != null) {
            returnAlert.setStatus("CANCELED");
            reviewAlertRepository.save(returnAlert);
            log.info("ReturnAlert 상태가 CANCELED로 업데이트되었습니다: {}", borrowHist.getBorrowSeq());
        }
    }


    //모듈화 코드
    public void ScheduleNotification(BorrowHistJpaEntity borrowHist, String alertType, String title, String content, LocalDateTime time, PushType pushType) {
        PushRequest request = PushRequest.builder()
                .userId(borrowHist.getBorrower().getUserId())
                .title(title)
                .pushType(pushType)
                .content(content)
                .moveToId(String.valueOf(borrowHist.getBorrowSeq()))
                .build();
        String taskKey = borrowHist.getBorrowSeq() + "_" + alertType;
        // 작업 스케줄링 및 참조 저장
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> SendNotification(request, borrowHist, alertType),
                Date.from(time.atZone(ZoneId.systemDefault()).toInstant())
        );
            scheduledTasks.put(taskKey, future);
            log.info("{} 작업이 {}에 예약되었습니다.", alertType, time);
        }

    @Async
    public void SendNotification(PushRequest request, BorrowHistJpaEntity borrowHist, String alertType) {
        try {
            log.info("{} 작업이 {}에 시작되었습니다.", alertType, LocalDateTime.now());
            ReviewAlertJpaEntity alert = reviewAlertRepository.findOneByBorrowHistAndType(borrowHist, alertType);
            if (alert == null) {
                // 해당 스케줄러 백업 등록
                alert = ReviewAlertJpaEntity.builder()
                        .borrowHist(borrowHist)
                        .status("PENDING")
                        .type(alertType)
                        .build();
                ReviewAlertJpaEntity savedAlert = reviewAlertRepository.save(alert);
                savedAlert.setStatus("COMPLETED");
                alert = savedAlert;
            } else {
                alert.setStatus("COMPLETED");
            }

            reviewAlertRepository.save(alert);

            if ("REVIEW".equals(alertType) && !borrowHistRepository.CheckUsersForReviews(borrowHist)) {
                pushService.sendPush(request);
            } else if ("RETURN".equals(alertType)) {
                pushService.sendPush(request);
            }

        } catch (IOException e) {
            throw new CustomException(ErrorCode.BadRequest, alertType + " 관련 알림 오류입니다. 담당자에게 문의해주세요", HttpStatus.BAD_REQUEST);
        }
    }


}


//모듈화 전 코드

//    public void ReturnNotification(BorrowHistJpaEntity borrowHist) {
//        LocalDateTime time =borrowHist.getEndedAt().minusDays(1).atStartOfDay().plusHours(6);
//
//        PushRequest request=PushRequest.builder()
//                .userId(borrowHist.getBorrower().getUserId())
//                .title("물건을 잘 이용하고 계신가요?")
//                .pushType(PushType.RETURN_ALERT)
//                .content(borrowHist.getBorrower().getNickname()+"님! <"+borrowHist.getItem().getTitle()+">의 반납일이 하루 남았습니다!")
//                .moveToId(String.valueOf(borrowHist.getBorrowSeq()))
//                .build();
//         // 작업 스케줄링 및 참조 저장
//        ScheduledFuture<?> future = taskScheduler.schedule(
//                () -> SendReturnNotification(request,borrowHist),
//                Date.from(time.atZone(ZoneId.systemDefault()).toInstant())
//        );
//        scheduledTasks.put(borrowHist.getBorrowSeq(), future);
//        log.info("반납 요청 작업이 {}에 예약되었습니다.", time);
//    }

//    public void ReviewNotification(BorrowHistJpaEntity borrowHist) {
//
//        LocalDateTime time = borrowHist.getEndedAt().plusDays(1).atStartOfDay().plusHours(9);
//        PushRequest request=PushRequest.builder()
//                .userId(borrowHist.getBorrower().getUserId())
//                .title("물건을 잘 이용하셨나요?")
//                .pushType(PushType.REVIEW_ALERT)
//                .content(borrowHist.getBorrower().getNickname()+"님! 이용하신 <"+borrowHist.getItem().getTitle()+"> 는 어떠셨나요? 이용 후기를 남겨주세요!")
//                .moveToId(String.valueOf(borrowHist.getBorrowSeq()))
//                .build();
//         // 작업 스케줄링 및 참조 저장
//        ScheduledFuture<?> future = taskScheduler.schedule(
//                () -> SendReviewNotification(request, borrowHist),
//                Date.from(time.atZone(ZoneId.systemDefault()).toInstant())
//        );
//        scheduledTasks.put(borrowHist.getBorrowSeq(), future);
//        log.info("리뷰 요청 작업이 {}에 예약되었습니다.", time);
//    }
//    @Async
//    public void SendReviewNotification(PushRequest request,BorrowHistJpaEntity borrowHist)  {
//        try {
//                log.info("리뷰 요청 작업이 {}에 시작되었습니다.", LocalDateTime.now());
//                ReviewAlertJpaEntity reviewAlert=reviewAlertRepository.findOneByBorrowHistAndType(borrowHist,"REVIEW");
//                if(reviewAlert==null){
//                    //해당 스케줄러 백업 등록
//                    reviewAlert = ReviewAlertJpaEntity.builder()
//                            .borrowHist(borrowHist)
//                            .status("PENDING")
//                            .type("REVIEW")
//                            .build();
//                    ReviewAlertJpaEntity savedReviewAlert=reviewAlertRepository.save(reviewAlert);
//                    savedReviewAlert.setStatus("COMPLETED");
//                    reviewAlert=savedReviewAlert;
//                }else{
//                    reviewAlert.setStatus("COMPLETED");
//                }
//
//                reviewAlertRepository.save(reviewAlert);
//
//                if(!borrowHistRepository.CheckUsersForReviews(borrowHist)){
//                    pushService.sendPush(request);
//                }
//
//            } catch (IOException e) {
//                throw new CustomException(ErrorCode.BadRequest, " 리뷰 관련 알림 오류입니다. 담당자에게 문의해주세요", HttpStatus.BAD_REQUEST);
//            }
//    }
//    @Async
//    public void SendReturnNotification(PushRequest request,BorrowHistJpaEntity borrowHist)  {
//        try {
//            ReviewAlertJpaEntity returnAlert = reviewAlertRepository.findOneByBorrowHistAndType(borrowHist, "RETURN");
//            if (returnAlert == null) {
//                returnAlert = ReviewAlertJpaEntity.builder()
//                        .borrowHist(borrowHist)
//                        .status("PENDING")
//                        .type("RETURN")
//                        .build();
//                ReviewAlertJpaEntity savedReturnAlert = reviewAlertRepository.save(returnAlert);
//                savedReturnAlert.setStatus("COMPLETED");
//                returnAlert = savedReturnAlert;
//            } else {
//                returnAlert.setStatus("COMPLETED");
//            }
//            reviewAlertRepository.save(returnAlert);
//            pushService.sendPush(request);
//        }catch (IOException e) {
//                throw new CustomException(ErrorCode.BadRequest, " 반납 관련 알림 오류입니다. 담당자에게 문의해주세요", HttpStatus.BAD_REQUEST);
//            }
//    }
