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

    private MessageController controller;

    @BeforeEach
    void setUp() {
        controller = new MessageController(kafkaMonitor, messageInspector,
            messageFormatProperties, schemaRegistryProperties, protobufProperties);
    }

    @Test
    void testPartitionOffsetInfo_DefaultConstructor() {
        var info = new MessageController.PartitionOffsetInfo();
        assertNull(info.getPartition());
        assertNull(info.getOffset());
        assertNull(info.getCount());
        assertTrue(info.isEmpty());
    }

    @Test
    void testPartitionOffsetInfo_ParameterizedConstructor() {
        var info = new MessageController.PartitionOffsetInfo(1, 100L, 50L, MessageFormat.DEFAULT);
        assertEquals(1, info.getPartition());
        assertEquals(100L, info.getOffset());
        assertEquals(50L, info.getCount());
        assertEquals(MessageFormat.DEFAULT, info.getFormat());
        assertFalse(info.isEmpty());
    }

    // Skipping tests that try to access private methods
    /*
    @Test
    void testGetDeserializer_Default() {
        MessageDeserializer deserializer = controller.getDeserializer("test-topic",
            MessageFormat.DEFAULT, "", "", false);
        assertTrue(deserializer instanceof DefaultMessageDeserializer);
    }

    @Test
    void testGetDeserializer_Avro() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");
        when(schemaRegistryProperties.getAuth()).thenReturn("auth");

        MessageDeserializer deserializer = controller.getDeserializer("test-topic",
            MessageFormat.AVRO, "", "", false);
        assertTrue(deserializer instanceof AvroMessageDeserializer);
    }

    @Test
    void testGetDeserializer_Protobuf_WithDescFile() {
        when(protobufProperties.getDirectory()).thenReturn("/tmp");

        MessageDeserializer deserializer = controller.getDeserializer("test-topic",
            MessageFormat.PROTOBUF, "test.desc", "TestMessage", false);
        assertTrue(deserializer instanceof ProtobufMessageDeserializer);
    }

    @Test
    void testGetDeserializer_Protobuf_WithSchemaRegistry() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");
        when(schemaRegistryProperties.getAuth()).thenReturn("auth");

        MessageDeserializer deserializer = controller.getDeserializer("test-topic",
            MessageFormat.PROTOBUF, "", "", false);
        assertTrue(deserializer instanceof ProtobufSchemaRegistryMessageDeserializer);
    }

    @Test
    void testGetSerializer_Default() {
        var serializer = controller.getSerializer("test-topic", MessageFormat.DEFAULT, "", "");
        assertNotNull(serializer);
    }

    @Test
    void testGetSerializer_Avro() {
        when(schemaRegistryProperties.getConnect()).thenReturn("http://localhost:8081");
        when(schemaRegistryProperties.getAuth()).thenReturn("auth");

        var serializer = controller.getSerializer("test-topic", MessageFormat.AVRO, "", "");
        assertNotNull(serializer);
    }

    @Test
    void testGetSerializer_Protobuf() {
        when(protobufProperties.getDirectory()).thenReturn("/tmp");

        var serializer = controller.getSerializer("test-topic",
            MessageFormat.PROTOBUF, "test.desc", "TestMessage");
        assertNotNull(serializer);
    }
    */
}
