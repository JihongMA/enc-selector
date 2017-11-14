/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License,
 *
 * Contributors:
 *     Hao Jiang - initial API and implementation
 *
 */
package edu.uchicago.cs.encsel.dataset.parquet;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.column.values.factory.ValuesWriterFactory;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter.Builder;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.schema.EncMessageType;
import org.apache.parquet.schema.MessageType;

public class ParquetWriterBuilder extends Builder<List<String>, ParquetWriterBuilder> {

	private WriteSupport<List<String>> writeSupport = null;

	private Field field = null;

	public ParquetWriterBuilder(Path file, MessageType schema) {
		super(file);
		writeSupport = new StringWriteSupport(schema);
		try {
			field = Builder.class.getDeclaredField("encodingPropsBuilder");
			field.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		getEncodingPropertiesBuilder().withValuesWriterFactory(new AdaptiveValuesWriterFactory());
	}

	public ParquetWriterBuilder(Path file, EncMessageType schema, ValuesWriterFactory factory) {
		super(file);
		writeSupport = new StringWriteSupport(schema);
		try {
			field = Builder.class.getDeclaredField("encodingPropsBuilder");
			field.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
        getEncodingPropertiesBuilder().withValuesWriterFactory(factory);
    }

	@Override
	protected ParquetWriterBuilder self() {
		return this;
	}

	@Override
	protected WriteSupport<List<String>> getWriteSupport(Configuration conf) {
		return writeSupport;
	}

	protected ParquetProperties.Builder getEncodingPropertiesBuilder() {
		try {
			return (ParquetProperties.Builder) field.get(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ParquetWriter<List<String>> buildDefault(Path file, MessageType schema, boolean useDictionary)
			throws IOException {
		ParquetWriterBuilder builder = new ParquetWriterBuilder(file, schema);

		return builder.withValidation(false).withCompressionCodec(CompressionCodecName.UNCOMPRESSED)
				.withDictionaryEncoding(useDictionary).withRowGroupSize(ParquetWriter.DEFAULT_BLOCK_SIZE)
				.withPageSize(ParquetWriter.DEFAULT_PAGE_SIZE)
				.withDictionaryPageSize(500 * ParquetWriter.DEFAULT_PAGE_SIZE).build();
	}
}
