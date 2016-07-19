package co.com.prototype.pokemap;


import org.androidannotations.rest.spring.annotations.RestService;
import org.junit.Test;

import java.util.List;

import co.com.prototype.pokemap.Model.Beans.PokemonPosition;

/**
 * Created by carlosmario on 17/07/2016.
 */

public class RestClientTest {

    @RestService
    co.com.prototype.pokemap.Model.Repository.RestService service;

    @Test
    public void getPokemonByNameTest(){
        List<PokemonPosition> pos = service.getPokemonPositions("pikachu");

        assert( pos.size() > 0  );
    }

}