//package site.billbill.apiserver.model;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.EntityListeners;
//import jakarta.persistence.MappedSuperclass;
//import lombok.Getter;
//import lombok.Setter;
//import org.hibernate.annotations.CreationTimestamp;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//import java.time.OffsetDateTime;
//
//@MappedSuperclass
//@Getter @Setter
//@EntityListeners(AuditingEntityListener.class)
//public abstract class BaseTime {
//    @CreationTimestamp
//    @Column(name = "created_at", insertable = true, updatable = false)
//    private final OffsetDateTime createdAt = OffsetDateTime.now();
//
//    @CreationTimestamp
//    @Column(name = "updated_at", insertable = false, updatable = true)
//    private OffsetDateTime updatedAt = OffsetDateTime.now();
//}
