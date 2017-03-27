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
 */

package edu.uchicago.cs.encsel.ptnmining.parser

/**
  * Created by harper on 3/24/17.
  */
trait Token {
  def value: String
  def isData:Boolean = false
  override def toString = value
}

class TWord(v: AnyRef) extends Token {
  val value = v.toString
  override def isData = true
}

class TInt(v: AnyRef) extends Token {
  val value = v.toString
  override def isData = true
}

class TDouble(v: AnyRef) extends Token {
  val value = v.toString
  override def isData = true
}

class TSpace extends Token {
  val value = " "
}

class TSymbol(v: AnyRef) extends Token {
  val value = v.toString
}

class TPara(t: Int, l: Boolean) extends Token {
  val paraType = t
  val left = l

  def matches(another: TPara) =
    another.paraType == this.paraType && another.left != this.left

  def value: String = {
    val data = Array("(", ")", "{", "}", "[", "]")
    data(paraType * 2 + (if (left) 0 else 1))
  }
}

class TGroup(t: Int, l: Seq[Token]) extends Token {
  val sym = t
  val content = l

  def value = "G(%s)".format(content.map(_.value).mkString(""))
}