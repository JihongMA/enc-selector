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

package edu.uchicago.cs.encsel.query.simdscan

import java.io.File

import edu.uchicago.cs.encsel.parquet.{EncReaderProcessor, ParquetReaderHelper}
import edu.uchicago.cs.encsel.query.NonePrimitiveConverter
import edu.uchicago.cs.encsel.query.bitmap.RoaringBitmap
import edu.uchicago.cs.encsel.query.tpch.TPCHSchema
import edu.uchicago.cs.encsel.util.perf.Profiler
import org.apache.parquet.VersionParser
import org.apache.parquet.column.impl.ColumnReaderImpl
import org.apache.parquet.column.page.PageReadStore
import org.apache.parquet.hadoop.metadata.BlockMetaData

object ExecuteQ1 extends App {

  val profiler = new Profiler

  val time = ParquetReaderHelper.profile(new File(args(0)).toURI, new EncReaderProcessor {
    override def processRowGroup(version: VersionParser.ParsedVersion,
                                 meta: BlockMetaData,
                                 rowGroup: PageReadStore): Unit = {
      // quantity 4
      // Extend price 5
      // discount 6
      // tax 7
      // line status 9
      // ship date 10
      // ship date <= '1998-09-01'
      // Scan ship date to generate bitmap

      profiler.reset
      profiler.mark

      val shipDateCol = TPCHSchema.lineitemSchema.getColumns().get(10)

      val shipDateReader = new ColumnReaderImpl(shipDateCol, rowGroup.getPageReader(shipDateCol),
        new NonePrimitiveConverter, version)

      // Generate bitmap
      val bitmap = new RoaringBitmap
      for (i <- 0L until shipDateReader.getTotalValueCount) {
        val date = shipDateReader.getBinary.toStringUsingUTF8
        if (date.compareTo("1998-09-01") <= 0) {
          bitmap.set(i, true)
        }
        shipDateReader.consume()
      }

      profiler.pause
      val genbm = profiler.stop

      println("Generate Bitmap: count %d, time %d".format(shipDateReader.getTotalValueCount, genbm.wallclock))


      profiler.reset
      profiler.mark
      // Use bitmap to scan and decode other columns
      val selected = Array(4, 5, 6, 7, 9).map(i => {
        val cd = TPCHSchema.lineitemSchema.getColumns().get(i)
        new ColumnReaderImpl(cd, rowGroup.getPageReader(cd), new NonePrimitiveConverter, version)
      })

      var counter = 0
      // Scan all columns
      bitmap.foreach((index) => {
        while (counter < index) {
          selected.foreach(col => {
            col.skip()
            col.consume()
          })
          counter += 1
        }
        selected.foreach(col => {
          col.writeCurrentValueToConverter()
          col.consume()
        })
        counter += 1
      })

      profiler.pause
      val scan = profiler.stop
      println("Decode: count %d, time %d".format(shipDateReader.getTotalValueCount, scan.wallclock))
    }
  })

  println("Total time: %d".format(time.wallclock))
}
