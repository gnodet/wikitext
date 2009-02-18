package org.xplanner.soap.XPlanner;

import java.net.Proxy;
import java.net.URL;

import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.CommonsHTTPSender;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.IProxyProvider;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.commons.net.WebUtil;

@SuppressWarnings( { "serial" })
public class XPlannerHttpSender extends CommonsHTTPSender {

	public static final String PROXY = "org.eclipse.mylyn.xplanner.proxy"; //$NON-NLS-1$

	public static final String HTTP_USER = "org.eclipse.mylyn.xplanner.httpUser"; //$NON-NLS-1$

	public static final String HTTP_PASSWORD = "org.eclipse.mylyn.xplanner.httpPassword"; //$NON-NLS-1$

	public XPlannerHttpSender() {
		this.connectionManager = WebUtil.getConnectionManager();
	}

	@Override
	protected HostConfiguration getHostConfiguration(HttpClient client, MessageContext context, URL url) {
		final Proxy proxy = (Proxy) context.getProperty(PROXY);
		String httpUser = (String) context.getProperty(HTTP_USER);
		String httpPassword = (String) context.getProperty(HTTP_PASSWORD);
		WebLocation webLocation = new WebLocation(url.toString(), httpUser, httpPassword, new IProxyProvider() {
			public Proxy getProxyForHost(String host, String proxyType) {
				return proxy;
			}
		});
		return WebUtil.createHostConfiguration(client, webLocation, new NullProgressMonitor());
	}
}
