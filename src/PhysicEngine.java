import java.util.ArrayList;

public class PhysicEngine implements Engine {
    private ArrayList<DynamicSprite> movingSpriteList;
    private ArrayList<Sprite> environment;
    private GameEventListener gameEventListener; // Interface to handle game events

    public PhysicEngine() {
        movingSpriteList = new ArrayList<>();
        environment = new ArrayList<>();
    }

    // Add a dynamic sprite to the moving sprite list
    public void addToMovingSpriteList(DynamicSprite sprite) {
        if (!movingSpriteList.contains(sprite)) {
            movingSpriteList.add(sprite);
        }
    }

    // Add a sprite to the environment (static objects like walls, traps)
    public void addToEnvironmentList(Sprite sprite) {
        if (!environment.contains(sprite)) {
            environment.add(sprite);
        }
    }

    // Set the environment (list of static objects like walls, traps)
    public void setEnvironment(ArrayList<Sprite> environment) {
        this.environment = environment;
    }

    // Set the game event listener to handle game over events
    public void setGameEventListener(GameEventListener listener) {
        this.gameEventListener = listener;
    }

    @Override
    public void update() {
        for (DynamicSprite dynamicSprite : movingSpriteList) {
            // Check if the dynamic sprite can move in the environment
            dynamicSprite.moveIfPossible(environment);

            for (Sprite sprite : environment) {
                // Check if the sprite is a SolidSprite (e.g., traps)
                if (sprite instanceof SolidSprite) {
                    SolidSprite solidSprite = (SolidSprite) sprite;

                    // Check for collision with the trap sprite
                    if (solidSprite.getHitBox().intersects(dynamicSprite.getHitBox())) {
                        System.out.println("Trap collision detected! Game Over.");

                        // Trigger the Game Over event if a collision occurs
                        if (gameEventListener != null) {
                            //gameEventListener.onGameOver();
                        }
                        return; // Stop further updates after game over
                    }
                }
            }
        }
    }
}