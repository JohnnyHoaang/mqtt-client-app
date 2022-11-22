package com.mycompany.mqtt.client.app;

import java.io.IOException;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Stop;

public class FXDashboard extends HBox {
    
    public FXDashboard() throws IOException {
        this.buildScreen();
    }
    private void buildScreen() throws IOException {

        var PREF_WIDTH = 350;
        var PREF_HEIGHT = 300;
        var gaugeTile = TileBuilder.create()
            .skinType(Tile.SkinType.GAUGE)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Temp Gauge")
            .text("Temperature")
            .unit("Celsius")
            .textVisible(true)
            .value(0)
            .gradientStops(new Stop(0, Tile.BLUE),
                           new Stop(0.25, Tile.GREEN),
                           new Stop(0.5, Tile.YELLOW),
                           new Stop(0.75, Tile.ORANGE),
                           new Stop(1, Tile.RED))
            .strokeWithGradient(true)
            .animated(true)
            .build();

        var percentageTile = TileBuilder.create()
            .skinType(Tile.SkinType.PERCENTAGE)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .title("Humidity")
            .unit("Percent")
            .description("Humidity")
            .maxValue(60)
            .build();

        //Setup a tile with an exit button to end the application
        var exitButton = new Button("Exit");

        //Setup event handler for the exit button
        exitButton.setOnAction(e -> endApplication());

        var exitTile = TileBuilder.create()
            .skinType(Tile.SkinType.CUSTOM)
            .prefSize(PREF_WIDTH, PREF_HEIGHT)
            .textSize(Tile.TextSize.BIGGER)
            .title("Quit the application")
            .graphic(exitButton)
            .roundedCorners(false)
            .build();
        
        var tilesColumn1 = new VBox(gaugeTile, percentageTile, exitTile);
        tilesColumn1.setMinWidth(PREF_WIDTH);
        tilesColumn1.setSpacing(5);

        this.getChildren().add(tilesColumn1);
        this.setSpacing(5);
    }

    private void endApplication() {
        Platform.exit();
    }

}
