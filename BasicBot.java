public class BasicBot {
    int posX, posY; // Current position of the bot
    Terrainn terrain; // The terrain the bot is in

    public BasicBot(Terrainn terrain) {
        this.terrain = terrain;
        this.posX = 0; // Starting X position
        this.posY = 0; // Starting Y position
        // You might want to start at a 'spawn' location instead
    }

    public void moveTo(int targetX, int targetY) {
        // Simple logic to move towards targetX and targetY
        while (posX != targetX) {
            if (posX < targetX) posX++;
            else if (posX > targetX) posX--;
            if (!isValidPosition(posX, posY)) break; // Check for walls or holes
        }
        while (posY != targetY) {
            if (posY < targetY) posY++;
            else if (posY > targetY) posY--;
            if (!isValidPosition(posX, posY)) break; // Check for walls or holes
        }
    }

    private boolean isValidPosition(int x, int y) {
        Terrainn.cell c = terrain.field[x][y];
        return !c.get_mat().equals("wall") && !((Terrainn.cell) c).get_mat().equals("hole");
    }
}
