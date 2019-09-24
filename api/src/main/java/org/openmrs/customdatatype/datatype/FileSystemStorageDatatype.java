package org.openmrs.customdatatype.datatype;

import org.apache.commons.io.FileUtils;
import org.openmrs.customdatatype.SerializingCustomDatatype;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

public class FileSystemStorageDatatype extends SerializingCustomDatatype<String> {

	public static final String DEFAULT_ENCODING = "utf-8";

	private String fileName;

	@Override
	public void setConfiguration(String config) {
		this.fileName = config;
	}

	@Override
	public String serialize(String typedValue) {
		writeStringToFile(typedValue, new File(this.fileName));
		return this.fileName;
	}

	private void writeStringToFile(String typedValue, File file) {
		try {
			FileUtils.writeStringToFile(file, typedValue, DEFAULT_ENCODING);
		}
		catch (IOException e) {
			throw new UncheckedIOException("Unable to save the file with name [" + this.fileName + "]", e);
		}
	}

	@Override
	public String deserialize(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName), DEFAULT_ENCODING);
		}
		catch (IOException e) {
			throw new UncheckedIOException(
					"Unable to read from the file with name [" + new File(fileName).getAbsoluteFile() + "]", e);
		}
	}
}
