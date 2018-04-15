package util

import com.softwaremill.sttp._
import com.softwaremill.sttp.okhttp.OkHttpFutureBackend
import com.typesafe.scalalogging.StrictLogging
import okhttp3.{Dispatcher, OkHttpClient}
import org.slf4j.MDC
import util.MdcUtil.MdcExecutorService

import scala.concurrent.{ExecutionContext, Future}

class SttpClient(implicit ec: ExecutionContext) extends StrictLogging {
  private val okHttpClient = new OkHttpClient.Builder().dispatcher(new Dispatcher(new MdcExecutorService(new Dispatcher().executorService()))).build
  private implicit val backend = OkHttpFutureBackend.usingClient(okHttpClient)

  def get(): Future[String] = {
    logger.info("Client.getSttp()")
    sttp.get(uri"https://httpbin.org").send().map { _ =>
      if (MDC.get("RequestId") == null) {
        logger.error("No MDC in map()", new RuntimeException("No MDC"))
      }
      MDC.get("RequestId")
    }
  }
}
