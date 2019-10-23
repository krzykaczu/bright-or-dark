package DarkImgsFilter

import java.awt.image.BufferedImage
import org.scalatest.FunSpec

class ImgFilterTest extends FunSpec {
  describe("ImgsFilter") {
    describe("calculateLuminance method"){
      it("returns 0 for R=0,G=0,B=0") {
        assert(ImgFilter.calculateLuminance(0, 0, 0) === 0)
      }
      it("returns 0 for R=255,G=255,B=255") {
        assert(Math.round(ImgFilter.calculateLuminance(255, 255, 255)) === 255)
      }
    }

    describe("calculateMeanLuminanceRatio method"){
      it("returns 100 for luminance = 0") {
        assert(ImgFilter.calculateMeanLuminanceRatio(0) === 100)
      }
      it("returns 0 for luminance = 255") {
        assert(ImgFilter.calculateMeanLuminanceRatio(0) === 100)
      }
    }

    describe("getMeanLuminance method") {
      val img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB)
      val lumRatio = ImgFilter.getMeanLuminance(img)
      it("calculates luminance ratio in range between 0 and 100") {
        assert(lumRatio >= 0 && lumRatio <= 100)
      }
    }
  }
}
