package kafdrop.controller;

import kafdrop.model.ConsumerVO;
import kafdrop.service.KafkaMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ConsumerControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    private ConsumerController controller;

    @BeforeEach
    void setUp() {
        controller = new ConsumerController(kafkaMonitor);
    }

    @Test
    void consumerDetail_WhenConsumerExists_ShouldReturnView() throws ConsumerNotFoundException {
        String groupId = "test-group";
        ConsumerVO consumer = new ConsumerVO(groupId);

        when(kafkaMonitor.getConsumersByGroup(groupId))
            .thenReturn(Collections.singletonList(consumer));

        String view = controller.consumerDetail(groupId, model);

        assertEquals("consumer-detail", view);
        verify(model).addAttribute("consumer", consumer);
    }

    @Test
    void consumerDetail_WhenConsumerNotFound_ShouldThrowException() {
        String groupId = "non-existent-group";

        when(kafkaMonitor.getConsumersByGroup(groupId))
            .thenReturn(Collections.emptyList());

        assertThrows(ConsumerNotFoundException.class, () ->
            controller.consumerDetail(groupId, model));
    }

    @Test
    void getConsumer_WhenConsumerExists_ShouldReturnConsumer() throws ConsumerNotFoundException {
        String groupId = "test-group";
        ConsumerVO consumer = new ConsumerVO(groupId);

        when(kafkaMonitor.getConsumersByGroup(groupId))
            .thenReturn(Collections.singletonList(consumer));

        ConsumerVO result = controller.getConsumer(groupId);

        assertEquals(consumer, result);
    }

    @Test
    void getConsumer_WhenConsumerNotFound_ShouldThrowException() {
        String groupId = "non-existent-group";

        when(kafkaMonitor.getConsumersByGroup(groupId))
            .thenReturn(Collections.emptyList());

        assertThrows(ConsumerNotFoundException.class, () ->
            controller.getConsumer(groupId));
    }
}
