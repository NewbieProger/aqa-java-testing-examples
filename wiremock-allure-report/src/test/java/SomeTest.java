import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import report.MockReportHandler;

import java.util.List;

public class SomeTest {

	@Test
	public void someTest() {

		List<ServeEvent> serveEventList = null; //Here's your collected ServeEvents from wiremock
		MockReportHandler.attachOuterAndStubContent(serveEventList);

		Assertions.assertThat(serveEventList)
				.size()
				.isEqualTo(3);
	}
}
