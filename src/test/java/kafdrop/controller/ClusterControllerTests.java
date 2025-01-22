package kafdrop.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class ClusterControllerTests {

    @Test
    void blankBuildProperties_ShouldReturnDefaultProperties() throws Exception {
        Method method = ClusterController.class.getDeclaredMethod("blankBuildProperties");
        method.setAccessible(true);

        BuildProperties properties = (BuildProperties) method.invoke(null);

        assertNotNull(properties);
        assertEquals("3.x", properties.get("version"));
        assertTrue(properties.get("time").matches("\\d+"));
    }

}
