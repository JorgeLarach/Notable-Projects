package cs2.game
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.image.Image
import cs2.util.Vec2
import scala.collection.mutable.Buffer
import scala.util.Random


class EnemySwarm(private val nRows:Int, private val nCols:Int) {
  
  var list = Buffer[Enemy]()
  var enemyBuls = Buffer[Bullet]()
  var enemyCoolOff = 0
  
  override def clone():EnemySwarm = {
    val x = new EnemySwarm(nRows, nCols)
    x.list = this.list.map(_.clone)
    x.enemyBuls = this.enemyBuls.map(_.clone)
    x.enemyCoolOff = this.enemyCoolOff
    x
  }

  //creating enemies with random positions, populating list, and assigning them all as splittable
  for(x <- 0 until nRows) for(y <- 0 until nCols) list += new Enemy(SpaceGameApp.enemyImg, new Vec2(Random.between(0, (SpaceGameApp.screenWidth - SpaceGameApp.enemyImg.width.value)), Random.between(0, (SpaceGameApp.screenHeight/2 - SpaceGameApp.enemyImg.height.value))), SpaceGameApp.enBulImg) 
  for(elem <- list) elem.splittable = true
  
}