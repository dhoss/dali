package in.stonecolddev.dali

import in.stonecolddev.dali.ImageHandler.FileStore.FileImage
import in.stonecolddev.dali.ImageHandler._
import org.scalamock.function.{MockFunction1, MockFunction3}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage

class ImageHandlerTest extends AnyFlatSpec with should.Matchers with MockFactory {

  "ImageHandler.Resizer" should "call resize correctly" in new MockImageTest {
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

  "ImageHandler.Image" should "call read and store correctly" in new MockImageTest {
    mockRead expects imageLocation returns bufferedImg
    mockStore expects(imageLocation, mimeType, bufferedImg)

    MockImage.reader(imageLocation) shouldBe bufferedImg
    MockImage.writer(imageLocation, mimeType, bufferedImg)
  }

  "FileStore.FileImage" should "store an image correctly" in new FileImageTest {
    // TODO move this to FileImageTest
    try {
      fi.write()

      val actual = readTestImage()
      actual.getHeight shouldBe bufferedImg.getHeight
      actual.getWidth shouldBe bufferedImg.getWidth
    } finally {
      cleanup()
    }
  }

  it should "read and image correctly" in new FileImageTest {
    try {
      writeTestImage()

      val actual = fi.read()
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

    lazy val fi: FileImage =
      FileImage(imageLocation, bufferedImg.getHeight(), bufferedImg.getWidth(), name, description, mimeType, bufferedImg)

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
    val name: String
    val description: String
    val mimeType = "image/jpg"
    val format: MimeType = mimeType.split("/")(1)
    lazy val imageLocation = s"/tmp/dali/images/${name.split("\\s").mkString("+")}.${format}"


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

  trait MockImageTest extends ImageTest {

    val mockRead: MockFunction1[ImageLocation, BufferedImage] = mockFunction[ImageLocation, BufferedImage]
    val mockStore: MockFunction3[ImageLocation, MimeType, BufferedImage, Unit] =
      mockFunction[ImageLocation, MimeType, BufferedImage, Unit]

    override val name = "mock image name"
    override val description = "mock image description"

    object MockImage extends Image {
      override val location: ImageLocation = imageLocation
      override val height: Int = 250
      override val width: Int = 250
      override val name: String = name
      override val description: String = description
      override val reader: Reader = mockRead
      override val writer: Writer = mockStore
      override val mimeType: String = mimeType
      override val data: BufferedImage = inMemoryBufferedImage()
    }
  }
}