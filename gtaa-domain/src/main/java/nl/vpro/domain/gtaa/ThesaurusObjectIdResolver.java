package nl.vpro.domain.gtaa;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class ThesaurusObjectIdResolver extends TypeIdResolverBase {
    static {
         for (JsonSubTypes.Type type :ThesaurusObject.class.getAnnotation(JsonSubTypes.class).value()) {
            Scheme.init(type.value());
        }
    }

    @Override
    public String idFromValue(Object value) {
        return idFromValueAndType(value, value.getClass());

    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return suggestedType.getAnnotation(GTAAScheme.class).value().name().toLowerCase();

    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }
    @Override
    public JavaType typeFromId(DatabindContext context, String id)  throws IOException {
        return TypeFactory.defaultInstance().constructSimpleType(Scheme.valueOf(id.toUpperCase()).getImplementation(), new JavaType[0]);
    }
}
