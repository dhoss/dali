package in.stonecolddev.dali

import in.stonecolddev.dali.ImageHandler.{ImageLocation, Metadata, Read, Resizer, Store}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage

class ImageHandlerTest extends AnyFlatSpec with should.Matchers with MockFactory {

  val bufferedImg = fakeBufferedImage()
  val mockImg = MockImage()
  val mockRead = mockFunction[ImageLocation, BufferedImage]
  val mockStore = mockFunction[BufferedImage, Unit]

  "ImageHandler.Resizer" should "resize an image" in {
    val resizeStrategy = new Resizer {
      override def resize = (width: Int, height: Int, buffered: BufferedImage) => {
        fakeBufferedImage(width, height)
      }
    }

    val r = resizeStrategy.resize(150, 150, bufferedImg)
    r.getHeight should equal(150)
    r.getWidth should equal(150)
  }

  case class MockImage(
   override val location: ImageLocation = "/stupid/dumb/path",
   override val height: Int = 250,
   override val width: Int = 250,
   override val name: String = "fart",
   override val description: String = "a fart.",
   override val read: Read = mockRead,
   override val store: Store = mockStore) extends Metadata

  private def fakeBufferedImage(): BufferedImage = fakeBufferedImage(250, 250)

  private def fakeBufferedImage(width: Int, height: Int) = {
    import java.awt.Graphics
    import java.awt.image.BufferedImage
    val bufferedImage: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val g: Graphics = bufferedImage.getGraphics

    g.drawString("dali", 20, 20)

    bufferedImage
  }
}
