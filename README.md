Simple NTP Service – One Producer - Multiple Consumers
======================================================

I) Application
Single Producer and multiple Consumers application. There is one Producer and N Consumers. N is a positive integer specified as a command line parameter. 
The Consumers registers with the Producer and starts receiving the Time Logs sent by the producer. Producer keeps on sending time messages to its
registered consumers every second.
Consumers also send k KeepAlive messages to its producer in intervals of 5 second to specify that it’s still expecting time messages.
Producer stops sending messages to its registered consumers if it doesn’t get a KeepAlive message from a consumer within 10 seconds.


II) Design 
A) Message- The class contains case classes for messages that can be sent between Producer and Consumers.
1)	SendTimeLog 
2)	SendKeepAlive 
3)	StartConsumer(startTime: FiniteDuration, interval: FiniteDuration) 
4)	StartProducing(startTime: FiniteDuration, interval: FiniteDuration) 
5)	Time(time: DateTime)
6)	Register
7)	KeepAlive
 * Time, Register and KeepAlive are used for communicating between Producers and Consumers
 * SendTimeLog, StartProducing are for Producer
 * SendKeepAlive and StartConsumer are for Consumer

B) Producer- Actor which keeps track of its registered consumers and is responsible for sending time logs to its consumers.
Producer receives Register, StartProducing, SendTimeLog and KeepAlive messages.
Producer sends Time messages to Consumers. 
While sending message it checks-
    a) Whether there are any consumers registered.
    b) It stops sending messages to consumers that have not sent KeepAlive message within 10 seconds.
   c) It shuts down the system if none of the consumers have sent KeepAlive messages within 30 seconds.

C) Consumer- Registers with Producer and receives Time message from the producer.
It receives Time, SendKeepAlive and StartConsumer messages.
It sends Register and KeepAlive messages to producer.

D) ProducerConsumerScheduler- It creates N consumer actors and Producer actor. It also sends them StartConsumer and StartProducing messages, respectively.

E) ProducerConsumerNTPTest has some test cases.

III) How To Run
1) To compile –> sbt compile
2) To test –> sbt test
3) To Run -> sbt
>run-main com.ntp.ProducerConsumerScheduler <Integer>

For example ->run-main com.ntp.ProducerConsumerScheduler 3

IV) Improvements
Read the parameters such as message interval for time message, message interval for KeepAlive messages, etc can be read from a configuration file.

