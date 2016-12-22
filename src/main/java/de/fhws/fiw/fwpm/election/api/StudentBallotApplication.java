package de.fhws.fiw.fwpm.election.api;


import de.fhws.fiw.fwpm.election.authentication.AuthFilter;
import de.fhws.fiw.fwpm.election.executors.CleanUpDBExecutorService;
import de.fhws.fiw.fwpm.election.storage.Persistency;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

import javax.ws.rs.ApplicationPath;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christianbraun on 13/05/16.
 */
@ApplicationPath("/api")
public class StudentBallotApplication extends ResourceConfig {

	private CleanUpDBExecutorService cleanUpDBExecutorService;

	public StudentBallotApplication() {
		super();
		cleanUpDBExecutorService = new CleanUpDBExecutorService();

		registerClasses(getResourceClasses());
		register(RolesAllowedDynamicFeature.class);
		register(new AuthFilter());
		register(new CorsFilter());
		register(new ContainerLifecycleListener() {

			@Override
			public void onStartup(Container container) {
				cleanUpDBExecutorService.start();
			}

			@Override
			public void onReload(Container container) {}

			@Override
			public void onShutdown(Container container) {
				try {
					cleanUpDBExecutorService.stop();
					Persistency.getInstance(false).closeConnectionPool();
					AuthFilter.userCache.clear();
					System.out.println("Shutdown Connection Pool");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}


	public Set<Class<?>> getResourceClasses() {
		final Set<Class<?>> classes = new HashSet<>();
		classes.add(EntryResource.class);
		classes.add(BallotResource.class);
		classes.add(StatisticResource.class);
		return classes;
	}
}
