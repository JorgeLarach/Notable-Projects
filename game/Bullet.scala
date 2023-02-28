package cs2.game
import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.scene.canvas.GraphicsContext


class Bullet(pic:Image, initPos:Vec2, private var vel:Vec2) extends Sprite(pic, initPos) {
  
  override def display(g:GraphicsContext):Unit = {
    if(state == "alive") g.drawImage(pic, initPos.x, initPos.y) 
    else if(state == "exploding"){
      if(count < 15){
        g.drawImage(SpaceGameApp.bulColArr(count/5), (initPos.x + ((pic.width.value)/2))-((SpaceGameApp.bulColArr(count/5).width.value)/2), initPos.y-(SpaceGameApp.bulColArr(count/5).height.value/2))
        count += 1
      } else {
        count = 0
        state = "dead"
      }
    }
  }

  override def clone():Bullet = {
    var x = new Bullet(pic, initPos.clone, vel.clone)
    x.state = this.state
    x.count = this.count
    x
  }

  def timeStep():Unit = { if(state == "alive") initPos += vel}
  
  def outOfBounds():Boolean = {
    var outOfBounds = false
    if(pos.y > SpaceGameApp.screenHeight || (pos.y+img.height.value) < 0) outOfBounds = true
    outOfBounds
  }

}
