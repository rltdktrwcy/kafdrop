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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        lenient().when(messageFormatProperties.getFormat()).thenReturn(MessageFormat.DEFAULT);
        lenient().when(messageFormatProperties.getKeyFormat()).thenReturn(MessageFormat.DEFAULT);
        controller = new TopicController(kafkaMonitor, true, true, messageFormatProperties);
    }

    @Test
    void topicDetails_ExistingTopic_ReturnsTopicDetailView() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        List<ConsumerVO> consumers = Collections.emptyList();

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(consumers);

        String view = controller.topicDetails(topicName, model);

        assertEquals("topic-detail", view);
        verify(model).addAttribute("topic", topic);
        verify(model).addAttribute("consumers", consumers);
        verify(model).addAttribute("topicDeleteEnabled", true);
    }

    @Test
    void topicDetails_NonExistentTopic_ThrowsTopicNotFoundException() {
        String topicName = "non-existent-topic";
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> controller.topicDetails(topicName, model));
    }

    @Test
    void deleteTopic_Success_RedirectsToHome() {
        String topicName = "test-topic";
        doNothing().when(kafkaMonitor).deleteTopic(topicName);

        String view = controller.deleteTopic(topicName, model);

        assertEquals("redirect:/", view);
        verify(kafkaMonitor).deleteTopic(topicName);
    }

    @Test
    void deleteTopic_WhenDisabled_ShowsError() {
        controller = new TopicController(kafkaMonitor, false, true, messageFormatProperties);
        String topicName = "test-topic";

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(new TopicVO(topicName)));

        String view = controller.deleteTopic(topicName, model);

        verify(model).addAttribute("deleteErrorMessage", "Not configured to be deleted.");
        assertEquals("topic-detail", view);
    }

    @Test
    void getAllTopics_ReturnsListOfTopics() {
        List<TopicVO> expectedTopics = Arrays.asList(
            new TopicVO("topic1"),
            new TopicVO("topic2")
        );
        when(kafkaMonitor.getTopics()).thenReturn(expectedTopics);

        List<TopicVO> actualTopics = controller.getAllTopics();

        assertEquals(expectedTopics, actualTopics);
    }

    @Test
    void createTopic_Success_ReturnsCreateView() {
        CreateTopicVO createTopicVO = new CreateTopicVO();
        createTopicVO.setName("new-topic");
        createTopicVO.setPartitionsNumber(1);
        createTopicVO.setReplicationFactor(1);

        when(kafkaMonitor.getBrokers()).thenReturn(Collections.emptyList());

        String view = controller.createTopic(createTopicVO, model);

        assertEquals("topic-create", view);
        verify(kafkaMonitor).createTopic(createTopicVO);
    }

    @Test
    void createTopic_WhenDisabled_ShowsError() {
        controller = new TopicController(kafkaMonitor, true, false, messageFormatProperties);
        CreateTopicVO createTopicVO = new CreateTopicVO();
        createTopicVO.setName("new-topic");

        String view = controller.createTopic(createTopicVO, model);

        verify(model).addAttribute("errorMessage", "Not configured to be created.");
        assertEquals("topic-create", view);
    }

    @Test
    void getConsumers_ExistingTopic_ReturnsConsumersList() {
        String topicName = "test-topic";
        TopicVO topic = new TopicVO(topicName);
        List<ConsumerVO> expectedConsumers = Collections.singletonList(new ConsumerVO("consumer-group"));

        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.of(topic));
        when(kafkaMonitor.getConsumersByTopics(Collections.singleton(topic))).thenReturn(expectedConsumers);

        List<ConsumerVO> actualConsumers = controller.getConsumers(topicName);

        assertEquals(expectedConsumers, actualConsumers);
    }

    @Test
    void getConsumers_NonExistentTopic_ThrowsTopicNotFoundException() {
        String topicName = "non-existent-topic";
        when(kafkaMonitor.getTopic(topicName)).thenReturn(Optional.empty());

        assertThrows(TopicNotFoundException.class, () -> controller.getConsumers(topicName));
    }
}
