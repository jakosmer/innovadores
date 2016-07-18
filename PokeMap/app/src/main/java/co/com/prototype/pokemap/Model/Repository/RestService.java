package co.com.prototype.pokemap.Model.Repository;

import org.androidannotations.rest.spring.annotations.Accept;
import org.androidannotations.rest.spring.annotations.Body;
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
@Rest(rootUrl = "192.168.1.55:9000/", converters = { MappingJackson2HttpMessageConverter.class })
public interface RestService extends RestClientHeaders {

    @Post("findPokemon")
    @Accept(MediaType.APPLICATION_JSON)
    List<PokemonPosition> getPokemonPositions(@Body String pokemonName);

}
