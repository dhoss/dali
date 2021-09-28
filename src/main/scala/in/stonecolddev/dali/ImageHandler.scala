package in.stonecolddev.dali

import javax.imageio.stream.{ImageInputStream, ImageOutputStream}

object ImageHandler {

  type ImageLocation = String
  type MimeType = String

  type ResizeStrategy = (Int, Int, ImageInputStream) => ImageOutputStream

  type Writer = (ImageLocation, MimeType, ImageInputStream) => Unit
  type Reader = ImageLocation => ImageOutputStream

  case class Image(
    location: ImageLocation,
    height: Int,
    width: Int,
    name: String,
    description: String,
    mimeType: String)

  trait Resizer {
    def resize: ResizeStrategy
  }

  trait Store {
    def read: Reader
    def write: Writer
  }

  object Store {

    import java.io.File
    import javax.imageio.ImageIO

    object Factory {
      def file() =
        FileStore(
          (imageLocation: String) =>
            ImageIO.createImageOutputStream(new File(imageLocation)),
          {
            (imageLocation, mimeType, imgOs: ImageInputStream) =>
              // TODO: generate a slug, /p/a/th here
              val file = new File(imageLocation)
              // TODO: make this less shitty
              new File(file.getParent).mkdirs()
              ImageIO.write(ImageIO.read(imgOs), mimeType.split("/")(1), file)
          })
    }

    case class FileStore(read: Reader, write: Writer) extends Store
  }

  object FileResizer {


  }
}