package service

import scala.util.control.NonFatal
import play.api.{PlayException, Application, Plugin, Logger}

/**
 * Created by alain on 08/05/15.
 */

class BirtPlugin(app: Application) extends Plugin {

  private var _helper: Option[BirtPluginHelper] = None
  def helper = _helper.getOrElse(throw new RuntimeException("BirtPlugin error: no BirtPluginHelper available?"))

  override def onStart() {
    try {
      val configuration = BirtPlugin.parseConfig(app)
      _helper = Some(BirtPluginHelper(configuration, app))
      _helper.map { h =>
        Logger.info("Starting BIRT...")
        h.birt.start()
        Logger.info("BIRT started successfully.")
        Logger.info(s"BIRT logs will be there: ${h.birtConfig.logDir}")
      }
    } catch {
      case NonFatal(e) =>
        throw new PlayException("BirtPlugin Initialization Error", "An exception occurred while initializing the BirtPlugin.", e)
    }
  }

  override def onStop() {
    try {
      _helper.map { h =>
        Logger.info("Stopping BIRT...")
        h.birt.stop()
        Logger.info("BIRT stopped successfully.")
      }
    } catch {
      case e: Throwable => Logger.info("Error when stopping BIRT: " + e.getMessage)
    } finally {
      _helper = None
    }
  }

  override def enabled = true

}

object BirtPlugin {

  private def parseConfig(app: Application):BirtConfiguration = {

    (for {
      logDir <- app.configuration.getString("birt.log.dir")
    } yield {
      BirtConfiguration(logDir)
    }) getOrElse {
      throw app.configuration.globalError(s"BirtPlugin Error, some configuration parameter are missing! Please check you conf/application.conf")
    }
  }

  def current(implicit app: Application): BirtPlugin = app.plugin[BirtPlugin] match {
    case Some(plugin) => plugin
    case _            => throw new PlayException("BirtPlugin Error", "The BirtPlugin has not been initialized! Please check your conf/play.plugins file.")
  }

  def birt(implicit app: Application) = current.helper.birt
}

case class BirtConfiguration(logDir: String)

private[service] case class BirtPluginHelper(birtConfig: BirtConfiguration, app: Application) {
  lazy val birt = new Birt(birtConfig.logDir)
}