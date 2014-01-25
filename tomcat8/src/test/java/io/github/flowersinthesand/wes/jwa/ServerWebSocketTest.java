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

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import io.github.flowersinthesand.wes.Action;
import io.github.flowersinthesand.wes.ServerWebSocket;
import io.github.flowersinthesand.wes.test.ServerWebSocketTestTemplate;

import java.io.File;

import javax.servlet.ServletContext;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.Constants;
import org.junit.Test;

public class ServerWebSocketTest extends ServerWebSocketTestTemplate {

	Tomcat tomcat;

	@Override
	protected void startServer() throws Exception {
		ServerEndpointConfig config = new JwaBridge("/test").websocketAction(new Action<ServerWebSocket>() {
			@Override
			public void on(ServerWebSocket ws) {
				performer.serverAction().on(ws);
			}
		})
		.config();
		
		tomcat = new Tomcat();
		tomcat.setPort(port);
		Context context = tomcat.addWebapp("/", new File("src/test").getAbsolutePath());
		tomcat.start();

		ServletContext ctx = context.getServletContext();
		ServerContainer container = (ServerContainer) ctx.getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
		container.addEndpoint(config);
	}
	
	@Test
	public void unwrap() {
		performer.serverAction(new Action<ServerWebSocket>() {
			@Override
			public void on(ServerWebSocket ws) {
				assertThat(ws.unwrap(Session.class), instanceOf(Session.class));
				performer.start();
			}
		})
		.connect();
	}

	@Override
	protected void stopServer() throws Exception {
		tomcat.getServer().stop();
	}

}
