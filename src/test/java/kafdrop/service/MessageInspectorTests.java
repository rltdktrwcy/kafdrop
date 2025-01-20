package kafdrop.service;

import kafdrop.model.MessageVO;
import kafdrop.util.Deserializers;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageInspectorTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Deserializers deserializers;

    private MessageInspector messageInspector;

    @BeforeEach
    void setUp() {
        messageInspector = new MessageInspector(kafkaMonitor);
    }

    @Test
    void getMessages_WithPartition_ShouldReturnMessagesFromKafkaMonitor() {
        // Arrange
        String topicName = "test-topic";
        int partitionId = 1;
        long offset = 0L;
        int count = 10;

        List<MessageVO> expectedMessages = Arrays.asList(new MessageVO(), new MessageVO());
        TopicPartition topicPartition = new TopicPartition(topicName, partitionId);

        when(kafkaMonitor.getMessages(topicPartition, offset, count, deserializers))
            .thenReturn(expectedMessages);

        // Act
        List<MessageVO> actualMessages = messageInspector.getMessages(topicName, partitionId, offset, count, deserializers);

        // Assert
        assertEquals(expectedMessages, actualMessages);
        verify(kafkaMonitor).getMessages(topicPartition, offset, count, deserializers);
    }

    @Test
    void getMessages_WithoutPartition_ShouldReturnMessagesFromKafkaMonitor() {
        // Arrange
        String topicName = "test-topic";
        int count = 10;

        List<MessageVO> expectedMessages = Arrays.asList(new MessageVO(), new MessageVO());

        when(kafkaMonitor.getMessages(topicName, count, deserializers))
            .thenReturn(expectedMessages);

        // Act
        List<MessageVO> actualMessages = messageInspector.getMessages(topicName, count, deserializers);

        // Assert
        assertEquals(expectedMessages, actualMessages);
        verify(kafkaMonitor).getMessages(topicName, count, deserializers);
    }
}
