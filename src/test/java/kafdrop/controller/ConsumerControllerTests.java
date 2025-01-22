package kafdrop.controller;

import kafdrop.model.ConsumerVO;
import kafdrop.service.KafkaMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ConsumerControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    @InjectMocks
    private ConsumerController controller;

    private MockMvc mockMvc;

    @ControllerAdvice
    private static class TestExceptionHandler {
        @ExceptionHandler(ConsumerNotFoundException.class)
        public ResponseEntity<String> handleConsumerNotFound(ConsumerNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new TestExceptionHandler())
                .build();
    }

    @Test
    void consumerDetail_ExistingGroup_ReturnsConsumerDetailView() throws Exception {
        String groupId = "test-group";
        ConsumerVO consumer = new ConsumerVO(groupId);

        when(kafkaMonitor.getConsumersByGroup(groupId))
                .thenReturn(Collections.singletonList(consumer));

        String view = controller.consumerDetail(groupId, model);

        assertEquals("consumer-detail", view);
        verify(model).addAttribute("consumer", consumer);
    }

    @Test
    void consumerDetail_NonExistingGroup_ThrowsException() {
        String groupId = "non-existing-group";

        when(kafkaMonitor.getConsumersByGroup(groupId))
                .thenReturn(Collections.emptyList());

        assertThrows(ConsumerNotFoundException.class, () ->
                controller.consumerDetail(groupId, model));
    }

    @Test
    void getConsumer_ExistingGroup_ReturnsConsumerVO() throws Exception {
        String groupId = "test-group";
        ConsumerVO consumer = new ConsumerVO(groupId);

        when(kafkaMonitor.getConsumersByGroup(groupId))
                .thenReturn(Collections.singletonList(consumer));

        mockMvc.perform(get("/consumer/{groupId}", groupId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.groupId").value(groupId));
    }

    @Test
    void getConsumer_NonExistingGroup_Returns404() throws Exception {
        String groupId = "non-existing-group";

        when(kafkaMonitor.getConsumersByGroup(groupId))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/consumer/{groupId}", groupId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
