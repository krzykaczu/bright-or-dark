package DarkImgsFilter

import org.scalatest.FunSpec
import java.awt.image.BufferedImage
import java.io.File

class FileHandlersTest extends FunSpec {
  describe("FileHandler / Loader") {
    val file = new File("/sth/sth/sth/file.scala")
    describe("getExtension method") {
      it("extracts file's extension") {
        assert(Loader.getExtension(file) === "scala")
      }
    }
    describe("getFileName method") {
      it("extracts file's name") {
        assert(Loader.getFileName(file) === "file")
      }
    }
  }
  describe("Saver") {
    describe("buildOutFileName method") {
      val imgMock = new BufferedImage(200,200,BufferedImage.TYPE_INT_ARGB)
      val fileMock = new File("/sth/sth/sth/file.scala")
      val outDirMock = "/my/outdir"
      val brightOrDarkMock = "_bright_"
      val dataMock = 59
      it("returns resulting file path properly") {
        assert(new Saver(imgMock,fileMock,outDirMock,brightOrDarkMock,dataMock)
          .buildOutFileName(outDirMock,"file",brightOrDarkMock,dataMock,"png")
          === "/my/outdir/file_bright_59.png")
      }
    }
  }
  describe("ImgCopier") {
    describe("copyFileAndCheckIfBright method") {
      val outDir = "./out"
      //that is not a proper way to test it -> jpg file should be mocked
      val file = new File("./in/a.jpg")
      it("returns true for cutoffPoint = 84") {
        val cutoffPointMock = 84
        assert(ImgCopier.copyFileAndCheckIfBright(cutoffPointMock,file,outDir) === true)
      }
      it("returns false for cutoffPoint = 50") {
        val cutoffPointMock = 50
        assert(ImgCopier.copyFileAndCheckIfBright(cutoffPointMock,file,outDir) === false)
      }
    }
  }
}
