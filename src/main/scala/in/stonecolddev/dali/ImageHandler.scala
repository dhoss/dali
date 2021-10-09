package in.stonecolddev.dali


import java.awt.image.BufferedImage
import java.io.{FileInputStream, InputStream}

object ImageHandler {

  type ImageLocation = String
  type MimeType = String

  type Width = Int
  type Height = Int

  // This really does need to be a BufferedImage otherwise we end up doing
  // gymnastics and fighting the code
  type ResizeStrategy = (Width, Height, InputStream) => BufferedImage

  // a Writer needs to take an InputStream to read from
  type Writer = (ImageLocation, MimeType, InputStream) => Unit
  // a Reader needs to return what it read as an InputStream so things can read from it
  type Reader = ImageLocation => InputStream

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

  object Resizer {
    def apply(): DefaultResizer = {
      import net.coobird.thumbnailator.Thumbnails
      DefaultResizer(
        (width: Width, height: Height, is: InputStream) => {
          // Devin TODO: since this is going to be a BufferedImage,
          //        this should be common code
          Thumbnails.of(is)
                    .size(width, height)
                    .asBufferedImage()
        })
    }

    case class DefaultResizer(resize: ResizeStrategy) extends Resizer
  }

  trait Store {
    def read: Reader
    def write: Writer
  }

  object Store {
    object Strategy {
      def file(): FileStore = {
        import java.io.File
        import javax.imageio.ImageIO
        FileStore(
          // Reader
          (imageLocation: String) => new FileInputStream(imageLocation),
          // Writer
          {
            (imageDestination, mimeType, is: InputStream) =>
              // TODO: generate a slug
              // TODO: generate directory hash
              val file = new File(imageDestination)
              // TODO: make this less shitty
              new File(file.getParent).mkdirs()
              ImageIO.write(ImageIO.read(is), mimeType.split("/")(1), file)
          })
      }
    }

    case class FileStore(read: Reader, write: Writer) extends Store
  }
}