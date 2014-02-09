package com.ntp

import akka.actor.{ ActorRef, Actor }
import org.joda.time.DateTime
import scala.collection.mutable.HashMap
import scala.concurrent.ExecutionContext.Implicits.global

class Producer(val id: String) extends Actor {
  import Message._
  private val consumersSet = HashMap.empty[ActorRef, DateTime]

  def getConsumersSet: HashMap[ActorRef, DateTime] = consumersSet

  /**
   * Gets Time difference between the datetime passed as an argument and the current time
   * @param datetime
   * @return
   */
  private def getTimeDiffInSecs(datetime: DateTime): Long =
    (new DateTime().getMillis - datetime.getMillis) / 1000

  /**
   * The internal method used to send messages to its registered consumers.
   * a) It checks whether there are any consumers registered
   * b) It stops sending messages to consumers that have not sent KeepAlive message in last 10 secs
   * c) It shuts down the producer and system if all the consumers have stopped sending KeepAlive messages for last 30 secs
   */
  private def sendMessage = {
    println("Producer checking for Consumers--")
    if (consumersSet.isEmpty) {
      println("From- " + id + " No Consumers Yet!!")
    } else if (consumersSet.valuesIterator.forall { dateTime => getTimeDiffInSecs(dateTime) > 30 }) {
      context.system.shutdown()
    } else {
      consumersSet.foreach {
        case (consumer, dateTime) => if (getTimeDiffInSecs(dateTime) <= 10)
          consumer ! Time(new DateTime())
      }
    }
  }

  def receive = {
    case StartProducing(startTime, msgInterval) => context.system.scheduler.schedule(startTime, msgInterval, self, SendTimeLog)
    case SendTimeLog => sendMessage
    case Register =>
      println(id + " Registered" + sender)
      consumersSet += (sender -> new DateTime)
    case KeepAlive => consumersSet += (sender -> new DateTime)
    case _ =>
  }
}
