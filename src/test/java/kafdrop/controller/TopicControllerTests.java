package kafdrop.controller;

import kafdrop.config.MessageFormatConfiguration;
import kafdrop.model.ConsumerVO;
import kafdrop.model.CreateTopicVO;
import kafdrop.model.TopicVO;
import kafdrop.service.KafkaMonitor;
import kafdrop.service.TopicNotFoundException;
import kafdrop.util.MessageFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
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

    private TopicController topicController;

    @BeforeEach
    void setUp() {
        topicController = new TopicController(kafkaMonitor, true, true, messageFormatProperties);
    }

    @Test
    void testTopicDetails() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        List<ConsumerVO> consumers = new ArrayList<>();

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(consumers);
        when(messageFormatProperties.getFormat()).thenReturn(MessageFormat.DEFAULT);
        when(messageFormatProperties.getKeyFormat()).thenReturn(MessageFormat.DEFAULT);

        String result = topicController.topicDetails(topicName, model);

        assertEquals("topic-detail", result);
        verify(model).addAttribute("topic", topic);
        verify(model).addAttribute("consumers", consumers);
        verify(model).addAttribute("topicDeleteEnabled", true);
    }

    @Test
    void testTopicDetailsNotFound() {
        String topicName = "non-existent-topic";
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> {
            topicController.topicDetails(topicName, model);
        });
    }

    @Test
    void testDeleteTopic() {
        String topicName = "test-topic";
        doNothing().when(kafkaMonitor).deleteTopic(topicName);

        String result = topicController.deleteTopic(topicName, model);

        assertEquals("redirect:/", result);
        verify(kafkaMonitor).deleteTopic(topicName);
    }

    @Test
    void testDeleteTopicWhenDisabled() {
        topicController = new TopicController(kafkaMonitor, false, true, messageFormatProperties);
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(messageFormatProperties.getFormat()).thenReturn(MessageFormat.DEFAULT);
        when(messageFormatProperties.getKeyFormat()).thenReturn(MessageFormat.DEFAULT);

        String result = topicController.deleteTopic(topicName, model);

        verify(model).addAttribute("deleteErrorMessage", "Not configured to be deleted.");
        verify(kafkaMonitor, never()).deleteTopic(any());
    }

    @Test
    void testCreateTopicPage() {
        when(kafkaMonitor.getBrokers()).thenReturn(new ArrayList<>());

        String result = topicController.createTopicPage(model);

        assertEquals("topic-create", result);
        verify(model).addAttribute("topicCreateEnabled", true);
        verify(model).addAttribute("brokersCount", 0);
    }

    @Test
    void testGetTopic() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));

        TopicVO result = topicController.getTopic(topicName);

        assertEquals(topic, result);
    }

    @Test
    void testGetTopicNotFound() {
        String topicName = "non-existent-topic";
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> {
            topicController.getTopic(topicName);
        });
    }

    @Test
    void testGetAllTopics() {
        List<TopicVO> topics = new ArrayList<>();
        when(kafkaMonitor.getTopics()).thenReturn(topics);

        List<TopicVO> result = topicController.getAllTopics();

        assertEquals(topics, result);
    }

    @Test
    void testGetConsumers() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        List<ConsumerVO> consumers = new ArrayList<>();

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(consumers);

        List<ConsumerVO> result = topicController.getConsumers(topicName);

        assertEquals(consumers, result);
    }

    @Test
    void testCreateTopic() {
        CreateTopicVO createTopicVO = new CreateTopicVO();
        createTopicVO.setName("new-topic");
        when(kafkaMonitor.getBrokers()).thenReturn(new ArrayList<>());

        String result = topicController.createTopic(createTopicVO, model);

        assertEquals("topic-create", result);
        verify(kafkaMonitor).createTopic(eq(createTopicVO));
        verify(model).addAttribute("topicCreateEnabled", true);
    }

    @Test
    void testCreateTopicWhenDisabled() {
        topicController = new TopicController(kafkaMonitor, true, false, messageFormatProperties);
        CreateTopicVO createTopicVO = new CreateTopicVO();
        when(kafkaMonitor.getBrokers()).thenReturn(new ArrayList<>());

        String result = topicController.createTopic(createTopicVO, model);

        assertEquals("topic-create", result);
        verify(model).addAttribute("errorMessage", "Not configured to be created.");
        verify(kafkaMonitor, never()).createTopic(any());
    }
}
