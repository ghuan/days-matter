package com.msf.util.cnd.converter.support;

import org.springframework.core.convert.converter.Converter;

import java.net.InetSocketAddress;

public class StringToInetSocketAddress implements Converter<String, InetSocketAddress> {
	public StringToInetSocketAddress() {
	}

	public InetSocketAddress convert(String source) {
		int i = source.indexOf(58);
		String host;
		int port;
		if (i > -1) {
			host = source.substring(0, i);
			port = Integer.parseInt(source.substring(i + 1));
		} else {
			host = source;
			port = 0;
		}
		return new InetSocketAddress(host, port);
	}
}
