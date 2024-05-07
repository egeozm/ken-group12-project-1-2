public class BasicBot {
    int posX, posY; // Current position of the bot
    Terrainn terrain; // The terrain the bot is in
    int targetX, targetY; // Target position

    public BasicBot(Terrainn terrain, int targetX, int targetY) {
        this.terrain = terrain;
        this.posX = 0; // Starting X position
        this.posY = 0; // Starting Y position
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public int[] decideNextMove() {
        int[] bestMove = new int[]{posX, posY};
        double bestDistance = Double.MAX_VALUE;

        int[][] moves = {
                {posX + 1, posY}, // East
                {posX - 1, posY}, // West
                {posX, posY + 1}, // South
                {posX, posY - 1}  // North
        };

        for (int[] move : moves) {
            if (isValidPosition(move[0], move[1])) {
                double distance = calculateDistance(move[0], move[1], targetX, targetY);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestMove = move;
                }
            }
        }

        posX = bestMove[0];
        posY = bestMove[1];
        return bestMove;
    }

    private boolean isValidPosition(int x, int y) {
        if (x >= 0 && x < terrain.field.length && y >= 0 && y < terrain.field[x].length) {
            Terrainn.cell c = terrain.field[x][y];
            return !c.get_mat().equals("wall") && !c.get_mat().equals("hole");
        }
        return false;
    }

    private double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
