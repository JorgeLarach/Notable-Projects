package cs2.game
import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.scene.canvas.GraphicsContext
import scala.collection.mutable.Buffer
import scala.math._


abstract class Sprite (val img:Image, var pos:Vec2) {
  
  var state = "alive"
  var count = 0

  def move(direction:Vec2):Unit = this.pos += direction

  def moveTo (location:Vec2):Unit = { 
    pos.x = location.x
    pos.y = location.y
  }

  def display(g:GraphicsContext)

  def intersection(other:Sprite):Boolean = {
    /* temp variables to help readability and to visualize logic */ 
    var intersection = false
    var otherLeftEdge = other.pos.x
    var otherRightEdge = otherLeftEdge + other.img.width.value
    var callingLeftEdge = pos.x
    var callingRightEdge = callingLeftEdge + img.width.value
    var otherTop = other.pos.y
    var otherBottom = otherTop + other.img.height.value
    var callingTop = pos.y
    var callingBottom = callingTop + img.height.value

    if((otherRightEdge >= callingLeftEdge && otherRightEdge <= callingRightEdge) || (otherLeftEdge <= callingRightEdge && otherLeftEdge >= callingLeftEdge))
      if((otherTop <= callingBottom && otherTop >= callingTop)||(otherBottom >= callingTop && otherBottom <= callingBottom))
        intersection = true
    intersection

  }

}
/* Collisions I'm Checking For:
  - Player Vs. Buffer[Enemy]
  - Player Vs. Buffer[EnemyBuls]
  - Buffer[PlayerBuls] vs Buffer[Enemy]
  - Buffer[PlayerBuls] vs Buffer[EnemyBuls]
*/