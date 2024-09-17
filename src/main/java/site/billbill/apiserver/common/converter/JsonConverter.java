package site.billbill.apiserver.common.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Converter
@RequiredArgsConstructor
public class JsonConverter<T> implements AttributeConverter<T, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (ObjectUtils.isEmpty(attribute)) {
            return null;
        } else {
            try {
                return objectMapper.writeValueAsString(attribute);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (StringUtils.hasText(dbData)) {
            Class<?> aClass = GenericTypeResolver.resolveTypeArgument(getClass(), JsonConverter.class);
            try {
                return (T) objectMapper.readValue(dbData, aClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;

    }
}
