package DarkImgsFilter

import pureconfig._
import pureconfig.generic.auto._
import scala.collection.mutable
import scala.collection.immutable
import scala.collection.parallel.CollectionConverters._

object Main extends App {

  case class Config(
                     inDir: String,
                     outDir: String,
                     referenceInDir: String,
                     cutoffPoint: Int,
                     extensions: immutable.Set[String]
                   )
  val config = ConfigSource.default.loadOrThrow[Config]

  println("Loading input files...")
  val inFiles = Loader.getListOfFiles(config.inDir,config.extensions)
  var setOfBrights = mutable.Set[String]()
  // copy files, check if img is bright and, if so, append it to a set
  println("Processing files...")
  for (file <- inFiles)
    if (ImgCopier.copyFileAndCheckIfBright(config.cutoffPoint, file, config.outDir))
      setOfBrights += Loader.getFileName(file)

  //calculate score based on if the resulting set of brights reflects the reference directory
  println("Calculating algorithm's accuracy...")
  val refFiles = Loader.getListOfFiles(config.referenceInDir,config.extensions)
  val refFilesLst = mutable.Set[String]()
  for (refFile <- refFiles)
    refFilesLst += Loader.getFileName(refFile)

  val accuracy = refFilesLst.intersect(setOfBrights).size * 100 / refFilesLst.size

  println(s"Algorithm's accuracy: $accuracy %")
}

