package report;

import com.fasterxml.jackson.core.JsonProcessingException;
import configuration.ObjectMapperConfiguration;
import exceptions.WiremockTestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportHandlers {

	public static String prettyPrintJson(String jsonString) {
		try {
			return ObjectMapperConfiguration.getDefaultObjectMapper().readTree(jsonString).toPrettyString();
		} catch (JsonProcessingException e) {
			throw new WiremockTestException(
					"Something went wrong during parsing string to json:\n" + jsonString, e);
		}
	}

	public static String prettyPrintXml(String xmlString) {
		try {
			InputSource src = new InputSource(new StringReader(xmlString));
			Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 2);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			Writer out = new StringWriter();
			transformer.transform(new DOMSource(document), new StreamResult(out));
			return out.toString().replaceAll("\\s+\\n", "\n");

		} catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
			throw new WiremockTestException(
					"Something went wrong during parsing string to xml:\n" + xmlString, e);
		}

	}
}
