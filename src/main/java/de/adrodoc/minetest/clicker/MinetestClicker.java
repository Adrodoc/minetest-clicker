package de.adrodoc.minetest.clicker;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MinetestClicker extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  private final VBox list = new VBox();

  @Override
  public void start(Stage primaryStage) throws Exception {
    AtomicReference<String> materialRef = new AtomicReference<>(getRandomMaterial());
    ImageView breakableBlock = new ImageView(getMaterialImage(materialRef.get()));
    breakableBlock.setPickOnBounds(true);

    Map<String, Slot> slots = new HashMap<>();
    breakableBlock.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
      String material = materialRef.get();
      Slot slot = slots.computeIfAbsent(material, Slot::new);
      slot.incrementCount();
      materialRef.set(getRandomMaterial());
      breakableBlock.setImage(getMaterialImage(materialRef.get()));
    });
    Parent root = new HBox(createStoneBackground(breakableBlock), list);
    primaryStage.setScene(new Scene(root, 300, 500));
    primaryStage.show();
  }

  private StackPane createStoneBackground(ImageView breakableBlock) {
    return new StackPane(new ImageView(getMaterialImage("default_stone")), breakableBlock);
  }

  private Image getMaterialImage(String material) {
    InputStream is = getClass().getResourceAsStream("/" + material + ".png");
    double requestedWidth = 50;
    double requestedHeight = 50;
    boolean preserveRatio = true;
    boolean smooth = true;
    return new Image(is, requestedWidth, requestedHeight, preserveRatio, smooth);
  }

  private final Random random = new Random();
  private final Map<String, Integer> materialWeights = Map.ofEntries( //
      Map.entry("default_dirt", 100), //
      Map.entry("default_gravel", 50), //
      Map.entry("default_mineral_coal", 20), //
      Map.entry("default_mineral_copper", 10), //
      Map.entry("default_mineral_diamond", 1), //
      Map.entry("default_mineral_gold", 5), //
      Map.entry("default_mineral_iron", 10), //
      Map.entry("default_mineral_tin", 10), //
      Map.entry("default_sand", 50), //
      Map.entry("default_stone", 250) //
  );
  private final int totalWeight = materialWeights.values().stream().mapToInt(it -> it).sum();

  public String getRandomMaterial() {
    int r = random.nextInt(totalWeight);
    for (Entry<String, Integer> entry : materialWeights.entrySet()) {
      r -= entry.getValue();
      if (r < 0) {
        return entry.getKey();
      }
    }
    throw new AssertionError();
  }

  class Slot {
    private final Label counterLabel = new Label(String.valueOf(0));
    private int count;

    public Slot(String material) {
      ImageView imageView = new ImageView(getMaterialImage(material));
      StackPane materialNode = createStoneBackground(imageView);
      Label materialLabel = new Label(material);
      HBox node = new HBox(8, materialNode, materialLabel, counterLabel);
      node.setAlignment(Pos.CENTER_LEFT);
      list.getChildren().add(node);
    }

    public void incrementCount() {
      counterLabel.setText(String.valueOf(++count));
    }
  }
}
