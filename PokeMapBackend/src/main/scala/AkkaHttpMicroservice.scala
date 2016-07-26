import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.IOException

import dto.{FindPokemon, PokemonPosition}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.math._
import spray.json.DefaultJsonProtocol
import web.rest.CallRestService
import spray.json._
import DefaultJsonProtocol._

case class IpInfo(query: String, country: Option[String], city: Option[String], lat: Option[Double], lon: Option[Double])

case class IpPairSummaryRequest(ip1: String, ip2: String)

case class IpPairSummary(distance: Option[Double], ip1Info: IpInfo, ip2Info: IpInfo)

object IpPairSummary {
  def apply(ip1Info: IpInfo, ip2Info: IpInfo): IpPairSummary = IpPairSummary(calculateDistance(ip1Info, ip2Info), ip1Info, ip2Info)

  private def calculateDistance(ip1Info: IpInfo, ip2Info: IpInfo): Option[Double] = {
    (ip1Info.lat, ip1Info.lon, ip2Info.lat, ip2Info.lon) match {
      case (Some(lat1), Some(lon1), Some(lat2), Some(lon2)) =>
        // see http://www.movable-type.co.uk/scripts/latlong.html
        val φ1 = toRadians(lat1)
        val φ2 = toRadians(lat2)
        val Δφ = toRadians(lat2 - lat1)
        val Δλ = toRadians(lon2 - lon1)
        val a = pow(sin(Δφ / 2), 2) + cos(φ1) * cos(φ2) * pow(sin(Δλ / 2), 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        Option(EarthRadius * c)
      case _ => None
    }
  }

  private val EarthRadius = 6371.0
}

trait Protocols extends DefaultJsonProtocol {
  implicit val ipInfoFormat = jsonFormat5(IpInfo.apply)
  implicit val ipPairSummaryRequestFormat = jsonFormat2(IpPairSummaryRequest.apply)
  implicit val ipPairSummaryFormat = jsonFormat3(IpPairSummary.apply)
  implicit val pokemonInfoFormat = jsonFormat5(PokemonPosition.apply)
  implicit val findPokemonFormat = jsonFormat3(FindPokemon.apply)

}

trait Service extends Protocols {
  implicit val system: ActorSystem
  implicit def executor: ExecutionContextExecutor
  implicit val materializer: Materializer

  def config: Config
  val logger: LoggingAdapter

  val pokemonList = List(
    PokemonPosition("Pikachu", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Magikarp", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Rattata", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Hitmonchan", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Snorlax", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),

    PokemonPosition("Jynx", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Grimer", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Koffing", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Drowzee", Some("Colombia"), Some("Medellin"), Some(-123.0881), Some(38.3845)),

    PokemonPosition("Drowzee", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),

    PokemonPosition("Ditto", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845)),
    PokemonPosition("Squirtle", Some("Colombia"), Some("Medellin"), Some(-122.0881), Some(37.3845))

  )

  lazy val ipApiConnectionFlow: Flow[HttpRequest, HttpResponse, Any] =
    Http().outgoingConnection(config.getString("services.ip-api.host"), config.getInt("services.ip-api.port"))

  def ipApiRequest(request: HttpRequest): Future[HttpResponse] = Source.single(request).via(ipApiConnectionFlow).runWith(Sink.head)

  def fetchIpInfo(ip: String): Future[Either[String, IpInfo]] = {
    ipApiRequest(RequestBuilding.Get(s"/json/$ip")).flatMap { response =>
      response.status match {
        case OK => Unmarshal(response.entity).to[IpInfo].map(Right(_))
        case BadRequest => Future.successful(Left(s"$ip: incorrect IP format"))
        case _ => Unmarshal(response.entity).to[String].flatMap { entity =>
          val error = s"FreeGeoIP request failed with status code ${response.status} and entity $entity"
          logger.error(error)
          Future.failed(new IOException(error))
        }
      }
    }
  }

  val routes = {
    logRequestResult("PokeMapBackend") {
      pathPrefix("ip") {
        (get & path(Segment)) { ip =>
          complete {
            fetchIpInfo(ip).map[ToResponseMarshallable] {
              case Right(ipInfo) => ipInfo
              case Left(errorMessage) => BadRequest -> errorMessage
            }
          }
        } ~
        (post & entity(as[IpPairSummaryRequest])) { ipPairSummaryRequest =>
          complete {
            val ip1InfoFuture = fetchIpInfo(ipPairSummaryRequest.ip1)
            val ip2InfoFuture = fetchIpInfo(ipPairSummaryRequest.ip2)
            ip1InfoFuture.zip(ip2InfoFuture).map[ToResponseMarshallable] {
              case (Right(info1), Right(info2)) => IpPairSummary(info1, info2)
              case (Left(errorMessage), _) => BadRequest -> errorMessage
              case (_, Left(errorMessage)) => BadRequest -> errorMessage
            }
          }
        }
      }~pathPrefix("findPokemon") {
          (get & path(Segment)) { pokemonName =>

            val pokemonInfo:Array[String] = pokemonName.toString.split(",")

            val fiteredPokemon = pokemonList.filter(p => p.name == pokemonInfo.headOption.get)

            complete {
              fiteredPokemon.toJson
            }
        }~(post & entity(as[FindPokemon])) { findPokemon =>

            println(findPokemon)
            var respuesta = List.empty[PokemonPosition]

            if(findPokemon.name.isDefined && findPokemon.lon.isDefined && findPokemon.lat.isDefined) {
                respuesta = pokemonList.filter(p => p.name == findPokemon.name.get && p.lon.equals(findPokemon.lon) && p.lat.get == findPokemon.lat.get)
            }else if(findPokemon.name.isDefined ){
                respuesta = pokemonList.filter(p => p.name == findPokemon.name.get)
            }else {
              "Los parametros para la busqueda no pueden arrojar resutlados"
            }
            println("Respuesta: ")
            println(respuesta)

            complete {
              respuesta.toJson
              }
        }
      }~pathPrefix("findActivePokemon") {
        (get & path(Segment)) { pokemonName =>

          //https://pokevision.com/map/data/34.0089404989527/-118.49765539169312
          val respuesta  = CallRestService.getActivePokemons("https://pokevision.com/map/data/" ,"34.0089404989527", "-118.49765539169312" )

          complete {
            respuesta.toJson
          }
        }~(post & entity(as[FindPokemon])) { findPokemon =>

          println(findPokemon)
          //https://pokevision.com/map/data/34.0089404989527/-118.49765539169312
          val respuesta  = CallRestService.getActivePokemons("https://pokevision.com/map/data/" ,findPokemon.lon.get.toString, findPokemon.lat.get.toString )

          complete {
            respuesta.toJson
          }
        }
      }

    }
  }
}

object AkkaHttpMicroservice extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
