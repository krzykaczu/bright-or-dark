package DarkImgsFilter

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.parallel.CollectionConverters._

/** Singleton object for labeling bright imgs based on imgs' mean luminance calculation */
object ImgFilter {

  /** Calculates relative luminance (https://en.wikipedia.org/wiki/Relative_luminance)
   *
   *  @param r pixel's R component
   *  @param g pixel's G component
   *  @param b pixel's B component
   *  @return luminance
   *
   */
  def calculateLuminance(r: Int, g: Int, b: Int): Double = 0.2126*r + 0.7152*g + 0.0722*b

  /** Calculates mean luminance ratio based on project assumptions -> perfectly white = 0 / perfectly black = 100
   *
   *  @param lum mean relative luminance calculated for all pixels of an img
   *  @return mean luminance ratio
   *
   */
  def calculateMeanLuminanceRatio(lum: Double): Long = 100 - Math.round(lum * 100 / 255)

  /** Calculates mean luminance for all pixels of a provided img
   *
   *  @param img BufferedImage-type param returned from from ImageIO.read method
   *  @return mean luminance ratio
   *
   */
  def getMeanLuminance(img: BufferedImage): Long = {
    // Obtain width and height of the image
    val w = img.getWidth
    val h = img.getHeight

    // For comprehension yield's default type -> Vector
    val colorVector = for {
      x <- 0 until w
      y <- 0 until h
    } yield new Color(img.getRGB(x,y))

    // Creating a parallel collection
    val parallelColorVector = colorVector.par
    val meanLuminanceOfPixels = parallelColorVector.map(x => calculateLuminance(x.getRed, x.getGreen, x.getBlue)).sum / parallelColorVector.size

    calculateMeanLuminanceRatio(meanLuminanceOfPixels)
  }
}