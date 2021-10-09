package in.stonecolddev.dali

import in.stonecolddev.dali.ImageHandler._
import org.scalamock.function.{MockFunction1, MockFunction3}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage
import java.io.{Reader => _, Writer => _, _}
import java.nio.file.{Files, Path}
import javax.imageio.ImageIO

class ImageHandlerTest extends AnyFlatSpec with should.Matchers with MockFactory {

  "ImageHandler.Resizer" should "call resize correctly" in new ImageTest {
    val resizeStrategy: Resizer = Resizer()

    bi.getHeight should equal(height)
    bi.getWidth should equal(width)

    val r: BufferedImage = resizeStrategy.resize(150, 150, imgIs)
    r.getHeight should equal(150)
    r.getWidth should equal(150)
  }

  "ImageHandler.Store" should "call read and store correctly" in new ImageTest {
    val mockRead: MockFunction1[ImageLocation, InputStream] =
      mockFunction[ImageLocation, InputStream]
    mockRead expects imageLocation returns imgIs

    val mockStore: MockFunction3[ImageLocation, MimeType, InputStream, Unit] =
      mockFunction[ImageLocation, MimeType, InputStream, Unit]
    mockStore expects(imageLocation, mimeType, imgIs)

    val store: Store = new Store {
      override def read: Reader = mockRead
      override def write: Writer = mockStore
    }

    store.read(imageLocation) shouldBe imgIs
    store.write(imageLocation, mimeType, imgIs)
  }

  "ImageHandler.FileStore" should "store an image correctly" in new FileImageTest {
    import fi._
    write(imageLocation, mimeType, imgIs)

    val actual: BufferedImage = readTestImage()
    actual.getHeight shouldBe bi.getHeight
    actual.getWidth shouldBe bi.getWidth
  }

  it should "read an image correctly" in new FileImageTest {
    import fi._

    val actual: BufferedImage = ImageIO.read(read(tempImage(width, height).toString))
    actual.getHeight shouldBe bi.getHeight
    actual.getWidth shouldBe bi.getWidth
  }

  trait FileImageTest extends ImageTest {
    import Store._

    import java.io.File
    import javax.imageio.ImageIO

    override lazy val name = "file store image name"

    lazy val fi: Store.FileStore = Strategy.file()

    def readTestImage(): BufferedImage = ImageIO.read(writeTestImage())
    def writeTestImage(): File = tempImage(width, height)
  }

  trait ImageTest {
    val imgIs: InputStream = new FileInputStream(tempImage(width, height))
    val bi: BufferedImage = bufferedImage(250, 250)

    lazy val name = "test image name"
    lazy val mimeType = "image/jpg"
    lazy val format: MimeType = mimeType.split("/")(1)
    lazy val path: Path = Files.createTempDirectory(null)
    lazy val height = 250
    lazy val width = 250
    // TODO: maybe make configurable
    lazy val imageLocation = s"${path}/${s"${name.split("\\s").mkString("+")}"}.${format}"

    private def bufferedImage(width: Int, height: Int): BufferedImage = {
      import java.awt.Graphics
      import java.awt.image.BufferedImage
      val bufferedImage: BufferedImage =
        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
      val g: Graphics = bufferedImage.getGraphics

      g.drawString("dali", 20, 20)
      bufferedImage
    }

    def tempImage(
      width: Int,
      height: Int,
      name: String = name,
      format: String = format): File = {

      path.toFile.deleteOnExit()
      val f = File.createTempFile(name, format, path.toFile)
      ImageIO.write(bufferedImage(width, height), format, f)
      f
    }
  }
}