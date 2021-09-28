package in.stonecolddev.dali

import in.stonecolddev.dali.ImageHandler._
import org.scalamock.function.{MockFunction1, MockFunction3}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.{Files, Path}
import javax.imageio.ImageIO
import javax.imageio.stream.{ImageInputStream, ImageOutputStream}

class ImageHandlerTest extends AnyFlatSpec with should.Matchers with MockFactory {

  "ImageHandler.Resizer" should "call resize correctly" in new ImageTest {
    val resizeStrategy: Resizer = new Resizer {
      override def resize: ResizeStrategy = (h: Int, w: Int, _: ImageInputStream) =>
        imageOutputStream(h, w)
    }

    bi.getHeight should equal(height)
    bi.getWidth should equal(width)

    val r: BufferedImage = fromImgOs(resizeStrategy.resize(150, 150, imgOs))
    r.getHeight should equal(150)
    r.getWidth should equal(150)
  }

  "ImageHandler.Store" should "call read and store correctly" in new ImageTest {
    val mockRead: MockFunction1[ImageLocation, ImageOutputStream] =
      mockFunction[ImageLocation, ImageOutputStream]
    val mockStore: MockFunction3[ImageLocation, MimeType, ImageInputStream, Unit] =
      mockFunction[ImageLocation, MimeType, ImageInputStream, Unit]
    mockRead expects imageLocation returns imgOs
    mockStore expects(imageLocation, mimeType, imgOs)

    val store: Store = new Store {
      override def read: Reader = mockRead
      override def write: Writer = mockStore
    }

    store.read(imageLocation) shouldBe imgOs
    store.write(imageLocation, mimeType, imgOs)
  }

  "ImageHandler.FileStore" should "store an image correctly" in new FileImageTest {
    import fi._
    write(imageLocation, mimeType, imgOs)

    val actual: BufferedImage = readTestImage()
    actual.getHeight shouldBe bi.getHeight
    actual.getWidth shouldBe bi.getWidth
  }

  it should "read an image correctly" in new FileImageTest {
    import fi._

    val actual: BufferedImage =
      fromImgOs(read(tempImage(width, height).toString))
    actual.getHeight shouldBe bi.getHeight
    actual.getWidth shouldBe bi.getWidth
  }

  trait FileImageTest extends ImageTest {
    import Store._

    import java.io.File
    import javax.imageio.ImageIO

    override lazy val name = "file store image name"
    override val description = "file store image description"

    lazy val fi: Store.FileStore = Strategy.file()

    def readTestImage(): BufferedImage = ImageIO.read(writeTestImage())
    def writeTestImage(): File = tempImage(width, height)
  }

  trait ImageTest {
    val imgOs: ImageOutputStream = imageOutputStream()
    val description = "test image description"
    val bi: BufferedImage = bufferedImage(250, 250)

    lazy val name = "test image name"
    lazy val mimeType = "image/jpg"
    lazy val format: MimeType = mimeType.split("/")(1)
    lazy val path: Path = Files.createTempDirectory(null)
    lazy val slug = s"${name.split("\\s").mkString("+")}"
    lazy val height = 250
    lazy val width = 250
    // TODO: maybe make configurable
    lazy val imageLocation = s"${path}/${slug}.${format}"
    lazy val testImage: Image = Image(
      imageLocation,
      height,
      width,
      name,
      description,
      mimeType)

    def fromImgOs(os: ImageOutputStream): BufferedImage = ImageIO.read(os)

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

    def imageOutputStream(): ImageOutputStream =
      imageOutputStream(250, 250)

    def imageOutputStream(width: Int, height: Int): ImageOutputStream =
      ImageIO.createImageOutputStream(tempImage(width, height))
  }
}