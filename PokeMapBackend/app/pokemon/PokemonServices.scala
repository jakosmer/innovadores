package pokemon

import java.io.{BufferedReader, IOException, InputStream, InputStreamReader}
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Calendar

import POGOProtos.Map.Pokemon.MapPokemonOuterClass.MapPokemon
import POGOProtos.Map.Pokemon.WildPokemonOuterClass.WildPokemon
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeTokenRequest, GoogleClientSecrets, GoogleTokenResponse}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.pokegoapi.api.PokemonGo
import com.pokegoapi.api.map.MapObjects
import POGOProtos.Map.Pokemon.NearbyPokemonOuterClass.NearbyPokemon
import com.pokegoapi.util.Log
import dto._
import okhttp3.OkHttpClient
import com.pokegoapi.auth._
import play.api.libs.json.Json
import rest.CallRestService
import tools.Crypter

import scala.collection.JavaConversions._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


case class Message(eventName: String, message: List[PokemonPosition])

/**
  * Created by arcearta on 2016/07/26.
  */
class PokemonServices extends App {

  val urToEmit = "http://50.116.54.176:3000/emitMessage";

  def convertStreamToString(is: InputStream): String = {
    def inner(reader: BufferedReader, sb: StringBuilder): String = {
      val line = reader.readLine()
      if (line != null) {
        try {
          inner(reader, sb.append(line + "\n"))
        } catch {
          case e: IOException => e.printStackTrace()
        } finally {
          try {
            is.close()
          } catch {
            case e: IOException => e.printStackTrace()
          }
        }
      }
      sb.toString()
    }
    inner(new BufferedReader(new InputStreamReader(is)), new StringBuilder())
  }

  @throws[IOException]
  private def refreshMyToken(auth_code: String): String = {
    var refreshToken: String = "";
    val saveCode = UserSession.findSession(auth_code)

    if (saveCode.isDefined) {
      refreshToken = saveCode.get
    } else {
      val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JacksonFactory.getDefaultInstance, new BufferedReader(new InputStreamReader(getUrl)))
      val tokenResponse: GoogleTokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport,
        JacksonFactory.getDefaultInstance,
        "https://www.googleapis.com/oauth2/v4/token",
        clientSecrets.getDetails.getClientId,
        clientSecrets.getDetails.getClientSecret, auth_code, "").execute

      val accessToken: String = tokenResponse.getAccessToken
      refreshToken = tokenResponse.getRefreshToken

      if (refreshToken != null && !"".equals(refreshToken))
        UserSession.saveSession(auth_code, refreshToken)
    }
    refreshToken
  }

  def getUrl: InputStream = {
    val dato = this.getClass.getResourceAsStream("/" + "client_secret.json")
    //println(convertStreamToString(dato))
    dato
  }

  def authenticate(token: Option[String], http: OkHttpClient): (CredentialProvider, OkHttpClient) = {
    var auth: GoogleCredentialProvider = null
    val decryptedValue: String = Crypter.decryptAES(token.get)

    val provider: GoogleUserCredentialProvider = new GoogleUserCredentialProvider(http)
    //provider.login(decryptedValue)
    auth = new GoogleCredentialProvider(http, decryptedValue)
    (auth, http)
  }

  def getRefresh(auth_code: String): String = {
    val http: OkHttpClient = new OkHttpClient
    val provider: GoogleUserCredentialProvider = new GoogleUserCredentialProvider(http)
    provider.login(auth_code)
    val encryptedValue: String = Crypter.encryptAES(provider.getRefreshToken)
    encryptedValue
  }

  def getCacheable(position: Position, findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient
    var listPokemons = List[PokemonPosition]()

    val datos = authenticate(findPokemon.token, http)
    Thread.sleep(2000)
    val go: PokemonGo = new PokemonGo(datos._1, datos._2)

    go.getRequestHandler
    go.setLocation(position.latitud, position.longitud, 0)
    val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

    val catchablePokemon: List[MapPokemon] = spawnPoints.getCatchablePokemons.toList
    println("catchablePokemon in area:" + catchablePokemon.size)

    catchablePokemon.foreach(cp => {
      val timeTohide = if (cp.getExpirationTimestampMs > 0) cp.getExpirationTimestampMs else Calendar.getInstance().getTime().getTime + 120000
      listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, timeTohide, Some(Position(cp.getLatitude, cp.getLongitude))))
    })
    listPokemons
  }

  def sendMessage(): Unit = {
    println("--ingreso a sendmessage")
    val listPokemons = List[PokemonPosition](PokemonPosition(20, "ratata", 123456755, Some(Position(12355, 12455))),
      PokemonPosition(26, "ratata", 123456755, Some(Position(12355, 12455))),
      PokemonPosition(40, "dinora", 123445755, Some(Position(12355, 1275))))
    val mensaje = Message("57", listPokemons)
    val urToEmt = "http://50.116.54.176:3000/emitMessage";
    CallRestService.sendMessage(urToEmt, mensaje)
  }

  def getAllNearPokemons(findPokemon: FindPokemon): List[PokemonPosition] = {

    var listPokemons = List[PokemonPosition]()

    try {

      //val boxes = getBoundingBox(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 300)
      listPokemons = getCacheable(findPokemon.position.get, findPokemon)

      if (listPokemons.size > 0) {
      Future {
        //boxes.foreach(position => {
         // Thread.sleep(6000)
        //val otros = getCacheable(position, findPokemon)


            val df = new DecimalFormat("#.#")
            df.setRoundingMode(RoundingMode.DOWN)

            val lat: String = df.format(findPokemon.position.get.latitud)
            val lon: String = df.format(findPokemon.position.get.longitud)

            var result: String = lat + lon
            result = result.replace(".", "").replace(",", "")

            val mensaje = Message(result, listPokemons)
            val urToEmt = "http://50.116.54.176:3000/emitMessage";
            CallRestService.sendMessage(urToEmt, mensaje)
          }
        //})
      }

      //println("Pokemons iniciales: " + listPokemons)

      /*
      val wildPokemons: List[WildPokemon] = spawnPoints.getWildPokemons.toList
      println("wildPokemons in area:" + wildPokemons.size)
      wildPokemons.foreach(cp => {
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonData.getPokemonId.getNumber, cp.getPokemonData.getPokemonId.name, 0, Some(Position(cp.getLatitude, cp.getLongitude))))
      })*/

      /*val nearbyPokemons: List[NearbyPokemon] = spawnPoints.getNearbyPokemons.toList
      println("nearbyPokemons in area:" + nearbyPokemons.size)

      nearbyPokemons.foreach(cp => {
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, 0, Some(Position(cp.getLatitude, cp.getLongitude))))
      })*/

      listPokemons

    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        listPokemons
      }
    }
  }

  def getCatchablePokemons(findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient

    var listPokemons = List[PokemonPosition]()

    try {

      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(3000)

      //6.254010, -75.578931
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      val catchablePokemon: List[MapPokemon] = spawnPoints.getCatchablePokemons.toList

      println("Pokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {
        val timeTohide = if (cp.getExpirationTimestampMs > 0) cp.getExpirationTimestampMs else Calendar.getInstance().getTime().getTime + 120000
        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonId.getNumber, cp.getPokemonId.name, timeTohide, Some(Position(cp.getLatitude, cp.getLongitude))))
      })
      listPokemons
    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        listPokemons
      }
    }
  }


  def getNearPokemon(findPokemon: FindPokemon): List[PokemonPosition] = {
    val http: OkHttpClient = new OkHttpClient

    var listPokemons = List[PokemonPosition]()

    try {

      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(4000)

      //6.254010, -75.578931
      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      val catchablePokemon: List[WildPokemon] = spawnPoints.getWildPokemons.toList

      println("Pokemon in area:" + catchablePokemon.size)

      catchablePokemon.foreach(cp => {

        listPokemons = listPokemons ++ List(PokemonPosition(cp.getPokemonData.getPokemonId.getNumber, cp.getPokemonData.getPokemonId.name, cp.getTimeTillHiddenMs, Some(Position(cp.getLatitude, cp.getLongitude))))
      })
      listPokemons
    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        listPokemons
      }
    }
  }


  def getGyms(findPokemon: FindPokemon): List[Gym] = {
    val http: OkHttpClient = new OkHttpClient

    try {
      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)

      Thread.sleep(4000)

      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)
      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      println("Gyms :" + spawnPoints.getGyms.size())

      val listGyms = spawnPoints.getGyms.toList.map(gym => Gym(gym.getOwnedByTeam.name, Position(gym.getLatitude, gym.getLongitude)))
      listGyms
    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        List[Gym]()
      }
    }
  }

  def getPokeStop(findPokemon: FindPokemon): List[Stop] = {
    val http: OkHttpClient = new OkHttpClient

    try {
      val datos = authenticate(findPokemon.token, http)
      val go: PokemonGo = new PokemonGo(datos._1, datos._2)
      Thread.sleep(4000)

      go.setLocation(findPokemon.position.get.latitud, findPokemon.position.get.longitud, 0)

      val spawnPoints: MapObjects = go.getMap.getMapObjects(findPokemon.width)

      println("PokeStops :" + spawnPoints.getPokestops.size())

      val pokeStops = spawnPoints.getPokestops.map(stop => Stop("", Position(stop.getLatitude, stop.getLongitude)))
      pokeStops.toList

    }
    catch {
      case e: Any => {
        Log.e("Main", "Failed to login or server issue: ", e)
        List[Stop]()
      }
    }
  }

  def getBoundingBox(pLatitude: Double, pLongitude: Double, pDistanceInMeters: Int) = {

    val latRadian = Math.toRadians(pLatitude);

    val degLatKm = 110.574235;
    val degLongKm = 110.572833 * Math.cos(latRadian);
    val deltaLat = pDistanceInMeters / 1000.0 / degLatKm;
    val deltaLong = pDistanceInMeters / 1000.0 / degLongKm;

    val minLat = pLatitude - deltaLat;
    val minLong = pLongitude - deltaLong;
    val maxLat = pLatitude + deltaLat;
    val maxLong = pLongitude + deltaLong;

    val initial = Position(pLatitude, pLongitude)

    val listaPosicionesList = List(initial,
      Position(minLat, minLong),
      Position(minLat, maxLong),
      Position(maxLat, maxLong),
      Position(maxLat, minLong)
    )

    listaPosicionesList
  }


}
