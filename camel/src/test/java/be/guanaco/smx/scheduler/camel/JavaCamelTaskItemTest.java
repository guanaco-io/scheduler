package be.guanaco.smx.scheduler.camel;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.createMock;

/**
 * Test cases for {@link JavaCamelTaskItem}
 */
public class JavaCamelTaskItemTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testIncrementalUpdatesNotSupported() {
        JavaCamelTaskItem<String> item = new JavaCamelTaskItem<String>() {
            @Override
            protected List<String> getExchangeBodies() {
                return null;
            }

            @Override
            protected List<String> getTargetEndpoints(String body) {
                return null;
            }
        };
        // this should throw an UnsupportedOperationException
        item.handle(createMock(ProducerTemplate.class), new CamelTaskItem.Incremental());
    }

}
