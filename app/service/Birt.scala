package service

/**
 * Created by alain on 08/05/15.
 * Inspired by: https://github.com/amaseng/play2-birt
 */

import java.util.logging.Level
import java.io.{File, InputStream, ByteArrayOutputStream, ByteArrayInputStream}

import org.eclipse.birt.report.engine.api._
import org.eclipse.birt.core.framework.Platform
import org.eclipse.core.internal.registry.RegistryProviderFactory

class Birt(logDir: String) {

  lazy val engine: IReportEngine = {
    val config = new EngineConfig

    val logDirFile = new File(logDir)
    if (!logDirFile.exists) logDirFile.mkdirs()

    config.setLogConfig(logDir, Level.WARNING)
    config.setEngineHome(".")

    Platform.startup(config)
    val factory = Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY).asInstanceOf[IReportEngineFactory]
    val engine = factory.createReportEngine(config)
    engine.changeLogLevel(Level.WARNING)
    engine
  }

  def start() {
    // --- Because of : http://www.eclipse.org/forums/index.php/t/545694/
    System.setSecurityManager(null);
    // ------------------------------
    engine
  }

  def stop() {
    engine.destroy()
    Platform.shutdown()
    RegistryProviderFactory.releaseDefault()
  }

  private def runReport(runnable: IReportRunnable, format: ReportFormat, parametersMap: Map[String, AnyRef], dataMap: Map[String, AnyRef]): InputStream = {

    import collection.JavaConverters._

    val options = new HTMLRenderOption()
    val outStream = new ByteArrayOutputStream()
    options.setOutputStream(outStream)
    options.setOutputFormat(format.formatString)

    val task = engine.createRunAndRenderTask(runnable)
    task.setAppContext(dataMap.asJava)
    task.setRenderOption(options)
    task.setParameterValues(parametersMap.asJava)
    task.run()
    task.close()

    new ByteArrayInputStream(outStream.toByteArray)
  }


  def generateReport(templateInputStream: InputStream, format: ReportFormat, parametersMap: Map[String, AnyRef], dataMap: Map[String, AnyRef]): InputStream = {
    runReport(engine.openReportDesign(templateInputStream), format, parametersMap, dataMap)
  }
}
