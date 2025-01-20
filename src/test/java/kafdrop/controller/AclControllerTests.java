package kafdrop.controller;

import kafdrop.model.AclVO;
import kafdrop.service.KafkaMonitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AclControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    private AclController controller;

    private List<AclVO> mockAcls;

    @BeforeEach
    void setUp() {
        controller = new AclController(kafkaMonitor);
        mockAcls = Arrays.asList(
            new AclVO("Topic", "test-topic", "LITERAL", "User:test", "*", "READ", "ALLOW"),
            new AclVO("Group", "test-group", "LITERAL", "User:test", "*", "READ", "ALLOW")
        );
    }

    @Test
    void acls_ShouldReturnViewNameAndPopulateModel() {
        when(kafkaMonitor.getAcls()).thenReturn(mockAcls);

        String viewName = controller.acls(model);

        assertThat(viewName).isEqualTo("acl-overview");
        verify(model).addAttribute("acls", mockAcls);
    }

    @Test
    void getAllTopics_ShouldReturnAclsList() {
        when(kafkaMonitor.getAcls()).thenReturn(mockAcls);

        List<AclVO> result = controller.getAllTopics();

        assertThat(result).isEqualTo(mockAcls);
        assertThat(result).hasSize(2);
    }
}
