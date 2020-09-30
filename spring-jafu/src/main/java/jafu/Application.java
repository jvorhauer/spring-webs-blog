package jafu;

import static org.springframework.fu.jafu.Jafu.reactiveWebApplication;
import static org.springframework.fu.jafu.r2dbc.R2dbcDsl.r2dbc;
import static org.springframework.fu.jafu.webflux.WebFluxServerDsl.webFlux;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.fu.jafu.ConfigurationDsl;
import org.springframework.fu.jafu.JafuApplication;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.util.function.Consumer;

public class Application {

  public static final JafuApplication app = reactiveWebApplication(
    app -> app.enable(Application.dataConfig).enable(Application.webConfig)
  );

  public static void main(final String[] args) {
    app.run(args);
  }


  public static Consumer<ConfigurationDsl> dataConfig = conf ->
                conf.beans(beans ->
                             beans.bean(ConnectionFactoryInitializer.class, () -> {
                               ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
                               initializer.setConnectionFactory(beans.ref(ConnectionFactory.class));
                               initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("table.sql")));
                               return initializer;
                             })
                             .bean(ToDoRepository.class)

                ).enable(r2dbc(dsl -> dsl.url("r2dbc:h2:mem:///testdb;DB_CLOSE_DELAY=-1")));

  public static Consumer<ConfigurationDsl> webConfig = conf ->
                 conf.beans(beans ->
                              beans.bean(ToDoHandler.class, () -> new ToDoHandler(beans.ref(ToDoRepository.class)))
                 ).enable(webFlux(server -> {
                   server.port(8080)
                     .router(r ->
                               r.GET("/todos", conf.ref(ToDoHandler.class)::all)
                                .POST("/todos", conf.ref(ToDoHandler.class)::add))
                     .codecs(codecs -> codecs.string().jackson());
                 }));
}
