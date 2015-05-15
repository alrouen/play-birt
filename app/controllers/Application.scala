package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import play.api._
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.Play.current
import service.PdfReportFormat

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("GlopGlop"))
  }

  def test = Action(parse.json) { request =>
    Play.resourceAsStream("personScripted.rptdesign").map { input =>
      val params = Map("JsonDataSource" -> request.body.toString())
      val is = service.BirtPlugin.birt.generateReport(input, PdfReportFormat, params, Map.empty)
      val dataContent: Enumerator[Array[Byte]] = Enumerator.fromStream(is)
      is.close()
      Ok.chunked(dataContent).withHeaders(CONTENT_TYPE -> PdfReportFormat.mimeType)
    } getOrElse {
      Ok("no report file")
    }
  }


}

/*
*
*
*
* {"bindings":
*   [
*     {
*       "first_name":"Gyula",
*       "last_name":"Szabo",
*       "phone":"2276",
*       "nationality":"Hungarian",
*       "address":
*         [
*           {"zip_code":"1412","city":"Luxembourg","street":"rue Dante","number":"12"}
*         ]
*     }
*   ]
* }
*
*
* */