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

package edu.uchicago.cs.encsel.dataset.feature.subattr

import java.io.InputStream
import java.nio.file.{Files, Paths}

import edu.uchicago.cs.encsel.dataset.column.Column
import edu.uchicago.cs.encsel.dataset.feature.resource.ScanTimeUsage.featureType
import edu.uchicago.cs.encsel.dataset.feature.{Feature, FeatureExtractor}
import edu.uchicago.cs.encsel.parquet.{ParquetTupleReader, ParquetTupleVReader}
import edu.uchicago.cs.encsel.query.operator.VerticalSelect
import edu.uchicago.cs.encsel.util.perf.Profiler
import edu.uchicago.cs.encsel.util.FileUtils
import org.slf4j.LoggerFactory

object SubattrScanTimeUsage extends FeatureExtractor {
  val logger = LoggerFactory.getLogger(getClass)

  def featureType = "SubattrScanTimeUsage"

  def supportFilter: Boolean = false

  val profiler = new Profiler

  def extract(col: Column, input: InputStream, prefix: String): Iterable[Feature] = {
    val subtable = FileUtils.addExtension(col.colFile, "subtable")
    if (Files.exists(Paths.get(subtable))) {
      val tupleReader = new ParquetTupleVReader(subtable)
      var counter = 0
      profiler.reset
      profiler.mark
      while (counter < tupleReader.getNumOfRecords) {
        tupleReader.read()
        counter += 1
      }
      tupleReader.close()
      profiler.pause
      val time = profiler.stop
      Iterable(
        new Feature(featureType, "vread_wallclock", time.wallclock),
        new Feature(featureType, "vread_cpu", time.cpu)
      )
    } else {
      Iterable()
    }
  }

}
