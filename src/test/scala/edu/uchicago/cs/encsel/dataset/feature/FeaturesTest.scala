package edu.uchicago.cs.encsel.dataset.feature

import java.io.File

import edu.uchicago.cs.encsel.dataset.column.Column
import edu.uchicago.cs.encsel.model.DataType
import org.junit.Assert._
import org.junit.Test

import scala.io.Source

class FeaturesTest {

  @Test
  def testFeatures: Unit = {
    val col = new Column(new File("src/test/resource/test_columner.csv").toURI, 0, "id", DataType.INTEGER)
    col.colFile = new File("src/test/resource/test_col_int.data").toURI

    val features = Features.extract(col)
    val fa = features.toArray

    assertTrue(fa(0).featureType.equals("EncFileSize"))
    assertEquals("PLAIN_file_size", fa(0).name)
    assertEquals(275.0, fa(0).value, 0.001)

    assertTrue(fa(1).featureType.equals("EncFileSize"))
    assertEquals("DICT_file_size", fa(1).name)
    assertEquals(302.0, fa(1).value, 0.001)

    assertTrue(fa(2).featureType.equals("EncFileSize"))
    assertEquals("BP_file_size", fa(2).name)
    assertEquals(265.0, fa(2).value, 0.001)

    assertTrue(fa(3).featureType.equals("EncFileSize"))
    assertEquals("RLE_file_size", fa(3).name)
    assertEquals(279.0, fa(3).value, 0.001)

    assertTrue(fa(4).featureType.equals("EncFileSize"))
    assertEquals("DELTABP_file_size", fa(4).name)
    assertEquals(346.0, fa(4).value, 0.001)

  }

  @Test
  def testFeaturesWithFilter: Unit = {
    val col = new Column(new File("src/test/resource/test_columner.csv").toURI, 0, "id", DataType.INTEGER)
    col.colFile = new File("src/test/resource/test_col_int.data").toURI

    val filter = Filter.firstNFilter(5)

    val features = Features.extract(col, filter, "ak_")
    val fa = features.toArray

    assertEquals(32, fa.size)

    assertEquals("ak_Sparsity", fa(0).featureType)
    assertEquals("count", fa(0).name)
    assertEquals(5.0, fa(0).value, 0.001)

    assertEquals("ak_Sparsity", fa(1).featureType)
    assertEquals("empty_count", fa(1).name)
    assertEquals(0.0, fa(1).value, 0.001)

    assertEquals("ak_Sparsity", fa(2).featureType)
    assertEquals("valid_ratio", fa(2).name)
    assertEquals(1.0, fa(2).value, 0.001)

    assertEquals("ak_Entropy", fa(3).featureType)
    assertEquals("line_max", fa(3).name)
    assertEquals(1.055, fa(3).value, 0.001)

    assertEquals("ak_Entropy", fa(4).featureType)
    assertEquals("line_min", fa(4).name)
    assertEquals(0.693, fa(4).value, 0.001)

  }

  @Test
  def testFilterFile: Unit = {
    Features.filterFile(new File("src/test/resource/test_col_int.data").toURI,
      new File("src/test/resource/test_col_int.data.filtered").toURI, Filter.firstNFilter(5))

    val lines = Source.fromFile(new File("src/test/resource/test_col_int.data.filtered")).getLines().toArray
    assertEquals(5,lines.size)

    assertEquals("34",lines(0))
    assertEquals("24",lines(1))
    assertEquals("24",lines(2))
    assertEquals("2424",lines(3))
    assertEquals("23234",lines(4))
  }
}

class FeatureTest {
  @Test
  def testEqualHash: Unit = {

    val fa = new Feature
    val fb = new Feature

    fa.featureType = "ABC"
    fa.name = "ttt"

    fb.featureType = "ABC"
    fb.name = "ttt"

    assertTrue(fa.equals(fb))
    assertEquals(fa.hashCode(), fb.hashCode())
  }
}