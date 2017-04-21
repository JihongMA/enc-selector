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
package edu.uchicago.cs.ndnn

import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer
import scala.util.control.Breaks._

trait Evaluator {
  def init: Unit

  def record[D](batch: Batch[D], loss: Double, acc: Int): Unit

  def loss: Double

  def summary: String
}

class MeanLossEvaluator extends Evaluator {
  var batchCounter = 0
  var lossSum = 0d
  var accSum = 0

  def init: Unit = {
    batchCounter = 0
    lossSum = 0
    accSum = 0
  }

  def record[D](batch: Batch[D], loss: Double, acc: Int) = {
    batchCounter += 1
    lossSum += loss
    accSum += acc
  }

  def loss: Double = lossSum / batchCounter

  def summary: String =
    """Average loss %f, average accuracy %f""".format(lossSum / batchCounter, accSum.toDouble / batchCounter)
}

trait Trainer[D, T <: Dataset[D], G <: Graph[D]] {
  val logger = LoggerFactory.getLogger(getClass)

  protected val trainHistory = new ArrayBuffer[(Double, Double)]

  def getTrainSet: T

  def getTestSet: T

  def getGraph: G

  protected def getParamStore: ParamStore = EmptyStore

  protected def getEvaluator: Evaluator

  protected def setupGraph(graph: G, batch: Batch[D])

  protected def earlyStop = false

  def train(epoches: Int, trainBatchSize: Int = 50, testBatchSize: Int = 50): Unit = {
    val trainset = getTrainSet

    val graph = getGraph
    val loadedParams = getParamStore.load
    if (loadedParams.nonEmpty)
      graph.load(loadedParams)

    // Initial test and loss
    var bestLoss = evaluate(testBatchSize)

    var i = 0
    var stop = false

    while (i < epoches && !stop) {
      logger.info("[Epoch %d]".format(i))
      val startTime = System.currentTimeMillis()

      val graph = getGraph

      getEvaluator.init
      trainset.batches(trainBatchSize).foreach(batch => {
        setupGraph(graph, batch)
        val loss = graph.train
        getEvaluator.record(batch, loss, -1)
      })
      graph.epochDone

      val trainLoss = getEvaluator.loss
      val testLoss = evaluate(testBatchSize)

      trainHistory += ((trainLoss, testLoss))

      if (testLoss < bestLoss) {
        bestLoss = testLoss
        // Only store the param when better result found
        if (null != getParamStore) {
          getParamStore.store(graph.dump())
        }
      }

      stop = earlyStop

      val stopTime = System.currentTimeMillis()
      logger.info("Training time %f mins".format((stopTime - startTime) / 60000d))
      logger.info("Training Loss %f, test loss %f".format(trainLoss, testLoss))

      i += 1
    }
  }

  protected def evaluate(testBatchSize: Int): Double = {
    val testset = getTestSet
    val evaluator = getEvaluator
    val graph = getGraph

    evaluator.init

    testset.batches(testBatchSize).foreach {
      batch => {
        setupGraph(graph, batch)
        val (loss, acc) = graph.test
        evaluator.record(batch, loss, acc)
      }
    }
    logger.info("Test Result: %s".format(evaluator.summary))
    evaluator.loss
  }
}

abstract class SimpleTrainer[D, T <: Dataset[D], G <: Graph[D]](trainset: T, testset: T, graph: G)
  extends Trainer[D, T, G] {

  protected var evaluator: Evaluator = new MeanLossEvaluator()
  protected var paramStore: ParamStore = new FileStore("model")

  def getTrainSet: T = trainset

  def getTestSet: T = testset

  def getGraph: G = graph

  override protected def getParamStore = paramStore

  override protected def getEvaluator = evaluator
}
