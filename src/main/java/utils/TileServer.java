package utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.TileBitmap;
import org.mapsforge.core.model.Tile;
import org.mapsforge.map.awt.graphics.AwtGraphicFactory;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.renderer.DatabaseRenderer;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.rendertheme.rule.RenderThemeFuture;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.lang.reflect.Method;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class TileServer {

    private final File mapFile;
    private HttpServer server;

    private MapDataStore mapDataStore;
    private GraphicFactory graphicFactory;
    private DisplayModel displayModel;
    private RenderThemeFuture themeFuture;
    private DatabaseRenderer renderer;

    public TileServer(File tilesDir) {
        // Find map file regardless of where it's launched
        File file = new File("src/main/resources/tunisia.map");
        if (!file.exists()) {
            file = new File("tunisia.map");
            if (!file.exists()) {
                try {
                    file = new File(getClass().getClassLoader().getResource("tunisia.map").toURI());
                } catch (Exception e) {
                }
            }
        }
        this.mapFile = file;
        System.out.println("[TileServer] Using map file: " + mapFile.getAbsolutePath());
    }

    public int start() throws IOException {
        try {
            if (mapFile.exists()) {
                graphicFactory = AwtGraphicFactory.INSTANCE;
                mapDataStore = new MapFile(mapFile);
                displayModel = new DisplayModel();
                themeFuture = new RenderThemeFuture(graphicFactory, InternalRenderTheme.DEFAULT, displayModel);
                new Thread(themeFuture).start();

                // Initialize a memory tile cache to prevent NullPointerException
                org.mapsforge.map.layer.cache.TileCache tileCache = new org.mapsforge.map.layer.cache.InMemoryTileCache(
                        128);
                // Reflection to bypass any compiler constructor ambiguity in DatabaseRenderer
                Class<?> clazz = Class.forName("org.mapsforge.map.layer.renderer.DatabaseRenderer");
                for (java.lang.reflect.Constructor<?> c : clazz.getConstructors()) {
                    if (c.getParameterCount() == 7) {
                        // Parameters: MapDataStore, GraphicFactory, TileCache, TileBasedLabelStore,
                        // isTransparent, renderLabels, HillsRenderConfig
                        renderer = (DatabaseRenderer) c.newInstance(mapDataStore, graphicFactory, tileCache, null, true,
                                true, null);
                        System.out.println(
                                "[TileServer] DatabaseRenderer initialized successfully with 7-arg constructor.");
                        break;
                    } else if (c.getParameterCount() == 4) {
                        // Fallback for older Mapsforge versions taking (MapDataStore, GraphicFactory,
                        // TileCache, RenderThemeFuture)
                        renderer = (DatabaseRenderer) c.newInstance(mapDataStore, graphicFactory, null, themeFuture);
                        System.out.println(
                                "[TileServer] DatabaseRenderer initialized successfully with 4-arg constructor.");
                        break;
                    }
                }
                if (renderer == null) {
                    System.err.println(
                            "[TileServer] WARNING: Could not initialize DatabaseRenderer! (No 4-arg constructor found)");
                    System.err.println("[TileServer] Available constructors:");
                    for (java.lang.reflect.Constructor<?> c : clazz.getConstructors()) {
                        System.err.println("  " + c);
                    }
                }
            } else {
                System.err.println("[TileServer] FATAL: tunisia.map not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/", this::handleTile);
        server.start();

        int port = server.getAddress().getPort();
        System.out.println(
                "[TileServer] Serving offline Mapsforge tiles on http://localhost:" + port + "/{z}/{x}/{y}.png");
        return port;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("[TileServer] Stopped.");
        }
        if (mapDataStore != null) {
            mapDataStore.close();
        }
    }

    private void handleTile(HttpExchange ex) throws IOException {
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Cache-Control", "max-age=86400");

        String path = ex.getRequestURI().getPath(); // "/{z}/{x}/{y}.png"
        try {
            String[] parts = path.substring(1).replace(".png", "").split("/");
            if (parts.length == 3 && renderer != null) {
                byte z = Byte.parseByte(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);

                Tile tile = new Tile(x, y, z, 256);
                // Reflection to bypass RendererJob constructor differences
                Object job = null;
                Class<?> jobClass = Class.forName("org.mapsforge.map.layer.renderer.RendererJob");
                for (java.lang.reflect.Constructor<?> c : jobClass.getConstructors()) {
                    if (c.getParameterCount() == 7) {
                        job = c.newInstance(tile, mapDataStore, themeFuture, displayModel, 1.0f, false, false);
                        break;
                    }
                }

                TileBitmap bitmap = null;
                if (job != null) {
                    Method executeJob = renderer.getClass().getMethod("executeJob", jobClass);
                    bitmap = (TileBitmap) executeJob.invoke(renderer, job);
                }

                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    boolean success = false;
                    try {
                        Method compressMethod = bitmap.getClass().getMethod("compress", OutputStream.class);
                        compressMethod.invoke(bitmap, baos);
                        success = true;
                    } catch (Exception e) {
                        try {
                            // org.mapsforge.map.awt.graphics.AwtBitmap has .getImage() or .getAwtImage()
                            // maybe? (it's actually java.awt.Image image() but checking combinations)
                            Method getImageMethod = null;
                            try {
                                getImageMethod = bitmap.getClass().getMethod("getImage");
                            } catch (Exception ex2) {
                                try {
                                    getImageMethod = bitmap.getClass().getMethod("getAwtImage");
                                } catch (Exception ex3) {
                                    getImageMethod = bitmap.getClass().getMethod("image");
                                }
                            }
                            if (getImageMethod != null) {
                                Object awtImg = getImageMethod.invoke(bitmap);
                                if (awtImg instanceof BufferedImage) {
                                    ImageIO.write((BufferedImage) awtImg, "png", baos);
                                    success = true;
                                } else if (awtImg instanceof java.awt.Image) {
                                    BufferedImage bImg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                                    java.awt.Graphics2D g = bImg.createGraphics();
                                    g.drawImage((java.awt.Image) awtImg, 0, 0, null);
                                    g.dispose();
                                    ImageIO.write(bImg, "png", baos);
                                    success = true;
                                }
                            }
                        } catch (Exception ex2) {
                            ex2.printStackTrace();
                        }
                    }

                    if (success) {
                        byte[] data = baos.toByteArray();
                        if (data.length > 0) {
                            ex.getResponseHeaders().add("Content-Type", "image/png");
                            ex.sendResponseHeaders(200, data.length);
                            try (OutputStream os = ex.getResponseBody()) {
                                os.write(data);
                            }
                            bitmap.decrementRefCount();
                            return;
                        }
                    } else {
                        System.err.println("[TileServer] Tile rendering failed! Image conversion unsuccessful.");
                    }
                    bitmap.decrementRefCount();
                } else {
                    System.err.println("[TileServer] executeJob returned null bitmap for tile: " + path);
                }
            } else {
                if (renderer == null)
                    System.err.println("[TileServer] Cannot render tile, renderer is null.");
            }
        } catch (Exception e) {
            System.err.println("[TileServer] Exception during handleTile:");
            e.printStackTrace();
        }

        // Return empty transparent PNG if rendering failed
        System.out.println("[TileServer] Returning transparent tile for " + path);
        byte[] empty = TRANSPARENT_PNG;
        ex.getResponseHeaders().add("Content-Type", "image/png");
        ex.sendResponseHeaders(200, empty.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(empty);
        }
    }

    private static final byte[] TRANSPARENT_PNG = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4, (byte) 0x89,
            0x00, 0x00, 0x00, 0x0B, 0x49, 0x44, 0x41, 0x54,
            0x78, (byte) 0x9C, 0x62, 0x00, 0x00, 0x00, 0x02, 0x00,
            0x01, (byte) 0xE2, 0x21, (byte) 0xBC, 0x33,
            0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44,
            (byte) 0xAE, 0x42, 0x60, (byte) 0x82
    };
}
