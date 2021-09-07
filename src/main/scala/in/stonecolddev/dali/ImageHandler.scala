package in.stonecolddev.dali



import java.awt.image.BufferedImage

object ImageHandler {

  type ImageLocation = String
  type MimeType = String

  type ResizeStrategy = (Int, Int, BufferedImage) => BufferedImage

  type Writer = (ImageLocation, MimeType, BufferedImage) => Unit
  type Reader = ImageLocation => BufferedImage

  trait Resizer {
    def resize: ResizeStrategy
  }

  trait Image extends Store {
    val location: ImageLocation
    val height: Int
    val width: Int
    val name: String
    val description: String
    val mimeType: String
    val data: BufferedImage
  }

  trait Store {
    def reader: Reader
    def writer: Writer
  }

  object FileStore {

    import java.io.File
    import javax.imageio.ImageIO

    trait FileStore extends Store {
      def reader: Reader =
        (imageLocation: String) => ImageIO.read(new File(imageLocation))

      def writer: Writer = {
        (imageLocation, mimeType, bufferedImage: BufferedImage) =>
          val file = new File(imageLocation)
          // TODO: make this less shitty
          new File(file.getParent).mkdirs()
          ImageIO.write(bufferedImage, mimeType.split("/")(1), file)
      }
    }

    case class FileImage(
      location: ImageLocation, // TODO: generate a slug here
      height: Int,
      width: Int,
      name: String,
      description: String,
      mimeType: String,
      data: BufferedImage) extends Image with FileStore {
        def write(): Unit = writer(location, mimeType, data)
        def read(): BufferedImage = reader(location)
    }
  }
}