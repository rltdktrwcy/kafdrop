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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AclControllerTests {

    @Mock
    private KafkaMonitor kafkaMonitor;

    @Mock
    private Model model;

    private AclController aclController;

    @BeforeEach
    void setUp() {
        aclController = new AclController(kafkaMonitor);
    }

    @Test
    void acls_ShouldReturnViewNameAndAddAclsToModel() {
        // Arrange
        List<AclVO> mockAcls = Arrays.asList(
            new AclVO("TOPIC", "test-topic", "LITERAL", "User:test", "*", "READ", "ALLOW"),
            new AclVO("GROUP", "test-group", "LITERAL", "User:test", "*", "READ", "ALLOW")
        );
        when(kafkaMonitor.getAcls()).thenReturn(mockAcls);

        // Act
        String viewName = aclController.acls(model);

        // Assert
        assertThat(viewName).isEqualTo("acl-overview");
        verify(model).addAttribute("acls", mockAcls);
        verify(kafkaMonitor).getAcls();
    }

    @Test
    void getAllTopics_ShouldReturnListOfAcls() {
        // Arrange
        List<AclVO> mockAcls = Arrays.asList(
            new AclVO("TOPIC", "test-topic", "LITERAL", "User:test", "*", "READ", "ALLOW"),
            new AclVO("GROUP", "test-group", "LITERAL", "User:test", "*", "READ", "ALLOW")
        );
        when(kafkaMonitor.getAcls()).thenReturn(mockAcls);

        // Act
        List<AclVO> result = aclController.getAllTopics();

        // Assert
        assertThat(result).isEqualTo(mockAcls);
        verify(kafkaMonitor).getAcls();
    }
}
