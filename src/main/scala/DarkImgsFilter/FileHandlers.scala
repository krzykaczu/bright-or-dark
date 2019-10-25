package DarkImgsFilter

import java.awt.image.BufferedImage
import java.io.{File, FileNotFoundException, IOException}
import javax.imageio.ImageIO
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/** Base class providing getExtension and getFileName methods for Loader and Saver instances. */
abstract class FileHandler {

  /** Extracts extension of the provided file
   *
   *  @param f file
   *  @return file extension
   */
  def getExtension(f: File): String = f.getName.split("\\.").lastOption match {
    case Some(i) => i
    case None => ""
  }

  /** Extracts file name of the provided file
   *
   *  @param f file
   *  @return file name
   */
  def getFileName(f: File): String = Option(f.getName.split("\\.").init.mkString(".")) match {
    case Some(i) => i.toString
    case None => ""
  }
}

/** Singleton object loading files from the provided directory for further processing */
object Loader extends FileHandler {
  /** Reads files with the specified extensions from the specified directory
   *
   *  @param dir String containing path of the input directory
   *  @param extensions a set of file extensions
   *  @return list of files
   */
  def getListOfFiles(dir: String, extensions: Set[String]):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).filter(x => extensions.contains(getExtension(x))).toList
    } else {
      List[File]()
    }
  }
}

/** Saves processed files to the provided directory */
object Saver extends FileHandler {
  /** Builds the output file's full path
   *
   *  @param outDir String containing path of the output directory
   *  @param fileName String containing file's name
   *  @param brightOrDark String added to the output file's name
   *  @param lumin the computed value of mean luminance ratio added to the output file's name
   *  @param ext String containing file's extension
   *  @return the output file's full path
   */
  def buildOutFileName(outDir: String, fileName: String, brightOrDark: String, lumin: Long, ext: String): String
          = outDir + "/" + fileName + brightOrDark + lumin + "." + ext

  /** Method creating the output file
   *
   *  @param img BufferedImage-type param returned from from ImageIO.read method
   *  @param file each individual file processed by the algorithm
   *  @param outDir String containing path of the output directory
   *  @param brightOrDark String added to the output file's name
   *  @param data the computed value of mean luminance ratio added to the output file's name
   *  @return output img file saved to the provided path
   */
  def apply(img: BufferedImage, file: File, outDir: String, brightOrDark: String, data: Long): Unit = {
    Await.result(
      Future {
        try {
          ImageIO.write(img, getExtension(file), new File(buildOutFileName(outDir,getFileName(file),brightOrDark,data,getExtension(file))))
        } catch {
          case x: IllegalArgumentException => println("Exception: One or many parameters are null")
          case y: IOException => println("Input/output Exception")
        }
      }, Duration.Inf
    )
  }
}

/** Singleton object for reading files and returning BufferedImage-type output */
object ImgReader {
  /** Reads the provided file and returns BufferedImage-type output
   *
   *  @param file input file
   *  @return BufferImage
   */
  def readFile(file: File): Option[BufferedImage] = {
    Await.result (
        Future {
          try {
            Some(ImageIO.read(file))
          } catch {
            case x: FileNotFoundException =>
              println("Exception: File missing")
              None
            case y: IOException =>
              println("Input/output Exception")
              None
          }
        }, Duration.Inf
      )
    }
}

/** Singleton object for copying, saving and labeling the read files based on the mean luminance computations */
object ImgCopier {
  /** Reads the provided file and returns BufferedImage-type output
   *
   *  @param cutOffPoint user defined threshold of the mean luminance ratio
   *  @param file input file
   *  @param outDir String containing path of the output directory
   *  @return Boolean value returned for the sake of algorithm's efficiency computation
   */
  def copyFileAndCheckIfBright(cutOffPoint: Double, file: File, outDir: String): Boolean = {
    val photo =
      ImgReader.readFile(file) match {
        case Some(i) => i
      }
    val photosMeanLuminance = ImgFilter.getMeanLuminance(photo)
    var isBright = false

    // save file to output directory
    if (photosMeanLuminance < cutOffPoint) {
      //implicit use of the apply method
      Saver(photo, file, outDir, "_bright_", photosMeanLuminance)
      isBright = true
    } else {
      Saver(photo, file, outDir, "_dark_", photosMeanLuminance)
    }
    isBright
  }
}