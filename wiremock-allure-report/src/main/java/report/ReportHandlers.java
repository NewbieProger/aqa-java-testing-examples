package report;

import com.fasterxml.jackson.core.JsonProcessingException;
import configuration.ObjectMapperConfiguration;
import exceptions.WiremockTestException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportHandlers {

	public static String prettyPrintJson(String json) {
		try {
			return ObjectMapperConfiguration.getDefaultObjectMapper().readTree(json).toPrettyString();
		} catch (JsonProcessingException e) {
			throw new WiremockTestException(
					"Something went wrong during parsing string to json:\n" + json);
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
			return out.toString().replaceAll("\\s+\\n","\n");
		} catch (Exception e) {
			throw new WiremockTestException("Error occurs when pretty-printing xml:\n" + xmlString, e);
		}
	}
}
