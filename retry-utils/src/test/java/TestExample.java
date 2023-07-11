import manager.TestManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestExample {

    /**
     * Here's you can see an example of using one of methods for waiting<br>
     * We check that DB has been cleared and if not we wait for it<br>
     * At 20th row you can see that we attach something if retires count exhausted
     */
    @Test
    public void testExampleFirst() {
        List<String> listOfRows = RetryUtils.getUntil(
                RetryUtils.RETRY_SPEC_WITH_INITIAL_DELAY,
                TestManager::fetchSomeInfoFromDataBase,
                ArrayList::isEmpty,
                new RetryUtils.RetryCallback() {
                    @Override
                    public void onRetriesCountExhausted() {
                        TestManager.attachSomeInfoToReport();
                    }
                }
        );

        Assert.assertFalse("List is empty", listOfRows.isEmpty());
    }

}
