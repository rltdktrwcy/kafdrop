package kafdrop.controller;

import kafdrop.config.MessageFormatConfiguration.MessageFormatProperties;
import kafdrop.config.ProtobufDescriptorConfiguration.ProtobufDescriptorProperties;
import kafdrop.config.SchemaRegistryConfiguration.SchemaRegistryProperties;
import kafdrop.service.KafkaMonitor;
import kafdrop.service.MessageInspector;
import kafdrop.util.MessageDeserializer;
import kafdrop.util.MessageFormat;
import kafdrop.util.MessageSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        messageController = new MessageController(
            kafkaMonitor,
            messageInspector,
            messageFormatProperties,
            schemaRegistryProperties,
            protobufProperties
        );
    }

    @Test
    @DisplayName("PartitionOffsetInfo should initialize with default values")
    void testPartitionOffsetInfoDefaultConstructor() {
        var partitionOffsetInfo = new MessageController.PartitionOffsetInfo();
        assertTrue(partitionOffsetInfo.isEmpty());
        assertNull(partitionOffsetInfo.getPartition());
        assertNull(partitionOffsetInfo.getOffset());
        assertNull(partitionOffsetInfo.getCount());
        assertNull(partitionOffsetInfo.getFormat());
        assertNull(partitionOffsetInfo.getKeyFormat());
        assertNull(partitionOffsetInfo.getDescFile());
        assertNull(partitionOffsetInfo.getMsgTypeName());
        assertFalse(partitionOffsetInfo.getIsAnyProto());
    }

    @Test
    @DisplayName("PartitionOffsetInfo should initialize with provided values")
    void testPartitionOffsetInfoParameterizedConstructor() {
        var partitionOffsetInfo = new MessageController.PartitionOffsetInfo(1, 100L, 10L, MessageFormat.DEFAULT);

        assertFalse(partitionOffsetInfo.isEmpty());
        assertEquals(1, partitionOffsetInfo.getPartition());
        assertEquals(100L, partitionOffsetInfo.getOffset());
        assertEquals(10L, partitionOffsetInfo.getCount());
        assertEquals(MessageFormat.DEFAULT, partitionOffsetInfo.getFormat());
    }

    @Test
    @DisplayName("PartitionOffsetInfo setters should work correctly")
    void testPartitionOffsetInfoSetters() {
        var partitionOffsetInfo = new MessageController.PartitionOffsetInfo();

        partitionOffsetInfo.setPartition(2);
        partitionOffsetInfo.setOffset(200L);
        partitionOffsetInfo.setCount(20L);
        partitionOffsetInfo.setFormat(MessageFormat.AVRO);
        partitionOffsetInfo.setKeyFormat(MessageFormat.DEFAULT);
        partitionOffsetInfo.setDescFile("test.desc");
        partitionOffsetInfo.setMsgTypeName("TestMessage");
        partitionOffsetInfo.setIsAnyProto(true);

        assertEquals(2, partitionOffsetInfo.getPartition());
        assertEquals(200L, partitionOffsetInfo.getOffset());
        assertEquals(20L, partitionOffsetInfo.getCount());
        assertEquals(MessageFormat.AVRO, partitionOffsetInfo.getFormat());
        assertEquals(MessageFormat.DEFAULT, partitionOffsetInfo.getKeyFormat());
        assertEquals("test.desc", partitionOffsetInfo.getDescFile());
        assertEquals("TestMessage", partitionOffsetInfo.getMsgTypeName());
        assertTrue(partitionOffsetInfo.getIsAnyProto());
    }

    @Test
    @DisplayName("getDeserializer should return correct deserializer for AVRO format")
    void testGetDeserializerForAvroFormat() throws Exception {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");
        when(schemaRegistryProperties.getAuth()).thenReturn("auth");

        Method getDeserializerMethod = MessageController.class.getDeclaredMethod(
            "getDeserializer", String.class, MessageFormat.class, String.class, String.class, boolean.class);
        getDeserializerMethod.setAccessible(true);

        MessageDeserializer deserializer = (MessageDeserializer) getDeserializerMethod.invoke(
            messageController, "test-topic", MessageFormat.AVRO, "", "", false);

        assertNotNull(deserializer);
        assertTrue(deserializer.getClass().getSimpleName().contains("Avro"));
    }

    @Test
    @DisplayName("getSerializer should return correct serializer for AVRO format")
    void testGetSerializerForAvroFormat() throws Exception {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");
        when(schemaRegistryProperties.getAuth()).thenReturn("auth");

        Method getSerializerMethod = MessageController.class.getDeclaredMethod(
            "getSerializer", String.class, MessageFormat.class, String.class, String.class);
        getSerializerMethod.setAccessible(true);

        MessageSerializer serializer = (MessageSerializer) getSerializerMethod.invoke(
            messageController, "test-topic", MessageFormat.AVRO, "", "");

        assertNotNull(serializer);
        assertTrue(serializer.getClass().getSimpleName().contains("Avro"));
    }

    @Test
    @DisplayName("getDeserializer should return correct deserializer for PROTOBUF format")
    void testGetDeserializerForProtobufFormat() throws Exception {
        when(protobufProperties.getDirectory()).thenReturn("/proto");

        Method getDeserializerMethod = MessageController.class.getDeclaredMethod(
            "getDeserializer", String.class, MessageFormat.class, String.class, String.class, boolean.class);
        getDeserializerMethod.setAccessible(true);

        MessageDeserializer deserializer = (MessageDeserializer) getDeserializerMethod.invoke(
            messageController, "test-topic", MessageFormat.PROTOBUF, "test.desc", "TestMessage", false);

        assertNotNull(deserializer);
        assertTrue(deserializer.getClass().getSimpleName().contains("Protobuf"));
    }

    @Test
    @DisplayName("getSerializer should return correct serializer for PROTOBUF format")
    void testGetSerializerForProtobufFormat() throws Exception {
        when(protobufProperties.getDirectory()).thenReturn("/proto");

        Method getSerializerMethod = MessageController.class.getDeclaredMethod(
            "getSerializer", String.class, MessageFormat.class, String.class, String.class);
        getSerializerMethod.setAccessible(true);

        MessageSerializer serializer = (MessageSerializer) getSerializerMethod.invoke(
            messageController, "test-topic", MessageFormat.PROTOBUF, "test.desc", "TestMessage");

        assertNotNull(serializer);
        assertTrue(serializer.getClass().getSimpleName().contains("Protobuf"));
    }
}
