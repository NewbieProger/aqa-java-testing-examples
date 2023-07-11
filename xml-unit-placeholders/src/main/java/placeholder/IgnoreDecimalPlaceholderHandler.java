package placeholder;

import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.placeholder.PlaceholderHandler;

import java.math.BigDecimal;

public class IgnoreDecimalPlaceholderHandler implements PlaceholderHandler {

	/**
	 * example: ${xmlunit.ignoreDecimal(555.555000)}<br>
	 * note: the check compares the numbers mathematically
	 */
	private static final String PLACEHOLDER_NAME = "ignoreDecimal";

	@Override
	public String getKeyword() {
		return PLACEHOLDER_NAME;
	}

	@Override
	public ComparisonResult evaluate(String testText, String... placeholderParameters) {
		if (placeholderParameters.length > 0 && placeholderParameters[0] != null && !placeholderParameters[0].equals("")) {
			int testTextIntValue = new BigDecimal(testText).intValue();
			int placeholderParamIntValue = new BigDecimal(placeholderParameters[0]).intValue();
			if (testTextIntValue == placeholderParamIntValue) {
				return ComparisonResult.EQUAL;
			}
		}
		return ComparisonResult.DIFFERENT;
	}
}
