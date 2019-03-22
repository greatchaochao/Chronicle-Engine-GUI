package jp.mufg.chronicle.queue;

import jp.mufg.api.util.FromChronicle;
import jp.mufg.api.util.ToChronicle;
import jp.mufg.chronicle.queue.testclasses.*;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.tools.ChronicleTools;
import net.openhft.lang.model.DataValueClasses;
import org.junit.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChronicleObjectSerializationTest
{
    // TODO Fix test so files are deleted on Windows.
    static final AtomicInteger counter = new AtomicInteger();
    String chronicleQueueBase = OS.TARGET + "/Chronicle/data";
    Chronicle chronicle;
    EnumTestInterface writer;
    FromChronicle<EnumTestInterfaceImpl> reader;

    @Before
    public void setUp() throws IOException {
        String path = chronicleQueueBase + "/" + counter.getAndIncrement();
        ChronicleTools.deleteDirOnExit(path);

        chronicle = ChronicleQueueBuilder.vanilla(path).build();

        EnumTestInterfaceImpl enumTestImpl = new EnumTestInterfaceImpl();

        writer = ToChronicle.of(EnumTestInterface.class, chronicle);
        ExcerptTailer tailer = chronicle.createTailer();
        tailer.toEnd();
        reader = FromChronicle.of(enumTestImpl, tailer);
    }

    @After
    public void tearDown() throws IOException {
        chronicle.close();
        ChronicleTools.deleteDirOnExit(chronicleQueueBase);
    }

    @Test
    public void testObjectWithEnum()
    {
        ObjectWithEnum objectWithEnum = new ObjectWithEnum();
        objectWithEnum.setSomeString("some random string");
        objectWithEnum.setTestEnum(TestEnum.TEST1);

        //Put on queue
        writer.writeObjectWithEnum(objectWithEnum);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithoutEnum()
    {
        ObjectWithoutEnum objectWithoutEnum = new ObjectWithoutEnum();
        objectWithoutEnum.setSomeString("some random string");
        objectWithoutEnum.setSomeDouble(1.0);
        objectWithoutEnum.setSomeInt(42);

        //Put on queue
        writer.writeObjectWithoutEnum(objectWithoutEnum);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithoutEnumAutoGeneratedClass()
    {
        ObjectWithoutEnumDataValueClass objectWithoutEnumDataValueClass =
                DataValueClasses.newInstance(ObjectWithoutEnumDataValueClass.class);
        objectWithoutEnumDataValueClass.setSomeString("some random string");
        objectWithoutEnumDataValueClass.setSomeDouble(1.0);
        objectWithoutEnumDataValueClass.setSomeInt(42);

        //Put on queue
        writer.writeObjectWithoutEnumDataValueClass(objectWithoutEnumDataValueClass);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithoutEnumExternalizable()
    {
        ObjectWithoutEnumExternalizable objectWithoutEnumExternalizable = new ObjectWithoutEnumExternalizable();
        objectWithoutEnumExternalizable.setSomeString("some random string");
        objectWithoutEnumExternalizable.setSomeDouble(1.0);
        objectWithoutEnumExternalizable.setSomeInt(42);

        //Put on queue
        writer.writeObjectWithoutEnumExternalizable(objectWithoutEnumExternalizable);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithEnumExternalizable()
    {
        ObjectWithEnumExternalizable objectWithEnumExternalizable = new ObjectWithEnumExternalizable();
        objectWithEnumExternalizable.setSomeString("some random string");
        objectWithEnumExternalizable.setTestEnum(TestEnum.RANDOM);

        //Put on queue
        writer.writeObjectWithEnumExternalizable(objectWithEnumExternalizable);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithEnumExternalizableAndStringObjectMap()
    {
        ObjectWithEnumExternalizable objectWithEnumExternalizable = new ObjectWithEnumExternalizable();
        objectWithEnumExternalizable.setSomeString("some random string");
        objectWithEnumExternalizable.setTestEnum(TestEnum.RANDOM);

        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("String1", "newString");
        stringObjectMap.put("Double1", 2.0);

        //Put on queue
        writer.writeObjectWithEnumExternalizableAndStringObjectMap(objectWithEnumExternalizable, stringObjectMap);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithEnumExternalizableAndStringDoubleMap()
    {
        ObjectWithEnumExternalizable objectWithEnumExternalizable = new ObjectWithEnumExternalizable();
        objectWithEnumExternalizable.setSomeString("some random string");
        objectWithEnumExternalizable.setTestEnum(TestEnum.RANDOM);

        Map<String, Double> stringDoubleMap = new HashMap<>();
        stringDoubleMap.put("Key1", 1.0);
        stringDoubleMap.put("Key2", 2.0);

        //Put on queue
        writer.writeObjectWithEnumExternalizableAndStringDoubleMap(objectWithEnumExternalizable, stringDoubleMap);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    public void testObjectWithEnumExternalizableAndEnumObjectMap()
    {
        ObjectWithEnumExternalizable objectWithEnumExternalizable = new ObjectWithEnumExternalizable();
        objectWithEnumExternalizable.setSomeString("some random string");
        objectWithEnumExternalizable.setTestEnum(TestEnum.RANDOM);

        Map<TestEnum, Object> enumObjectMap = new HashMap<>();
        enumObjectMap.put(TestEnum.RANDOM, "StringValue");
        enumObjectMap.put(TestEnum.TEST2, 2.1);

        //Put on queue
        writer.writeObjectWithEnumExternalizableAndEnumObjectMap(objectWithEnumExternalizable, enumObjectMap);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    @Test
    @Ignore("TODO Not supported currently")
    public void testDataValueClassWithEnumAndObjectMap()
    {
        Map<TestEnum, Object> enumObjectMap = new HashMap<>();
        enumObjectMap.put(TestEnum.RANDOM, "StringValue");
        enumObjectMap.put(TestEnum.TEST2, 2.1);

        //Put on queue
        MapFieldDataValueClass mapFieldDataValueClass = DataValueClasses.newInstance(MapFieldDataValueClass.class);
        mapFieldDataValueClass.setSomeString("SomeString");
        mapFieldDataValueClass.setMap(new HashMap<String, Double>());

        writer.writeObjectWithMapDataValueClass(mapFieldDataValueClass);

        //Read from queue
        Assert.assertTrue(reader.readOne());
        Assert.assertFalse(reader.readOne());
        Assert.assertFalse(reader.readOne());
    }

    //This fails due to StreamCorruptedException. I am guessing this is because it cannot parse values with different types (type of Object)
    //Exception in thread "main" java.lang.IllegalStateException: java.lang.IllegalStateException: java.io.StreamCorruptedException: UTF length invalid 84 remaining: 22
    @Test
    @Ignore("TODO Fix test")
    public void testWriteReadStringObjectMapStraightToFromQueue() throws IOException
    {
        ExcerptAppender appender = chronicle.createAppender();
        appender.startExcerpt();

        Map<String, Object> putMap = new HashMap<String, Object>();
        putMap.put("TestKey", "TestValue");
        putMap.put("TestKey2", 1.0);

        appender.writeMap(putMap);

        appender.finish();

        ExcerptTailer tailer = chronicle.createTailer();

        Map<String, Object> newMap = new HashMap<String, Object>();

        Assert.assertTrue(tailer.nextIndex());

        tailer.readMap(newMap, String.class, Object.class);

        Assert.assertEquals(putMap, newMap);

        Assert.assertFalse(tailer.nextIndex());
    }

    @Test
    public void testWriteReadStringStringMapStraightToFromQueue() throws IOException
    {
        ExcerptAppender appender = chronicle.createAppender();
        appender.startExcerpt();

        Map<String, String> putMap = new HashMap<String, String>();
        putMap.put("TestKey", "TestValue");
        putMap.put("TestKey2", "sdsd");

        appender.writeMap(putMap);

        appender.finish();

        ExcerptTailer tailer = chronicle.createTailer();

        Map<String, String> newMap = new HashMap<String, String>();

        Assert.assertTrue(tailer.nextIndex());

        tailer.readMap(newMap, String.class, String.class);

        Assert.assertEquals(putMap, newMap);

        Assert.assertFalse(tailer.nextIndex());
    }

    @Test
    public void testWriteReadStringDoubleMapStraightToFromQueue() throws IOException
    {
        ExcerptAppender appender = chronicle.createAppender();
        appender.startExcerpt();

        Map<String, Double> putMap = new HashMap<String, Double>();
        putMap.put("TestKey", 1.0);
        putMap.put("TestKey2", 2.0);

        appender.writeMap(putMap);

        appender.finish();

        ExcerptTailer tailer = chronicle.createTailer();

        Map<String, Double> newMap = new HashMap<String, Double>();

        Assert.assertTrue(tailer.nextIndex());

        tailer.readMap(newMap, String.class, Double.class);

        Assert.assertEquals(putMap, newMap);

        Assert.assertFalse(tailer.nextIndex());
    }

//    @Test
//    public void test()
//    {
//
//        //Read from queue
//        Assert.assertTrue(reader.readOne());
//        Assert.assertFalse(reader.readOne());
//        Assert.assertFalse(reader.readOne());
//    }
//
//    @Test
//    public void test()
//    {
//
//        //Read from queue
//        Assert.assertTrue(reader.readOne());
//        Assert.assertFalse(reader.readOne());
//        Assert.assertFalse(reader.readOne());
//    }
}