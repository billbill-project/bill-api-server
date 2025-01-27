package site.billbill.apiserver.model.post;

import io.micrometer.core.annotation.Counted;
import jakarta.persistence.*;
import lombok.*;
import site.billbill.apiserver.common.converter.BooleanConverter;
import site.billbill.apiserver.common.converter.StringListConverter;
import site.billbill.apiserver.model.BaseTime;
import site.billbill.apiserver.model.user.UserJpaEntity;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemsJpaEntity extends BaseTime {
    @Id
    @Column(name = "item_id", nullable = false)
    private String id;
    @ManyToOne
    @JoinColumn(name="owner_id")
    private UserJpaEntity owner;
    @Convert(converter = StringListConverter.class)
    @Column(name = "images", nullable = true)
    private List<String> images;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name="content",nullable = false)
    private String content;
    @Column(name="like_count",nullable = false)
    private int likeCount;
    @Column(name="view_count",nullable = false)
    private int viewCount;
    @Column(name = "del_yn", nullable = false)
    @Convert(converter = BooleanConverter.class)
    private boolean delYn;
    @Column(name="item_status",nullable = false)
    private int itemStatus;
    @ManyToOne
    @JoinColumn(name="category_id")
    private ItemsCategoryJpaEntity category;


    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL, optional = true) // 양방향 관계 설정
    private ItemsLocationJpaEntity location;

}
