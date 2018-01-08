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

package edu.uchicago.cs.encsel.query.offheap

import java.io.File
import java.lang.management.ManagementFactory

import edu.uchicago.cs.encsel.parquet.{EncReaderProcessor, ParquetReaderHelper}
import edu.uchicago.cs.encsel.query.tpch._
import edu.uchicago.cs.encsel.query.{Bitmap, NonePrimitiveConverter}
import org.apache.parquet.VersionParser
import org.apache.parquet.column.impl.ColumnReaderImpl
import org.apache.parquet.column.page._
import org.apache.parquet.hadoop.metadata.BlockMetaData

import scala.collection.JavaConversions._

object Jni extends App {
  val entryWidth = 26
  val cd = TPCHSchema.lineitemSchema.getColumns()(1)

  test("Scalar", new EqualScalar(5000, entryWidth))
  test("JniScalar", new EqualJniScalar(5000, entryWidth))

  def test(name: String, pred: Predicate): Unit = {
    val predVisitor = new PredicateVisitor(cd, pred)
    val mbean = ManagementFactory.getThreadMXBean
    val repeat = 10
    var clocktime = 0L
    var cputime = 0L
    var usertime = 0L
    for (i <- 0 until repeat) {
      val clockstart = System.currentTimeMillis
      val cpustart = mbean.getCurrentThreadCpuTime
      val userstart = mbean.getCurrentThreadUserTime

      ParquetReaderHelper.read(new File("/home/harper/TPCH/offheap/lineitem.parquet").toURI, new EncReaderProcessor() {

        override def processRowGroup(version: VersionParser.ParsedVersion,
                                     meta: BlockMetaData,
                                     rowGroup: PageReadStore): Unit = {
          val pageReader = rowGroup.getPageReader(cd)
          var page = pageReader.readPage()
          while (page != null) {
            val res = page.accept(predVisitor)
            page = pageReader.readPage()
            res.clear()
          }
        }
      })
      clocktime = clocktime + (System.currentTimeMillis() - clockstart)
      cputime = cputime + (mbean.getCurrentThreadCpuTime - cpustart)
      usertime = usertime + (mbean.getCurrentThreadUserTime - userstart)
    }

    println("%s,%d,%d,%d".format(name, clocktime / repeat, cputime / repeat, usertime / repeat))
  }

}

object Offheap extends App {
  val entryWidth = 26
  val cd = TPCHSchema.lineitemSchema.getColumns()(1)

  test("Scalar", new EqualScalar(5000, entryWidth))
  test("Int", new EqualInt(5000, entryWidth))
  test("Long", new EqualLong(5000, entryWidth))

  def test(name: String, pred: Predicate): Unit = {
    val predVisitor = new PredicateVisitor(cd, pred)
    val mbean = ManagementFactory.getThreadMXBean
    val repeat = 50
    var clocktime = 0L
    var cputime = 0L
    var usertime = 0L
    for (i <- 0 until repeat) {
      val clockstart = System.currentTimeMillis
      val cpustart = mbean.getCurrentThreadCpuTime
      val userstart = mbean.getCurrentThreadUserTime

      ParquetReaderHelper.read(new File("/home/harper/TPCH/offheap/lineitem.parquet").toURI, new EncReaderProcessor() {

        override def processRowGroup(version: VersionParser.ParsedVersion,
                                     meta: BlockMetaData,
                                     rowGroup: PageReadStore): Unit = {
          val pageReader = rowGroup.getPageReader(cd)
          var page = pageReader.readPage()
          while (page != null) {
            val res = page.accept(predVisitor)
            page = pageReader.readPage()
            res.clear()
          }
        }
      })
      clocktime = clocktime + (System.currentTimeMillis() - clockstart)
      cputime = cputime + (mbean.getCurrentThreadCpuTime - cpustart)
      usertime = usertime + (mbean.getCurrentThreadUserTime - userstart)
    }

    println("%s,%d,%d,%d".format(name, clocktime / repeat, cputime / repeat, usertime / repeat))
  }
}

object Onheap extends App {

  val pred: Any => Boolean = (data: Any) => {
    data.toString.toInt == 5000
  }
  val cd = TPCHSchema.lineitemSchema.getColumns()(1)

  val mbean = ManagementFactory.getThreadMXBean
  val repeat = 50
  var clocktime = 0L
  var cputime = 0L
  var usertime = 0L
  for (i <- 0 until repeat) {
    val clockstart = System.currentTimeMillis
    val cpustart = mbean.getCurrentThreadCpuTime
    val userstart = mbean.getCurrentThreadUserTime

    ParquetReaderHelper.read(new File("/home/harper/TPCH/offheap/lineitem.parquet").toURI, new EncReaderProcessor() {

      override def processRowGroup(version: VersionParser.ParsedVersion,
                                   meta: BlockMetaData,
                                   rowGroup: PageReadStore): Unit = {
        val colReader = new ColumnReaderImpl(cd, rowGroup.getPageReader(cd), new NonePrimitiveConverter, version);
        val bitmap = new Bitmap(rowGroup.getRowCount)
        for (i <- 0L until rowGroup.getRowCount) {
          bitmap.set(i, pred(colReader.getInteger))
          colReader.consume
        }
      }
    })

    clocktime = clocktime + (System.currentTimeMillis() - clockstart)
    cputime = cputime + (mbean.getCurrentThreadCpuTime - cpustart)
    usertime = usertime + (mbean.getCurrentThreadUserTime - userstart)
  }
  println("%s,%d,%d,%d".format("Onheap", clocktime / repeat, cputime / repeat, usertime / repeat))
}