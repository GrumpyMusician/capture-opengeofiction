package com.captureogf;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapRectangleImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;
import org.openstreetmap.gui.jmapviewer.tilesources.AbstractOsmTileSource;

public class BoundingBoxMap extends JMapViewer {

    private Coordinate startCoordinate;
    private Coordinate endCoordinate;
    private MapRectangle selection;

    private double minLat;
    private double minLon;
    private double maxLat;
    private double maxLon;

    private boolean hasSelection = false;

    public BoundingBoxMap() {
        // FIXED: Changed {zoom} to {z} to match the replacement logic inside getTileUrl
        AbstractOsmTileSource customTileSource = new AbstractOsmTileSource("Opengeofiction", "https://tile.opengeofiction.net/ogf-carto/{z}/{x}/{y}.png", "OGF/1.0") {
            @Override
            public String getTileUrl(int zoom, int tileX, int tileY) {
                return baseUrl
                        .replace("{z}", String.valueOf(zoom))
                        .replace("{x}", String.valueOf(tileX))
                        .replace("{y}", String.valueOf(tileY));
            }
            
            // @Override public int getMaxZoom() { return 19; }
            // @Override public int getMinZoom() { return 0; }
        };
        setTileSource(customTileSource);

        MouseAdapter selectionHandler = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                startCoordinate = toCoordinate(getPosition(e.getPoint()));

                if (startCoordinate == null) {
                    return;
                }

                endCoordinate = startCoordinate;
                hasSelection = false;

                if (selection != null) {
                    removeMapRectangle(selection);
                    selection = null;
                }

                updateSelection();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (startCoordinate == null) {
                    return;
                }

                endCoordinate = toCoordinate(getPosition(e.getPoint()));
                updateSelection();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (startCoordinate == null
                        || e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }

                endCoordinate = toCoordinate(getPosition(e.getPoint()));

                if (endCoordinate != null) {
                    updateSelection();
                }

                startCoordinate = null;
            }
        };

        addMouseListener(selectionHandler);
        addMouseMotionListener(selectionHandler);
    }

    private Coordinate toCoordinate(ICoordinate coordinate) {
        if (coordinate == null) {
            return null;
        }

        return new Coordinate(
            coordinate.getLat(),
            coordinate.getLon()
        );
    }

    private void updateSelection() {
        if (startCoordinate == null || endCoordinate == null) {
            return;
        }

        minLat = Math.min(
            startCoordinate.getLat(),
            endCoordinate.getLat()
        );

        maxLat = Math.max(
            startCoordinate.getLat(),
            endCoordinate.getLat()
        );

        minLon = Math.min(
            startCoordinate.getLon(),
            endCoordinate.getLon()
        );

        maxLon = Math.max(
            startCoordinate.getLon(),
            endCoordinate.getLon()
        );

        Coordinate topLeft = new Coordinate(maxLat, minLon);
        Coordinate bottomRight = new Coordinate(minLat, maxLon);

        if (selection != null) {
            removeMapRectangle(selection);
        }

        java.awt.BasicStroke stroke = new java.awt.BasicStroke(2.5f);

        org.openstreetmap.gui.jmapviewer.Style style =
            new org.openstreetmap.gui.jmapviewer.Style(
                java.awt.Color.RED,
                null,
                stroke,
                org.openstreetmap.gui.jmapviewer.MapRectangleImpl
                    .getDefaultStyle().getFont()
            );

        selection = new MapRectangleImpl(null, null, topLeft, bottomRight, style) {
            @Override
            public void paint(java.awt.Graphics g, java.awt.Point topLeft,
                            java.awt.Point bottomRight) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setColor(new java.awt.Color(255, 0, 0, 70));
                g2.fillRect(topLeft.x, topLeft.y,
                            bottomRight.x - topLeft.x,
                            bottomRight.y - topLeft.y);
                g2.dispose();

                super.paint(g, topLeft, bottomRight);
            }
        };
        addMapRectangle(selection);

        hasSelection = true;
    }

    public boolean hasSelection() {
        return hasSelection;
    }

    public double getMinLat() {
        return minLat;
    }

    public double getMinLon() {
        return minLon;
    }

    public double getMaxLat() {
        return maxLat;
    }

    public double getMaxLon() {
        return maxLon;
    }

    public void clearSelection() {
        if (selection != null) {
            removeMapRectangle(selection);
            selection = null;
        }

        startCoordinate = null;
        endCoordinate = null;
        hasSelection = false;
        repaint();
    }
}
