package utils;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RetryAnalyzer implements IRetryAnalyzer {

    // Number of retries after the first failure. Override with -Dretry.count=2
    private static final int MAX_RETRY = Integer.getInteger("retry.count", 1);

    // Tracked per scenario, because all scenarios share the same runScenario method
    private static final Map<String, Integer> attempts = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        String key = result.getMethod().getMethodName() + Arrays.toString(result.getParameters());
        int used = attempts.getOrDefault(key, 0);
        if (used < MAX_RETRY) {
            attempts.put(key, used + 1);
            System.out.println("Retrying scenario (" + (used + 1) + "/" + MAX_RETRY + "): " + key);
            return false;
        }
        return false;
    }
}