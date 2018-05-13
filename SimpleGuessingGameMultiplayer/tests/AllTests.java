import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import coretests.GameRoundTests;
import coretests.GameTests;

@RunWith(Suite.class)
@SuiteClasses({
	GameTests.class,
	GameRoundTests.class
})
public class AllTests {

}
