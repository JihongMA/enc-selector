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
package edu.uchicago.cs.encsel.ndnn

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.indexing.NDArrayIndex
import org.nd4j.linalg.indexing.SpecifiedIndex
import org.nd4j.linalg.util.NDArrayUtil
import org.nd4j.linalg.indexing.INDArrayIndex
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

object Index {

  /**
   * Get element in data specified by idx
   *
   * @param data of shape [A,B,C,D,...,M,N]
   * @param idx  of shape [A,B,C,D,...,M,1?]
   * @return			  shape [A,B,C,D,...,M,1?]
   */
  def get(data: INDArray, idx: INDArray): INDArray = {

    val datashape = data.shape
    val idxshape = idx.shape
    // Check shape
    if (!checkShape(datashape, idxshape))
      throw new IllegalArgumentException("Incorrect shape")

    val rownum = datashape.dropRight(1).product
    val rowlength = datashape.last

    val flatten = data.reshape(datashape.product, 1)
    val idxflatten = idx.dup.reshape(idxshape.product, 1)
    val offset = Nd4j.create((0 until rownum)
      .map(_.doubleValue() * rowlength).toArray).reshape(rownum, -1)
    idxflatten.addi(offset)

    //    flatten.get(new SpecifiedIndex(NDArrayUtil.toInts(idxflatten): _*),
    //      NDArrayIndex.all()).reshape(idxshape: _*)
    Nd4j.create(NDArrayUtil.toInts(idxflatten)
      .map { flatten.getScalar(_) }.toList, idxshape)
  }

  def put(data: INDArray, idx: INDArray, toput: INDArray): Unit = {

    val datashape = data.shape
    val idxshape = idx.shape
    val toputshape = toput.shape
    // Check shape
    if (!checkShape(datashape, idxshape) || !checkShape(datashape, toputshape))
      throw new IllegalArgumentException("Incorrect shape")

    val rownum = datashape.dropRight(1).product
    val rowlength = datashape.last

    val flatten = data.reshape(datashape.product, -1)
    val idxflatten = idx.dup.reshape(idxshape.product, -1)
    val offset = Nd4j.create((0 until rownum)
      .map(_.doubleValue() * rowlength).toArray).reshape(rownum, -1)
    idxflatten.addi(offset)
    val valueflatten = toput.reshape(idxshape.product, -1)

    NDArrayUtil.toInts(idxflatten).zip(valueflatten.data().asDouble())
      .foreach(p => flatten.putScalar(p._1, p._2))
  }

  protected def checkShape(dataShape: Array[Int], idxShape: Array[Int]): Boolean = {

    dataShape.length match {
      case x if x == idxShape.length => {
        for (i <- 0 until dataShape.length - 1) {
          if (dataShape(i) != idxShape(i))
            return false
        }
        if (idxShape.last != 1)
          return false
        return true
      }
      case xp1 if xp1 == idxShape.length + 1 => {
        for (i <- 0 until dataShape.length - 1) {
          if (dataShape(i) != idxShape(i))
            return false
        }
        return true
      }
      case _ => false
    }
  }

  def point(total: Int, axis: Int, idx: Int): Array[INDArrayIndex] = {
    point(total, Map((axis, idx)))
  }

  def point(total: Int, idices: Map[Int, Int]): Array[INDArrayIndex] = {
    (0 until total).map(i => i match {
      case in if idices.contains(in) => NDArrayIndex.point(idices.getOrElse(in, -1))
      case _ => NDArrayIndex.all()
    }).toArray
  }

  def range(total: Int, axis: Int, rge: (Int, Int)): Array[INDArrayIndex] = {
    range(total, Map((axis, rge)))
  }

  def range(total: Int, idices: Map[Int, (Int, Int)]): Array[INDArrayIndex] = {
    (0 until total).map(i => i match {
      case in if idices.contains(in) => {
        val range = idices.getOrElse(in, throw new RuntimeException())
        NDArrayIndex.interval(range._1, range._2)
      }
      case _ => NDArrayIndex.all()
    }).toArray
  }
}

object Broadcast {
  /**
   * Assume the arrays are broadcast-able. Compute the different axis.
   * The lowest dim is 0.
   */
  def diff(ashape: Array[Int], bshape: Array[Int]): (Array[Int], Array[Int]) = {
    val maxlen = Math.max(ashape.length, bshape.length)
    val apadded = ashape.reverse.padTo(maxlen, 1).reverse
    val bpadded = bshape.reverse.padTo(maxlen, 1).reverse
    val maxdim = apadded.zipAll(bpadded, 0, 0).map(p => Math.max(p._1, p._2))

    (apadded.zip(maxdim).zipWithIndex
      .filter(p => p._1._1 < p._1._2).map(_._2),
      bpadded.zip(maxdim).zipWithIndex
      .filter(p => p._1._1 < p._1._2).map(_._2))
  }

  /**
   * Look for broadcast-able axis
   */
  def axis(toshape: Array[Int], fromshape: Array[Int]): Array[Int] = {
    val maxlen = Math.max(toshape.length, fromshape.length)
    val apadded = toshape.reverse.padTo(maxlen, 1).reverse
    val bpadded = fromshape.reverse.padTo(maxlen, 1).reverse
    apadded.zip(bpadded).zipWithIndex
      .filter(p => p._1._1 == p._1._2 && p._1._1 != 1).map(_._2)
  }
}