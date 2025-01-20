package kafdrop.controller;

import kafdrop.model.BrokerVO;
import kafdrop.model.TopicVO;
import kafdrop.service.BrokerNotFoundException;
import kafdrop.service.KafkaMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrokerControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    private BrokerController controller;

    private final BrokerVO testBroker = new BrokerVO(1, "localhost", 9092, "rack1", false);
    private final List<TopicVO> testTopics = Arrays.asList(new TopicVO("topic1"), new TopicVO("topic2"));

    @BeforeEach
    void setUp() {
        controller = new BrokerController(kafkaMonitor);
    }

    @Test
    void brokerDetails_ValidId_ReturnsView() {
        when(kafkaMonitor.getBroker(1)).thenReturn(Optional.of(testBroker));
        when(kafkaMonitor.getTopics()).thenReturn(testTopics);

        String view = controller.brokerDetails(1, model);

        assertEquals("broker-detail", view);
        verify(model).addAttribute(eq("broker"), eq(testBroker));
        verify(model).addAttribute(eq("topics"), eq(testTopics));
    }

    @Test
    void brokerDetails_InvalidId_ThrowsException() {
        when(kafkaMonitor.getBroker(99)).thenReturn(Optional.empty());

        assertThrows(BrokerNotFoundException.class, () ->
            controller.brokerDetails(99, model)
        );
    }

    @Test
    void brokerDetailsJson_ValidId_ReturnsBroker() {
        when(kafkaMonitor.getBroker(1)).thenReturn(Optional.of(testBroker));

        BrokerVO result = controller.brokerDetailsJson(1);

        assertEquals(testBroker, result);
    }

    @Test
    void brokerDetailsJson_InvalidId_ThrowsException() {
        when(kafkaMonitor.getBroker(99)).thenReturn(Optional.empty());

        assertThrows(BrokerNotFoundException.class, () ->
            controller.brokerDetailsJson(99)
        );
    }

    @Test
    void brokerDetailsJson_ReturnsAllBrokers() {
        List<BrokerVO> brokers = Arrays.asList(
            testBroker,
            new BrokerVO(2, "localhost", 9093, "rack1", true)
        );
        when(kafkaMonitor.getBrokers()).thenReturn(brokers);

        List<BrokerVO> result = controller.brokerDetailsJson();

        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(brokers);
    }
}
