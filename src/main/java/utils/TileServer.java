package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.Executors;

/**
 * Minimal embedded HTTP server that serves OSM tile files from a local folder.
 *
 * Tiles must be stored under <tiles_dir>/{z}/{x}/{y}.png
 *
 * Usage:
 * TileServer srv = new TileServer(tilesDirectory);
 * int port = srv.start(); // returns the port chosen
 * // … open WebView, set tile URL to "http://localhost:<port>/{z}/{x}/{y}.png"
 * srv.stop(); // call when the map window is closed
 */
public class TileServer {

    private final File tilesDir;
    private HttpServer server;

    public TileServer(File tilesDir) {
        this.tilesDir = tilesDir;
    }

    /** Start the server on a free port and return that port number. */
    public int start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0); // port 0 = OS assigns free port
        server.setExecutor(Executors.newCachedThreadPool());

        server.createContext("/", this::handleTile);
        server.start();

        int port = server.getAddress().getPort();
        System.out.println("[TileServer] Serving tiles on http://localhost:" + port + "/{z}/{x}/{y}.png");
        return port;
    }

    /** Stop the server. */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[TileServer] Stopped.");
        }
    }

    // ------------------------------------------------------------------
    private void handleTile(HttpExchange ex) throws IOException {
        // Allow CORS so the WebView JS can fetch the tiles
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Cache-Control", "max-age=86400");

        // Path is "/{z}/{x}/{y}.png"
        String path = ex.getRequestURI().getPath(); // e.g. "/13/2498/3041.png"
        File tile = new File(tilesDir, path.replace('/', File.separatorChar));

        if (tile.exists() && tile.isFile()) {
            byte[] data = Files.readAllBytes(tile.toPath());
            ex.getResponseHeaders().add("Content-Type", "image/png");
            ex.sendResponseHeaders(200, data.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(data);
            }
        } else {
            // Proxy the tile from OSM and cache it locally
            String[] servers = { "a", "b", "c" };
            String serverPrefix = servers[(int) (Math.random() * servers.length)];
            String osmUrl = "https://" + serverPrefix + ".tile.openstreetmap.org" + path;

            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(osmUrl)
                        .openConnection();
                conn.setRequestProperty("User-Agent", "JavaFX-MapApp/1.0");
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(5000);

                try (InputStream in = conn.getInputStream()) {
                    byte[] data = in.readAllBytes();
                    // Save to cache
                    tile.getParentFile().mkdirs();
                    Files.write(tile.toPath(), data);

                    // Serve
                    ex.getResponseHeaders().add("Content-Type", "image/png");
                    ex.sendResponseHeaders(200, data.length);
                    try (OutputStream os = ex.getResponseBody()) {
                        os.write(data);
                    }
                }
            } catch (Exception e) {
                // Tile not found or network error — return a 1×1 transparent PNG
                byte[] empty = TRANSPARENT_PNG;
                ex.getResponseHeaders().add("Content-Type", "image/png");
                ex.sendResponseHeaders(200, empty.length);
                try (OutputStream os = ex.getResponseBody()) {
                    os.write(empty);
                }
            }
        }
    }

    // Minimal 1×1 transparent PNG (67 bytes)
    private static final byte[] TRANSPARENT_PNG = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // PNG signature
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, // IHDR chunk
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4, (byte) 0x89,
            0x00, 0x00, 0x00, 0x0B, 0x49, 0x44, 0x41, 0x54, // IDAT chunk
            0x78, (byte) 0x9C, 0x62, 0x00, 0x00, 0x00, 0x02, 0x00,
            0x01, (byte) 0xE2, 0x21, (byte) 0xBC, 0x33,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, // IEND chunk
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
    };
}
