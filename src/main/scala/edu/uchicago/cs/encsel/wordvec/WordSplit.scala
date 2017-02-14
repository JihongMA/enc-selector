/**
 * *****************************************************************************
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
 *
 * *****************************************************************************
 */
package edu.uchicago.cs.encsel.wordvec

import scala.collection.mutable.Buffer
import org.apache.commons.lang.StringUtils

class WordSplit {

  private val vowel = Set('a', 'e', 'i', 'o', 'u').toSeq

  def split(input: String): (Buffer[String], Double) = {
    input match {
      case x if x.contains("_") => {
        // Separator
        var parts = x.split("_")
        var fidelity = 1d
        (parts.map(part => { var lookup = Dict.lookup(part); fidelity *= lookup._2; lookup._1 })
          .filter(StringUtils.isNotEmpty(_)).toBuffer, fidelity)
      }
      case x if !x.equals(x.toUpperCase()) && !x.equals(x.toLowerCase()) => {
        // Camel style
        split(x.replaceAll("([A-Z])", "_\1"))
      }
      case _ => {
        var candidate: (Buffer[String], Double) = (null, 0)
        // Scan and recognize
        for (i <- 1 to input.length() - 1) {
          var guessword = Dict.lookup(input.substring(0, i))
          var remain = split(input.substring(i))
          var fidelity = guessword._2 * remain._2
          if (fidelity > candidate._2)
            candidate = (guessword._1 +=: remain._1.toBuffer, fidelity)
        }
        candidate
      }
    }
  }
}