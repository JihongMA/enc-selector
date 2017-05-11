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

package edu.uchicago.cs.encsel.dataset

import edu.uchicago.cs.encsel.dataset.feature.{Features, Filter, Sortness}
import edu.uchicago.cs.encsel.dataset.persist.Persistence
import edu.uchicago.cs.encsel.dataset.persist.jpa.JPAPersistence
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

/**
  * Created by harper on 4/23/17.
  */
object AddMissingFeature extends App {

  val logger = LoggerFactory.getLogger(getClass)

  val persist = new JPAPersistence

  val missed = Seq(new Sortness(50), new Sortness(100), new Sortness(200), new Sortness(500))

  val prefix = args.length match {
    case gt if gt > 0 => args(0)
    case _ => ""
  }

  val filter = args.length match {
    case gt if gt > 0 =>
      args(1) match {
        case "none" => null
        case "firstn" => Filter.firstNFilter(args(2).toInt)
        case "iid" => Filter.iidSamplingFilter(args(2).toDouble)
        case "size" => Filter.sizeFilter(args(2).toInt)
        case "minsize" => Filter.minSizeFilter(args(2).toInt, args(3).toDouble)
        case _ => throw new IllegalArgumentException(args(1))
      }
    case _ => null
  }

  Features.extractors.clear()
  Features.extractors ++= missed

  val persistence = Persistence.get
  persistence.load().foreach(column => {
    if (filter == null) {
      column.features ++= Features.extract(column)
    } else {
      column.features ++= Features.extract(column, filter, prefix)
    }
    persistence.save(Seq(column))
  })
}