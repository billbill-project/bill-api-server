package site.billbill.apiserver.common.utils.ULID;

import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Component;

@Component
public class ULIDUtil {
    public static String generatorULID(String type) {
        return type + "-" + UlidCreator.getMonotonicUlid().toString();
    }
}
