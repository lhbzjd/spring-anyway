package ink.anyway.component.web.supple.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

import java.io.IOException;

public class CustomNullStringSerializerProvider extends DefaultSerializerProvider {

    public CustomNullStringSerializerProvider() { super(); }
    public CustomNullStringSerializerProvider(CustomNullStringSerializerProvider provider,
                                              SerializationConfig config,
                                              SerializerFactory jsf) {
        super(provider, config, jsf);
    }
    @Override
    public CustomNullStringSerializerProvider createInstance(SerializationConfig config,
                                                                           SerializerFactory jsf) {
        return new CustomNullStringSerializerProvider(this, config, jsf);
    }

    @Override
    public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
        if (property.getType().getRawClass().equals(String.class)) {
            return EmptyStringSerializer.INSTANCE;
        } else {
            return super.findNullValueSerializer(property);
        }
    }

    public static class EmptyStringSerializer extends JsonSerializer<Object> {
        public static final JsonSerializer<Object> INSTANCE = new CustomNullStringSerializerProvider.EmptyStringSerializer();

        private EmptyStringSerializer() {}

        @Override
        public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                throws IOException {

            jsonGenerator.writeString("");
        }
    }
}
