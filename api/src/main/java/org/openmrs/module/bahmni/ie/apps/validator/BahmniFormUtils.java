package org.openmrs.module.bahmni.ie.apps.validator;

public class BahmniFormUtils {

	private final static String VALID_FILE_NAME_CHAR_REGEX = "[^a-zA-Z0-9_\\-.]";

	public static String normalizeFileName(String fileName) {
		return fileName.replaceAll(VALID_FILE_NAME_CHAR_REGEX, "_");
	}

}
