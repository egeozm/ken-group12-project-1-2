public class BasicBot {
    private Terrainn terrainn;

    public BasicBot(Terrainn terrainn) {
        this.terrainn = terrainn;
    }

    // Basic decision-making
    public String decideNextMove(int x, int z) {
        if (x < 0 || x >= terrainn.x_width || z < 0 || z >= terrainn.z_width) {
            return "out of bounds";
        }
        Terrainn.cell currentCell = terrainn.field[x][z];
        String material = currentCell.get_material();
        int maxTilt = 10;

        switch (material) {
            case "wall":
            case "water":
                return "stay";
            case "grass":
            case "sand":
                if (Math.abs(currentCell.getx_tilt()) > maxTilt || Math.abs(currentCell.getz_tilt()) > maxTilt) {
                    return "stay";
                }
                return "move forward";
            default:
                return "stay";
        }

    }
}
