package fr.cerema.dsi.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.cerema.dsi.commons.datastore.GeoPkgReader;
import fr.cerema.dsi.commons.datastore.SQLiteTableReader;
import fr.cerema.dsi.commons.datastore.ShpReader;

@Configuration
public class ReaderConfig {

    @Bean
    public ShpReader shpReaderBean(){
        return new ShpReader();
    }

    @Bean
    public GeoPkgReader geoPkgReaderBean(){
        return new GeoPkgReader();
    }
    
    @Bean
    public SQLiteTableReader sQLiteTableReaderBean(){
        return new SQLiteTableReader();
    }
}
