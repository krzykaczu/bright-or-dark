package DarkImgsFilter

import org.scalatest.FunSpec
import java.awt.image.BufferedImage
import java.io.File

class FileHandlersTest extends FunSpec {
  describe("FileHandler / Loader") {
    val file = new File("/sth/sth/sth/file.scala")
    val emptyFile = new File("")
    describe("getExtension method") {
      it("extracts file's extension") {
        assert(Loader.getExtension(file) === "scala")
      }
      it("handles empty files properly") {
        assert(Loader.getExtension(emptyFile) === "")
      }
    }
    describe("getFileName method") {
      it("extracts file's name") {
        assert(Loader.getFileName(file) === "file")
      }
      it("handles empty files properly") {
        assert(Loader.getFileName(emptyFile) === "")
      }
    }
  }
  describe("Saver") {
    describe("buildOutFileName method") {
      val outDirMock = "/my/outdir"
      val brightOrDarkMock = "_bright_"
      val dataMock = 59
      it("returns resulting file path properly") {
        assert(Saver.buildOutFileName(outDirMock,"file",brightOrDarkMock,dataMock,"png")
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
      it("returns false for cutoffPoint = 66") {
        val cutoffPointMock = 66
        assert(ImgCopier.copyFileAndCheckIfBright(cutoffPointMock,file,outDir) === false)
      }
    }
  }
}
