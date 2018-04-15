package util

import com.typesafe.scalalogging.StrictLogging
import markatta.futiles.Timeouts.timeout
import org.slf4j.MDC
import play.api.libs.ws.WSClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class Client(wsClient: WSClient)(implicit ec: ExecutionContext) extends StrictLogging {

  def get(): Future[String] = {
    logger.info("Client.get()")
    wsClient.url("https://httpbin.org").get().map { _ =>
      if (Thread.currentThread().getName.startsWith("AsyncHttpClient")) throw new RuntimeException("AHC!")
      if (MDC.get("RequestId") == null) {
        logger.error("No MDC in map()")
        throw new RuntimeException("No MDC")
      }
      "hello"
    }
  }

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
