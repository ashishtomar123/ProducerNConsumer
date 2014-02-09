package com.ntp

import org.joda.time.DateTime
import scala.concurrent.duration.FiniteDuration

/**
 * Time, Register and KeepAlive are used for communicating between Producers and Consumers
 * SendTimeLog, StartProducing are for Producer
 * SendKeepAlive and StartConsumer are for Consumer
 */
object Message {
  sealed abstract class MessageType

  sealed abstract class ProducerMessage extends MessageType
  sealed abstract class ConsumerMessage extends MessageType

  case object SendTimeLog extends ProducerMessage
  case object SendKeepAlive extends ConsumerMessage

  case class StartConsumer(startTime: FiniteDuration, interval: FiniteDuration) extends ConsumerMessage
  case class StartProducing(startTime: FiniteDuration, interval: FiniteDuration) extends ProducerMessage

  case class Time(time: DateTime) extends ConsumerMessage
  case object Register extends ProducerMessage
  case object KeepAlive extends ProducerMessage
}
