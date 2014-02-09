package com.ntp

import akka.actor.{ Props, ActorSystem }
import concurrent.duration._

//sbt -J-Xss3m -J-Xmx5g run

object ProducerConsumerScheduler extends App {

  //Checking that the argument is a Single Integer Value
  val consumersCntArg: Option[Int] =
    args match {
      case Array(cnt) => try {
        Some(cnt.toInt)
      } catch {
        case ex: Throwable => None
      }
      case _ => None
    }

  consumersCntArg match {
    case Some(consumerCount: Int) if consumerCount > 0 => {
      val system = ActorSystem("producer-consumer-system")

      val startTime = Duration(0, SECONDS)
      val interval = Duration(5, SECONDS)
      val msgInterval = Duration(1, SECONDS)

      import Message._
      //Create producer and N Consumers
      val producer = system.actorOf(Props(new Producer("Producer")))
      val consumers = { for (i <- 1 to consumerCount) yield system.actorOf(Props(new Consumer("Consumer" + i, producer))) }.toList

      //Start N Consumers and Producer
      consumers.map(consumer => consumer ! StartConsumer(startTime, interval))
      producer ! StartProducing(startTime, msgInterval)
    }
    case _ => println("****Please enter an integer value for the number of Consumers with the command line parameter.")

  }
}

