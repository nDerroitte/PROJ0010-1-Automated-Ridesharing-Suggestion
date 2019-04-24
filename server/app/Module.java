import com.google.inject.AbstractModule;
import java.time.Clock;

import services.MongoInterface;
import services.MongoDB;
import services.HabitGenerator;
import services.ThreadExecutor;
/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
	    bind(MongoInterface.class).to(MongoDB.class);
        bind(HabitGenerator.class).to(ThreadExecutor.class);
    }

}
