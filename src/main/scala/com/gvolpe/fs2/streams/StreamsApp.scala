package com.gvolpe.fs2.streams

import fs2.{Stream, Task}

object StreamsApp extends App {

  val program = Stream.eval(Task.delay(println("Hello World!")))
  program.run.unsafeRun()

}
