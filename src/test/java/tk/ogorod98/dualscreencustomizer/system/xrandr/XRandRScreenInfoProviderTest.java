package tk.ogorod98.dualscreencustomizer.system.xrandr;

import java.util.concurrent.CountDownLatch;
import junit.framework.TestCase;
import org.junit.Test;
import tk.ogorod98.dualscreencustomizer.screeninfo.ScreenInfoRegistry;

public class XRandRScreenInfoProviderTest extends TestCase {

	@Test
	public void testSimpleUsecase() throws InterruptedException {
		final var provider = new XRandRScreenInfoProvider();
		final ScreenInfoRegistry[] registry = new ScreenInfoRegistry[1];
		final CountDownLatch latch = new CountDownLatch(1);
		provider.onUpdate(newRegistry -> {
			synchronized (this) {
				if (registry[0] == null) {
					registry[0] = newRegistry;
					latch.countDown();
				}
			}
		});
		provider.actionPerformed(null);
		latch.await();
		provider.dispose();
		System.out.println(registry[0]);
	}

}