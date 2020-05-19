package stuff.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes

class KafkaConsts {
    static final JSON = new ObjectMapper()
    static final JSON_SE = new JsonSerializer()

    static <T> Serde<T> createJsonSerde(Class<T> c) {
        Serdes.serdeFrom(JSON_SE, new JsonDeserializer<T>(c))
    }
}
