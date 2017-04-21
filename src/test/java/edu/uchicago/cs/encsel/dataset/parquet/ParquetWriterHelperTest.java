/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contributors:
 *     Hao Jiang - initial API and implementation
 *******************************************************************************/
package edu.uchicago.cs.encsel.dataset.parquet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import edu.uchicago.cs.encsel.model.IntEncoding;
import edu.uchicago.cs.encsel.model.StringEncoding;

public class ParquetWriterHelperTest {

	@Before
	public void deleteFile() throws IOException {
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_str.data.DELTAL"));
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_str.data.DICT"));
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_str.data.PLAIN"));

		Files.deleteIfExists(Paths.get("src/test/resource/test_col_int.data.DICT"));
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_int.data.BP"));
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_int.data.DELTABP"));
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_int.data.RLE"));
		Files.deleteIfExists(Paths.get("src/test/resource/test_col_int.data.PLAIN"));
	}

	@Test
	public void testWriteStr() throws IOException {

		String file = "src/test/resource/test_col_str.data";

		ParquetWriterHelper.singleColumnString(new File(file).toURI(), StringEncoding.DICT);
		ParquetWriterHelper.singleColumnString(new File(file).toURI(), StringEncoding.DELTAL);
		ParquetWriterHelper.singleColumnString(new File(file).toURI(), StringEncoding.PLAIN);
		
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_str.data.DELTAL")));
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_str.data.DICT")));
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_str.data.PLAIN")));
	}

	@Test
	public void testWriteInt() throws IOException {
		String file = "src/test/resource/test_col_int.data";

		ParquetWriterHelper.singleColumnInt(new File(file).toURI(), IntEncoding.DICT);
		ParquetWriterHelper.singleColumnInt(new File(file).toURI(), IntEncoding.BP);
		ParquetWriterHelper.singleColumnInt(new File(file).toURI(), IntEncoding.DELTABP);
		ParquetWriterHelper.singleColumnInt(new File(file).toURI(), IntEncoding.RLE);
		ParquetWriterHelper.singleColumnInt(new File(file).toURI(), IntEncoding.PLAIN);
		

		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_int.data.DICT")));
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_int.data.BP")));
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_int.data.DELTABP")));
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_int.data.RLE")));
		assertTrue(Files.exists(Paths.get("src/test/resource/test_col_int.data.PLAIN")));
	}
	
	@Test
	public void testDetermineBitLength() {
		assertEquals(13,ParquetWriterHelper.scanIntBitLength(new File("src/test/resource/bitlength_test").toURI()));
	}
}
