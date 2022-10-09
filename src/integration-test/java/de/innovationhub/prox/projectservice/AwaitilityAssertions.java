package de.innovationhub.prox.projectservice;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import org.awaitility.core.ThrowingRunnable;

public class AwaitilityAssertions {
  public static void assertEventually(ThrowingRunnable assertion) {
    await().atMost(10, TimeUnit.SECONDS).untilAsserted(assertion);
  }
}
