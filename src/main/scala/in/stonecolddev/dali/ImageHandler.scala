package in.stonecolddev.dali

import java.awt.image.BufferedImage

object ImageHandler {

  type ImageLocation = String
  type MimeType = String

  type ResizeStrategy = (Int, Int, BufferedImage) => BufferedImage

  type Writer = (ImageLocation, MimeType, BufferedImage) => Unit
  type Reader = ImageLocation => BufferedImage

  case class Image(
    location: ImageLocation, // TODO: generate a slug, /p/a/th here
    height: Int,
    width: Int,
    name: String,
    description: String,
    mimeType: String,
    data: BufferedImage)

  trait Resizer {
    def resize: ResizeStrategy
  }

  trait Store {
    def read: Reader
    def write: Writer
  }

  object FileStore {

    import java.io.File
    import javax.imageio.ImageIO

    // TODO: determine if this is a good name
    object Factory {
      def apply() = new FileStore()
    }

    class FileStore extends Store {
      def read: Reader =
        (imageLocation: String) => ImageIO.read(new File(imageLocation))

      def write: Writer = {
        (imageLocation, mimeType, bufferedImage: BufferedImage) =>
          val file = new File(imageLocation)
          // TODO: make this less shitty
          new File(file.getParent).mkdirs()
          ImageIO.write(bufferedImage, mimeType.split("/")(1), file)
      }
    }
  }
}