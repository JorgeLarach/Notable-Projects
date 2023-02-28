package cs2.game
import scalafx.scene.image.Image
import cs2.game.EnemySwarm
import cs2.util.Vec2
import scala.collection.mutable.Buffer
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode
import scalafx.scene.canvas.Canvas
import scalafx.Includes._
import scala.collection.mutable.Set

class Player(avatar:Image, initPos:Vec2, private val bulletPic:Image) extends Sprite(avatar, initPos) {

  var playerBuls = Buffer[Bullet]()
  var lives = 3
  var coolOff = 0

  def display(g:GraphicsContext):Unit = {
    if(state == "alive") g.drawImage(avatar, initPos.x, initPos.y)
    else if(state == "exploding"){
      if(count < 60){
        playerBuls.clear
        g.drawImage(SpaceGameApp.exploArr(count/15), initPos.x-15, initPos.y-15, 90, 90)
        count += 1
      } else {
        lives -= 1
        moveTo(new Vec2(SpaceGameApp.origin.x, SpaceGameApp.origin.y))
        state = "alive"
        count = 0
      }
    }
    coolOff -= 1
  }

  override def clone():Player = {
    val x = new Player(avatar, initPos.clone, bulletPic)
    x.playerBuls = this.playerBuls.map(_.clone)
    x.lives = this.lives
    x.coolOff = this.coolOff
    x.state = this.state
    x.count = this.count
    x
  }

  def shoot():Bullet = {new Bullet(bulletPic, new Vec2(initPos.x+(avatar.width.value/2).toDouble-(bulletPic.width/2).toDouble, initPos.y-bulletPic.height.value), new Vec2(0, -5))}
  
}