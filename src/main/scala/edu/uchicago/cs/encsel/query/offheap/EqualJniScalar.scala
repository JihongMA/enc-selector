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

package edu.uchicago.cs.encsel.query.offheap

import java.nio.{ByteBuffer, ByteOrder}

class EqualJniScalar(val target: Int, val entryWidth: Int) extends Predicate {

  System.loadLibrary("EqualJniScalar")

  override def execute(input: ByteBuffer, offset: Int, size: Int): ByteBuffer = {
    val result = ByteBuffer.allocateDirect(Math.ceil(size.toDouble / 64).toInt * 8).order(ByteOrder.LITTLE_ENDIAN);
    if (input.isDirect)
      executeDirect(input, offset, size, target, entryWidth, result);
    else
      executeHeap(input.array(), offset, size, target, entryWidth, result);
    return result;
  }

  @native def executeDirect(input: ByteBuffer, offset: Int, size: Int,
                            target: Int, entryWidth: Int, result: ByteBuffer): Unit;

  @native def executeHeap(input: Array[Byte], offset: Int, size: Int,
                          target: Int, entryWidth: Int, result: ByteBuffer): Unit;
}
