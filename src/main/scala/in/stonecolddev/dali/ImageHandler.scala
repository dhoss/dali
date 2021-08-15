package in.stonecolddev.dali

import java.awt.image.BufferedImage

object ImageHandler {

  type ImageLocation = String

  type ResizeStrategy = (Int, Int, BufferedImage) => BufferedImage

  type Store = BufferedImage => Unit

  type Read = ImageLocation => BufferedImage

  trait Resizer {
    def resize: ResizeStrategy
  }

  trait Image {
    val location: ImageLocation
    val height: Int
    val width: Int
    val name: String
    val description: String
    def read: Read
    def store: Store
  }
}
