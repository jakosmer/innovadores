package co.com.prototype.pokemap.Model.Repository;

import org.androidannotations.rest.spring.annotations.Accept;
import org.androidannotations.rest.spring.annotations.Body;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.api.MediaType;
import org.androidannotations.rest.spring.api.RestClientHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.List;

import co.com.prototype.pokemap.Model.Beans.PokemonPosition;

/**
 * Created by carlosmario on 17/07/2016.
 */
@Rest(rootUrl = "http://localhost:9000", converters = { MappingJackson2HttpMessageConverter.class })
public interface RestService extends RestClientHeaders {

    @Get("/findPokemon/{pokemonName}")
    //@Accept(MediaType.APPLICATION_JSON)
    List<PokemonPosition> getPokemonPositions(@Path String pokemonName);

}
