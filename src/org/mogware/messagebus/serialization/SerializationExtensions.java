package org.mogware.messagebus.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public final class SerializationExtensions {
    private SerializationExtensions() {
    }

    public static byte[] serialize(Serializer serializer, Object graph) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        serializer.serialize(stream, graph);
        return stream.toByteArray();
    }

    public static Object deserialize(Serializer serializer, byte[] source,
            Class type, String format, String encoding) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(source);
        return serializer.deserialize(inputStream, type, format, encoding);
    }
}
