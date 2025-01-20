package kafdrop.controller;

import kafdrop.config.MessageFormatConfiguration;
import kafdrop.model.ConsumerVO;
import kafdrop.model.CreateTopicVO;
import kafdrop.model.TopicVO;
import kafdrop.service.KafkaMonitor;
import kafdrop.service.TopicNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TopicControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    @Mock
    private MessageFormatConfiguration.MessageFormatProperties messageFormatProperties;

    private TopicController controller;

    @BeforeEach
    void setUp() {
        controller = new TopicController(kafkaMonitor, true, true, messageFormatProperties);
    }

    @Test
    void testTopicDetails() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        List<ConsumerVO> consumers = Collections.emptyList();

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(consumers);
        when(messageFormatProperties.getFormat()).thenReturn(null);
        when(messageFormatProperties.getKeyFormat()).thenReturn(null);

        String view = controller.topicDetails(topicName, model);

        assertEquals("topic-detail", view);
        verify(model).addAttribute("topic", topic);
        verify(model).addAttribute("consumers", consumers);
        verify(model).addAttribute("topicDeleteEnabled", true);
    }

    @Test
    void testTopicDetailsNotFound() {
        String topicName = "non-existent-topic";
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> controller.topicDetails(topicName, model));
    }

    @Test
    void testDeleteTopicSuccess() {
        String topicName = "test-topic";
        doNothing().when(kafkaMonitor).deleteTopic(topicName);

        String view = controller.deleteTopic(topicName, model);

        assertEquals("redirect:/", view);
        verify(kafkaMonitor).deleteTopic(topicName);
    }

    @Test
    void testDeleteTopicDisabled() {
        controller = new TopicController(kafkaMonitor, false, true, messageFormatProperties);
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(Collections.emptyList());

        String view = controller.deleteTopic(topicName, model);

        verify(model).addAttribute("deleteErrorMessage", "Not configured to be deleted.");
        verify(kafkaMonitor, never()).deleteTopic(any());
    }

    @Test
    void testGetAllTopics() {
        List<TopicVO> topics = Arrays.asList(
            new TopicVO("topic1"),
            new TopicVO("topic2")
        );
        when(kafkaMonitor.getTopics()).thenReturn(topics);

        List<TopicVO> result = controller.getAllTopics();

        assertEquals(topics, result);
        verify(kafkaMonitor).getTopics();
    }

    @Test
    void testGetTopic() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));

        TopicVO result = controller.getTopic(topicName);

        assertEquals(topic, result);
    }

    @Test
    void testGetTopicNotFound() {
        String topicName = "non-existent-topic";
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> controller.getTopic(topicName));
    }

    @Test
    void testGetConsumers() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        List<ConsumerVO> consumers = Collections.singletonList(new ConsumerVO("test-group"));

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(consumers);

        List<ConsumerVO> result = controller.getConsumers(topicName);

        assertEquals(consumers, result);
    }

    @Test
    void testCreateTopic() {
        CreateTopicVO createTopicVO = new CreateTopicVO();
        createTopicVO.setName("new-topic");
        createTopicVO.setPartitionsNumber(1);
        createTopicVO.setReplicationFactor(1);

        when(kafkaMonitor.getBrokers()).thenReturn(Collections.emptyList());

        String view = controller.createTopic(createTopicVO, model);

        assertEquals("topic-create", view);
        verify(kafkaMonitor).createTopic(eq(createTopicVO));
        verify(model).addAttribute("topicCreateEnabled", true);
    }

    @Test
    void testCreateTopicDisabled() {
        controller = new TopicController(kafkaMonitor, true, false, messageFormatProperties);
        CreateTopicVO createTopicVO = new CreateTopicVO();
        createTopicVO.setName("new-topic");

        String view = controller.createTopic(createTopicVO, model);

        assertEquals("topic-create", view);
        verify(model).addAttribute("errorMessage", "Not configured to be created.");
        verify(kafkaMonitor, never()).createTopic(any());
    }
}
