package cs2.game
import scalafx.scene.image.Image
import cs2.util.Vec2
import scala.util.Random
import scalafx.scene.canvas.GraphicsContext
import cs2.game.EnemySwarm
import scala.collection.mutable.Buffer
import scalafx.scene.image.Image


class Enemy(pic:Image, initPos:Vec2, private val bulletPic:Image) extends Sprite(pic, initPos) {

  val splitEnemy = new Image(getClass().getResource("/images/Sprites/newEnemy.png").toString) 
  var splittable = false
  var enVel = if(splittable) new Vec2(Random.between(3, 7), Random.between(3, 7)) else new Vec2(Random.between(1, 5), Random.between(1, 5))

  
  if(Random.nextBoolean) enVel.x = -enVel.x
  if(Random.nextBoolean) enVel.y = -enVel.y
  
  def shoot():Bullet = { 
    new Bullet(
      bulletPic, 
      new Vec2(initPos.x+(pic.width.value/2).toDouble-(bulletPic.width/2).toDouble, (initPos.y+pic.height.value)), 
      new Vec2(0,5)
    ) 
  }

  override def clone():Enemy = {
    val x = new Enemy(pic, initPos.clone, bulletPic)
    x.enVel = this.enVel.clone
    x.state = this.state
    x.count = this.count
    x.splittable = this.splittable
    x
  }

  def timeStep(list:Buffer[Enemy]):Unit = {
    if(initPos.x + pic.width.value >= SpaceGameApp.screenWidth) enVel.x = -enVel.x
    if(initPos.x < 0) enVel.x = -enVel.x
    if(initPos.y < 0 ) enVel.y = -enVel.y
    if(initPos.y + pic.height.value >= SpaceGameApp.screenHeight/2)enVel.y = -enVel.y
    initPos += enVel
  }

  def display(g:GraphicsContext):Unit = {
    if(state == "alive" && splittable) g.drawImage(splitEnemy, initPos.x, initPos.y) 
    else if(state == "alive" && !splittable) g.drawImage(pic, initPos.x, initPos.y) 
    else if(state == "exploding"){
      if(count < 60){
        g.drawImage(SpaceGameApp.exploArr(count/15), initPos.x-15, initPos.y-15, 70, 70)
        count += 1
      } else {
        state = "dead"
        count = 0
      }
    }
  }
  
}
