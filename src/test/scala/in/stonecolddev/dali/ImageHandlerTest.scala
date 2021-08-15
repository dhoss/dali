package in.stonecolddev.dali

import in.stonecolddev.dali.ImageHandler.{ImageLocation, Image, Read, Resizer, Store}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage

class ImageHandlerTest extends AnyFlatSpec with should.Matchers with MockFactory {

  "ImageHandler.Resizer" should "resize an image" in new MockImageTest {
    val resizeStrategy = new Resizer {
      override def resize = (width: Int, height: Int, buffered: BufferedImage) => {
        fakeBufferedImage(width, height)
      }
    }

    bufferedImg.getHeight should equal(250)
    bufferedImg.getWidth should equal(250)

    val r = resizeStrategy.resize(150, 150, bufferedImg)
    r.getHeight should equal(150)
    r.getWidth should equal(150)
  }

  "Image" should "read and store an image" in new MockImageTest {
    mockRead expects imageLocation returns bufferedImg
    mockStore expects bufferedImg

    MockImage.read(imageLocation) shouldBe bufferedImg
    MockImage.store(bufferedImg)
  }

  trait MockImageTest {

    val bufferedImg = fakeBufferedImage()
    val imageLocation = "/stupid/dumb/path"
    val mockRead = mockFunction[ImageLocation, BufferedImage]
    val mockStore = mockFunction[BufferedImage, Unit]


    object MockImage extends Image {
      override val location: ImageLocation = imageLocation
      override val height: Int = 250
      override val width: Int = 250
      override val name: String = "fart"
      override val description: String = "a fart."
      override val read: Read = mockRead
      override val store: Store = mockStore
    }

    private def fakeBufferedImage(): BufferedImage = fakeBufferedImage(250, 250)

    def fakeBufferedImage(width: Int, height: Int) = {
      import java.awt.Graphics
      import java.awt.image.BufferedImage
      val bufferedImage: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      val g: Graphics = bufferedImage.getGraphics

      g.drawString("dali", 20, 20)

      bufferedImage
    }
  }
}