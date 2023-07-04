package report;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import enums.ContentType;
import io.qameta.allure.Allure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;

import java.util.List;
import java.util.Objects;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MockReportHandler {

	private static final String MOCK_ATTACHMENT_TITLE = "%s request: [ %s ]: %s";
	private static final String PRETTY_PRINTED_BODY = "Content-Type: %s\n\n %s";
	public static final String PARAGRAPH_SEPARATOR = "\n-----------------\n";

	public static void attachOuterAndStubContent(List<ServeEvent> serveEvents) {
		serveEvents.forEach(serveEvent -> formAttachedStubContent(serveEvent, serveEvent.getWasMatched()));
	}

	private static StringBuilder attachRequestInfo(ServeEvent serveEvent) {
		return new StringBuilder()
				.append("Received Request:\n\n")
				.append(serveEvent.getRequest())
				.append(PARAGRAPH_SEPARATOR)
				.append("Request Body:\n\n")
				.append(getReceivedRequestBody(serveEvent));
	}

	private static StringBuilder attachStubInfo(ServeEvent serveEvent, StringBuilder stringBuilder) {
		return stringBuilder
				.append(PARAGRAPH_SEPARATOR)
				.append("Matched Stub Mapping:\n\n")
				.append(serveEvent.getStubMapping())
				.append(PARAGRAPH_SEPARATOR)
				.append("Stub Response Body:\n\n")
				.append(getStubResponseBody(serveEvent));
	}

	private static void formAttachedStubContent(ServeEvent serveEvent, boolean requestWasMatched) {
		String stateRequest = requestWasMatched ? "Matched" : "Unmatched";
		Allure.attachment(String.format(MOCK_ATTACHMENT_TITLE,
						stateRequest,
						serveEvent.getRequest().getMethod(),
						serveEvent.getRequest().getUrl()),
				getContentBasedOnRequestStatus(serveEvent, requestWasMatched));
	}

	private static String getContentBasedOnRequestStatus(ServeEvent serveEvent, boolean requestWasMatched) {
		if (requestWasMatched) {
			return MockReportHandler.attachStubInfo(serveEvent,
					MockReportHandler.attachRequestInfo(serveEvent)).toString();
		} else {
			return MockReportHandler.attachRequestInfo(serveEvent).toString();
		}
	}

	private static String getReceivedRequestBody(ServeEvent serveEvent) {
		LoggedRequest requestReceived = serveEvent.getRequest();
		String receivedRequestBody = requestReceived.getBodyAsString();

		if (Strings.isNullOrEmpty(receivedRequestBody)) {
			return "Request doesn't have body";
		}

		return getPrettyPrintBody(receivedRequestBody, getRequestMimeType(requestReceived));
	}

	private static String getStubResponseBody(ServeEvent serveEvent) {
		String bodyAsString = serveEvent.getResponse().getBodyAsString();

		if (Strings.isNullOrEmpty(bodyAsString)) {
			return "Stub response doesn't have body";
		}

		return getPrettyPrintBody(bodyAsString, getStubMimeType(serveEvent));
	}

	private static String getRequestMimeType(LoggedRequest requestReceived) {
		String mimeTypePart = Objects.isNull(requestReceived.getHeaders()) ? null
				: requestReceived.contentTypeHeader().mimeTypePart();

		if (Strings.isNullOrEmpty(mimeTypePart)) {
			return "Request's mimeType is null or empty";
		}

		return mimeTypePart;
	}

	private static String getStubMimeType(ServeEvent serveEvent) {
		String mimeType = serveEvent.getResponse().getMimeType();

		if (Strings.isNullOrEmpty(mimeType)) {
			return "Stub response's mimeType is null or empty";
		}

		return mimeType;
	}

	private static String getPrettyPrintBody(String extractedBody, String mimeTypePart) {
		return switch (ContentType.getNameByValue(mimeTypePart)) {
			case APPLICATION_JSON -> String.format(PRETTY_PRINTED_BODY,
					mimeTypePart, ReportHandlers.prettyPrintJson(extractedBody));
			case APPLICATION_XML, APPLICATION_SOAP_XML, TEXT_XML -> String.format(PRETTY_PRINTED_BODY,
					mimeTypePart, ReportHandlers.prettyPrintXml(extractedBody));
			case UNDEFINED -> String.format(PRETTY_PRINTED_BODY,
					mimeTypePart, extractedBody);
		};
	}
}