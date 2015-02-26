package jp.mufg.examples.ObjectWithEnumExamples;

import com.sun.tools.javac.util.*;
import jp.mufg.api.util.*;
import net.openhft.chronicle.*;
import net.openhft.chronicle.tools.*;
import net.openhft.lang.model.*;

import java.io.*;

/**
 * Created by daniels on 26/02/2015.
 */
public class Run
{
    public static void main(String[] args)
    {
        //This writes two objects to the queue for some reason
        runObjectWithEnumExample();

        //This writes two objects to the queue for some reason
//        runObjectWithoutEnumExample();

        //This only writes one object to the queue as expected
//        runObjectWithoutEnumAutoGeneratedClassExample();

        //This only writes one object to the queue as expected
//        runObjectWithoutEnumExternalizable();

//        runObjectWithEnumExternalizable();

    }

    private static void runObjectWithEnumExample()
    {
        try
        {
            String chronicleQueueBase = "C:\\LocalFolder\\Chronicle\\data";
            Chronicle chronicle = ChronicleQueueBuilder.vanilla(chronicleQueueBase).build();
            ChronicleTools.deleteDirOnExit(chronicleQueueBase);

            EnumTestImpl enumTest = new EnumTestImpl();

            EnumTest writer = ToChronicle.of(EnumTest.class, chronicle);
            FromChronicle<EnumTestImpl> reader = FromChronicle.of(enumTest, chronicle.createTailer());

            ObjectWithEnum objectWithEnum = new ObjectWithEnum();
            objectWithEnum.setSomeString("some random string");
            objectWithEnum.setTestEnum(TestEnum.TEST1);

            //Put a MapMarketDataUpdate on the queue
            writer.writeObjectWithEnum(objectWithEnum);

            //This should read the MapMarketDataUpdate from the queue and call the implementation
            Assert.check(reader.readOne());
            Assert.check(!reader.readOne());
            Assert.check(!reader.readOne());

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void runObjectWithoutEnumExample()
    {
        try
        {
            String chronicleQueueBase = "C:\\LocalFolder\\Chronicle\\data";
            Chronicle chronicle = ChronicleQueueBuilder.vanilla(chronicleQueueBase).build();
            ChronicleTools.deleteDirOnExit(chronicleQueueBase);

            EnumTestImpl enumTest = new EnumTestImpl();

            EnumTest writer = ToChronicle.of(EnumTest.class, chronicle);
            FromChronicle<EnumTestImpl> reader = FromChronicle.of(enumTest, chronicle.createTailer());

            ObjectWithoutEnum objectWithoutEnum = new ObjectWithoutEnum();
            objectWithoutEnum.setSomeString("some random string");
            objectWithoutEnum.setSomeDouble(1.0);
            objectWithoutEnum.setSomeInt(42);

            //Put a MapMarketDataUpdate on the queue
            writer.writeObjectWithoutEnum(objectWithoutEnum);

            //This should read the MapMarketDataUpdate from the queue and call the implementation
            Assert.check(reader.readOne());
            Assert.check(!reader.readOne());
            Assert.check(!reader.readOne());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void runObjectWithoutEnumAutoGeneratedClassExample()
    {
        try
        {
            String chronicleQueueBase = "C:\\LocalFolder\\Chronicle\\data";
            Chronicle chronicle = ChronicleQueueBuilder.vanilla(chronicleQueueBase).build();
            ChronicleTools.deleteDirOnExit(chronicleQueueBase);

            EnumTestImpl enumTest = new EnumTestImpl();

            EnumTest writer = ToChronicle.of(EnumTest.class, chronicle);
            FromChronicle<EnumTestImpl> reader = FromChronicle.of(enumTest, chronicle.createTailer());

            ObjectWithoutEnumDataValueClass objectWithoutEnumDataValueClass = DataValueClasses.newInstance(ObjectWithoutEnumDataValueClass.class);
            objectWithoutEnumDataValueClass.setSomeString("some random string");
            objectWithoutEnumDataValueClass.setSomeDouble(1.0);
            objectWithoutEnumDataValueClass.setSomeInt(42);

            //Put a MapMarketDataUpdate on the queue
            writer.writeObjectWithoutEnumDataValueClass(objectWithoutEnumDataValueClass);

            //This should read the MapMarketDataUpdate from the queue and call the implementation
            Assert.check(reader.readOne());
            Assert.check(!reader.readOne());
            Assert.check(!reader.readOne());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void runObjectWithoutEnumExternalizable()
    {
        try
        {
            String chronicleQueueBase = "C:\\LocalFolder\\Chronicle\\data";
            Chronicle chronicle = ChronicleQueueBuilder.vanilla(chronicleQueueBase).build();
            ChronicleTools.deleteDirOnExit(chronicleQueueBase);

            EnumTestImpl enumTest = new EnumTestImpl();

            EnumTest writer = ToChronicle.of(EnumTest.class, chronicle);
            FromChronicle<EnumTestImpl> reader = FromChronicle.of(enumTest, chronicle.createTailer());

            ObjectWithoutEnumExternalizable objectWithoutEnumExternalizable = new ObjectWithoutEnumExternalizable();
            objectWithoutEnumExternalizable.setSomeString("some random string");
            objectWithoutEnumExternalizable.setSomeDouble(1.0);
            objectWithoutEnumExternalizable.setSomeInt(42);

            //Put a MapMarketDataUpdate on the queue
            writer.writeObjectWithoutEnumExternalizable(objectWithoutEnumExternalizable);

            //This should read the MapMarketDataUpdate from the queue and call the implementation
            Assert.check(reader.readOne());
            Assert.check(!reader.readOne());
            Assert.check(!reader.readOne());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void runObjectWithEnumExternalizable()
    {
        try
        {
            String chronicleQueueBase = "C:\\LocalFolder\\Chronicle\\data";
            Chronicle chronicle = ChronicleQueueBuilder.vanilla(chronicleQueueBase).build();
            ChronicleTools.deleteDirOnExit(chronicleQueueBase);

            EnumTestImpl enumTest = new EnumTestImpl();

            EnumTest writer = ToChronicle.of(EnumTest.class, chronicle);
            FromChronicle<EnumTestImpl> reader = FromChronicle.of(enumTest, chronicle.createTailer());

            ObjectWithEnumExternalizable objectWithEnumExternalizable = new ObjectWithEnumExternalizable();
            objectWithEnumExternalizable.setSomeString("some random string");
            objectWithEnumExternalizable.setTestEnum(TestEnum.RANDOM);

            //Put a MapMarketDataUpdate on the queue
            writer.writeObjectWithEnumExternalizable(objectWithEnumExternalizable);

            //This should read the MapMarketDataUpdate from the queue and call the implementation
            Assert.check(reader.readOne());
            Assert.check(!reader.readOne());
            Assert.check(!reader.readOne());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}