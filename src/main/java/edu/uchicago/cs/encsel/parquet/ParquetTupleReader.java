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
 * under the License.
 *
 * Contributors:
 *     Hao Jiang - initial API and implementation
 */

package edu.uchicago.cs.encsel.parquet;

import edu.uchicago.cs.encsel.query.RowFieldPrimitiveConverter;
import edu.uchicago.cs.encsel.query.RowTempTable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.VersionParser;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.column.ColumnReader;
import org.apache.parquet.column.impl.ColumnReaderImpl;
import org.apache.parquet.column.page.PageReadStore;
import org.apache.parquet.format.converter.ParquetMetadataConverter;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.metadata.ParquetMetadata;
import org.apache.parquet.schema.MessageType;

import java.io.IOException;
import java.net.URI;


/**
 * Read tuples from Parquet file. The logic is similar to <code>InternalParquetRecordReader</code>
 */
public class ParquetTupleReader {

    private Configuration conf = new Configuration();

    private ParquetMetadata meta;

    private ParquetFileReader fileReader;

    private VersionParser.ParsedVersion version;

    private MessageType schema;

    private PageReadStore currentRowGroup;

    private SingleRowTempTable rowTempTable;

    private long rowGroupReadCounter = 0;

    private long readCounter = 0;

    private ColumnReader[] readers;

    private RowFieldPrimitiveConverter[] converters;

    public ParquetTupleReader(URI inputFile) throws IOException, VersionParser.VersionParseException {
        Path inputPath = new Path(inputFile);
        meta = ParquetFileReader.readFooter(conf, inputPath, ParquetMetadataConverter.NO_FILTER);
        schema = meta.getFileMetaData().getSchema();
        rowTempTable = new SingleRowTempTable(schema);
        fileReader = ParquetFileReader.open(conf, inputPath, meta);
        readers = new ColumnReader[meta.getFileMetaData().getSchema().getColumns().size()];
        version = VersionParser.parse(meta.getFileMetaData().getCreatedBy());
        readyRead();
    }

    public MessageType getSchema() {
        return schema;
    }

    public Object[] read() throws IOException {
        readyRead();
        rowTempTable.start();
        for (int i = 0; i < readers.length; i++) {
            ColumnReader colReader = readers[i];
            ColumnDescriptor cd = schema.getColumns().get(i);
            if (colReader.getCurrentDefinitionLevel() == cd.getMaxDefinitionLevel()) {
                colReader.writeCurrentValueToConverter();
            }
            colReader.consume();
        }
        Object[] result = rowTempTable.getCurrentRecord().getData();
        rowTempTable.end();
        rowGroupReadCounter++;
        readCounter++;
        return result;
    }

    protected void readyRead() throws IOException {
        if (currentRowGroup == null || rowGroupReadCounter == currentRowGroup.getRowCount()) {
            currentRowGroup = fileReader.readNextRowGroup();
            rowGroupReadCounter = 0;
            for (int i = 0; i < readers.length; i++) {
                ColumnDescriptor cd = schema.getColumns().get(i);
                readers[i] = new ColumnReaderImpl(cd, currentRowGroup.getPageReader(cd),
                        rowTempTable.getConverter(i).asPrimitiveConverter(), version);
            }
        }
    }

    protected static class SingleRowTempTable extends RowTempTable {

        SingleRowTempTable(MessageType schema) {
            super(schema);
        }

        @Override
        public void start() {
        }

        @Override
        public void end() {
        }
    }
}
