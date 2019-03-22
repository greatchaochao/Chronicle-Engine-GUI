package jp.mufg.chronicle.map.eventlistener;

import ddp.api.TestUtils;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.engine.api.map.KeyValueStore;
import net.openhft.chronicle.engine.api.map.MapView;
import net.openhft.chronicle.engine.api.pubsub.TopicSubscriber;
import net.openhft.chronicle.engine.map.ChronicleMapKeyValueStore;
import net.openhft.chronicle.engine.map.VanillaMapView;
import net.openhft.chronicle.engine.server.ServerEndpoint;
import net.openhft.chronicle.engine.tree.VanillaAsset;
import net.openhft.chronicle.engine.tree.VanillaAssetTree;
import net.openhft.chronicle.network.TCPRegistry;
import net.openhft.chronicle.wire.WireType;
import net.openhft.chronicle.wire.YamlLogging;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Created by daniels on 31/03/2015.
 */
public class ChronicleMapEventListenerStatelessClientTest {
    private static final String _mapBasePath = OS.TARGET + "/ChronicleMapEventListenerStatelessClientTest";
    private static final VanillaAssetTree serverAssetTree = new VanillaAssetTree().forServer(true);
    private static final AtomicInteger _noOfEventsTriggered = new AtomicInteger();
    private static ChronicleTestEventListener _chronicleTestEventListener;
    private static VanillaAssetTree clientAssetTree;
    private static Map<String, String> _StringStringMap;
    private static Map<String, String> _StringStringMapClient;
    private static ServerEndpoint serverEndpoint;
    private final String _value1;
    private final String _value2;

    public ChronicleMapEventListenerStatelessClientTest() {
        char[] value = new char[2 << 20];
        Arrays.fill(value, 'Z');
        _value1 = new String(value);
        Arrays.fill(value, 'd');
        _value2 = new String(value);
    }

    @BeforeClass
    public static void beforeClass() throws IOException {
        TCPRegistry.createServerSocketChannelFor("ChronicleMapEventListenerStatelessClientTest");

        clientAssetTree = new VanillaAssetTree().forRemoteAccess
                ("ChronicleMapEventListenerStatelessClientTest", WireType.TEXT, Throwable::printStackTrace);

        TestUtils.deleteRecursive(new File(_mapBasePath));

        VanillaAsset root = serverAssetTree.root();
        root.addWrappingRule(MapView.class, "map directly to KeyValueStore", VanillaMapView::new, KeyValueStore.class);
        serverAssetTree.root().addLeafRule(KeyValueStore.class, "use Chronicle Map", (context, asset) ->
                new ChronicleMapKeyValueStore(context.basePath(_mapBasePath).entries(50).averageValueSize(2 << 20).putReturnsNull(true), asset));

        serverEndpoint = new ServerEndpoint("ChronicleMapEventListenerStatelessClientTest", serverAssetTree, "cluster");

        _noOfEventsTriggered.set(0);

        _StringStringMap = serverAssetTree.acquireMap("chronicleMapString?putReturnsNull=true", String.class, String.class);

        _chronicleTestEventListener = new ChronicleTestEventListener();

        // TODO change this to be a remote session.
        //_StringStringMapClient = clientAssetTree.acquireMap("chronicleMapString", String.class, String.class);
        clientAssetTree.registerTopicSubscriber("chronicleMapString", String.class,
                String.class, _chronicleTestEventListener);
        YamlLogging.setAll(false);

    }

    @AfterClass
    public static void tearDown() {
//        _StringStringMap.clear();
        clientAssetTree.close();
        serverEndpoint.close();
        serverAssetTree.close();
    }

    /**
     * Test that event listener is triggered for every put.
     *
     */
    @Test
    @Ignore("TODO FIX")
    public void testMapEvenListenerClientPut() {
        String testKey = "TestKeyPut";
        int noOfIterations = 50;

        testIterateAndAlternate(
                (x) -> _StringStringMap.put(testKey, x),
                (x) -> _StringStringMap.put(testKey, x),
                noOfIterations);
    }

    /**
     * Test that event listener is triggered for every replace.
     *
     */
    @Test
    @Ignore("TODO FIX")
    public void testMapEvenListenerReplace() {
        String testKey = "TestKeyGetReplace";
        int noOfIterations = 50;

        _StringStringMap.put(testKey, _value2);

        Consumer<String> consumer = (x) -> _StringStringMap.replace(testKey, x);

        testIterateAndAlternate(
                consumer,
                consumer,
                noOfIterations);
    }

    /**
     * Performs the given number of iterations and alternates between calling consumer1 and
     * consumer2 passing either _value1 or _value2.
     *
     * @param consumer1      Consumer1 to call.
     * @param consumer2      Consumer2 to call.
     * @param noOfIterations Number of iterations to perform.
     */
    private void testIterateAndAlternate(Consumer<String> consumer1, Consumer<String> consumer2, int noOfIterations) {
        _noOfEventsTriggered.set(0);
        long startTime = System.nanoTime();
        int count = 0;
        while (System.nanoTime() - startTime < 5e9) {
            for (int i = 0; i < noOfIterations; i++) {
                if (i % 2 == 0) {
                    consumer1.accept(_value1);
                } else {
                    consumer2.accept(_value2);
                }
                final int count2 = count;
                waitFor(() -> _noOfEventsTriggered.get() >= count2);
                count++;
            }
        }
        TestUtils.calculateAndPrintRuntime(startTime, count / noOfIterations);

        Assert.assertEquals(count, _noOfEventsTriggered.get(), 1);
        try {
            //Give it a chance to print the times.
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void waitFor(BooleanSupplier b) {
        for (int i = 1; i <= 50; i++)
            if (!b.getAsBoolean())
                Jvm.pause(i * i);
    }

    static class ChronicleTestEventListener implements TopicSubscriber<String, String> {
        @Override
        public void onMessage(String topic, String message) {
            _noOfEventsTriggered.incrementAndGet();
        }
    }
}