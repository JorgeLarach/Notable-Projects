package cs2.game
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.paint.Color
import scalafx.scene.input.KeyEvent
import scalafx.scene.input.KeyCode
import scalafx.Includes._
import scalafx.animation._
import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.application.JFXApp
import scalafx.scene.canvas.GraphicsContext
import scalafx.event.ActionEvent
import scala.collection.mutable.Buffer
import scala.util.Random
import scalafx.scene.input.MouseEvent
import cs2.adt.LinkedStack

/* Phase I Notes 
    HW2, completed 10/3
    * predetermined size of swarm. might ask user to input through text areas or whatnot 
    * currently all bullets are grouped and displayed together, might separate for collision reasons 
*/

/* Phase II Notes
    HW3, completed 10/11
    * current collision logic (in Sprite) has temporary variables names to help me visualize it. Logic is still suboptimal
    * super inefficient animation timer code dealing with iterating through collections to check for collisions, will rework
    * removing indexes from listbuffers while in loop breaks code, unfortunate (hopefully temporary) solution in place (deadBuls, deadEnemy, deadPlayerBuls)
*/

/* Phase III Notes
    HW4, completed 11/14
    * sorry for the huge wall of declarations and the atrocity I committed in line 306
    * might add a "chin" to the bottom of the screen for UI stuff in phase IV
*/

/* Phase IV Notes
    HW5, completed 12/4
    * removed levels system, less complicated now
    * stopped keeping track of time passed, too complicated
    * added rewind functionality
    * added "splittable" (yellow) enemies that spit out some more enemies when killed
*/

object SpaceGameApp extends JFXApp {
    val explo0 = new Image(getClass().getResource("/images/Sprites/Explosion/Explosion1.png").toString) 
    val explo1 = new Image(getClass().getResource("/images/Sprites/Explosion/Explosion2.png").toString)
    val explo2 = new Image(getClass().getResource("/images/Sprites/Explosion/Explosion3.png").toString)  
    val explo3 = new Image(getClass().getResource("/images/Sprites/Explosion/Explosion4.png").toString)
    val explo4 = new Image(getClass().getResource("/images/Sprites/Explosion/Explosion5.png").toString)
    val BulletCol0 = new Image(getClass().getResource("/images/Sprites/Bullet Collision/BulletCol0.png").toString)
    val BulletCol1 = new Image(getClass().getResource("/images/Sprites/Bullet Collision/BulletCol1.png").toString)
    val BulletCol2 = new Image(getClass().getResource("/images/Sprites/Bullet Collision/BulletCol2.png").toString)
    val enemyImg = new Image(getClass().getResource("/images/Sprites/enemySprite.png").toString) 
    val enBulImg = new Image(getClass().getResource("/images/Sprites/enemyBullet.png").toString)
    val playerImg = new Image(getClass().getResource("/images/Sprites/playerSprite.png").toString)  
    val bulletImg = new Image(getClass().getResource("/images/Sprites/Bullet.png").toString)
    val titleImg = new Image(getClass().getResource("/images/Text/TitleTrans.png").toString)
    val playNow = new Image(getClass().getResource("/images/Text/PlayNow.png").toString)
    val playAgain = new Image(getClass().getResource("/images/Text/PlayAgain.png").toString)
    val livesLeft = new Image(getClass().getResource("/images/Text/LivesLeft.png").toString)
    val gameOverText = new Image(getClass().getResource("/images/Text/gameOver.png").toString)
    val youWin = new Image(getClass().getResource("/images/Text/YouWin.png").toString)
    val scoreImg = new Image(getClass().getResource("/images/Text/Score.png").toString)
    val timeImg = new Image(getClass().getResource("/images/Text/Time.png").toString)
    
    val levelImg = new Image(getClass().getResource("/images/Text/Level.png").toString)
    val numbers = Array[Image](new Image(getClass().getResource("/images/Text/0.png").toString),
                               new Image(getClass().getResource("/images/Text/1.png").toString),
                               new Image(getClass().getResource("/images/Text/2.png").toString),
                               new Image(getClass().getResource("/images/Text/3.png").toString),
                               new Image(getClass().getResource("/images/Text/4.png").toString),
                               new Image(getClass().getResource("/images/Text/5.png").toString),
                               new Image(getClass().getResource("/images/Text/6.png").toString),
                               new Image(getClass().getResource("/images/Text/7.png").toString),             
                               new Image(getClass().getResource("/images/Text/8.png").toString),            
                               new Image(getClass().getResource("/images/Text/9.png").toString))             
    var exploArr = Array[Image](explo0, explo1, explo2, explo3, explo4)
    var bulColArr = Array[Image](BulletCol0, BulletCol1, BulletCol2)
    val screenWidth = 800
    val screenHeight = 800
    val origin:Vec2 = Vec2(((screenWidth/2)-(playerImg.width.value/2)), (screenHeight-playerImg.height.value))
    var nCols = 4
    var nRows = 4
    var interval:Double = 0
    var livesDisplaySize = 30
    var score = 0
    var mathCool = 0
    var lastTime = 0L
    var rewind = false
    var gameState = "splash"
    var player = new Player(playerImg, new Vec2(origin.x, origin.y) , bulletImg)
    var showLives:Array[Image] = Array.ofDim[Image](player.lives)
    var swarm = new EnemySwarm(nCols,nRows)
    var deadBuls = Buffer[Bullet]()
    var deadEnemyBuls = Buffer[Bullet]()
    var deadEnemy = Buffer[Enemy]()
    var starInfo:Buffer[Array[Int]] = Buffer[Array[Int]]()
    var anim:Buffer[Vec2] = Buffer[Vec2]()  
    var keys = Set[String]()
    var toAdd = Buffer[Enemy]()
    var larSize:Double = 1

    

    var gameStack = new LinkedStack[GameState]()
    gameStack.push(new GameState)


    class GameState () {
        val playerState = player.clone
        val enemyState = swarm.clone
        val frameScore = score
    }

    stage = new JFXApp.PrimaryStage {
        title = "Space Game!"
        scene = new Scene(800,800){
            val canvas = new Canvas(800, 800)
            content = canvas
            val g = canvas.graphicsContext2D            
            
            //calling single use methods
            genStars()
            userControls()

            val timer = AnimationTimer(t => {
                

                if(gameState == "splash"){
                    /* ANIMATED SPLASH SCREEN */

                    //displaying title screen
                    drawBackground()
                    g.drawImage(titleImg, screenWidth.toInt/2-(titleImg.width.value/2), 200) 
                    g.setFill(Color.Orange)
                    g.fillRect(300,550,200,50)
                    g.drawImage(playNow, 329, 559) 
                    g.setFill(Color.White)

                    //covering it up with white squares and removing one for each frame
                    anim.foreach(x => g.fillRect(x.x, x.y, 80, 80))
                    if(anim.length >= 1) anim -= anim.head    
                }

                if(gameState == "game"){
                    /* GAME BODY */                   

                    checkKeys()
                    drawBackground()
                    drawSprites(gameStack.peek)
                    drawUI()

                    if(rewind && gameStack.len > 1) {
                        val tmp = gameStack.pop() 
                        player = tmp.playerState
                        swarm = tmp.enemyState
                        score = tmp.frameScore  
                        drawUI()
                        drawSprites(tmp)
                    } 

                    if(lastTime == 0) lastTime = t
                    interval = (t - lastTime) / 1e9

                    if(!rewind){

                        if(larSize == 0) larSize = gameStack.len
                        if(gameStack.len > larSize) larSize = gameStack.len
                        
                        if(player.state == "exploding") swarm.enemyBuls.clear
                        if(player.lives == 0) gameState = "gameOver"
                        player.display(g)
                        drawUI()
                    
                        //calculating enemy cooldown time & generating bullets (REWORK)
                        mathCool = math.round(40.625-((5*(swarm.list.size))/8)).toInt
                        if(swarm.enemyCoolOff < 0 && player.state != "exploding"){
                            var chosen = Random.shuffle(swarm.list).head.shoot()
                            if(swarm.list.size >= 2 && chosen.state != "exploding") swarm.enemyBuls += chosen
                            if(swarm.list.size == 1) for(elem <- swarm.list) if(elem.state != "exploding")swarm.enemyBuls += elem.shoot()
                            if(swarm.list.size <= 5) swarm.enemyCoolOff = mathCool+5
                            else swarm.enemyCoolOff = mathCool   
                        }
                        swarm.enemyCoolOff -= 1
                        
                        /* NON-BULLET RELATED COLLISION LOGIC */
                        for(p <- swarm.list){
                            //if player insters. splittable enemy
                            if(player.intersection(p) && p.state == "alive") player.state = "exploding"
                            if(p.state == "dead" && p.splittable) {
                                for(i <- 0 until Random.between(2, 5)) toAdd += new Enemy(enemyImg, new Vec2(p.pos.clone.x, p.pos.clone.y), enBulImg)
                                //randomly offsetting new offspring children to prevent them all getting destroyed with one bullet, doesn't really work
                                for(elem <- toAdd) elem.pos += new Vec2(Random.between(-3, 2), Random.between(-3, 2))
                                deadEnemy += p
                            }
                            else if(p.state == "dead") deadEnemy += p

                            if(p.state == "alive") p.timeStep(swarm.list)
                            
                            p.display(g)
                        } 
                        
                        /* PLAYER BULLET LOGIC */
                        for(p <- player.playerBuls){
                            //checks each player bullet against each enemy bullet
                            for(h <- swarm.enemyBuls) {
                                if(h.intersection(p) && p.state == "alive"){
                                    p.state = "exploding"
                                    deadEnemyBuls += h 
                                }
                                if(p.state == "dead") deadBuls += p
                            }
                            //checks each player bullet against each enemy
                            for(h <- swarm.list){
                                if(h.intersection(p) && h.state == "alive"){
                                    deadBuls += p
                                    h.state = "exploding"
                                    score += 100
                                }
                            }
                            //checks out of bounds
                            if(p.outOfBounds) deadBuls += p
                            p.display(g)
                            p.timeStep()
                        }

                        /* ENEMY BULLET LOGIC: checks if enemy bullet hits player and if enemy bullet goes out of bounds */
                        for(p <- swarm.enemyBuls){
                            if(player.intersection(p) && player.state == "alive"){
                                player.state = "exploding"
                                deadEnemyBuls += p
                            }
                            if(p.outOfBounds) deadEnemyBuls += p
                            p.display(g)
                            p.timeStep()
                        }

                        //level win check
                        if(swarm.list.length == 0){
                            gameState = "gameOver"
                        } 

                        //removes sprites
                        for(p <- deadEnemyBuls) swarm.enemyBuls -= p 
                        for(p <- deadBuls) player.playerBuls -= p
                        for(p <- deadEnemy) swarm.list -= p
                        deadEnemyBuls.clear
                        deadBuls.clear
                        deadEnemy.clear

                        for(elem <- toAdd) if(Random.between(0, 100) < 20) elem.splittable = true
                        swarm.list ++= toAdd
                        toAdd.clear

                        gameStack.push(new GameState)
                    }
                }

                if(gameState == "gameOver"){
                    /* GAME OVER SCREEN */

                    var stringScore = score.toString
                    var count = 0
                    drawBackground()

                    if(player.lives == 0) g.drawImage(gameOverText, (screenWidth/2) - (gameOverText.width.value/2), 100)
                    else g.drawImage(youWin, (screenWidth/2) - (youWin.width.value/2), 100)
                    g.drawImage(scoreImg, (screenWidth/2) - (scoreImg.width.value*1.5), 300, 86, 14)
                    for(i <- stringScore){
                        g.drawImage(numbers(i.toInt - 48), 10 + (screenWidth/2) +  (count*numbers(i.toInt - 48).width.value), 300)
                        count += 1
                    }
                    g.setFill(Color.Orange)
                    g.fillRect(300,550,200,50)
                    g.setFill(Color.Black)
                    g.drawImage(playAgain, 400-(playAgain.width.value/2), 575-(playAgain.height.value/2))
                }
            }) 

            canvas.requestFocus()
            timer.start()

            
            
            //methods to organize code

            def drawStar(x:Int, y:Int, which:Int){
                g.setFill(Color.White)
                if(which == 0) g.fillRect(x,y, 4,4)  
                else if (which == 1){
                    g.setStroke(Color.White)
                    g.strokeLine(x+4, y, x+4, y+8)
                    g.strokeLine(x+3, y+2, x+5, y+2)
                    g.strokeLine(x+2, y+3, x+6, y+3)
                    g.strokeLine(x, y+4, x+8, y+4)
                    g.strokeLine(x+2, y+5, x+6, y+5)
                    g.strokeLine(x+3, y+6, x+5, y+6)
                }
            } 

            def drawBackground():Unit = {
                g.setFill(Color.Black)
                g.fillRect(0,0, width.value, height.value)
                for(elem <- starInfo) drawStar(elem(0), elem(1), elem(2)) 
            } 

            def drawUI():Unit = {
                var stringScore = score.toString

                //drawing reverse progress bar
                var barProg:Double = (gameStack.len.toDouble/larSize)*100
                g.setFill(Color.hsb(barProg, 1, 1))
                g.fillRect(5, screenHeight-scoreImg.height.value-40, barProg, 15)
                g.strokeRect(5, screenHeight-scoreImg.height.value-40, 100, 15)


                //drawing score
                var count = 0
                g.drawImage(scoreImg, 5, screenHeight-scoreImg.height.value-10)
                for(i <- stringScore){
                    g.drawImage(numbers(i.toInt - 48), 10 + scoreImg.width.value + (count*9), screenHeight - scoreImg.height.value - 10, 9, 9)
                    count += 1
                }
                
                //drawing lives
                g.drawImage(livesLeft, screenWidth - (livesLeft.width.value), screenHeight - livesDisplaySize - livesLeft.height.value - 10)
                for(i <- 0 until player.lives){
                    showLives(i) = playerImg
                    g.drawImage(showLives(i), (screenWidth-livesDisplaySize) -(i*livesDisplaySize), screenHeight-livesDisplaySize, livesDisplaySize, livesDisplaySize)
                }
            }     

            def drawSprites(gameState:GameState):Unit = {
                gameState.playerState.display(g)

                //displaying enemies and enemy bullets
                for(elem <- gameState.playerState.playerBuls) elem.display(g)
                for(elem <- gameState.enemyState.list) elem.display(g)
                for(elem <- gameState.enemyState.enemyBuls) elem.display(g)
            }

            def resetGame():Unit = {
                player.playerBuls.clear
                swarm.enemyBuls.clear
                player.lives = 3
                nRows = 4
                nCols = 4
                score = 0
                mathCool = 0
                lastTime = 0
                larSize = 500
                gameState = "game"
                player.moveTo(new Vec2(origin.x, origin.y))
                swarm = new EnemySwarm(nCols,nRows)          
                gameStack.clear      
                gameStack.push(new GameState)
            }

            def checkKeys():Unit = {
                if(player.state != "exploding"){
                    if((keys.contains("UP") || keys.contains("W")) || (keys.contains("UP") && keys.contains("W"))) if(player.pos.y > 0) player.move(new Vec2(0,-3))
                    if((keys.contains("LEFT") || keys.contains("A")) || (keys.contains("LEFT") && keys.contains("A"))) if(player.pos.x > 0) player.move(new Vec2(-3, 0))
                    if((keys.contains("DOWN") || keys.contains("S")) || (keys.contains("DOWN") && keys.contains("W"))) if(player.pos.y+player.img.height.value < SpaceGameApp.screenHeight) player.move(new Vec2(0,3))
                    if((keys.contains("RIGHT") || keys.contains("D")) || (keys.contains("RIGHT") && keys.contains("D"))) if(player.pos.x + player.img.width.value < SpaceGameApp.screenWidth) player.move(new Vec2(3,0)) 
                    if(keys.contains("R") && !gameStack.isEmpty) SpaceGameApp.rewind = true
                    else if(!keys.contains("R"))SpaceGameApp.rewind = false
                    if(keys.contains("SPACE") && player.coolOff < 0){
                        player.playerBuls += player.shoot()
                        player.coolOff = 50
                    }
                }
            }

            //single use methods
            def genStars():Unit = {
                for(h <- 0 until 800 by 80){
                    for(w <- 0 until 800 by 80){
                        anim += Vec2(w,h)
                        var which = Random.nextDouble()
                        if(which <= 0.5) starInfo += Array(Random.between(0, 74) + w, Random.between(0, 74) + h, 0)
                        else if(which > 0.5 && which <= 0.8) starInfo += Array(Random.between(0, 71) + w, Random.between(0, 71) + h, 1)
                    }
                }
            }

            def userControls():Unit = {
                //instantiating key and mouse input controls
                canvas.onKeyPressed = (e:KeyEvent) => (keys += e.code.toString)
                canvas.onKeyReleased = (e:KeyEvent) => (keys -= e.code.toString)
                canvas.onMouseClicked = (e:MouseEvent) => {
                    if(((e.x >= 300 && e.x <= 500) && (e.y >= 550 && e.y <= 600)) && gameState == "splash") gameState = "game"
                    if(((e.x >= 300 && e.x <= 500) && (e.y >= 550 && e.y <= 600) && gameState == "gameOver")) resetGame()
                    if(gameState == "splash" || gameState == "gameOver") starInfo += Array(e.x.toInt, e.y.toInt, Random.between(0,2))
                }  
            }  
            


        }
    }
}



//useful png website (cmd+click):
//https://www.pngfind.com
//retro font website:
//https://www.fontspace.com/category/galaga