package org.bahmni.customdatatype.datatype;

import org.apache.commons.io.FileUtils;
import org.bahmni.customdatatype.datatype.FileSystemStorageDatatype;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import static org.junit.Assert.assertEquals;

public class FileSystemStorageDatatypeTest {

    FileSystemStorageDatatype fileSystemStorageDatatype;

    @Before
    public void setUp() throws Exception {
        fileSystemStorageDatatype = new FileSystemStorageDatatype();
    }

    @Test
    public void ensureThatTheValueIsStoredInFileSystemAndReferenceCaptured() throws Exception {
        fileSystemStorageDatatype.setConfiguration("./testData_1.txt");
        String fileName = fileSystemStorageDatatype.serialize("this is some sample data");

        assertEquals(true, new File(fileName).exists());
        assertEquals("./testData_1.txt",fileName);
    }

    @Test
    public void ensureThatTheValueIsOverwrittenInFileSystemAndReferenceCaptured() throws Exception {
        File sampleDataFile = new File("./testData_2.txt");
        FileUtils.writeStringToFile(sampleDataFile,"initial value");

        fileSystemStorageDatatype.setConfiguration("./testData_2.txt");
        String fileName = fileSystemStorageDatatype.serialize("updated value");

        assertEquals(true, sampleDataFile.exists());
        assertEquals("./testData_2.txt",fileName);

        String actualValue = FileUtils.readFileToString(sampleDataFile);
        assertEquals("updated value", actualValue);
    }

    @Test
    public void ensureThatTheFileContentIsReturned() throws IOException {
        String fileName = "./testData_3.txt";
        File sampleDataFile = new File(fileName);
        FileUtils.writeStringToFile(sampleDataFile,"initial value");

        fileSystemStorageDatatype.setConfiguration(fileName);
        String content = fileSystemStorageDatatype.deserialize(fileName);

        assertEquals("initial value",content);
    }

    @Test(expected = UncheckedIOException.class)
    public void ensureThatAProperExceptionIsReturnedWhenFileNameIsInvalid(){
        String fileName = "something.txt";

        fileSystemStorageDatatype.setConfiguration(fileName);
        fileSystemStorageDatatype.deserialize(fileName);
    }

}