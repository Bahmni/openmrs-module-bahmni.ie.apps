/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at https://www.bahmni.org/license/mplv2hd.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.bahmni.module.bahmni.ie.apps.validator;

import org.junit.Test;

import static org.junit.Assert.*;

public class BahmniFormUtilsTest {

	@Test
	public void ensureThatReplaceAllSpacesToUnderlineOfTheFileName() {

		String fileName = " Test It    abcdefg ";
		fileName = BahmniFormUtils.normalizeFileName(fileName);
		assertEquals("_Test_It____abcdefg_", fileName);
	}

	@Test
	public void ensureThatReplaceAllSpecialCharactersToUnderlineOfTheFileName() {

		String fileName = "Test&a*b@c%d111(8`9。，；'｀。.～！＃$^)-=+\\/\":啊";

		fileName = BahmniFormUtils.normalizeFileName(fileName);

		assertEquals("Test_a_b_c_d111_8_9______.______-_______", fileName);
	}

}
