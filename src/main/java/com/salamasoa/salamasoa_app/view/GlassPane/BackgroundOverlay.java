package com.salamasoa.salamasoa_app.view.GlassPane;

import javax.swing.*;
import java.awt.*;

public final class BackgroundOverlay {

    private BackgroundOverlay() {
        // Empêche la création d'un objet BackgroundOverlay.
    }

    /**
     * Place un voile sombre transparent sur la fenêtre principale.
     *
     * @param owner fenêtre principale de l'application
     * @return un handle permettant de retirer le voile plus tard
     */
    public static OverlayHandle show(Window owner) {
        if (!(owner instanceof RootPaneContainer)) {
            return null;
        }

        RootPaneContainer rootPaneContainer =
                (RootPaneContainer) owner;

        Component previousGlassPane = rootPaneContainer.getGlassPane();

        boolean previousGlassPaneVisible =
                previousGlassPane != null && previousGlassPane.isVisible();

        DimmedPanel dimmedPanel = new DimmedPanel();

        rootPaneContainer.setGlassPane(dimmedPanel);
        dimmedPanel.setVisible(true);

        return new OverlayHandle(
                rootPaneContainer,
                previousGlassPane,
                previousGlassPaneVisible,
                dimmedPanel
        );
    }

    /**
     * Objet qui mémorise l'ancien GlassPane et permet de supprimer
     * l'overlay lorsque le formulaire est fermé.
     */
    public static class OverlayHandle {

        private final RootPaneContainer rootPaneContainer;
        private final Component previousGlassPane;
        private final boolean previousGlassPaneVisible;
        private final Component overlay;

        private boolean closed = false;

        private OverlayHandle(
                RootPaneContainer rootPaneContainer,
                Component previousGlassPane,
                boolean previousGlassPaneVisible,
                Component overlay
        ) {
            this.rootPaneContainer = rootPaneContainer;
            this.previousGlassPane = previousGlassPane;
            this.previousGlassPaneVisible = previousGlassPaneVisible;
            this.overlay = overlay;
        }

        /**
         * Retire le fond sombre et remet la fenêtre principale
         * dans son état initial.
         */
        public void close() {
            if (closed) {
                return;
            }

            overlay.setVisible(false);

            if (previousGlassPane != null) {
                rootPaneContainer.setGlassPane(previousGlassPane);
                previousGlassPane.setVisible(previousGlassPaneVisible);
            }

            closed = true;
        }
    }

    /**
     * Panneau transparent dessiné sur toute la fenêtre principale.
     */
    private static class DimmedPanel extends JPanel {

        public DimmedPanel() {
            setOpaque(false);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();

            graphics2D.setColor(new Color(25, 20, 20, 125));
            graphics2D.fillRect(0, 0, getWidth(), getHeight());

            graphics2D.dispose();
        }
    }
}