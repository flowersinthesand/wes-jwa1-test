/*
 * Copyright 2013-2014 Donghwan Kim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.flowersinthesand.wes.jwa;

import io.github.flowersinthesand.wes.Action;
import io.github.flowersinthesand.wes.ServerWebSocket;
import io.github.flowersinthesand.wes.test.ServerWebSocketTestTemplate;

import java.io.File;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredArchive;
import org.junit.BeforeClass;

public class ServerWebSocketTest extends ServerWebSocketTestTemplate {

	static AtomicReference<Performer> performerRef = new AtomicReference<>();
	static GlassFishRuntime runtime;
	GlassFish glassfish;
	
	@BeforeClass
	public static void setup() throws GlassFishException {
		// Due to GlassFishException: Already bootstrapped
		runtime = GlassFishRuntime.bootstrap();
	}

	@Override
	protected void startServer() throws Exception {
		GlassFishProperties props = new GlassFishProperties();
		props.setPort("http-listener", port);

		glassfish = runtime.newGlassFish(props);
		glassfish.start();
		
		Deployer deployer = glassfish.getDeployer();
		ScatteredArchive archive = new ScatteredArchive("testapp", ScatteredArchive.Type.WAR);
		archive.addClassPath(new File("target", "test-classes"));
		deployer.deploy(archive.toURI(), "--contextroot=/");
		
		performerRef.set(performer);
	}

	@Override
	protected void stopServer() throws Exception {
		glassfish.dispose();
	}
	
	public static class GlassfishServerApplicationConfig implements ServerApplicationConfig {
		
		@Override
		public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> arg0) {
			return Collections.singleton(new JwaBridge("/test").websocketAction(new Action<ServerWebSocket>() {
				@Override
				public void on(ServerWebSocket ws) {
					Performer performer = performerRef.getAndSet(null);
					performer.serverAction().on(ws);
				}
			})
			.config());
		}
		
		@Override
		public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> arg0) {
			return Collections.emptySet();
		}
		
	}

}
