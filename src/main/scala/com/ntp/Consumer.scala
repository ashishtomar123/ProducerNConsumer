package com.ntp

import akka.actor.{ Actor, ActorRef }
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext.Implicits.global

class Consumer(val id: String, producerRef: ActorRef) extends Actor {
  import Message._

  //Variable for keeping track of KeepAlive messages
  var k = new scala.util.Random().nextInt(13)

  def receive = {
    /*
        Upon receiving the StartConsumer message the consumer registers
         with the producer and schedules to send k keepAlive messages
     */
    case StartConsumer(startTime, interval) => {
      println(id + " Registering " + self)
      producerRef ! Register
      context.system.scheduler.schedule(interval, interval, self, SendKeepAlive)
    }
    case Time(timeLog: DateTime) => println("Time - " + timeLog + " from - " + id)
    case SendKeepAlive => if (k > 0) {
      k -= 1
      producerRef ! KeepAlive
    }
    case _ =>
  }
}
