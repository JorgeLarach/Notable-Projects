package cs2.algorithms

/* Jorge Larach, January 1, 2023 */

import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import scala.collection.mutable.Buffer
import java.io.{BufferedWriter, FileWriter, File, FileOutputStream}
import java.io.PrintWriter
import scala.collection.mutable.ListBuffer
import scala.util.Random
import org.apache.poi.ss.usermodel._ 
import scala.collection.JavaConverters._
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.ConditionalFormattingThreshold.RangeType
import org.apache.poi.ss.util.CellRangeAddress
import scala.util.Random
import java.awt._
import javafx.scene.paint
import org.apache.poi.xssf.usermodel.XSSFColor
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.imgscalr.Scalr

object PictureStuff extends App{
    /* note: .heic files don't work */    
    /* pictures taken on phone are rotated sideways, probably exif data bs */

    //change depending on if you want color or not
    var color = true

    //setting up and making the spreadsheet
    val out = new FileOutputStream("/Users/jorgelarach/Desktop/spreadSheet.xlsx")
    val workbook = new XSSFWorkbook
    val sheet = workbook.createSheet("new sheet")
    var styleMap = Map[(Int, Int, Int), XSSFCellStyle]()
    var colorSet = Set[(Int, Int, Int)]()


    var count = 0

    //setting up image
    var img = ImageIO.read(new File("/Users/jorgelarach/Desktop/Screenshots & Pictures/capitol.jpeg"))

    var w = img.getWidth() 
    var h = img.getHeight() 

 

    println("og image width and height: " + w + ", " + h)
    // img.flush()
    var red = 0
    var green = 0
    var blue = 0
    
    //image scaling
    val scaleFactor:Double = h.toDouble/w.toDouble //what w needs to be multiplied by to get scaled h

    var newImg = img
    img.flush()
    var newH = h
    var newW = w
    
    if(color) {
        newW = findCol(w)
        newH = (newW*scaleFactor).toInt
        newImg = Scalr.resize(img, newW, newH)
    }

    

    // println("old w/h: " + w + ", " + h + " new w/h: " + newW + ", " + newH)


    def findCol(wi:Int):Int = {
        colorSet.clear
        var he = (wi*scaleFactor).toInt
        newImg = Scalr.resize(img, wi, he)
        // var he = newImg.getHeight
        println("wi: " + wi + " he: " + he)


        for (y <- 0 until he) {
            for (x <- 0 until wi) {
                
                // println("x,y: " + x+","+y)
                val pix = newImg.getRGB(x, y).toInt
                red =   (pix >> 16) & 0xFF;
                green = (pix >>  8) & 0xFF;
                blue =  (pix      ) & 0xFF;
                colorSet += ((red, green, blue))  
            }
        }

        if(colorSet.size < 64000) wi
        else if(wi > 1000)findCol(wi-10)
        else findCol(wi-1)
        
    }


    sheet.setDefaultRowHeight(500)
    for(x <- 0 to newW) sheet.setColumnWidth(x, 1000)


   
    for (y <- 0 until newH) {
        val row = sheet.createRow(y)
        for (x <- 0 until newW) {
            // println("calculations done: " + count + "/" + newH*newW)
            println("processing... " + ((count.toDouble/(newH*newW).toDouble)*100).toInt.toString + "%")

            val pix = newImg.getRGB(x, y).toInt
            red =   (pix >> 16) & 0xFF;
            green = (pix >>  8) & 0xFF;
            blue =  (pix      ) & 0xFF;

            var cell = row.createCell(x)
            cell.setAsActiveCell()

            if(color){
          
                if(styleMap.contains(red, green, blue)) cell.setCellStyle(styleMap(red, green, blue))    
                else {
 
                    var style = workbook.createCellStyle()
                    style.setFillPattern(FillPatternType.SOLID_FOREGROUND)
                    style.setFillForegroundColor(new XSSFColor(Array[Byte](red.toByte, green.toByte, blue.toByte), null))
                    styleMap += (red, green, blue) -> style
                    cell.setCellStyle(styleMap(red, green, blue))
                    cell.setCellValue("1")                                            
                    
                }
                // println("iters: " + count + " styles: " + workbook.getNumCellStyles )  

            } 
            else {
                var average:Double = (red + green + blue) / 3.0
                cell.setCellValue(average)
            }
            count += 1
            
        }
    }
    println("processing... 100%")
    println("old w/h: " + w + ", " + h + " new w/h: " + newW + ", " + newH)


    if(!color){
        // conditional formatting shit
        var sheetCF = sheet.getSheetConditionalFormatting()
        var rule = sheetCF.createConditionalFormattingColorScaleRule()
        var clrFmt = rule.getColorScaleFormatting()
        clrFmt.getColors()(0).setARGBHex("FF000000")
        clrFmt.getColors()(1).setARGBHex("FFFFFFFF")
        clrFmt.getThresholds()(0).setRangeType(RangeType.MIN)
        // clrFmt.getThresholds()(0).setValue(0)
        clrFmt.getThresholds()(1).setRangeType(RangeType.MAX);
        // clrFmt.getThresholds()(1).setValue(255)
        val regions = Array[CellRangeAddress](CellRangeAddress.valueOf("A1:"+sheet.getActiveCell));
        sheetCF.addConditionalFormatting(regions, rule);
    }

    workbook.write(out)
    out.close()
    
}