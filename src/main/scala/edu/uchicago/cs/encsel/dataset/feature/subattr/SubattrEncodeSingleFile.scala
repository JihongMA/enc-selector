package edu.uchicago.cs.encsel.dataset.feature.subattr

import java.io.{BufferedReader, File, FileReader}

import edu.uchicago.cs.encsel.dataset.column.Column
import edu.uchicago.cs.encsel.dataset.feature.Feature
import edu.uchicago.cs.encsel.dataset.persist.jpa.{ColumnWrapper, JPAPersistence}
import edu.uchicago.cs.encsel.model.DataType._
import edu.uchicago.cs.encsel.model._
import edu.uchicago.cs.encsel.parquet.{EncContext, EncReaderProcessor, ParquetWriterBuilder}
import edu.uchicago.cs.encsel.ptnmining.compose.PatternComposer
import edu.uchicago.cs.encsel.ptnmining.persist.PatternWrapper
import edu.uchicago.cs.encsel.util.FileUtils
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.parquet.column.Encoding
import org.apache.parquet.format.converter.ParquetMetadataConverter
import org.apache.parquet.hadoop.ParquetFileReader
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName._
import org.apache.parquet.schema.Type.Repetition
import org.apache.parquet.schema.{MessageType, PrimitiveType}

import scala.collection.JavaConverters._

object SubattrEncodeSingleFile extends App {

  val em = new JPAPersistence().em
  val sql = "SELECT c FROM Column c WHERE EXISTS (SELECT p FROM Column p WHERE p.parentWrapper = c)"
  val childSql = "SELECT c FROM Column c WHERE c.parentWrapper = :parent"
  val patternSql = "SELECT p FROM Pattern p WHERE p.column = :col"

  em.createQuery(sql, classOf[ColumnWrapper]).getResultList.asScala.foreach(col => {
    val children = getChildren(col)
    children.find(_.colName == "unmatch") match {
      case Some(um) => {
        val unmatchedLine = parquetLineCount(um)
        val total = parquetLineCount(col)
        val unmatchRate = unmatchedLine.toDouble / total
        col.replaceFeatures(Iterable(new Feature("SubattrStat", "unmatch_rate", unmatchRate)))

        // Build a single table
        val validChildren = children.filter(_.colName != "unmatch").sortBy(_.colIndex)
        val pattern = getPattern(col).pattern
        writeChildren(col, new PatternComposer(pattern), validChildren)
      }
      case None => {

      }
    }
  })

  def getChildren(col: Column): Seq[Column] = {
    em.createQuery(childSql, classOf[ColumnWrapper]).setParameter("parent", col).getResultList.asScala
  }

  def parquetLineCount(col: Column): Long = {
//    val footer = ParquetFileReader.readFooter(new Configuration,
//      new Path(col.colFile),
//      ParquetMetadataConverter.NO_FILTER)
//    footer.getBlocks.asScala.map(_.getRowCount).sum
    FileUtils.numLine(col.colFile)
  }

  def getPattern(col: Column) = {
    em.createQuery(patternSql, classOf[PatternWrapper]).setParameter("col", col).getSingleResult
  }

  def writeChildren(col: Column, pattern: PatternComposer, children: Seq[Column]): Unit = {
    val file = FileUtils.addExtension(col.colFile, "subtable")
    val optionalColumns = pattern.optionalColumns

    // Setup encoding for each column
    val schema = new MessageType("table",
      children.map(c => {
        val rep = if (optionalColumns.contains(c.colIndex)) Repetition.OPTIONAL else Repetition.REQUIRED
        val typeName = c.dataType match {
          case INTEGER => INT32
          case STRING => BINARY
          case LONG => INT64
          case DataType.BOOLEAN => PrimitiveTypeName.BOOLEAN
          case DataType.DOUBLE => PrimitiveTypeName.DOUBLE
          case DataType.FLOAT => PrimitiveTypeName.FLOAT
        }
        new PrimitiveType(rep, typeName, col.colName)
      }): _*
    )

    schema.getColumns().toArray.zip(children).foreach(pair => {
      val cd = pair._1
      val col = pair._2
      val bestEnc = col.findFeatures("EncFileSize").filter(_.value > 0).minBy(_.value)
      val encName = bestEnc.name.replace("_file_size", "")

      val encoding = col.dataType match {
        case DataType.INTEGER => IntEncoding.valueOf(encName).parquetEncoding
        case DataType.STRING => StringEncoding.valueOf(encName).parquetEncoding
        case DataType.DOUBLE | DataType.FLOAT => FloatEncoding.valueOf(encName).parquetEncoding
        case DataType.LONG => LongEncoding.valueOf(encName).parquetEncoding
        case DataType.BOOLEAN => Encoding.RLE
      }
      // Fetch context from encoded file
      val encodedFile = FileUtils.addExtension(col.colFile, encName)


      EncContext.encoding.get().put(cd.toString, encoding)
      val context = EncReaderProcessor.getContext(encodedFile)(0).asInstanceOf[Array[AnyRef]]
      EncContext.context.get().put(cd.toString, context);
    })

    val writer = ParquetWriterBuilder.buildForTable(new Path(file), schema)

    val readers = children.map(c => {
      new BufferedReader(new FileReader(new File(c.colFile)))
    }).toList

    var valid = true
    while (valid) {
      val data = readers.map(_.readLine())
      writer.write(data.asJava)
      valid = data.exists(_ == null)
    }
    readers.foreach(_.close)
    writer.close()
  }
}
