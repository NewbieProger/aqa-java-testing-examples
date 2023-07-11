package placeholder;

import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.placeholder.PlaceholderHandler;

import java.math.BigDecimal;

public class MatchesDecimalPlaceholderHandler implements PlaceholderHandler {

	/**
	 * example: ${xmlunit.matchesDecimal(300.1200)}
	 */
	private static final String PLACEHOLDER_NAME = "matchesDecimal";

	@Override
	public String getKeyword() {
		return PLACEHOLDER_NAME;
	}

	@Override
	public ComparisonResult evaluate(String testText, String... placeholderParameters) {
		if (placeholderParameters.length > 0 && placeholderParameters[0] != null && !placeholderParameters[0].equals("")) {
			BigDecimal testTextIntValue = new BigDecimal(testText);
			BigDecimal placeholderParamValue = new BigDecimal(placeholderParameters[0]);
			if (testTextIntValue.compareTo(placeholderParamValue) == 0) {
				return ComparisonResult.EQUAL;
			}
		}
		return ComparisonResult.DIFFERENT;
	}
}
