package util

import java.util.concurrent.TimeUnit

import akka.dispatch._
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.duration.{Duration, FiniteDuration}

class MDCPropagatingDispatcherConfigurator(config: Config, prerequisites: DispatcherPrerequisites) extends MessageDispatcherConfigurator(config, prerequisites) {
  class MdcDispatcher(_configurator: MessageDispatcherConfigurator, id: String, throughput: Int, throughputDeadlineTime: Duration, executorServiceFactoryProvider: ExecutorServiceFactoryProvider,
                      shutdownTimeout: FiniteDuration) extends Dispatcher(_configurator, id, throughput, throughputDeadlineTime, executorServiceFactoryProvider, shutdownTimeout) with StrictLogging {
    override def execute(r: Runnable) = super.execute(new MdcUtil.WrappedRunnable(r))
  }

  private val instance = new MdcDispatcher(
    this,
    config.getString("id"),
    config.getInt("throughput"),
    FiniteDuration(config.getDuration("throughput-deadline-time", TimeUnit.NANOSECONDS), TimeUnit.NANOSECONDS),
    configureExecutor(),
    FiniteDuration(config.getDuration("shutdown-timeout", TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS))


  override def dispatcher(): MessageDispatcher = instance
}
