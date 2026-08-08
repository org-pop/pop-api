package com.acessibiliadade.pop.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    /**
     * Roda repair() antes de migrate() em todo startup.
     *
     * O banco de dev/homologação da equipe acumulou entradas em flyway_schema_history
     * de migrations que depois tiveram o conteúdo (ou o nome) alterado — o caso do
     * V005 antigo que rodou fora do repositório, e do rename V00X_ -> V00X__ que
     * mudou o checksum calculado sobre o texto do arquivo.
     *
     * repair() realinha checksums, descrições e nomes das entradas já aplicadas
     * com os arquivos locais, e marca como deletadas as migrations que existem no
     * banco mas sumiram do disco (ex.: V006 órfã de um experimento anterior).
     *
     * Não re-executa migration alguma: entradas com status "success" continuam
     * marcadas como aplicadas. É seguro rodar em todo startup.
     */
    @Bean
    public FlywayMigrationStrategy repairAndMigrate() {
        return flyway -> {
            flyway.repair();
            flyway.migrate();
        };
    }
}
