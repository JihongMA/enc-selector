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

package edu.uchicago.cs.encsel.ptnmining

import java.io.File

import edu.uchicago.cs.encsel.dataset.column.Column
import edu.uchicago.cs.encsel.dataset.persist.jpa.{ColumnWrapper, JPAPersistence}
import edu.uchicago.cs.encsel.model.DataType
import edu.uchicago.cs.encsel.ptnmining.MineColumn._
import edu.uchicago.cs.encsel.ptnmining.matching.GenRegexVisitor

import scala.collection.JavaConverters._

object MineFromFiles extends App {

  //  mineSingleFile
  mineAllFiles

  def mineAllFiles: Unit = {
    val start = args.length match {
      case 0 => 0
      case _ => args(0).toInt
    }

    val persist = new JPAPersistence

    val loadcols = persist.em.createQuery("SELECT c FROM Column c where c.id >= :id AND c.dataType = :dt AND c.parentWrapper IS NULL", classOf[ColumnWrapper]).setParameter("id", start).setParameter("dt", DataType.STRING).getResultList

    loadcols.asScala.foreach(column => {
      val colid = column.id
      val pattern = patternFromFile(column.colFile)
      val valid = numChildren(pattern) > 0
      if (valid) {
        val subcols = split(column, pattern)
        if (!subcols.isEmpty)
          persist.save(subcols)
      }
      val regex = new GenRegexVisitor
      pattern.visit(regex)
      println("%d:%s:%s".format(colid, valid, regex.get))
    })
  }

  def mineSingleFile: Unit = {
    val file = new File("/home/harper/pattern/test").toURI
    val pattern = patternFromFile(file)
    val valid = numChildren(pattern) > 0
    val regex = new GenRegexVisitor
    pattern.visit(regex)
    println("%s:%s".format(regex.get, valid))
    if (valid) {
      val col = new Column(null, -1, "demo", DataType.STRING)
      col.colFile = file
      val subcols = MineColumn.split(col, pattern)
      println(subcols.size)
    }
  }

}

