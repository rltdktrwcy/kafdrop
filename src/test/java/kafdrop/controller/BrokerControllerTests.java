package kafdrop.controller;

import kafdrop.model.BrokerVO;
import kafdrop.service.BrokerNotFoundException;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BrokerControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    @InjectMocks
    private BrokerController brokerController;

    private MockMvc mockMvc;

    private BrokerVO testBroker;
    private List<BrokerVO> testBrokers;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(brokerController).build();
        testBroker = new BrokerVO(1, "localhost", 9092, "rack1", true);
        testBrokers = Arrays.asList(
            testBroker,
            new BrokerVO(2, "localhost", 9093, "rack1", false)
        );
    }

    @Test
    void brokerDetails_ValidId_ReturnsView() {
        when(kafkaMonitor.getBroker(1)).thenReturn(Optional.of(testBroker));

        String viewName = brokerController.brokerDetails(1, model);

        assertEquals("broker-detail", viewName);
        verify(model).addAttribute("broker", testBroker);
        verify(model).addAttribute(eq("topics"), any());
    }

    @Test
    void brokerDetails_InvalidId_ThrowsException() {
        when(kafkaMonitor.getBroker(999)).thenReturn(Optional.empty());

        assertThrows(BrokerNotFoundException.class, () ->
            brokerController.brokerDetails(999, model));
    }

    @Test
    void brokerDetailsJson_ValidId_ReturnsBroker() throws Exception {
        when(kafkaMonitor.getBroker(1)).thenReturn(Optional.of(testBroker));

        mockMvc.perform(get("/broker/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.host").value("localhost"))
            .andExpect(jsonPath("$.port").value(9092))
            .andExpect(jsonPath("$.rack").value("rack1"))
            .andExpect(jsonPath("$.controller").value(true));
    }

    @Test
    void brokerDetailsJson_InvalidId_Returns404() throws Exception {
        when(kafkaMonitor.getBroker(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/broker/999").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAllBrokers_ReturnsAllBrokers() throws Exception {
        when(kafkaMonitor.getBrokers()).thenReturn(testBrokers);

        mockMvc.perform(get("/broker").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[0].controller").value(true))
            .andExpect(jsonPath("$[1].controller").value(false));
    }

    @Test
    void getAllBrokers_EmptyList_ReturnsEmptyArray() throws Exception {
        when(kafkaMonitor.getBrokers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/broker").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }
}
