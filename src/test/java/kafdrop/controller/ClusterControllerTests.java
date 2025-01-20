package kafdrop.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.test.util.ReflectionTestUtils;
import kafdrop.config.KafkaConfiguration;
import kafdrop.service.BuildInfo;
import kafdrop.service.KafkaMonitor;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ClusterControllerTests {

    @Test
    void testBlankBuildProperties() {
        // Mock dependencies
        KafkaConfiguration kafkaConfig = Mockito.mock(KafkaConfiguration.class);
        KafkaMonitor kafkaMonitor = Mockito.mock(KafkaMonitor.class);

        @SuppressWarnings("unchecked")
        ObjectProvider<BuildInfo> buildInfoProvider = Mockito.mock(ObjectProvider.class);
        when(buildInfoProvider.stream()).thenReturn(Stream.empty());

        ClusterController controller = new ClusterController(kafkaConfig, kafkaMonitor, buildInfoProvider, true);

        // Use reflection to access private method
        BuildProperties props = (BuildProperties) ReflectionTestUtils.invokeMethod(
            controller,
            "blankBuildProperties"
        );

        assertNotNull(props);
        assertEquals("3.x", props.getVersion());
        assertNotNull(props.getTime());
    }
}
