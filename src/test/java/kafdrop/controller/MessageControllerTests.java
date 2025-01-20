package kafdrop.controller;

import kafdrop.config.MessageFormatConfiguration.MessageFormatProperties;
import kafdrop.config.ProtobufDescriptorConfiguration.ProtobufDescriptorProperties;
import kafdrop.config.SchemaRegistryConfiguration.SchemaRegistryProperties;
import kafdrop.service.KafkaMonitor;
import kafdrop.service.MessageInspector;
import kafdrop.util.MessageFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MessageControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private MessageInspector messageInspector;

    @Mock
    private MessageFormatProperties messageFormatProperties;

    @Mock
    private SchemaRegistryProperties schemaRegistryProperties;

    @Mock
    private ProtobufDescriptorProperties protobufProperties;

    private MessageController messageController;

    @BeforeEach
    void setUp() {
        messageController = new MessageController(kafkaMonitor, messageInspector,
            messageFormatProperties, schemaRegistryProperties, protobufProperties);
    }

    @Test
    void testPartitionOffsetInfo_ValidValues() {
        var offsetInfo = new MessageController.PartitionOffsetInfo();
        offsetInfo.setPartition(1);
        offsetInfo.setOffset(100L);
        offsetInfo.setCount(50L);
        offsetInfo.setFormat(MessageFormat.DEFAULT);

        assertEquals(1, offsetInfo.getPartition());
        assertEquals(100L, offsetInfo.getOffset());
        assertEquals(50L, offsetInfo.getCount());
        assertEquals(MessageFormat.DEFAULT, offsetInfo.getFormat());
        assertFalse(offsetInfo.isEmpty());
    }

    @Test
    void testPartitionOffsetInfo_Empty() {
        var offsetInfo = new MessageController.PartitionOffsetInfo();
        assertTrue(offsetInfo.isEmpty());
    }

    // Skipping deserializer/serializer tests since methods are private
    /*
    @Test
    void testGetDeserializer_Default() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");

        MessageDeserializer deserializer = messageController.getDeserializer(
            "test-topic", MessageFormat.DEFAULT, "", "");

        assertTrue(deserializer instanceof DefaultMessageDeserializer);
    }

    @Test
    void testGetDeserializer_Avro() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");

        MessageDeserializer deserializer = messageController.getDeserializer(
            "test-topic", MessageFormat.AVRO, "", "");

        assertTrue(deserializer instanceof AvroMessageDeserializer);
    }

    @Test
    void testGetDeserializer_Protobuf() {
        when(protobufProperties.getDirectory()).thenReturn("/tmp");

        MessageDeserializer deserializer = messageController.getDeserializer(
            "test-topic", MessageFormat.PROTOBUF, "test.desc", "TestMessage");

        assertTrue(deserializer instanceof ProtobufMessageDeserializer);
    }

    @Test
    void testGetDeserializer_ProtobufSchemaRegistry() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");

        MessageDeserializer deserializer = messageController.getDeserializer(
            "test-topic", MessageFormat.PROTOBUF, "", "");

        assertTrue(deserializer instanceof ProtobufSchemaRegistryMessageDeserializer);
    }

    @Test
    void testGetDeserializer_MsgPack() {
        MessageDeserializer deserializer = messageController.getDeserializer(
            "test-topic", MessageFormat.MSGPACK, "", "");

        assertTrue(deserializer instanceof MsgPackMessageDeserializer);
    }

    @Test
    void testGetSerializer_Default() {
        MessageSerializer serializer = messageController.getSerializer(
            "test-topic", MessageFormat.DEFAULT, "", "");

        assertTrue(serializer instanceof DefaultMessageSerializer);
    }

    @Test
    void testGetSerializer_Avro() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");

        MessageSerializer serializer = messageController.getSerializer(
            "test-topic", MessageFormat.AVRO, "", "");

        assertTrue(serializer instanceof AvroMessageSerializer);
    }

    @Test
    void testGetSerializer_Protobuf() {
        when(protobufProperties.getDirectory()).thenReturn("/tmp");

        MessageSerializer serializer = messageController.getSerializer(
            "test-topic", MessageFormat.PROTOBUF, "test.desc", "TestMessage");

        assertTrue(serializer instanceof ProtobufMessageSerializer);
    }
    */
}
