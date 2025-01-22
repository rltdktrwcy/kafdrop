package kafdrop.controller;

import kafdrop.model.AclVO;
import kafdrop.service.KafkaMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AclControllerTests {

    private MockMvc mockMvc;

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    private AclController aclController;

    @BeforeEach
    void setUp() {
        aclController = new AclController(kafkaMonitor);
        mockMvc = MockMvcBuilders.standaloneSetup(aclController).build();
    }

    @Test
    void testAclsEndpoint() {
        List<AclVO> mockAcls = Arrays.asList(
            new AclVO("TOPIC", "test-topic", "LITERAL", "User:test", "*", "READ", "ALLOW"),
            new AclVO("GROUP", "test-group", "LITERAL", "User:test", "*", "READ", "ALLOW")
        );

        when(kafkaMonitor.getAcls()).thenReturn(mockAcls);

        String viewName = aclController.acls(model);

        assertEquals("acl-overview", viewName);
        verify(model).addAttribute("acls", mockAcls);
        verify(kafkaMonitor).getAcls();
    }

    @Test
    void testGetAllTopics() throws Exception {
        List<AclVO> mockAcls = Arrays.asList(
            new AclVO("TOPIC", "test-topic", "LITERAL", "User:test", "*", "READ", "ALLOW"),
            new AclVO("GROUP", "test-group", "LITERAL", "User:test", "*", "READ", "ALLOW")
        );

        when(kafkaMonitor.getAcls()).thenReturn(mockAcls);

        mockMvc.perform(get("/acl")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].resourceType").value("TOPIC"))
                .andExpect(jsonPath("$[0].name").value("test-topic"))
                .andExpect(jsonPath("$[1].resourceType").value("GROUP"))
                .andExpect(jsonPath("$[1].name").value("test-group"));

        verify(kafkaMonitor).getAcls();
    }

    @Test
    void testGetAllTopicsEmptyList() throws Exception {
        when(kafkaMonitor.getAcls()).thenReturn(List.of());

        mockMvc.perform(get("/acl")
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(kafkaMonitor).getAcls();
    }
}
