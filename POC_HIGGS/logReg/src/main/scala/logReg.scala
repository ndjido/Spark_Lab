/**
 * @author Ndjido Ardo BAR
 * @address ndjidardo.bar<at>bearingpoint.com
 */

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.LogisticRegressionWithLBFGS
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import java.io._
import org.apache.hadoop.fs.FileSystem
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.fs.FSDataOutputStream

object LogRegApp {
  def main(args: Array[String]) {

    // Configure spark context
    val conf = new SparkConf().setAppName("LogReg Application")
    val sc = new SparkContext(conf)

    // Load and parse and split dataset
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
    println("Loading data...\n")
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
    val hdfsShare = "/user/shared/"
    val data = sc.textFile(hdfsShare + args(0))
    //val data = sc.textFile(args(0))
    
    val parsedData = data.map { line =>
      val row = line.split(',')
      LabeledPoint(row(0).toDouble, Vectors.dense(row.tail.map(_.toDouble)))
    }

    // Splitting data : TODO => perform suffling
    val splits = parsedData.randomSplit(Array(.7, .3), seed = 13L)
    //training set
    val trainSet = splits(0).cache()
    // test set
    val testSet = splits(1).cache()

    // building model
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
    println("Building Logistic Model...\n")
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
    val model = (new LogisticRegressionWithLBFGS().setIntercept(true)).run(trainSet)
        
    val fileSystem = FileSystem.get(new Configuration())
    val writer = fileSystem.create(new Path(new File(hdfsShare + "LogReg_Perf_" + System.currentTimeMillis.toString + ".txt").getName))
    
    // prediction
    val trainPreds = trainSet.map { point =>
      val pred = model.predict(point.features)
      (pred, point.label)
    }

    val testPreds = testSet.map { point =>
      val pred = model.predict(point.features)
      (pred, point.label)
    }

    //model performance computation
    writer.write("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n".getBytes)
    writer.write("Model performance computation...\n".getBytes)
    writer.write("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n".getBytes)
    
    /**
     * @brief: Confusion Matrix computation
     */
    val trainConfMatrix = (new MulticlassMetrics(trainPreds)).confusionMatrix
    val testConfMatrix = (new MulticlassMetrics(testPreds)).confusionMatrix
    
    writer.write("[training] Confusion Matrix".getBytes)
    writer.write(trainConfMatrix.toString.getBytes)
    writer.write("[test set] Confusion Matrix".getBytes)
    writer.write(testConfMatrix.toString.getBytes)
    
    /**
     * @brief: compute errors
     */
    val trainError = trainPreds.map{case (p,v) => if(p!=v) 1 else 0}.mean() 
    val testError = testPreds.map{case (p,v) => if(p!=v) 1 else 0}.mean() 
    
    writer.write(("[training] Error = " + (trainError * 100).toString).getBytes)
    writer.write(("[test set] Error = " + (testError * 100).toString).getBytes)
    
     /**
     * @brief: TODO => compute  f1
     */
    
    /**
     * @brief: AUC computation => area under ROC(Receiver Operating Characteristics) curve 
     */
    val trainAUC = (new BinaryClassificationMetrics(trainPreds)).areaUnderROC()
    val testAUC = (new BinaryClassificationMetrics(testPreds)).areaUnderROC()

    writer.write(("[training] Area under ROC (AUC) = " + trainAUC.toString).getBytes)
    writer.write(("[test set] Area under ROC (AUC) = " + testAUC.toString).getBytes)
    
    writer.close()
    
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
    println("END\n")
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n")
    
  }
}