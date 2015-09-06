package org.mogware.messagebus.serialization;

import java.io.InputStream;
import java.io.OutputStream;

/**
* Provides the ability to serialize and deserialize an object graph.
*/

public interface Serializer {
    /**
    * Gets the value which indicates the encoding mechanism used.
    */
    String getContentEncoding();

    /**
     * Gets the MIME-type suffix
     */
    String getContentFormat();

    /**
    * Serializes the object graph provided and writes a serialized
    * representation to the output stream provided.
    */
    void serialize(OutputStream output, Object graph);

    /**
    * Deserializes the stream provided and reconstructs the corresponding
    * object graph.
    */
    Object deserialize(InputStream input, Class type, String format,
            String contentEncoding);
}
