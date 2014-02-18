package de.vanmar.android.yarrn.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class YarrnHttpMock extends NanoHTTPD {

    public YarrnHttpMock(final int port, final File wwwroot)
            throws IOException {
        super(port, wwwroot);
    }

    @Override
    public Response serve(final String uri, final String method,
                          final Properties header, final Properties parms,
                          final Properties files) {
        String uriToUse = uri;
        if (uriToUse.endsWith("list.json") && parms.containsKey("page")) {
            String page = parms.getProperty("page");
            if (!"1".equals(page)) {
                uriToUse = uri.replace("list.json", String.format("list%s.json", page));
            }
        }
        final Response response = super
                .serve(uriToUse, method, header, parms, files);
        if (uri.startsWith("/oauth/authorize")) {
            response.mimeType = MIME_HTML;
            response.addHeader("Content-Type", MIME_HTML);
            response.header.remove("Content-Disposition");
        }
        return response;
    }

    /**
     * Starts as a standalone file server and waits for Enter.
     */
    public static void main(final String[] args) {
        myOut.println("NanoHTTPD 1.27 (C) 2001,2005-2013 Jarno Elonen and (C) 2010 Konstantinos Togias\n"
                + "(Command line options: [-p port] [-d root-dir] [--licence])\nAdapted for Yarrn testing");

        // Defaults
        int port = 80;
        File wwwroot = new File(".").getAbsoluteFile();

        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-p")) {
                port = Integer.parseInt(args[i + 1]);
            } else if (args[i].equalsIgnoreCase("-d")) {
                wwwroot = new File(args[i + 1]).getAbsoluteFile();
            }
        }

        try {
            myOut.println("Starting server on port " + port);
            new YarrnHttpMock(port, wwwroot);
        } catch (final IOException ioe) {
            myErr.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        myOut.println("Now serving files in port " + port + " from \""
                + wwwroot + "\"");
        myOut.println("Hit Enter to stop.\n");

        try {
            System.in.read();
        } catch (final Throwable t) {
        }
    }

}
