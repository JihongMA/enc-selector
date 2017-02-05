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
package edu.uchicago.cs.encsel.app

import edu.uchicago.cs.encsel.util.FileUtils
import java.io.File
import java.nio.file.Paths
import java.nio.file.Files
import java.nio.file.Path
import edu.uchicago.cs.encsel.schema.SchemaGuesser
import org.slf4j.LoggerFactory
import edu.uchicago.cs.encsel.schema.Schema

object GuessSchema extends App {

  var guesser = new SchemaGuesser
  var logger = LoggerFactory.getLogger(getClass)

  if (args.length < 1)
    System.exit(1)
  var source = new File(args(0)).toURI()

  if (!Files.exists(Paths.get(source))) {
    System.exit(1)
  }

  FileUtils.scan(new File(source).toURI(), guessSchema)

  def guessSchema(file: Path): Unit = {
    if (logger.isDebugEnabled())
      logger.debug("Scanning %s".format(file.toUri().toString()))
    if (!FileUtils.isDone(file.toUri(), "gsdone") &&
      !FileUtils.isDone(file.toUri(), "done")) {
      var schema = guesser.guessSchema(file.toUri())
      if (null != schema) {
        if (logger.isDebugEnabled())
          logger.debug("Generating schema for %s".format(file.toUri().toString()))
        var schemaLocation = FileUtils.replaceExtension(file.toUri(), "schemagen")
        Schema.toParquetFile(schema, schemaLocation)
        if (logger.isDebugEnabled())
          logger.debug("Schema for %s written to %s".format(file.toUri().toString(), schemaLocation))
      } else {
        if (logger.isDebugEnabled())
          logger.debug("No schema generated for %s".format(file.toUri().toString()))
      }
      FileUtils.markDone(file.toUri(), "gsdone")
      if (logger.isDebugEnabled())
        logger.debug("Scanned %s".format(file.toUri().toString()))
    } else {
      if (logger.isDebugEnabled())
        logger.debug("Mark found, skipping %s".format(file.toUri().toString()))
    }
  }
}