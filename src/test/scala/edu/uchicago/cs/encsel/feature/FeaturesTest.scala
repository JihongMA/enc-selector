package edu.uchicago.cs.encsel.feature

import java.io.File

import org.junit.Assert._
import org.junit.Test

import edu.uchicago.cs.encsel.column.Column
import edu.uchicago.cs.encsel.model.DataType

class FeaturesTest {

  @Test
  def testFeatures: Unit = {
    var col = new Column(new File("src/test/resource/test_columner.csv").toURI(), 0, "id", DataType.INTEGER)
    col.colFile = new File("src/test/resource/test_col_int.data").toURI()

    var features = Features.extract(col)
    var fa = features.toArray

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
}