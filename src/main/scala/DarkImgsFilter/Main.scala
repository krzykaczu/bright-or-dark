package DarkImgsFilter

import pureconfig._
import pureconfig.generic.auto._
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.Set
import scala.collection.parallel.CollectionConverters._

object Main extends App {

  case class Config(
                     inDir: String,
                     outDir: String,
                     referenceInDir: String,
                     cutoffPoint: Int,
                     extensions: Set[String]
                   )
  val config = ConfigSource.default.loadOrThrow[Config]

  println("Loading input files...")
  val filesLoader = new Loader(config.inDir,config.extensions)
  val inFiles = filesLoader.getListOfFiles.par
  var lstOfBrights = new ArrayBuffer[String]
  // copy files, check if img is bright and, if so, append it to the ArrayBuffer
  println("Processing files...")
  for (file <- inFiles)
    if (ImgCopier.copyFileAndCheckIfBright(config.cutoffPoint, file, config.outDir))
      lstOfBrights += filesLoader.getFileName(file)

  //calculate score based on if the resulting list of brights reflects the reference directory
  println("Calculating algorithm's accuracy...")
  val refFilesLoader = new Loader(config.referenceInDir,config.extensions)
  val refFiles = refFilesLoader.getListOfFiles.par
  var score = 0
  for (refFile <- refFiles)
    if(lstOfBrights.contains(refFilesLoader.getFileName(refFile))) {
      score += 1
    }

  var accuracy = score * 100 / refFiles.size

  println(s"Algorithm's accuracy: $accuracy %")
}

