package util

import com.typesafe.scalalogging.StrictLogging
import markatta.futiles.Timeouts.timeout
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class Client(wsClient: WSClient)(implicit ec: ExecutionContext) extends StrictLogging {

  def get(): Future[String] = wsClient.url("https://httpbin.org").get().map(_ => "hello")

  def sleep(): Future[String] = {
    logger.info("sleep")
    Future {
      Thread.sleep(1000)
      "sleep"
    }
  }

  def timer(): Future[String] = {
    logger.info("timer")
    timeout(1.seconds)("timer")
  }
}
