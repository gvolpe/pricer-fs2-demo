package com.gvolpe.fs2.streams.utils

import cats.effect.IO
import fs2.{Pipe, Sink}

object FS2Utils {

  def liftSink[A](f: A => IO[Unit]): Sink[IO, A]     = liftPipe[A, Unit](f)

  def liftPipe[A, B](f: A => IO[B]): Pipe[IO, A, B]  = _.evalMap (f)

}
