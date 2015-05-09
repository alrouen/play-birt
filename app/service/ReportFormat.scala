package service

/**
 * Created by alain on 08/05/15.
 */
sealed trait ReportFormat {
  val formatString: String
  val mimeType: String
}

object PdfReportFormat extends ReportFormat {
  val formatString: String = "pdf"
  val mimeType: String = "application/pdf"
}

object XlsReportFormat extends ReportFormat {
  val formatString: String = "xls"
  val mimeType: String = "application/vnd.ms-excel"
}

object PostscriptReportFormat extends ReportFormat {
  val formatString: String = "postscript"
  val mimeType: String = "application/vnd.cups-ppd"
}

object ReportFormat {
  def apply(formatString: String): ReportFormat =
    formatString.toLowerCase match {
      case "pdf" => PdfReportFormat
      case "xls" => XlsReportFormat
      case "postscript" => PostscriptReportFormat
      case _ => throw new IllegalArgumentException("Format '" + formatString + "' not supported.")
    }
}
