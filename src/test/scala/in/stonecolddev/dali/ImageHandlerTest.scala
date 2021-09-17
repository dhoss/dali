package in.stonecolddev.dali

import in.stonecolddev.dali.ImageHandler._
import org.scalamock.function.{MockFunction1, MockFunction3}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage

class ImageHandlerTest extends AnyFlatSpec with should.Matchers with MockFactory {

  // TODO: is there any real point in testing a trait?
  "ImageHandler.Resizer" should "call resize correctly" in new ImageTest {
    val resizeStrategy: Resizer = new Resizer {
      override def resize: ResizeStrategy = (width: Int, height: Int, _: BufferedImage) => {
        inMemoryBufferedImage(width, height)
      }
    }

    bufferedImg.getHeight should equal(250)
    bufferedImg.getWidth should equal(250)

    val r: BufferedImage = resizeStrategy.resize(150, 150, bufferedImg)
    r.getHeight should equal(150)
    r.getWidth should equal(150)
  }

  "ImageHandler.Store" should "call read and store correctly" in new ImageTest {
    val mockRead: MockFunction1[ImageLocation, BufferedImage] = mockFunction[ImageLocation, BufferedImage]
    val mockStore: MockFunction3[ImageLocation, MimeType, BufferedImage, Unit] = mockFunction[ImageLocation, MimeType, BufferedImage, Unit]
    mockRead expects imageLocation returns bufferedImg
    mockStore expects(imageLocation, mimeType, bufferedImg)

    val store: Store = new Store {
      override def read: Reader = mockRead
      override def write: Writer = mockStore
    }

    store.read(imageLocation) shouldBe bufferedImg
    store.write(imageLocation, mimeType, bufferedImg)
  }

  "ImageHandler.FileStore" should "store an image correctly" in new FileImageTest {
    try {
      import fi._
      write(imageLocation, mimeType, bufferedImg)

      val actual = readTestImage()
      actual.getHeight shouldBe bufferedImg.getHeight
      actual.getWidth shouldBe bufferedImg.getWidth
    } finally {
      cleanup()
    }
  }

  it should "read an image correctly" in new FileImageTest {
    try {
      writeTestImage()
      import fi._
      val actual = read(imageLocation)
      actual.getHeight shouldBe bufferedImg.getHeight
      actual.getWidth shouldBe bufferedImg.getWidth
    } finally {
      cleanup()
    }
  }


  trait FileImageTest extends ImageTest {
    import java.io.File
    import javax.imageio.ImageIO

    override val name = "file store image name"
    override val description = "file store image description"

    private lazy val imageFile = new File(imageLocation)

    lazy val fi: FileStore.FileStore = FileStore.Factory()

    def readTestImage(): BufferedImage = ImageIO.read(imageFile)
    def writeTestImage(): Unit =
      ImageIO.write(bufferedImg, format, imageFile)


    def cleanup(): Boolean = {
      import scala.reflect.io.Directory
      new Directory(new File(imageLocation)).deleteRecursively()
    }
  }

  trait ImageTest {
    val bufferedImg: BufferedImage = inMemoryBufferedImage()
    val name = "test image name"
    val description = "test image description"
    val mimeType = "image/jpg"
    val format: MimeType = mimeType.split("/")(1)
    lazy val imageLocation = s"/tmp/dali/images/${name.split("\\s").mkString("+")}.${format}"

    lazy val testImage: Image = Image(
      imageLocation,
      250,
      250,
      name,
      description,
      mimeType,
      inMemoryBufferedImage())

    def inMemoryBufferedImage(): BufferedImage = inMemoryBufferedImage(250, 250)

    def inMemoryBufferedImage(width: Int, height: Int): BufferedImage = {
      import java.awt.Graphics
      import java.awt.image.BufferedImage
      val bufferedImage: BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      val g: Graphics = bufferedImage.getGraphics

      g.drawString("dali", 20, 20)

      bufferedImage
    }
  }
}