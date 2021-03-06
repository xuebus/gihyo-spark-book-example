/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.gihyo.spark.ch03.pairrdd_transformation

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

// scalastyle:off println

object CombineByKeyExample {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.WARN)

    val conf = new SparkConf().setAppName("CombineByKeyExample")
    val sc = new SparkContext(conf)

    run(sc)
    sc.stop()
  }

  def run(sc: SparkContext) {
    val fruits = sc.parallelize(
      Array(("Apple", 6), ("Orange", 1), ("Apple", 2), ("Orange", 5), ("PineApple", 1)))
    val fruitCountAvgs = fruits.combineByKey(
      createCombiner = (v: Int) => Acc(v.toDouble, 1),
      mergeValue = (partAcc: Acc, n: Int) => partAcc += n,
      mergeCombiners = (acc1: Acc, acc2: Acc) => acc1 ++= acc2
    ).mapValues(acc => acc.sum / acc.count)

    println(s"""fruits:         ${fruits.collect().mkString(", ")}""")
    println(s"""fruitCountAvgs: ${fruitCountAvgs.collect().mkString(", ")}""")
  }
}

// scalastyle:on println
