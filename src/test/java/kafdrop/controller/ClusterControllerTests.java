package kafdrop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import kafdrop.config.KafkaConfiguration;
import kafdrop.service.KafkaMonitor;
import kafdrop.service.BuildInfo;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ClusterControllerTests {

    @Test
    void testBlankBuildProperties() throws Exception {
        Method method = ClusterController.class.getDeclaredMethod("blankBuildProperties");
        method.setAccessible(true);

        BuildProperties buildProperties = (BuildProperties) method.invoke(null);

        assertNotNull(buildProperties);
        assertEquals("3.x", buildProperties.getVersion());
        assertNotNull(buildProperties.get("time"));
        assertTrue(Long.parseLong(buildProperties.get("time")) > 0);
    }

}
