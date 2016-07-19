package dto

/**
  * Created by arcearta on 2016/07/17.
  */
case  class PokemonInfo(name : String , typePokemon: String)

case  class FindPokemon(name : Option[String], lat: Option[Double], lon: Option[Double])

case  class PokemonPosition(name : String, country: Option[String], city: Option[String], lat: Option[Double], lon: Option[Double])

